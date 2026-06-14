import type { Request, Response } from "express";
import { BadRequestError, UnauthorizedError } from "../../shared/errors/AppError";
import { changePasswordSchema, idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { response } from "../../shared/utils/response";
import { updateAddressSchema, updateStudentProfileSchema } from "./student.schema";
import type { StudentService } from "./student.service";

export class StudentController {
  constructor(private readonly studentService: StudentService) {}

  async getProfile(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const profile = await this.studentService.getProfile(user.id);
    res.status(200).json(response.success(profile));
  }

  async updateProfile(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const data = updateStudentProfileSchema.parse(req.body);
    const profile = await this.studentService.updateProfile(user.id, data);
    res.status(200).json(response.success(profile, "Perfil atualizado com sucesso."));
  }

  async changePassword(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const data = changePasswordSchema.parse(req.body);
    await this.studentService.changePassword(user.id, data);
    res.status(200).json(response.success(null, "Senha alterada com sucesso."));
  }

  async updateAddress(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const data = updateAddressSchema.parse(req.body);
    const address = await this.studentService.updateAddress(user.id, data);
    res.status(200).json(response.success(address, "Endereço atualizado com sucesso."));
  }

  async updateResume(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    if (!req.file) throw new BadRequestError("O currículo é obrigatório.");

    const resumePath = `uploads/resumes/${req.file.filename}`;
    const result = await this.studentService.updateResume(user.id, resumePath);
    res.status(200).json(response.success(result, "Currículo atualizado com sucesso."));
  }

  async listApplications(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const query = paginationQuerySchema.parse(req.query);
    const { data, meta } = await this.studentService.listApplications(user.id, query);
    res.status(200).json(response.paginated(data, meta));
  }

  async cancelApplication(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { id } = idParamsSchema.parse(req.params);
    const result = await this.studentService.cancelApplication(user.id, id);
    res.status(200).json(response.success(result, "Candidatura cancelada."));
  }

  private requireUser(req: Request) {
    if (!req.user) throw new UnauthorizedError("Token não fornecido.");
    return req.user;
  }
}
