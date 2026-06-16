import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware } from "../../shared/middlewares/auth.middlewares";
import { AuthController } from "./auth.controller";
import { AuthRepository } from "./auth.repository";
import { AuthService } from "./auth.service";

const router = Router();

const repository = new AuthRepository(prisma);
const service = new AuthService(repository);
const controller = new AuthController(service);

router.post("/login", controller.login.bind(controller));
router.post("/register/student", controller.registerStudent.bind(controller));
router.post("/register/company", controller.registerCompany.bind(controller));

router.post("/totp/setup/confirm", authMiddleware, controller.totpSetupConfirm.bind(controller));
router.post("/totp/verify", authMiddleware, controller.totpVerify.bind(controller));

router.get("/me", authMiddleware, controller.me.bind(controller));

export default router;
