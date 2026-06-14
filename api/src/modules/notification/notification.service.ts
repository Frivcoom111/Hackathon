import { ForbiddenError, NotFoundError } from "../../shared/errors/AppError";
import type { PaginationMeta } from "../../shared/utils/response";
import type { NotificationRepository } from "./notification.repository";
import type { NotificationPayload } from "./notification.types";

export class NotificationService {
  constructor(private readonly notificationRepository: NotificationRepository) {}

  async create(userId: string, payload: NotificationPayload) {
    return this.notificationRepository.create(userId, payload);
  }

  async notifyCompany(companyId: string, payload: NotificationPayload): Promise<void> {
    await this.notificationRepository.createForCompanyMembers(companyId, payload);
  }

  async list(userId: string, page: number, limit: number, unread?: boolean) {
    const { data, total } = await this.notificationRepository.listByUser(userId, (page - 1) * limit, limit, unread);
    const meta: PaginationMeta = { page, limit, total, totalPages: Math.ceil(total / limit) };
    return { data, meta };
  }

  async markRead(userId: string, id: string): Promise<void> {
    const notification = await this.notificationRepository.getById(id);
    if (!notification) throw new NotFoundError("Notificação não encontrada.");
    if (notification.userId !== userId) throw new ForbiddenError("Acesso negado.");
    await this.notificationRepository.markRead(id);
  }

  async markAllRead(userId: string): Promise<void> {
    await this.notificationRepository.markAllRead(userId);
  }
}
