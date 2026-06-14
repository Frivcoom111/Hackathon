import type { PrismaClient } from "../../generated/prisma/client";
import type { AddressInput } from "./address.schema";

const addressSelect = {
  street: true,
  number: true,
  complement: true,
  district: true,
  city: true,
  state: true,
  zipCode: true,
} as const;

export class AddressRepository {
  constructor(private readonly prisma: PrismaClient) {}

  // ─── Donos (resolução do addressId vigente) ─────────────────────────────────

  async getStudentByUserId(userId: string) {
    return this.prisma.student.findUnique({ where: { userId }, select: { id: true, addressId: true } });
  }

  async getMemberByUserId(userId: string) {
    return this.prisma.companyMember.findUnique({
      where: { userId },
      select: { companyId: true },
    });
  }

  async getCompanyById(companyId: string) {
    return this.prisma.company.findUnique({ where: { id: companyId }, select: { id: true, addressId: true } });
  }

  // ─── Endereço ───────────────────────────────────────────────────────────────

  async getById(addressId: string) {
    return this.prisma.address.findUnique({ where: { id: addressId }, select: addressSelect });
  }

  async createForStudent(studentId: string, data: AddressInput) {
    return this.prisma.address.create({
      data: { ...data, student: { connect: { id: studentId } } },
      select: addressSelect,
    });
  }

  async createForCompany(companyId: string, data: AddressInput) {
    return this.prisma.address.create({
      data: { ...data, company: { connect: { id: companyId } } },
      select: addressSelect,
    });
  }

  async update(addressId: string, data: AddressInput) {
    return this.prisma.address.update({ where: { id: addressId }, data, select: addressSelect });
  }

  // Deletar o Address zera a FK addressId do dono (relação opcional → SetNull).
  async delete(addressId: string): Promise<void> {
    await this.prisma.address.delete({ where: { id: addressId } });
  }
}
