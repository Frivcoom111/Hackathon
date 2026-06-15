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
import type { StudentRepository } from "./student.repository";
import type { UpdateStudentProfileInput } from "./student.schema";

export class StudentService {
  constructor(
    private readonly studentRepository: StudentRepository,
    private readonly notificationService: NotificationService,
  ) {}

  async getProfile(userId: string) {
    const profile = await this.studentRepository.getProfile(userId);
    if (!profile) throw new NotFoundError("Perfil nao encontrado.");
    return profile;
  }

  async updateProfile(userId: string, data: UpdateStudentProfileInput) {
    const student = await this.getStudentOrThrow(userId);
    try {
      return await this.studentRepository.updateProfile(student.id, userId, data);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail ja esta em uso.");
      }
      throw error;
    }
  }

  async changePassword(userId: string, data: ChangePasswordInput): Promise<void> {
    const user = await this.studentRepository.getUserPassword(userId);
    if (!user) throw new NotFoundError("Usuario nao encontrado.");

    const isMatch = await compareHash(data.currentPassword, user.password);
    if (!isMatch) throw new UnauthorizedError("Senha atual incorreta.");

    await this.studentRepository.updatePassword(userId, await generateHash(data.newPassword));
  }

  async listApplications(userId: string, query: PaginationQuery) {
    const student = await this.getStudentOrThrow(userId);
    const { page, limit } = query;
    const { data, total } = await this.studentRepository.listApplications(student.id, (page - 1) * limit, limit);
    return { data, meta: this.buildMeta(page, limit, total) };
  }

  async cancelApplication(userId: string, applicationId: string) {
    const student = await this.getStudentOrThrow(userId);

    const application = await this.studentRepository.getApplicationById(applicationId);
    if (!application) throw new NotFoundError("Candidatura nao encontrada.");
    if (application.studentId !== student.id) throw new ForbiddenError("Acesso negado.");

    if (application.status !== "PENDING" && application.status !== "ANALYSING") {
      throw new BadRequestError("Candidatura nao pode ser cancelada neste status.");
    }

    const cancelled = await this.studentRepository.cancelApplication(applicationId);

    try {
      await this.notificationService.notifyCompany(application.job.companyId, {
        type: NotificationType.APPLICATION_CANCELLED,
        title: "Candidatura cancelada",
        message: `${application.student.name} cancelou a candidatura para a vaga "${application.job.title}".`,
      });
    } catch {
      // Falha ao notificar nao deve reverter o cancelamento.
    }

    return cancelled;
  }

  private async getStudentOrThrow(userId: string) {
    const student = await this.studentRepository.getStudentByUserId(userId);
    if (!student) throw new NotFoundError("Estudante nao encontrado.");
    return student;
  }

  private buildMeta(page: number, limit: number, total: number): PaginationMeta {
    return { page, limit, total, totalPages: Math.ceil(total / limit) };
  }
}
