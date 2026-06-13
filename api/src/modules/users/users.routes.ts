import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware, requireAdmin } from "../../shared/middlewares/auth.middlewares";
import { UserController } from "./users.controller";
import { UserRepository } from "./users.repository";
import { UserService } from "./users.service";

const router = Router();

const repository = new UserRepository(prisma);
const service = new UserService(repository);
const controller = new UserController(service);

router.use(authMiddleware, requireAdmin);

// Listagem paginada: aceita ?page= e ?limit= na query string
router.get("/", controller.findAll.bind(controller));

router.get("/:id", controller.findById.bind(controller));
router.post("/", controller.create.bind(controller));
router.patch("/:id", controller.update.bind(controller));
router.delete("/:id", controller.delete.bind(controller));

router.patch("/:id/password", controller.changePassword.bind(controller));
router.patch("/:id/role", controller.changeRole.bind(controller));
router.patch("/:id/status", controller.updateStatus.bind(controller));

export default router;
