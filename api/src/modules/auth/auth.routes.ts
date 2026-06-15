import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware } from "../../shared/middlewares/auth.middlewares";
import { response } from "../../shared/utils/response";
import { loginSchema, registerCompanySchema, registerStudentSchema, totpCodeSchema } from "./auth.schema";
import { AuthService } from "./auth.service";

const router = Router();
const service = new AuthService(prisma);

router.post("/login", async (req, res) => {
  const data = loginSchema.parse(req.body);
  const result = await service.login(data);

  res.status(200).json(response.success(result, "Login realizado com sucesso."));
});

router.post("/register/student", async (req, res) => {
  const data = registerStudentSchema.parse(req.body);
  const result = await service.registerStudent(data);

  res.status(201).json(response.success(result, "Aluno cadastrado com sucesso."));
});

router.post("/register/company", async (req, res) => {
  const data = registerCompanySchema.parse(req.body);
  const result = await service.registerCompany(data);

  res.status(201).json(response.success(result, "Empresa cadastrada com sucesso."));
});

router.get("/totp/setup", authMiddleware, async (req, res) => {
  const result = await service.setupTotp(req.user!.id);

  res.status(200).json(response.success(result, "Escaneie o QR Code no Authenticator."));
});

router.post("/totp/setup/confirm", authMiddleware, async (req, res) => {
  const data = totpCodeSchema.parse(req.body);
  const result = await service.confirmTotp(req.user!.id, data);

  res.status(200).json(response.success(result, "Authenticator configurado com sucesso."));
});

router.post("/totp/verify", authMiddleware, async (req, res) => {
  const data = totpCodeSchema.parse(req.body);
  const result = await service.verifyTotp(req.user!.id, data);

  res.status(200).json(response.success(result, "Codigo confirmado com sucesso."));
});

export default router;
