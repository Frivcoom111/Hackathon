import type { Request, Response } from "express";
import { UnauthorizedError } from "../../shared/errors/AppError";
import { response } from "../../shared/utils/response";
import { applyJobSchema, jobIdParamsSchema, listJobsQuerySchema } from "./jobs.schema";
import type { JobsService } from "./jobs.service";

export class JobsController {
  constructor(private readonly jobsService: JobsService) {}

  async list(req: Request, res: Response): Promise<void> {
    const query = listJobsQuerySchema.parse(req.query);
    const { data, meta } = await this.jobsService.list(query);
    res.status(200).json(response.paginated(data, meta));
  }

  async getById(req: Request, res: Response): Promise<void> {
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const job = await this.jobsService.getById(jobId);
    res.status(200).json(response.success(job));
  }

  async apply(req: Request, res: Response): Promise<void> {
    if (!req.user) throw new UnauthorizedError("Token não fornecido.");
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const { coverLetter } = applyJobSchema.parse(req.body);

    // Currículo é opcional na candidatura; quando enviado, guarda o caminho relativo.
    const resumePath = req.file ? `uploads/resumes/${req.file.filename}` : undefined;

    const application = await this.jobsService.apply(req.user.id, jobId, resumePath, coverLetter);
    res.status(201).json(response.success(application, "Candidatura enviada com sucesso."));
  }
}
