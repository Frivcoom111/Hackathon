import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { uploadResume } from "../../shared/middlewares/upload.middleware";
import { response } from "../../shared/utils/response";
import { loginSchema, registerCompanySchema, registerStudentSchema } from "./auth.schema";
import { AuthService } from "./auth.service";

const router = Router();
const service = new AuthService(prisma);

router.post("/login", async (req, res) => {
  const data = loginSchema.parse(req.body);
  const result = await service.login(data);

  res.status(200).json(response.success(result, "Login realizado com sucesso."));
});

router.post("/register/student", uploadResume.single("resume"), async (req, res) => {
  const data = registerStudentSchema.parse({
    ...req.body,
    resumePath: req.file?.path,
  });
  const result = await service.registerStudent(data);

  res.status(201).json(response.success(result, "Aluno cadastrado com sucesso."));
});

router.post("/register/company", async (req, res) => {
  const data = registerCompanySchema.parse(req.body);
  const result = await service.registerCompany(data);

  res.status(201).json(response.success(result, "Empresa cadastrada com sucesso."));
});

export default router;
