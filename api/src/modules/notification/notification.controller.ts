import type { Request, Response } from "express";
import { idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { requireUser } from "../../shared/utils/requireUser";
import { response } from "../../shared/utils/response";
import type { NotificationService } from "./notification.service";

export class NotificationController {
  constructor(private readonly notificationService: NotificationService) {}

  async list(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { page, limit } = paginationQuerySchema.parse(req.query);
    const unread = req.query.unread === "true";
    const { data, meta } = await this.notificationService.list(user.id, page, limit, unread);
    res.status(200).json(response.paginated(data, meta));
  }

  async markRead(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    const { id } = idParamsSchema.parse(req.params);
    await this.notificationService.markRead(user.id, id);
    res.status(200).json(response.success(null, "Notificação marcada como lida."));
  }

  async markAllRead(req: Request, res: Response): Promise<void> {
    const user = requireUser(req);
    await this.notificationService.markAllRead(user.id);
    res.status(200).json(response.success(null, "Notificações marcadas como lidas."));
  }
}
