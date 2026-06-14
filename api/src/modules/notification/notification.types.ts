export const NotificationType = {
  APPLICATION_STATUS: "APPLICATION_STATUS",
  NEW_APPLICATION: "NEW_APPLICATION",
  APPLICATION_CANCELLED: "APPLICATION_CANCELLED",
} as const;

export type NotificationType = (typeof NotificationType)[keyof typeof NotificationType];

export interface NotificationPayload {
  title: string;
  message: string;
  type: NotificationType;
}

export interface NotificationResponse {
  id: string;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
  createdAt: Date;
}
