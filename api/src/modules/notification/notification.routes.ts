import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { authMiddleware } from "../../shared/middlewares/auth.middlewares";
import { NotificationController } from "./notification.controller";
import { NotificationRepository } from "./notification.repository";
import { NotificationService } from "./notification.service";

const router = Router();

const repository = new NotificationRepository(prisma);
const service = new NotificationService(repository);
const controller = new NotificationController(service);

router.use(authMiddleware);

router.get("/", controller.list.bind(controller));
router.patch("/read-all", controller.markAllRead.bind(controller));
router.patch("/:id/read", controller.markRead.bind(controller));

export default router;
