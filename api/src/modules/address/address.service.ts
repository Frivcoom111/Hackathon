import { Role } from "../../generated/prisma/enums";
import { ConflictError, ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { AddressRepository } from "./address.repository";
import type { AddressInput } from "./address.schema";

type SelfOwner =
  | { kind: "student"; id: string; addressId: string | null }
  | { kind: "member"; id: string; addressId: string | null };

export class AddressService {
  constructor(private readonly addressRepository: AddressRepository) {}

  // ─── Endereço próprio (Student ou CompanyMember) ────────────────────────────

  async getSelf(userId: string, role: Role) {
    const owner = await this.resolveSelf(userId, role);
    if (!owner.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.getById(owner.addressId);
  }

  async createSelf(userId: string, role: Role, data: AddressInput) {
    const owner = await this.resolveSelf(userId, role);
    if (owner.addressId) throw new ConflictError("Já existe um endereço cadastrado.");
    return owner.kind === "student"
      ? this.addressRepository.createForStudent(owner.id, data)
      : this.addressRepository.createForMember(owner.id, data);
  }

  async updateSelf(userId: string, role: Role, data: AddressInput) {
    const owner = await this.resolveSelf(userId, role);
    if (!owner.addressId) throw new NotFoundError("Endereço não encontrado.");
    return this.addressRepository.update(owner.addressId, data);
  }

  async deleteSelf(userId: string, role: Role): Promise<void> {
    const owner = await this.resolveSelf(userId, role);
    if (!owner.addressId) throw new NotFoundError("Endereço não encontrado.");
    await this.addressRepository.delete(owner.addressId);
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

  private async resolveSelf(userId: string, role: Role): Promise<SelfOwner> {
    if (role === Role.STUDENT) {
      const student = await this.addressRepository.getStudentByUserId(userId);
      if (!student) throw new NotFoundError("Estudante não encontrado.");
      return { kind: "student", id: student.id, addressId: student.addressId };
    }
    if (role === Role.COMPANY) {
      const member = await this.addressRepository.getMemberByUserId(userId);
      if (!member) throw new NotFoundError("Membro não encontrado.");
      return { kind: "member", id: member.id, addressId: member.addressId };
    }
    throw new ForbiddenError("Este perfil não possui endereço.");
  }

  private async resolveCompany(userId: string) {
    const member = await this.addressRepository.getMemberByUserId(userId);
    if (!member) throw new NotFoundError("Membro não encontrado.");
    const company = await this.addressRepository.getCompanyById(member.companyId);
    if (!company) throw new NotFoundError("Empresa não encontrada.");
    return company;
  }
}
