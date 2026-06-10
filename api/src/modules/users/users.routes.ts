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
router.get("/", controller.findAll);

router.get("/:id", controller.findById);
router.post("/", controller.create);
router.patch("/:id", controller.update);
router.delete("/:id", controller.delete);

router.patch("/:id/password", controller.changePassword);
router.patch("/:id/role", controller.changeRole);
router.patch("/:id/status", controller.updateStatus);

export default router;
