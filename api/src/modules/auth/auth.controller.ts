import type { Request, Response } from "express";
import { response } from "../../shared/utils/response";
import { registerCompanySchema, registerStudentSchema } from "./auth.schema";
import type { AuthService } from "./auth.service";

export class AuthController {
  constructor(private readonly authService: AuthService) {}

  register = async (req: Request, res: Response): Promise<void> => {
    const data = registerStudentSchema.parse(req.body);

    // O currículo é salvo pelo multer; guardamos o caminho relativo (não o nome do cliente).
    const resumePath = req.file ? `uploads/resumes/${req.file.filename}` : undefined;

    const student = await this.authService.registerStudent({ ...data, resumePath });

    res.status(201).json(response.success(student, "Cadastro realizado com sucesso."));
  };

  registerCompany = async (req: Request, res: Response): Promise<void> => {
    const data = registerCompanySchema.parse(req.body);

    const company = await this.authService.registerCompany(data);

    res.status(201).json(response.success(company, "Cadastro enviado. Aguarde a aprovação."));
  };
}
