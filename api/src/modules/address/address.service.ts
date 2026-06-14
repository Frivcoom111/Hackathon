import { Role } from "../../generated/prisma/enums";
import { ConflictError, ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { AddressRepository } from "./address.repository";
import type { AddressInput } from "./address.schema";

export class AddressService {
  constructor(private readonly addressRepository: AddressRepository) {}

  // ─── Endereço próprio (Student) ─────────────────────────────────────────────

  async getSelf(userId: string, role: Role) {
    const student = await this.resolveStudent(userId, role);
    if (!student.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.getById(student.addressId);
  }

  async createSelf(userId: string, role: Role, data: AddressInput) {
    const student = await this.resolveStudent(userId, role);
    if (student.addressId) throw new ConflictError("Já existe um endereço cadastrado.");
    return this.addressRepository.createForStudent(student.id, data);
  }

  async updateSelf(userId: string, role: Role, data: AddressInput) {
    const student = await this.resolveStudent(userId, role);
    if (!student.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.update(student.addressId, data);
  }

  async deleteSelf(userId: string, role: Role): Promise<void> {
    const student = await this.resolveStudent(userId, role);
    if (!student.addressId) throw new NotFoundError("Endereço não encontrado.");
    await this.addressRepository.delete(student.addressId);
  }

  // ─── Endereço da empresa (membros leem; ADMIN escreve, garantido na rota) ───

  async getCompany(userId: string) {
    const company = await this.resolveCompany(userId);
    if (!company.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.getById(company.addressId);
  }

  async createCompany(userId: string, data: AddressInput) {
    const company = await this.resolveCompany(userId);
    if (company.addressId) throw new ConflictError("Já existe um endereço cadastrado.");
    return this.addressRepository.createForCompany(company.id, data);
  }

  async updateCompany(userId: string, data: AddressInput) {
    const company = await this.resolveCompany(userId);
    if (!company.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.update(company.addressId, data);
  }

  async deleteCompany(userId: string): Promise<void> {
    const company = await this.resolveCompany(userId);
    if (!company.addressId) throw new NotFoundError("Endereço não encontrado.");
    await this.addressRepository.delete(company.addressId);
  }

  // ─── Resolução do dono ──────────────────────────────────────────────────────

  private async resolveStudent(userId: string, role: Role) {
    if (role !== Role.STUDENT) throw new ForbiddenError("Apenas estudantes possuem endereço pessoal.");
    const student = await this.addressRepository.getStudentByUserId(userId);
    if (!student) throw new NotFoundError("Estudante não encontrado.");
    return student;
  }

  private async resolveCompany(userId: string) {
    const member = await this.addressRepository.getMemberByUserId(userId);
    if (!member) throw new NotFoundError("Membro não encontrado.");
    const company = await this.addressRepository.getCompanyById(member.companyId);
    if (!company) throw new NotFoundError("Empresa não encontrada.");
    return company;
  }
}
