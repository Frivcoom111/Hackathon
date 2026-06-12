import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware } from "../../shared/middlewares/auth.middlewares";
import { authRateLimiter } from "../../shared/middlewares/authRateLimit.middleware";
import { uploadResume } from "../../shared/middlewares/upload.middleware";
import { AuthController } from "./auth.controller";
import { AuthRepository } from "./auth.repository";
import { AuthService } from "./auth.service";

const router = Router();

const repository = new AuthRepository(prisma);
const service = new AuthService(repository);
const controller = new AuthController(service);

// `resume` é o currículo (multipart/form-data); o multer valida tipo/tamanho e salva o arquivo.
router.post("/register/student", authRateLimiter, uploadResume.single("resume"), controller.registerStudent);

router.post("/register/company", authRateLimiter, controller.registerCompany);

router.post("/login", authRateLimiter, controller.login);

// Setup não exige mfaVerified (o usuário ainda está concluindo o TOTP), apenas token válido.
router.get("/totp/setup", authMiddleware, controller.totpSetup);
router.post("/totp/setup/confirm", authMiddleware, authRateLimiter, controller.totpSetupConfirm);
router.post("/totp/verify", authMiddleware, authRateLimiter, controller.totpVerify);

router.get("/me", authMiddleware, controller.me);

export default router;
