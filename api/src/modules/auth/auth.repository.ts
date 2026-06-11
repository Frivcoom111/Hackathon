import type { PrismaClient } from "../../generated/prisma/client";
import type { CompanyResponse, RegisterCompanyInput, RegisterStudentInput, StudentResponse } from "./auth.schema";

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

  // ─── Login / TOTP ─────────────────────────────────────────────────────────

  // Credenciais + flags de TOTP. Inclui a hash da senha para comparação no service.
  async findUserByEmail(email: string) {
    return this.prisma.user.findUnique({
      where: { email },
      select: {
        id: true,
        email: true,
        password: true,
        role: true,
        isActive: true,
        totpSecret: true,
        totpEnabled: true,
      },
    });
  }

  // Usado no login COMPANY e na emissão do token final (role do membro + status da empresa).
  async findCompanyMemberByUserId(userId: string) {
    return this.prisma.companyMember.findUnique({
      where: { userId },
      select: {
        id: true,
        companyId: true,
        role: true,
        company: { select: { status: true } },
      },
    });
  }

  // Segredo/flag de TOTP para setup, confirm e verify.
  async findUserTotp(userId: string) {
    return this.prisma.user.findUnique({
      where: { id: userId },
      select: { email: true, totpSecret: true, totpEnabled: true },
    });
  }

  async saveTotpSecret(userId: string, secret: string): Promise<void> {
    await this.prisma.user.update({ where: { id: userId }, data: { totpSecret: secret } });
  }

  async enableTotp(userId: string): Promise<void> {
    await this.prisma.user.update({ where: { id: userId }, data: { totpEnabled: true } });
  }

  // ─── Perfis (GET /auth/me) ────────────────────────────────────────────────

  async findStudentProfile(userId: string) {
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
        address: {
          select: {
            street: true,
            number: true,
            complement: true,
            district: true,
            city: true,
            state: true,
            zipCode: true,
          },
        },
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

  async findCompanyProfile(userId: string) {
    return this.prisma.companyMember.findUnique({
      where: { userId },
      select: {
        id: true,
        name: true,
        cpf: true,
        phone: true,
        role: true,
        user: { select: { id: true, email: true, role: true, isActive: true, createdAt: true } },
        company: {
          select: {
            id: true,
            name: true,
            cnpj: true,
            description: true,
            phone: true,
            status: true,
            address: {
              select: {
                street: true,
                number: true,
                complement: true,
                district: true,
                city: true,
                state: true,
                zipCode: true,
              },
            },
          },
        },
      },
    });
  }

  async findUserById(userId: string) {
    return this.prisma.user.findUnique({
      where: { id: userId },
      select: { id: true, email: true, role: true, isActive: true, createdAt: true },
    });
  }
}
