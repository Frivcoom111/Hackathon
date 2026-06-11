import type { PrismaClient } from "../../generated/prisma/client";
import type { JobStatus } from "../../generated/prisma/enums";
import type {
  ChangeApplicationStatusInput,
  ChangeMemberRoleInput,
  CreateJobInput,
  UpdateCompanyProfileInput,
  UpdateJobInput,
} from "./company.schema";

// ─── Selects ──────────────────────────────────────────────────────────────────

const addressSelect = {
  street: true,
  number: true,
  complement: true,
  district: true,
  city: true,
  state: true,
  zipCode: true,
} as const;

const jobSelect = {
  id: true,
  companyId: true,
  courseId: true,
  title: true,
  description: true,
  area: true,
  requirements: true,
  salary: true,
  location: true,
  modality: true,
  status: true,
  createdAt: true,
  updatedAt: true,
} as const;

export class CompanyRepository {
  constructor(private readonly prisma: PrismaClient) {}

  // ─── Membro / empresa do usuário autenticado ──────────────────────────────

  // Identidade do membro logado: usada para ownership e bloqueio de auto-ação.
  async getMemberByUserId(userId: string) {
    return this.prisma.companyMember.findUnique({
      where: { userId },
      select: { id: true, companyId: true, role: true },
    });
  }

  async getCompanyProfile(companyId: string) {
    return this.prisma.company.findUnique({
      where: { id: companyId },
      select: {
        id: true,
        name: true,
        cnpj: true,
        description: true,
        phone: true,
        status: true,
        address: { select: addressSelect },
        members: { select: { id: true, name: true, role: true } },
      },
    });
  }

  async updateCompany(companyId: string, data: UpdateCompanyProfileInput) {
    return this.prisma.company.update({
      where: { id: companyId },
      data,
      select: { id: true, name: true, cnpj: true, description: true, phone: true, status: true },
    });
  }

  // ─── Membros ──────────────────────────────────────────────────────────────

  async listMembers(companyId: string) {
    return this.prisma.companyMember.findMany({
      where: { companyId },
      select: {
        id: true,
        name: true,
        cpf: true,
        phone: true,
        role: true,
        user: { select: { email: true, isActive: true, totpEnabled: true } },
      },
      orderBy: { createdAt: "asc" },
    });
  }

  async getMemberById(memberId: string) {
    return this.prisma.companyMember.findUnique({
      where: { id: memberId },
      select: { id: true, userId: true, companyId: true, role: true },
    });
  }

  async updateMemberRole(memberId: string, data: ChangeMemberRoleInput) {
    return this.prisma.companyMember.update({
      where: { id: memberId },
      data,
      select: { id: true, name: true, role: true },
    });
  }

  // Reset de TOTP: no próximo login o membro refaz o setup.
  async resetMemberTotp(userId: string): Promise<void> {
    await this.prisma.user.update({
      where: { id: userId },
      data: { totpSecret: null, totpEnabled: false },
    });
  }

  // ─── Vagas ──────────────────────────────────────────────────────────────────

  async listJobs(companyId: string, skip: number, take: number) {
    const [data, total] = await this.prisma.$transaction([
      this.prisma.job.findMany({
        where: { companyId, deletedAt: null },
        select: jobSelect,
        skip,
        take,
        orderBy: { createdAt: "desc" },
      }),
      this.prisma.job.count({ where: { companyId, deletedAt: null } }),
    ]);

    return { data, total };
  }

  async createJob(companyId: string, data: CreateJobInput) {
    const { courseId, ...rest } = data;
    return this.prisma.job.create({
      data: {
        ...rest,
        company: { connect: { id: companyId } },
        ...(courseId ? { course: { connect: { id: courseId } } } : {}),
      },
      select: jobSelect,
    });
  }

  async getJobById(jobId: string) {
    return this.prisma.job.findFirst({
      where: { id: jobId, deletedAt: null },
      select: jobSelect,
    });
  }

  async updateJob(jobId: string, data: UpdateJobInput) {
    const { courseId, ...rest } = data;
    return this.prisma.job.update({
      where: { id: jobId },
      data: {
        ...rest,
        ...(courseId ? { course: { connect: { id: courseId } } } : {}),
      },
      select: jobSelect,
    });
  }

  async updateJobStatus(jobId: string, status: JobStatus) {
    return this.prisma.job.update({
      where: { id: jobId },
      data: { status },
      select: jobSelect,
    });
  }

  // ─── Candidaturas ────────────────────────────────────────────────────────────

  async listApplications(jobId: string, skip: number, take: number) {
    const [data, total] = await this.prisma.$transaction([
      this.prisma.application.findMany({
        where: { jobId, deletedAt: null },
        select: {
          id: true,
          status: true,
          resumePath: true,
          coverLetter: true,
          createdAt: true,
          student: { select: { id: true, name: true, ra: true } },
        },
        skip,
        take,
        orderBy: { createdAt: "desc" },
      }),
      this.prisma.application.count({ where: { jobId, deletedAt: null } }),
    ]);

    return { data, total };
  }

  async getApplicationById(applicationId: string) {
    return this.prisma.application.findFirst({
      where: { id: applicationId, deletedAt: null },
      select: { id: true, status: true, jobId: true, job: { select: { companyId: true } } },
    });
  }

  async updateApplicationStatus(applicationId: string, data: ChangeApplicationStatusInput) {
    return this.prisma.application.update({
      where: { id: applicationId },
      data,
      select: { id: true, status: true },
    });
  }
}
