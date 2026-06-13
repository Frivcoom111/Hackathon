import type { PrismaClient } from "../../generated/prisma/client";
import type { UpdateAddressInput, UpdateStudentProfileInput } from "./student.schema";

const addressSelect = {
  street: true,
  number: true,
  complement: true,
  district: true,
  city: true,
  state: true,
  zipCode: true,
} as const;

export class StudentRepository {
  constructor(private readonly prisma: PrismaClient) {}

  // Identidade mínima do estudante logado (ownership, elegibilidade, endereço).
  async getStudentByUserId(userId: string) {
    return this.prisma.student.findUnique({
      where: { userId },
      select: { id: true, addressId: true, isEligible: true },
    });
  }

  async getProfile(userId: string) {
    return this.prisma.student.findUnique({
      where: { userId },
      select: {
        id: true,
        name: true,
        ra: true,
        cpf: true,
        phone: true,
        resumePath: true,
        isEligible: true,
        user: { select: { id: true, email: true, role: true, isActive: true, createdAt: true } },
        address: { select: addressSelect },
        courses: {
          where: { status: "ACTIVE" },
          select: {
            status: true,
            startedAt: true,
            finishedAt: true,
            course: { select: { id: true, name: true } },
          },
        },
      },
    });
  }

  // Atualiza name/phone (Student) e e-mail (User) numa transação.
  async updateProfile(studentId: string, userId: string, data: UpdateStudentProfileInput) {
    const { email, ...profile } = data;
    return this.prisma.$transaction(async (tx) => {
      if (email) await tx.user.update({ where: { id: userId }, data: { email } });
      return tx.student.update({
        where: { id: studentId },
        data: profile,
        select: { id: true, name: true, phone: true, user: { select: { email: true } } },
      });
    });
  }

  async getUserPassword(userId: string) {
    return this.prisma.user.findUnique({ where: { id: userId }, select: { password: true } });
  }

  async updatePassword(userId: string, passwordHash: string): Promise<void> {
    await this.prisma.user.update({ where: { id: userId }, data: { password: passwordHash } });
  }

  async updateResume(studentId: string, resumePath: string) {
    return this.prisma.student.update({
      where: { id: studentId },
      data: { resumePath },
      select: { id: true, resumePath: true },
    });
  }

  async updateAddress(addressId: string, data: UpdateAddressInput) {
    return this.prisma.address.update({
      where: { id: addressId },
      data,
      select: addressSelect,
    });
  }

  // Cria o endereço e o vincula ao estudante (FK addressId fica em Student).
  async createAddressForStudent(studentId: string, data: UpdateAddressInput) {
    return this.prisma.address.create({
      data: { ...data, student: { connect: { id: studentId } } },
      select: addressSelect,
    });
  }

  async listApplications(studentId: string, skip: number, take: number) {
    const [data, total] = await this.prisma.$transaction([
      this.prisma.application.findMany({
        where: { studentId, deletedAt: null },
        select: {
          id: true,
          status: true,
          resumePath: true,
          createdAt: true,
          job: { select: { id: true, title: true, company: { select: { name: true } } } },
        },
        skip,
        take,
        orderBy: { createdAt: "desc" },
      }),
      this.prisma.application.count({ where: { studentId, deletedAt: null } }),
    ]);

    return { data, total };
  }

  async getApplicationById(applicationId: string) {
    return this.prisma.application.findFirst({
      where: { id: applicationId, deletedAt: null },
      select: { id: true, studentId: true, status: true },
    });
  }

  // Soft delete: marca como CANCELLED e preenche deletedAt (nunca apaga a linha).
  async cancelApplication(applicationId: string) {
    return this.prisma.application.update({
      where: { id: applicationId },
      data: { status: "CANCELLED", deletedAt: new Date() },
      select: { id: true, status: true },
    });
  }
}
