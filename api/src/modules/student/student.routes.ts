import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireStudent } from "../../shared/middlewares/auth.middlewares";
import { NotificationRepository } from "../notification/notification.repository";
import { NotificationService } from "../notification/notification.service";
import { StudentController } from "./student.controller";
import { StudentRepository } from "./student.repository";
import { StudentService } from "./student.service";

const router = Router();

const repository = new StudentRepository(prisma);
const notificationService = new NotificationService(new NotificationRepository(prisma));
const service = new StudentService(repository, notificationService);
const controller = new StudentController(service);

// Todas as rotas exigem estudante autenticado.
router.use(authMiddleware, requireStudent);

router.get("/profile", controller.getProfile.bind(controller));
router.patch("/profile", controller.updateProfile.bind(controller));
router.patch("/password", controller.changePassword.bind(controller));
router.get("/applications", controller.listApplications.bind(controller));
router.delete("/applications/:id", controller.cancelApplication.bind(controller));

export default router;
