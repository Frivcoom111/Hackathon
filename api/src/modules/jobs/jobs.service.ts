import { BadRequestError, ConflictError, ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { PaginationMeta } from "../../shared/utils/response";
import type { JobsRepository } from "./jobs.repository";
import type { ListJobsQuery } from "./jobs.schema";

export class JobsService {
  constructor(private readonly jobsRepository: JobsRepository) {}

  async list(query: ListJobsQuery) {
    const { page, limit } = query;
    const { data, total } = await this.jobsRepository.listPublic(query, (page - 1) * limit, limit);
    const meta: PaginationMeta = { page, limit, total, totalPages: Math.ceil(total / limit) };
    return { data, meta };
  }

  async getById(jobId: string) {
    const job = await this.jobsRepository.getPublicById(jobId);
    if (!job) throw new NotFoundError("Vaga não encontrada.");
    return job;
  }

  async apply(userId: string, jobId: string, resumePath: string | undefined, coverLetter?: string) {
    const job = await this.jobsRepository.getJobForApply(jobId);
    if (!job) throw new NotFoundError("Vaga não encontrada.");
    if (job.status !== "ACTIVE" || job.company.status !== "APPROVED") {
      throw new BadRequestError("Vaga indisponível para candidatura.");
    }

    const student = await this.jobsRepository.getStudentByUserId(userId);
    if (!student) throw new NotFoundError("Estudante não encontrado.");
    if (!student.isEligible) throw new ForbiddenError("Estudante inelegível.");

    try {
      return await this.jobsRepository.createApplication(student.id, jobId, resumePath, coverLetter);
    } catch (error) {
      if ((error as { code?: string }).code === "P2002") {
        throw new ConflictError("Você já se candidatou a esta vaga.");
      }
      throw error;
    }
  }
}
