import type { Request, Response } from "express";
import { changePasswordSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { requireUser } from "../../shared/utils/requireUser";
import { response } from "../../shared/utils/response";
import { sendResumeFile } from "../../shared/utils/sendResumeFile";
import {
  changeApplicationStatusSchema,
  changeJobStatusSchema,
  createJobSchema,
  createMemberSchema,
  jobApplicationParamsSchema,
  jobIdParamsSchema,
  memberIdParamsSchema,
  updateCompanyProfileSchema,
  updateJobSchema,
  updateMemberSchema,
  updateMyProfileSchema,
} from "./company.schema";
import type { CompanyService } from "./company.service";

export class CompanyController {
  constructor(private readonly companyService: CompanyService) {}

  // ─── Perfil ───────────────────────────────────────────────────────────────

  async getProfile(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const company = await this.companyService.getProfile(user.id);
    res.status(200).json(response.success(company));
  }

  async updateProfile(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = updateCompanyProfileSchema.parse(req.body);
    const company = await this.companyService.updateProfile(user.id, data);
    res.status(200).json(response.success(company, "Perfil atualizado com sucesso."));
  }

  // ─── Membros (somente ADMIN) ────────────────────────────────────────────────

  async listMembers(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const members = await this.companyService.listMembers(user.id);
    res.status(200).json(response.success(members));
  }

  async createMember(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = createMemberSchema.parse(req.body);
    const member = await this.companyService.createMember(user.id, data);
    res.status(201).json(response.success(member, "Membro criado com sucesso."));
  }

  async updateMember(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { memberId } = memberIdParamsSchema.parse(req.params);
    const data = updateMemberSchema.parse(req.body);
    const member = await this.companyService.updateMember(user.id, memberId, data);
    res.status(200).json(response.success(member, "Membro atualizado com sucesso."));
  }

  async deleteMember(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { memberId } = memberIdParamsSchema.parse(req.params);
    await this.companyService.deleteMember(user.id, memberId);
    res.status(204).send();
  }

  async resetMemberTotp(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { memberId } = memberIdParamsSchema.parse(req.params);
    await this.companyService.resetMemberTotp(user.id, memberId);
    res.status(200).json(response.success(null, "TOTP do membro resetado."));
  }

  // ─── Dados próprios (qualquer membro) ───────────────────────────────────────

  async updateMe(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = updateMyProfileSchema.parse(req.body);
    const member = await this.companyService.updateMyProfile(user.id, data);
    res.status(200).json(response.success(member, "Dados atualizados com sucesso."));
  }

  async changeMyPassword(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = changePasswordSchema.parse(req.body);
    await this.companyService.changeMyPassword(user.id, data);
    res.status(200).json(response.success(null, "Senha alterada com sucesso."));
  }

  // ─── Vagas ────────────────────────────────────────────────────────────────

  async listJobs(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const query = paginationQuerySchema.parse(req.query);
    const { data, meta } = await this.companyService.listJobs(user.id, query);
    res.status(200).json(response.paginated(data, meta));
  }

  async createJob(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = createJobSchema.parse(req.body);
    const job = await this.companyService.createJob(user.id, data);
    res.status(201).json(response.success(job, "Vaga criada com sucesso."));
  }

  async getJob(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const job = await this.companyService.getJob(user.id, jobId);
    res.status(200).json(response.success(job));
  }

  async updateJob(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const data = updateJobSchema.parse(req.body);
    const job = await this.companyService.updateJob(user.id, jobId, data);
    res.status(200).json(response.success(job, "Vaga atualizada com sucesso."));
  }

  async changeJobStatus(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const { status } = changeJobStatusSchema.parse(req.body);
    const job = await this.companyService.changeJobStatus(user.id, jobId, status);
    res.status(200).json(response.success(job, "Status da vaga atualizado."));
  }

  // ─── Candidaturas ─────────────────────────────────────────────────────────

  async listApplications(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId } = jobIdParamsSchema.parse(req.params);
    const query = paginationQuerySchema.parse(req.query);
    const { data, meta } = await this.companyService.listApplications(user.id, jobId, query);
    res.status(200).json(response.paginated(data, meta));
  }

  async changeApplicationStatus(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId, id } = jobApplicationParamsSchema.parse(req.params);
    const data = changeApplicationStatusSchema.parse(req.body);
    const application = await this.companyService.changeApplicationStatus(user.id, jobId, id, data);
    res.status(200).json(response.success(application, "Status da candidatura atualizado."));
  }

  async downloadApplicationResume(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { jobId, id } = jobApplicationParamsSchema.parse(req.params);
    const resumePath = await this.companyService.getApplicationResumePath(user.id, jobId, id);
    sendResumeFile(res, resumePath);
  }
}
