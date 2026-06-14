import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";

const TAG = "Notifications";

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

const unauthorized = error("Token inválido ou expirado.");

const notificationOut = z
  .object({
    id: z.uuid(),
    title: z.string(),
    message: z.string(),
    type: z.string(),
    isRead: z.boolean(),
    createdAt: z.date(),
  })
  .openapi({ title: "Notification" });

registry.registerPath({
  method: "get",
  path: "/notifications",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista as notificações do usuário autenticado (paginado).",
  description: "Filtro opcional `unread=true` para retornar apenas não lidas.",
  request: { query: paginationQuerySchema.extend({ unread: z.coerce.boolean().optional() }) },
  responses: {
    200: {
      description: "Lista paginada de notificações.",
      content: { "application/json": { schema: paginatedResponse(notificationOut) } },
    },
    401: unauthorized,
  },
});

registry.registerPath({
  method: "patch",
  path: "/notifications/read-all",
  tags: [TAG],
  security: bearerAuth,
  summary: "Marca todas as notificações do usuário como lidas.",
  responses: {
    200: {
      description: "Notificações marcadas como lidas.",
      content: { "application/json": { schema: successResponse(z.null()) } },
    },
    401: unauthorized,
  },
});

registry.registerPath({
  method: "patch",
  path: "/notifications/{id}/read",
  tags: [TAG],
  security: bearerAuth,
  summary: "Marca uma notificação como lida.",
  request: { params: idParamsSchema },
  responses: {
    200: {
      description: "Notificação marcada como lida.",
      content: { "application/json": { schema: successResponse(z.null()) } },
    },
    401: unauthorized,
    403: error("Acesso negado."),
    404: error("Notificação não encontrada."),
  },
});
