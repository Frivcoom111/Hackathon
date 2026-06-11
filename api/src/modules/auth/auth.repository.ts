import type { PrismaClient } from "../../generated/prisma/client";
import type {
  CompanyResponse,
  RegisterCompanyInput,
  RegisterStudentInput,
  StudentResponse,
} from "./auth.schema";

export class AuthRepository {
  constructor(private readonly prisma: PrismaClient) {}

  // Cria User + Student + StudentCourse atomicamente. `data.password` já vem em hash.
  async createStudent(data: RegisterStudentInput): Promise<StudentResponse> {
    return this.prisma.$transaction(async (tx) => {
      const user = await tx.user.create({
        data: { email: data.email, password: data.password, role: "STUDENT" },
      });

      return tx.student.create({
        data: {
          userId: user.id,
          name: data.name,
          ra: data.ra,
          cpf: data.cpf,
          phone: data.phone,
          resumePath: data.resumePath,
          courses: {
            create: {
              status: data.status,
              startedAt: data.startedAt,
              finishedAt: data.finishedAt,
              course: { connect: { id: data.courseId } },
            },
          },
        },
        select: { userId: true, name: true, ra: true, phone: true, resumePath: true },
      });
    });
  }

  // Cria User + Address + Company + CompanyMember(ADMIN) atomicamente.
  // O usuário nasce inativo (isActive=false) e a empresa PENDING. `data.password` já vem em hash.
  async createCompany(data: RegisterCompanyInput): Promise<CompanyResponse> {
    return this.prisma.$transaction(async (tx) => {
      const user = await tx.user.create({
        data: { email: data.email, password: data.password, role: "COMPANY", isActive: false },
        select: { id: true },
      });

      const address = await tx.address.create({ data: data.address, select: { id: true } });

      return tx.company.create({
        data: {
          name: data.name,
          cnpj: data.cnpj,
          description: data.description,
          phone: data.phone,
          address: { connect: { id: address.id } },
          members: {
            create: {
              userId: user.id,
              role: "ADMIN",
              name: data.member.name,
              cpf: data.member.cpf,
              phone: data.member.phone,
            },
          },
        },
        select: {
          id: true,
          name: true,
          cnpj: true,
          status: true,
          members: { select: { userId: true, name: true, role: true } },
        },
      });
    });
  }
}
