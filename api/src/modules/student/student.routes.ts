import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireStudent } from "../../shared/middlewares/auth.middlewares";
import { uploadResume } from "../../shared/middlewares/upload.middleware";
import { StudentController } from "./student.controller";
import { StudentRepository } from "./student.repository";
import { StudentService } from "./student.service";

const router = Router();

const repository = new StudentRepository(prisma);
const service = new StudentService(repository);
const controller = new StudentController(service);

// Todas as rotas exigem estudante autenticado.
router.use(authMiddleware, requireStudent);

router.get("/profile", controller.getProfile);
router.patch("/profile", controller.updateProfile);
router.put("/address", controller.updateAddress);
router.patch("/resume", uploadResume.single("resume"), controller.updateResume);
router.get("/applications", controller.listApplications);
router.delete("/applications/:id", controller.cancelApplication);

export default router;
