import { BadRequestError, ConflictError, ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { PaginationMeta } from "../../shared/utils/response";
import type { NotificationService } from "../notification/notification.service";
import { NotificationType } from "../notification/notification.types";
import type { JobsRepository } from "./jobs.repository";
import type { ListJobsQuery } from "./jobs.schema";

export class JobsService {
  constructor(
    private readonly jobsRepository: JobsRepository,
    private readonly notificationService: NotificationService,
  ) {}

  async list(query: ListJobsQuery) {
    const { page, limit } = query;
    const { data, total } = await this.jobsRepository.listPublic(query, (page - 1) * limit, limit);
    const meta: PaginationMeta = { page, limit, total, totalPages: Math.ceil(total / limit) };
    return { data, meta };
  }

  async getById(jobId: string) {
    const job = await this.jobsRepository.getPublicById(jobId);
    if (!job) throw new NotFoundError("Vaga nao encontrada.");
    return job;
  }

  async apply(userId: string, jobId: string) {
    const job = await this.jobsRepository.getJobForApply(jobId);
    if (!job) throw new NotFoundError("Vaga nao encontrada.");
    if (job.status !== "ACTIVE" || job.company.status !== "APPROVED") {
      throw new BadRequestError("Vaga indisponivel para candidatura.");
    }

    const student = await this.jobsRepository.getStudentByUserId(userId);
    if (!student) throw new NotFoundError("Estudante nao encontrado.");
    if (!student.isEligible) throw new ForbiddenError("Estudante inelegivel.");
    if (!student.addressId) throw new BadRequestError("Cadastre um endereco antes de se candidatar.");

    let application: Awaited<ReturnType<JobsRepository["createApplication"]>>;
    try {
      application = await this.jobsRepository.createApplication(student.id, jobId);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("Voce ja se candidatou a esta vaga.");
      }
      throw error;
    }

    try {
      await this.notificationService.notifyCompany(job.company.id, {
        type: NotificationType.NEW_APPLICATION,
        title: "Nova candidatura",
        message: `Voce recebeu uma nova candidatura para a vaga "${job.title}".`,
      });
    } catch {
      // Falha ao notificar nao deve invalidar a candidatura criada.
    }

    return application;
  }
}
