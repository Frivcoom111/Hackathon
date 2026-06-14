import type { PrismaClient } from "../../generated/prisma/client";
import { ConflictError, NotFoundError, UnauthorizedError } from "../../shared/errors/AppError";
import { compareHash, generateHash } from "../../shared/utils/bcryptUtils";
import { generateToken } from "../../shared/utils/generateToken";
import type { LoginInput, RegisterCompanyInput, RegisterStudentInput } from "./auth.schema";

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

    const token = generateToken({
      sub: user.id,
      email: user.email,
      role: user.role,
      mfaVerified: true,
    });

    return {
      token,
      user: {
        id: user.id,
        email: user.email,
        role: user.role,
        name: user.student?.name ?? user.companyMember?.name ?? user.companyMember?.company.name ?? "Usuario",
        student: user.student,
        company: user.companyMember?.company ?? null,
      },
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
            resumePath: data.resumePath,
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

  private handleUniqueError(error: unknown) {
    if ((error as { code?: string }).code === "P2002") {
      throw new ConflictError("Ja existe um cadastro com alguns destes dados.");
    }
  }
}
