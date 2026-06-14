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
import type { StudentRepository } from "./student.repository";
import type { UpdateAddressInput, UpdateStudentProfileInput } from "./student.schema";

export class StudentService {
  constructor(private readonly studentRepository: StudentRepository) {}

  async getProfile(userId: string) {
    const profile = await this.studentRepository.getProfile(userId);
    if (!profile) throw new NotFoundError("Perfil não encontrado.");
    return profile;
  }

  async updateProfile(userId: string, data: UpdateStudentProfileInput) {
    const student = await this.getStudentOrThrow(userId);
    try {
      return await this.studentRepository.updateProfile(student.id, userId, data);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("E-mail já está em uso.");
      }
      throw error;
    }
  }

  async changePassword(userId: string, data: ChangePasswordInput): Promise<void> {
    const user = await this.studentRepository.getUserPassword(userId);
    if (!user) throw new NotFoundError("Usuário não encontrado.");

    const isMatch = await compareHash(data.currentPassword, user.password);
    if (!isMatch) throw new UnauthorizedError("Senha atual incorreta.");

    await this.studentRepository.updatePassword(userId, await generateHash(data.newPassword));
  }

  // Upsert: atualiza o endereço vinculado se já existir, senão cria e vincula.
  async updateAddress(userId: string, data: UpdateAddressInput) {
    const student = await this.getStudentOrThrow(userId);
    if (student.addressId) {
      return this.studentRepository.updateAddress(student.addressId, data);
    }
    return this.studentRepository.createAddressForStudent(student.id, data);
  }

  async updateResume(userId: string, resumePath: string) {
    const student = await this.getStudentOrThrow(userId);
    return this.studentRepository.updateResume(student.id, resumePath);
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
    if (!application) throw new NotFoundError("Candidatura não encontrada.");
    if (application.studentId !== student.id) throw new ForbiddenError("Acesso negado.");

    if (application.status !== "PENDING" && application.status !== "ANALYSING") {
      throw new BadRequestError("Candidatura não pode ser cancelada neste status.");
    }

    return this.studentRepository.cancelApplication(applicationId);
  }

  private async getStudentOrThrow(userId: string) {
    const student = await this.studentRepository.getStudentByUserId(userId);
    if (!student) throw new NotFoundError("Estudante não encontrado.");
    return student;
  }

  private buildMeta(page: number, limit: number, total: number): PaginationMeta {
    return { page, limit, total, totalPages: Math.ceil(total / limit) };
  }
}
