import type { ZodType } from "zod";
import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { idParamsSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import {
  changePasswordSchema,
  createUserSchema,
  updateRoleSchema,
  updateStatusSchema,
  updateUserSchema,
  userResponseSchema,
} from "./users.schema";

const TAG = "Users";

const jsonBody = (schema: ZodType) => ({
  content: { "application/json": { schema } },
});

const notFound = {
  description: "Usuário não encontrado.",
  content: { "application/json": { schema: errorResponse } },
};

const badRequest = {
  description: "Dados inválidos.",
  content: { "application/json": { schema: errorResponse } },
};

// ─── GET /users ─────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/users",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista usuários de forma paginada.",
  request: { query: paginationQuerySchema },
  responses: {
    200: {
      description: "Lista paginada de usuários.",
      content: { "application/json": { schema: paginatedResponse(userResponseSchema) } },
    },
  },
});

// ─── GET /users/:id ───────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/users/{id}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Busca um usuário pelo ID.",
  request: { params: idParamsSchema },
  responses: {
    200: {
      description: "Usuário encontrado.",
      content: { "application/json": { schema: successResponse(userResponseSchema) } },
    },
    404: notFound,
  },
});

// ─── POST /users ──────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/users",
  tags: [TAG],
  security: bearerAuth,
  summary: "Cria um novo usuário.",
  request: { body: jsonBody(createUserSchema) },
  responses: {
    201: {
      description: "Usuário criado com sucesso.",
      content: { "application/json": { schema: successResponse(userResponseSchema) } },
    },
    400: badRequest,
  },
});

// ─── PATCH /users/:id ─────────────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/users/{id}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza o e-mail de um usuário.",
  request: { params: idParamsSchema, body: jsonBody(updateUserSchema) },
  responses: {
    200: {
      description: "Usuário atualizado com sucesso.",
      content: { "application/json": { schema: successResponse(userResponseSchema) } },
    },
    400: badRequest,
    404: notFound,
  },
});

// ─── DELETE /users/:id ────────────────────────────────────────────────────────────
registry.registerPath({
  method: "delete",
  path: "/users/{id}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Remove um usuário.",
  request: { params: idParamsSchema },
  responses: {
    204: { description: "Usuário removido com sucesso." },
    404: notFound,
  },
});

// ─── PATCH /users/:id/password ────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/users/{id}/password",
  tags: [TAG],
  security: bearerAuth,
  summary: "Altera a senha de um usuário.",
  request: { params: idParamsSchema, body: jsonBody(changePasswordSchema) },
  responses: {
    200: {
      description: "Senha alterada com sucesso.",
      content: { "application/json": { schema: successResponse(userResponseSchema.nullable()) } },
    },
    400: badRequest,
    404: notFound,
  },
});

// ─── PATCH /users/:id/role ────────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/users/{id}/role",
  tags: [TAG],
  security: bearerAuth,
  summary: "Altera o cargo (role) de um usuário.",
  request: { params: idParamsSchema, body: jsonBody(updateRoleSchema) },
  responses: {
    200: {
      description: "Cargo atualizado com sucesso.",
      content: { "application/json": { schema: successResponse(userResponseSchema) } },
    },
    400: badRequest,
    404: notFound,
  },
});

// ─── PATCH /users/:id/status ──────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/users/{id}/status",
  tags: [TAG],
  security: bearerAuth,
  summary: "Ativa ou desativa um usuário.",
  request: { params: idParamsSchema, body: jsonBody(updateStatusSchema) },
  responses: {
    200: {
      description: "Status atualizado com sucesso.",
      content: { "application/json": { schema: successResponse(userResponseSchema) } },
    },
    400: badRequest,
    404: notFound,
  },
});
