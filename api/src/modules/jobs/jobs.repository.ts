import type { Prisma, PrismaClient } from "../../generated/prisma/client";
import type { ListJobsQuery } from "./jobs.schema";

// Apenas vagas ativas de empresas aprovadas são visíveis publicamente.
const publicJobWhere = (filters: ListJobsQuery): Prisma.JobWhereInput => ({
  status: "ACTIVE",
  deletedAt: null,
  company: { status: "APPROVED" },
  ...(filters.courseId ? { courseId: filters.courseId } : {}),
  ...(filters.modality ? { modality: filters.modality } : {}),
  ...(filters.area ? { area: { contains: filters.area } } : {}),
  ...(filters.search
    ? {
        OR: [{ title: { contains: filters.search } }, { description: { contains: filters.search } }],
      }
    : {}),
});

const listSelect = {
  id: true,
  title: true,
  area: true,
  location: true,
  modality: true,
  salary: true,
  createdAt: true,
  company: { select: { id: true, name: true } },
  course: { select: { id: true, name: true } },
} as const;

export class JobsRepository {
  constructor(private readonly prisma: PrismaClient) {}

  async listPublic(filters: ListJobsQuery, skip: number, take: number) {
    const where = publicJobWhere(filters);
    const [data, total] = await this.prisma.$transaction([
      this.prisma.job.findMany({ where, select: listSelect, skip, take, orderBy: { createdAt: "desc" } }),
      this.prisma.job.count({ where }),
    ]);

    return { data, total };
  }

  async getPublicById(jobId: string) {
    return this.prisma.job.findFirst({
      where: { id: jobId, status: "ACTIVE", deletedAt: null, company: { status: "APPROVED" } },
      select: {
        id: true,
        title: true,
        description: true,
        area: true,
        requirements: true,
        salary: true,
        location: true,
        modality: true,
        createdAt: true,
        company: { select: { id: true, name: true, description: true } },
        course: { select: { id: true, name: true } },
      },
    });
  }

  // Estado bruto da vaga (sem filtro de visibilidade) para validar a candidatura.
  async getJobForApply(jobId: string) {
    return this.prisma.job.findFirst({
      where: { id: jobId, deletedAt: null },
      select: { id: true, title: true, status: true, company: { select: { id: true, status: true } } },
    });
  }

  async getStudentByUserId(userId: string) {
    return this.prisma.student.findUnique({
      where: { userId },
      select: { id: true, isEligible: true, addressId: true, resumePath: true },
    });
  }

  async createApplication(studentId: string, jobId: string, resumePath: string) {
    return this.prisma.application.create({
      data: { studentId, jobId, resumePath },
      select: { id: true, status: true, createdAt: true },
    });
  }
}
