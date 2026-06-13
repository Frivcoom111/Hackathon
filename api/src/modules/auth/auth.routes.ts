import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { uploadResume } from "../../shared/middlewares/upload.middleware";
import { AuthController } from "./auth.controller";
import { AuthRepository } from "./auth.repository";
import { AuthService } from "./auth.service";

const router = Router();

const repository = new AuthRepository(prisma);
const service = new AuthService(repository);
const controller = new AuthController(service);

// `resume` é o currículo (multipart/form-data); o multer valida tipo/tamanho e salva o arquivo.
router.post("/register/student", uploadResume.single("resume"), controller.register);

router.post("/register/company", controller.registerCompany);

export default router;
