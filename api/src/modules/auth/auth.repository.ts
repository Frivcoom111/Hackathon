import type { PrismaClient } from "../../generated/prisma/client";
import type { RegisterStudentInput, StudentResponse } from "./auth.schema";

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
}
