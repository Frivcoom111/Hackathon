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

router.use(authMiddleware);

router.get("/", controller.list.bind(controller));
router.get("/:jobId", controller.getById.bind(controller));

// Candidatura exige estudante autenticado; currículo opcional via multipart.
router.post("/:jobId/apply", requireStudent, uploadResume.single("resume"), controller.apply.bind(controller));

export default router;
