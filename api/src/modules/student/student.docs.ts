import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import { updateAddressSchema, updateStudentProfileSchema } from "./student.schema";

const TAG = "Student";

const jsonBody = (schema: Parameters<typeof successResponse>[0]) => ({
  content: { "application/json": { schema } },
});

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

const unauthorized = error("Token inválido ou expirado.");

// ─── Doc schemas (saída) ──────────────────────────────────────────────────────

const addressOut = z
  .object({
    street: z.string(),
    number: z.string(),
    complement: z.string().nullable(),
    district: z.string(),
    city: z.string(),
    state: z.string(),
    zipCode: z.string(),
  })
  .openapi({ title: "StudentAddress" });

const applicationOut = z
  .object({
    id: z.uuid(),
    status: z.enum(["PENDING", "ANALYSING", "APPROVED", "REJECTED", "CANCELLED"]),
    resumePath: z.string().nullable(),
    job: z.object({ id: z.uuid(), title: z.string() }),
  })
  .openapi({ title: "StudentApplication" });

// ─── Perfil ────────────────────────────────────────────────────────────────────
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
    404: error("Perfil não encontrado."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/student/profile",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza name e/ou phone do estudante.",
  request: { body: jsonBody(updateStudentProfileSchema) },
  responses: {
    200: {
      description: "Perfil atualizado.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
  },
});

// ─── Endereço ───────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "put",
  path: "/student/address",
  tags: [TAG],
  security: bearerAuth,
  summary: "Cria ou atualiza o endereço do estudante (upsert).",
  request: { body: jsonBody(updateAddressSchema) },
  responses: {
    200: {
      description: "Endereço salvo.",
      content: { "application/json": { schema: successResponse(addressOut) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
  },
});

// ─── Currículo ──────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/student/resume",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza o currículo (multipart, campo `resume`).",
  request: {
    body: {
      content: {
        "multipart/form-data": {
          schema: z.object({
            resume: z.string().openapi({ type: "string", format: "binary" }),
          }),
        },
      },
    },
  },
  responses: {
    200: {
      description: "Currículo atualizado.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    400: error("O currículo é obrigatório ou o arquivo é inválido."),
    401: unauthorized,
  },
});

// ─── Candidaturas ─────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/student/applications",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista as candidaturas do estudante (paginado).",
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
  summary: "Cancela uma candidatura (apenas PENDING ou ANALYSING).",
  request: { params: idParamsSchema },
  responses: {
    200: {
      description: "Candidatura cancelada.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    400: error("Candidatura não pode ser cancelada neste status."),
    401: unauthorized,
    403: error("Acesso negado."),
    404: error("Candidatura não encontrada."),
  },
});
