import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { CourseController } from "./course.controller";
import { CourseRepository } from "./course.repository";
import { CourseService } from "./course.service";

const router = Router();

const repository = new CourseRepository(prisma);
const service = new CourseService(repository);
const controller = new CourseController(service);

// Listagem de cursos é pública: o formulário de cadastro precisa carregá-la
// antes de o usuário ter um token (cadastro é pré-login).
router.get("/", controller.list.bind(controller));

export default router;
