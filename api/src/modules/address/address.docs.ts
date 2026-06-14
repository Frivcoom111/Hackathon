import { bearerAuth, errorResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { addressSchema } from "./address.schema";

const TAG = "Address";

const jsonBody = (schema: Parameters<typeof successResponse>[0]) => ({
  content: { "application/json": { schema } },
});

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

const unauthorized = error("Token inválido ou expirado.");

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
  .openapi({ title: "Address" });

// Gera as 4 operações CRUD para um alvo (/me ou /company).
const registerCrud = (target: "me" | "company", summarySuffix: string) => {
  const path = `/address/${target}`;
  const adminNote = target === "company" ? " (apenas ADMIN da empresa)" : "";

  registry.registerPath({
    method: "post",
    path,
    tags: [TAG],
    security: bearerAuth,
    summary: `Cadastra o endereço ${summarySuffix}${adminNote}.`,
    request: { body: jsonBody(addressSchema) },
    responses: {
      201: {
        description: "Endereço cadastrado.",
        content: { "application/json": { schema: successResponse(addressOut) } },
      },
      400: error("Dados inválidos."),
      401: unauthorized,
      409: error("Já existe um endereço cadastrado."),
    },
  });

  registry.registerPath({
    method: "get",
    path,
    tags: [TAG],
    security: bearerAuth,
    summary: `Retorna o endereço ${summarySuffix}.`,
    responses: {
      200: { description: "Endereço.", content: { "application/json": { schema: successResponse(addressOut) } } },
      401: unauthorized,
      404: error("Endereço não encontrado."),
    },
  });

  registry.registerPath({
    method: "put",
    path,
    tags: [TAG],
    security: bearerAuth,
    summary: `Atualiza o endereço ${summarySuffix}${adminNote}.`,
    request: { body: jsonBody(addressSchema) },
    responses: {
      200: {
        description: "Endereço atualizado.",
        content: { "application/json": { schema: successResponse(addressOut) } },
      },
      400: error("Dados inválidos."),
      401: unauthorized,
      404: error("Endereço não encontrado."),
    },
  });

  registry.registerPath({
    method: "delete",
    path,
    tags: [TAG],
    security: bearerAuth,
    summary: `Remove o endereço ${summarySuffix}${adminNote}.`,
    responses: {
      204: { description: "Endereço removido." },
      401: unauthorized,
      404: error("Endereço não encontrado."),
    },
  });
};

registerCrud("me", "do estudante autenticado");
registerCrud("company", "da empresa");
