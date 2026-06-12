import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireStudent } from "../../shared/middlewares/auth.middlewares";
import { uploadResume } from "../../shared/middlewares/upload.middleware";
import { JobsController } from "./jobs.controller";
import { JobsRepository } from "./jobs.repository";
import { JobsService } from "./jobs.service";

const router = Router();

const repository = new JobsRepository(prisma);
const service = new JobsService(repository);
const controller = new JobsController(service);

// Listagem e detalhe são públicos (sem auth).
router.get("/", controller.list);
router.get("/:jobId", controller.getById);

// Candidatura exige estudante autenticado; currículo opcional via multipart.
router.post("/:jobId/apply", authMiddleware, requireStudent, uploadResume.single("resume"), controller.apply);

export default router;
