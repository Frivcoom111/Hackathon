import type { PrismaClient } from "../../generated/prisma/client";
import type { CourseResponse } from "./course.types";

const courseSelect = {
  id: true,
  name: true,
  code: true,
  periods: true,
} as const;

export class CourseRepository {
  constructor(private readonly prisma: PrismaClient) {}

  async listActive(): Promise<CourseResponse[]> {
    return this.prisma.course.findMany({ where: { isActive: true }, select: courseSelect, orderBy: { name: "asc" } });
  }
}
