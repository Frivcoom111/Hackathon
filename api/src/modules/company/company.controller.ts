import type { Request, Response } from "express";
import { UnauthorizedError } from "../../shared/errors/AppError";
import { paginationQuerySchema } from "../../shared/schemas/common.schema";
import { response } from "../../shared/utils/response";
import {
  changeApplicationStatusSchema,
  changeJobStatusSchema,
  changeMemberRoleSchema,
  createJobSchema,
  jobApplicationParamsSchema,
  jobIdParamsSchema,
  memberIdParamsSchema,
  updateCompanyProfileSchema,
  updateJobSchema,
} from "./company.schema";
import type { CompanyService } from "./company.service";

export class CompanyController {
  constructor(private readonly companyService: CompanyService) {}

  // ─── Perfil ───────────────────────────────────────────────────────────────

  async getProfile(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const company = await this.companyService.getProfile(user.id);
    res.status(200).json(response.success(company));
  }

  async updateProfile(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const data = updateCompanyProfileSchema.parse(req.body);
    const company = await this.companyService.updateProfile(user.id, data);
    res.status(200).json(response.success(company, "Perfil atualizado com sucesso."));
  }

  // ─── Membros ──────────────────────────────────────────────────────────────

  async listMembers(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const members = await this.companyService.listMembers(user.id);
    res.status(200).json(response.success(members));
  }

  async changeMemberRole(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { memberId } = memberIdParamsSchema.parse(req.params);
    const data = changeMemberRoleSchema.parse(req.body);
    const member = await this.companyService.changeMemberRole(user.id, memberId, data);
    res.status(200).json(response.success(member, "Cargo do membro atualizado."));
  }

  async resetMemberTotp(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { memberId } = memberIdParamsSchema.parse(req.params);
    await this.companyService.resetMemberTotp(user.id, memberId);
    res.status(200).json(response.success(null, "TOTP do membro resetado."));
  }

  // ─── Vagas ────────────────────────────────────────────────────────────────

  async listJobs(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const query = paginationQuerySchema.parse(req.query);
    const { data, meta } = await this.companyService.listJobs(user.id, query);
    res.status(200).json(response.paginated(data, meta));
  }

  async createJob(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const data = createJobSchema.parse(req.body);
    const job = await this.companyService.createJob(user.id, data);
    res.status(201).json(response.success(job, "Vaga criada com sucesso."));
  }

  async getJob(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const job = await this.companyService.getJob(user.id, jobId);
    res.status(200).json(response.success(job));
  }

  async updateJob(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const data = updateJobSchema.parse(req.body);
    const job = await this.companyService.updateJob(user.id, jobId, data);
    res.status(200).json(response.success(job, "Vaga atualizada com sucesso."));
  }

  async changeJobStatus(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const { status } = changeJobStatusSchema.parse(req.body);
    const job = await this.companyService.changeJobStatus(user.id, jobId, status);
    res.status(200).json(response.success(job, "Status da vaga atualizado."));
  }

  // ─── Candidaturas ─────────────────────────────────────────────────────────

  async listApplications(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const query = paginationQuerySchema.parse(req.query);
    const { data, meta } = await this.companyService.listApplications(user.id, jobId, query);
    res.status(200).json(response.paginated(data, meta));
  }

  async changeApplicationStatus(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { jobId, id } = jobApplicationParamsSchema.parse(req.params);
    const data = changeApplicationStatusSchema.parse(req.body);
    const application = await this.companyService.changeApplicationStatus(user.id, jobId, id, data);
    res.status(200).json(response.success(application, "Status da candidatura atualizado."));
  }

  // authMiddleware já popula req.user; guard satisfaz o tipo e reforça a segurança.
  private requireUser(req: Request) {
    if (!req.user) throw new UnauthorizedError("Token não fornecido.");
    return req.user;
  }
}
