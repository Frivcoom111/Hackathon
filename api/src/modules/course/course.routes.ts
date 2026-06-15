import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware } from "../../shared/middlewares/auth.middlewares";
import { CourseController } from "./course.controller";
import { CourseRepository } from "./course.repository";
import { CourseService } from "./course.service";

const router = Router();

const repository = new CourseRepository(prisma);
const service = new CourseService(repository);
const controller = new CourseController(service);

router.use(authMiddleware);

router.get("/", controller.list.bind(controller));

export default router;
