import type { PrismaClient } from "../../generated/prisma/client";
import type { NotificationPayload, NotificationResponse } from "./notification.types";

const notificationSelect = {
  id: true,
  title: true,
  message: true,
  type: true,
  isRead: true,
  createdAt: true,
} as const;

export class NotificationRepository {
  constructor(private readonly prisma: PrismaClient) {}

  async create(userId: string, payload: NotificationPayload): Promise<NotificationResponse> {
    return this.prisma.notification.create({ data: { userId, ...payload }, select: notificationSelect });
  }

  async createForCompanyMembers(companyId: string, payload: NotificationPayload): Promise<void> {
    const members = await this.prisma.companyMember.findMany({ where: { companyId }, select: { userId: true } });
    if (members.length === 0) return;
    await this.prisma.notification.createMany({
      data: members.map((m) => ({ userId: m.userId, ...payload })),
    });
  }

  async listByUser(userId: string, skip: number, take: number, unread?: boolean) {
    const where = { userId, ...(unread ? { isRead: false } : {}) };
    const [data, total] = await this.prisma.$transaction([
      this.prisma.notification.findMany({
        where,
        select: notificationSelect,
        skip,
        take,
        orderBy: { createdAt: "desc" },
      }),
      this.prisma.notification.count({ where }),
    ]);
    return { data, total };
  }

  async getById(id: string) {
    return this.prisma.notification.findUnique({ where: { id }, select: { id: true, userId: true } });
  }

  async markRead(id: string): Promise<void> {
    await this.prisma.notification.update({ where: { id }, data: { isRead: true } });
  }

  async markAllRead(userId: string): Promise<void> {
    await this.prisma.notification.updateMany({ where: { userId, isRead: false }, data: { isRead: true } });
  }
}
