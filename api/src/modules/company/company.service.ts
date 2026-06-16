import type { JobStatus } from "../../generated/prisma/enums";
import {
  BadRequestError,
  ConflictError,
  ForbiddenError,
  NotFoundError,
  UnauthorizedError,
} from "../../shared/errors/AppError";
import type { ChangePasswordInput, PaginationQuery } from "../../shared/schemas/common.schema";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import type { PaginationMeta } from "../../shared/utils/response";
import type { NotificationService } from "../notification/notification.service";
import { NotificationType } from "../notification/notification.types";
import type { CompanyRepository } from "./company.repository";
import type {
  ChangeApplicationStatusInput,
  CreateJobInput,
  CreateMemberInput,
  UpdateCompanyProfileInput,
  UpdateJobInput,
  UpdateMemberInput,
  UpdateMyProfileInput,
} from "./company.schema";

// Transições válidas de status de vaga. CLOSED é terminal.
const JOB_STATUS_TRANSITIONS: Record<JobStatus, JobStatus[]> = {
  ACTIVE: ["PAUSED", "CLOSED"],
  PAUSED: ["ACTIVE", "CLOSED"],
  CLOSED: [],
};

// Mensagem exibida ao estudante conforme o novo status da candidatura.
const APPLICATION_STATUS_MESSAGE: Partial<Record<ChangeApplicationStatusInput["status"], string>> = {
  ANALYSING: "está em análise",
  APPROVED: "foi aprovada",
  REJECTED: "foi rejeitada",
};

export class CompanyService {
  constructor(
    private readonly companyRepository: CompanyRepository,
    private readonly notificationService: NotificationService,
  ) {}

  // ─── Perfil ───────────────────────────────────────────────────────────────

  async getProfile(userId: string) {
    const member = await this.getMemberOrThrow(userId);
    const company = await this.companyRepository.getCompanyProfile(member.companyId);
    if (!company) throw new NotFoundError("Empresa não encontrada.");
    return company;
  }

  async updateProfile(userId: string, data: UpdateCompanyProfileInput) {
    const member = await this.getMemberOrThrow(userId);
    return this.companyRepository.updateCompany(member.companyId, data);
  }

  // ─── Membros ──────────────────────────────────────────────────────────────

  async listMembers(userId: string) {
    const member = await this.getMemberOrThrow(userId);
    return this.companyRepository.listMembers(member.companyId);
  }

