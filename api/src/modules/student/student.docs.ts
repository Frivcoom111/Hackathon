import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { changePasswordSchema, idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { updateStudentProfileSchema } from "./student.schema";

const TAG = "Student";

const jsonBody = (schema: Parameters<typeof successResponse>[0]) => ({
  content: { "application/json": { schema } },
});

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

const unauthorized = error("Token invalido ou expirado.");

const applicationOut = z
  .object({
    id: z.uuid(),
    status: z.enum(["PENDING", "ANALYSING", "APPROVED", "REJECTED", "CANCELLED"]),
    job: z.object({ id: z.uuid(), title: z.string() }),
  })
  .openapi({ title: "StudentApplication" });

registry.registerPath({
  method: "get",
  path: "/student/profile",
  tags: [TAG],
  security: bearerAuth,
  summary: "Retorna o perfil do estudante autenticado.",
  responses: {
    200: {
      description: "Perfil do estudante.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    401: unauthorized,
    404: error("Perfil nao encontrado."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/student/profile",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza dados basicos do estudante.",
  request: { body: jsonBody(updateStudentProfileSchema) },
  responses: {
    200: {
      description: "Perfil atualizado.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    400: error("Dados invalidos."),
    401: unauthorized,
    409: error("E-mail ja esta em uso."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/student/password",
  tags: [TAG],
  security: bearerAuth,
  summary: "Altera a propria senha.",
  request: { body: jsonBody(changePasswordSchema) },
  responses: {
    200: {
      description: "Senha alterada.",
      content: { "application/json": { schema: successResponse(z.null()) } },
    },
    400: error("Dados invalidos."),
    401: error("Senha atual incorreta."),
  },
});

registry.registerPath({
  method: "get",
  path: "/student/applications",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista as candidaturas do estudante.",
  request: { query: paginationQuerySchema },
  responses: {
    200: {
      description: "Lista paginada de candidaturas.",
      content: { "application/json": { schema: paginatedResponse(applicationOut) } },
    },
    401: unauthorized,
  },
});

registry.registerPath({
  method: "delete",
  path: "/student/applications/{id}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Cancela uma candidatura.",
  request: { params: idParamsSchema },
  responses: {
    200: {
      description: "Candidatura cancelada.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    400: error("Candidatura nao pode ser cancelada neste status."),
    401: unauthorized,
    403: error("Acesso negado."),
    404: error("Candidatura nao encontrada."),
  },
});
