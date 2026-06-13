import type { Request, Response } from "express";
import { UnauthorizedError } from "../../shared/errors/AppError";
import { response } from "../../shared/utils/response";
import { loginSchema, registerCompanySchema, registerStudentSchema, totpCodeSchema } from "./auth.schema";
import type { AuthService } from "./auth.service";

export class AuthController {
  constructor(private readonly authService: AuthService) {}

  async registerStudent(req: Request, res: Response): Promise<void> {
    const data = registerStudentSchema.parse(req.body);

    const resumePath = req.file ? `uploads/resumes/${req.file.filename}` : undefined;

    const student = await this.authService.registerStudent({ ...data, resumePath });

    res.status(201).json(response.success(student, "Cadastro realizado com sucesso."));
  }

  async registerCompany(req: Request, res: Response): Promise<void> {
    const data = registerCompanySchema.parse(req.body);

    const company = await this.authService.registerCompany(data);

    res.status(201).json(response.success(company, "Cadastro enviado. Aguarde a aprovação."));
  }

  async login(req: Request, res: Response): Promise<void> {
    const data = loginSchema.parse(req.body);

    const result = await this.authService.login(data);

    res.status(200).json(response.success(result, "Login realizado com sucesso."));
  }

  async totpSetup(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);

    const result = await this.authService.totpSetup(user.id, user.email);

    res.status(200).json(response.success(result));
  }

  async totpSetupConfirm(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { code } = totpCodeSchema.parse(req.body);

    const result = await this.authService.totpSetupConfirm(user.id, code);

    res.status(200).json(response.success(result, "TOTP configurado com sucesso."));
  }

  async totpVerify(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);
    const { code } = totpCodeSchema.parse(req.body);

    const result = await this.authService.totpVerify(user.id, code);

    res.status(200).json(response.success(result, "Verificação concluída."));
  }

  async me(req: Request, res: Response): Promise<void> {
    const user = this.requireUser(req);

    const profile = await this.authService.getMe(user.id, user.role);

    res.status(200).json(response.success(profile));
  }

  // authMiddleware já popula req.user; este guard satisfaz o tipo e é defesa em profundidade.
  private requireUser(req: Request) {
    if (!req.user) {
      throw new UnauthorizedError("Token não fornecido.");
    }
    return req.user;
  }
}
