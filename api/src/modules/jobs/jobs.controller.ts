import type { Request, Response } from "express";
import { requireUser } from "../../shared/utils/requireUser";
import { response } from "../../shared/utils/response";
import { jobIdParamsSchema, listJobsQuerySchema } from "./jobs.schema";
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
    const user = requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const resumePath = req.file ? `uploads/resumes/${req.file.filename}` : undefined;
    const application = await this.jobsService.apply(user.id, jobId, resumePath);
    res.status(201).json(response.success(application, "Candidatura enviada com sucesso."));
  }
}
