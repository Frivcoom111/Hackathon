import type { JobStatus } from "../../generated/prisma/enums";
import { BadRequestError, ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { PaginationQuery } from "../../shared/schemas/common.schema";
import type { PaginationMeta } from "../../shared/utils/response";
import type { CompanyRepository } from "./company.repository";
import type {
  ChangeApplicationStatusInput,
  ChangeMemberRoleInput,
  CreateJobInput,
  UpdateCompanyProfileInput,
  UpdateJobInput,
} from "./company.schema";

// Transições válidas de status de vaga. CLOSED é terminal.
const JOB_STATUS_TRANSITIONS: Record<JobStatus, JobStatus[]> = {
  ACTIVE: ["PAUSED", "CLOSED"],
  PAUSED: ["ACTIVE", "CLOSED"],
  CLOSED: [],
};

export class CompanyService {
  constructor(private readonly companyRepository: CompanyRepository) {}

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

  async changeMemberRole(actingUserId: string, memberId: string, data: ChangeMemberRoleInput) {
    const target = await this.getMemberOfSameCompany(actingUserId, memberId);
    this.assertNotSelf(actingUserId, target.userId);
    return this.companyRepository.updateMemberRole(memberId, data);
  }

  async resetMemberTotp(actingUserId: string, memberId: string): Promise<void> {
    const target = await this.getMemberOfSameCompany(actingUserId, memberId);
    this.assertNotSelf(actingUserId, target.userId);
    await this.companyRepository.resetMemberTotp(target.userId);
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

    return this.companyRepository.updateApplicationStatus(applicationId, data);
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