  async createMember(actingUserId: string, data: CreateMemberInput) {
    const acting = await this.getMemberOrThrow(actingUserId);
    const password = await generateHash(data.password);
    try {
      return await this.companyRepository.createMember(acting.companyId, { ...data, password });
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail ou CPF já cadastrado.");
      }
      throw error;
    }
  }

  async updateMember(actingUserId: string, memberId: string, data: UpdateMemberInput) {
    const target = await this.getMemberOfSameCompany(actingUserId, memberId);
    this.assertNotSelf(actingUserId, target.userId);
    return this.companyRepository.updateMember(memberId, data);
  }

  async deleteMember(actingUserId: string, memberId: string): Promise<void> {
    const target = await this.getMemberOfSameCompany(actingUserId, memberId);
    this.assertNotSelf(actingUserId, target.userId);
    await this.companyRepository.deleteMember(target.userId);
  }

  async resetMemberTotp(actingUserId: string, memberId: string): Promise<void> {
    const target = await this.getMemberOfSameCompany(actingUserId, memberId);
    this.assertNotSelf(actingUserId, target.userId);
    await this.companyRepository.resetMemberTotp(target.userId);
  }

  // ─── Dados próprios (qualquer membro) ───────────────────────────────────────

  async updateMyProfile(userId: string, data: UpdateMyProfileInput) {
    const member = await this.getMemberOrThrow(userId);
    try {
      return await this.companyRepository.updateMyProfile(member.id, userId, data);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail já está em uso.");
      }
      throw error;
    }
  }

  async changeMyPassword(userId: string, data: ChangePasswordInput): Promise<void> {
    const user = await this.companyRepository.getUserPassword(userId);
    if (!user) throw new NotFoundError("Usuário não encontrado.");

    const isMatch = await compareHash(data.currentPassword, user.password);
    if (!isMatch) throw new UnauthorizedError("Senha atual incorreta.");

    await this.companyRepository.updatePassword(userId, await generateHash(data.newPassword));
  }

  // ─── Vagas ────────────────────────────────────────────────────────────────

  async listJobs(userId: string, query: PaginationQuery) {
    const member = await this.getMemberOrThrow(userId);
    const { page, limit } = query;
    const { data, total } = await this.companyRepository.listJobs(member.companyId, (page - 1) * limit, limit);
    return { data, meta: this.buildMeta(page, limit, total) };
  }

  async createJob(userId: string, data: CreateJobInput) {
    const member = await this.getMemberOrThrow(userId);
    try {
      return await this.companyRepository.createJob(member.companyId, data);
    } catch (error) {
      if ((error as { code?: string }).code === "P2025") {
        throw new BadRequestError("Curso não encontrado.");
      }
      throw error;
    }
  }

  async getJob(userId: string, jobId: string) {
    const member = await this.getMemberOrThrow(userId);
    return this.assertJobOwnership(jobId, member.companyId);
  }

  async updateJob(userId: string, jobId: string, data: UpdateJobInput) {
    const member = await this.getMemberOrThrow(userId);
    await this.assertJobOwnership(jobId, member.companyId);
    try {
      return await this.companyRepository.updateJob(jobId, data);
    } catch (error) {
      if ((error as { code?: string }).code === "P2025") {
        throw new BadRequestError("Curso não encontrado.");
      }
      throw error;
    }
  }

  async changeJobStatus(userId: string, jobId: string, status: JobStatus) {
    const member = await this.getMemberOrThrow(userId);
    const job = await this.assertJobOwnership(jobId, member.companyId);

    if (job.status === status) return job;

    if (!JOB_STATUS_TRANSITIONS[job.status].includes(status)) {
      throw new BadRequestError(
        job.status === "CLOSED"
          ? "Vaga encerrada não pode ser reativada."
          : `Transição de ${job.status} para ${status} não permitida.`,
      );
    }

    return this.companyRepository.updateJobStatus(jobId, status);
  }

  // ─── Candidaturas ─────────────────────────────────────────────────────────

  async listApplications(userId: string, jobId: string, query: PaginationQuery) {
    const member = await this.getMemberOrThrow(userId);
    await this.assertJobOwnership(jobId, member.companyId);
    const { page, limit } = query;
    const { data, total } = await this.companyRepository.listApplications(jobId, (page - 1) * limit, limit);
    return { data, meta: this.buildMeta(page, limit, total) };
  }

  async changeApplicationStatus(
    userId: string,
    jobId: string,
    applicationId: string,
    data: ChangeApplicationStatusInput,
  ) {
    const member = await this.getMemberOrThrow(userId);
    await this.assertJobOwnership(jobId, member.companyId);

    const application = await this.companyRepository.getApplicationById(applicationId);
    if (!application || application.jobId !== jobId) {
      throw new NotFoundError("Candidatura não encontrada.");
    }

    // Fluxo: PENDING → ANALYSING → APPROVED|REJECTED. Estados finais são imutáveis.
    if (application.status !== "PENDING" && application.status !== "ANALYSING") {
      throw new BadRequestError("Candidatura não pode mudar de status neste estado.");
    }
    if (data.status === "APPROVED" || data.status === "REJECTED") {
      if (application.status !== "ANALYSING") {
        throw new BadRequestError("A candidatura precisa estar em análise antes de ser decidida.");
      }
    }

    const updated = await this.companyRepository.updateApplicationStatus(applicationId, data);

    const message = APPLICATION_STATUS_MESSAGE[data.status];
    if (message) {
      try {
        await this.notificationService.create(application.student.userId, {
          type: NotificationType.APPLICATION_STATUS,
          title: "Atualização da candidatura",
          message: `Sua candidatura para a vaga "${application.job.title}" ${message}.`,
        });
      } catch {
        // Falha ao notificar não deve reverter a mudança de status.
      }
    }

    return updated;
  }

  // Resolve o caminho do currículo de um candidato, garantindo que a vaga
  // pertence à empresa do membro autenticado. Usa o currículo da candidatura
  // e, se ausente, cai para o currículo do perfil do aluno.
  async getApplicationResumePath(userId: string, jobId: string, applicationId: string): Promise<string> {
    const member = await this.getMemberOrThrow(userId);
    await this.assertJobOwnership(jobId, member.companyId);

    const application = await this.companyRepository.getApplicationById(applicationId);
    if (!application || application.jobId !== jobId) {
      throw new NotFoundError("Candidatura não encontrada.");
    }

    const resumePath = application.resumePath ?? application.student.resumePath;
    if (!resumePath) throw new NotFoundError("Candidato não possui currículo.");
    return resumePath;
  }

  // ─── Helpers ──────────────────────────────────────────────────────────────

  private async getMemberOrThrow(userId: string) {
    const member = await this.companyRepository.getMemberByUserId(userId);
    if (!member) throw new ForbiddenError("Acesso negado.");
    return member;
  }

  private async getMemberOfSameCompany(actingUserId: string, memberId: string) {
    const acting = await this.getMemberOrThrow(actingUserId);
    const target = await this.companyRepository.getMemberById(memberId);
    if (!target || target.companyId !== acting.companyId) {
      throw new NotFoundError("Membro não encontrado.");
    }
    return target;
  }

  private assertNotSelf(actingUserId: string, targetUserId: string) {
    if (actingUserId === targetUserId) {
      throw new BadRequestError("Você não pode alterar seus próprios dados de acesso.");
    }
  }

  private async assertJobOwnership(jobId: string, companyId: string) {
    const job = await this.companyRepository.getJobById(jobId);
    if (!job) throw new NotFoundError("Vaga não encontrada.");
    if (job.companyId !== companyId) throw new ForbiddenError("Acesso negado.");
    return job;
  }

  private buildMeta(page: number, limit: number, total: number): PaginationMeta {
    return { page, limit, total, totalPages: Math.ceil(total / limit) };
  }
}
