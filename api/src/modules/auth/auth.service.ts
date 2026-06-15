import type { PrismaClient } from "../../generated/prisma/client";
import { generateSecret, generateURI, verify } from "otplib";
import QRCode from "qrcode";
import { ConflictError, NotFoundError, UnauthorizedError } from "../../shared/errors/AppError";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import { generateToken } from "../../shared/utils/generateToken";
import type { LoginInput, RegisterCompanyInput, RegisterStudentInput, TotpCodeInput } from "./auth.schema";

export class AuthService {
  constructor(private readonly prisma: PrismaClient) {}

  async login(data: LoginInput) {
    const user = await this.prisma.user.findUnique({
      where: { email: data.email },
      include: {
        student: true,
        companyMember: {
          include: {
            company: true,
          },
        },
      },
    });

    if (!user || !user.isActive) {
      throw new UnauthorizedError("E-mail ou senha invalidos.");
    }

    const passwordOk = await compareHash(data.password, user.password);
    if (!passwordOk) {
      throw new UnauthorizedError("E-mail ou senha invalidos.");
    }

    if (user.role === "STUDENT") {
      return {
        type: "AUTHENTICATED",
        token: this.makeToken(user, true),
        user: this.publicUser(user),
      };
    }

    const tempToken = this.makeToken(user, false);

    // Primeiro acesso (sem TOTP ativo): gera/salva o secret e ja devolve o QR
    // na propria resposta do login. O front confirma em POST /totp/setup/confirm.
    if (!user.totpEnabled || !user.totpSecret) {
      const secret = user.totpSecret ?? generateSecret();

      if (!user.totpSecret) {
        await this.prisma.user.update({
          where: { id: user.id },
          data: { totpSecret: secret },
        });
      }

      const otpauth = generateURI({
        issuer: "Portal Estagios UniALFA",
        label: user.email,
        secret,
      });
      const qrCode = await QRCode.toDataURL(otpauth);

      return {
        type: "TOTP_SETUP",
        tempToken,
        qrCode,
        otpauth,
        user: this.publicUser(user),
      };
    }

    // Acessos seguintes: TOTP ja ativo, basta validar o codigo em /totp/verify.
    return {
      type: "TOTP_REQUIRED",
      tempToken,
      user: this.publicUser(user),
    };
  }

  async confirmTotp(userId: string, data: TotpCodeInput) {
    const user = await this.findUserForTotp(userId);
    await this.checkTotpCode(user.totpSecret, data.code);

    await this.prisma.user.update({
      where: { id: user.id },
      data: { totpEnabled: true },
    });

    return {
      type: "AUTHENTICATED",
      token: this.makeToken(user, true),
      user: this.publicUser(user),
    };
  }

  async verifyTotp(userId: string, data: TotpCodeInput) {
    const user = await this.findUserForTotp(userId);

    if (!user.totpEnabled) {
      throw new UnauthorizedError("Authenticator ainda nao foi configurado.");
    }

    await this.checkTotpCode(user.totpSecret, data.code);

    return {
      type: "AUTHENTICATED",
      token: this.makeToken(user, true),
      user: this.publicUser(user),
    };
  }

  async registerStudent(data: RegisterStudentInput) {
    await this.ensureCourseExists(data.courseId);
    const password = await generateHash(data.password);

    try {
      return await this.prisma.$transaction(async (tx) => {
        const user = await tx.user.create({
          data: {
            email: data.email,
            password,
            role: "STUDENT",
          },
        });

        const student = await tx.student.create({
          data: {
            userId: user.id,
            name: data.name,
            ra: data.ra,
            cpf: data.cpf,
            phone: data.phone,
          },
        });

        const studentCourse = await tx.studentCourse.create({
          data: {
            studentId: student.id,
            courseId: data.courseId,
            status: data.status,
            startedAt: data.startedAt,
            finishedAt: data.status === "COMPLETED" ? data.finishedAt : null,
          },
          include: {
            course: true,
          },
        });

        return { user, student, studentCourse };
      });
    } catch (error) {
      this.handleUniqueError(error);
      throw error;
    }
  }

  async registerCompany(data: RegisterCompanyInput) {
    const password = await generateHash(data.password);

    try {
      return await this.prisma.$transaction(async (tx) => {
        const user = await tx.user.create({
          data: {
            email: data.email,
            password,
            role: "COMPANY",
          },
        });

        const address = await tx.address.create({
          data: data.address,
        });

        const company = await tx.company.create({
          data: {
            addressId: address.id,
            name: data.name,
            cnpj: data.cnpj,
            description: data.description,
            phone: data.phone,
            status: "PENDING",
          },
        });

        const member = await tx.companyMember.create({
          data: {
            companyId: company.id,
            userId: user.id,
            role: "ADMIN",
            name: data.member.name,
            cpf: data.member.cpf,
            phone: data.member.phone,
          },
        });

        return { user, company, member };
      });
    } catch (error) {
      this.handleUniqueError(error);
      throw error;
    }
  }

  private async ensureCourseExists(courseId: string) {
    const course = await this.prisma.course.findUnique({ where: { id: courseId } });
    if (!course) throw new NotFoundError("Curso nao encontrado.");
  }

  private async findUserForTotp(userId: string) {
    const user = await this.prisma.user.findUnique({
      where: { id: userId },
      include: {
        student: true,
        companyMember: {
          include: {
            company: true,
          },
        },
      },
    });

    if (!user || !user.isActive) {
      throw new UnauthorizedError("Usuario nao autorizado.");
    }

    if (user.role === "STUDENT") {
      throw new UnauthorizedError("Authenticator e usado apenas para empresa ou administrador.");
    }

    return user;
  }

  private async checkTotpCode(secret: string | null, code: string) {
    const result = secret ? await verify({ secret, token: code }) : null;

    if (!result?.valid) {
      throw new UnauthorizedError("Codigo do Authenticator invalido.");
    }
  }

  private makeToken(
    user: {
      id: string;
      email: string;
      role: "ADMIN" | "COMPANY" | "STUDENT";
      companyMember?: { role?: "ADMIN" | "RECRUITER" } | null;
    },
    mfaVerified: boolean,
  ) {
    return generateToken({
      sub: user.id,
      email: user.email,
      role: user.role,
      mfaVerified,
      companyMemberRole: user.companyMember?.role,
    });
  }

  private publicUser(user: {
    id: string;
    email: string;
    role: "ADMIN" | "COMPANY" | "STUDENT";
    student?: { name: string } | null;
    companyMember?: { name: string; company?: { name: string } | null } | null;
  }) {
    return {
      id: user.id,
      email: user.email,
      role: user.role,
      name: user.student?.name ?? user.companyMember?.name ?? user.companyMember?.company?.name ?? "Usuario",
    };
  }

  private handleUniqueError(error: unknown) {
    if ((error as { code?: string }).code === "P2002") {
      throw new ConflictError("Ja existe um cadastro com alguns destes dados.");
    }
  }
}
