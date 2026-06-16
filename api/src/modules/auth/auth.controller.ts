import type { Request, Response } from "express";
import { requireUser } from "../../shared/utils/requireUser";
import { response } from "../../shared/utils/response";
import { loginSchema, registerCompanySchema, registerStudentSchema, totpCodeSchema } from "./auth.schema";
import type { AuthService } from "./auth.service";

export class AuthController {
  constructor(private readonly authService: AuthService) {}

  async registerStudent(req: Request, res: Response): Promise<void> {
    const data = registerStudentSchema.parse(req.body);

    const student = await this.authService.registerStudent(data);

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

  async totpSetupConfirm(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = totpCodeSchema.parse(req.body);

    const result = await this.authService.confirmTotp(user.id, data);

    res.status(200).json(response.success(result, "TOTP configurado com sucesso."));
  }

  async totpVerify(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const data = totpCodeSchema.parse(req.body);

    const result = await this.authService.verifyTotp(user.id, data);

    res.status(200).json(response.success(result, "Verificação concluída."));
  }
}
