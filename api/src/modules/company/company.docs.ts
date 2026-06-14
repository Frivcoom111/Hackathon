import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { changePasswordSchema, paginationQuerySchema } from "../../shared/schemas/common.schema";
import {
  changeApplicationStatusSchema,
  changeJobStatusSchema,
  createJobSchema,
  createMemberSchema,
  jobApplicationParamsSchema,
  jobIdParamsSchema,
  memberIdParamsSchema,
  updateCompanyProfileSchema,
  updateJobSchema,
  updateMemberSchema,
  updateMyProfileSchema,
} from "./company.schema";

const TAG = "Company";

const jsonBody = (schema: Parameters<typeof successResponse>[0]) => ({
  content: { "application/json": { schema } },
});

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

// ─── Doc schemas (saída) ──────────────────────────────────────────────────────

const companyProfileSchema = z
  .object({
    id: z.uuid(),
    name: z.string(),
    cnpj: z.string(),
    description: z.string(),
    phone: z.string(),
    status: z.enum(["PENDING", "ANALYSING", "APPROVED", "BLOCKED"]),
  })
  .openapi({ title: "CompanyProfile" });

const memberSchema = z
  .object({
    id: z.uuid(),
    name: z.string(),
    cpf: z.string(),
    phone: z.string().nullable(),
    role: z.enum(["ADMIN", "RECRUITER"]),
    user: z.object({
      email: z.string(),
      isActive: z.boolean(),
      totpEnabled: z.boolean(),
    }),
  })
  .openapi({ title: "CompanyMemberItem" });

const jobSchema = z
  .object({
    id: z.uuid(),
    title: z.string(),
    area: z.string(),
    location: z.string(),
    modality: z.enum(["PRESENCIAL", "REMOTE", "HYBRID"]),
    status: z.enum(["ACTIVE", "PAUSED", "CLOSED"]),
    salary: z.number().nullable(),
  })
  .openapi({ title: "CompanyJob" });

const applicationSchema = z
  .object({
    id: z.uuid(),
    status: z.enum(["PENDING", "ANALYSING", "APPROVED", "REJECTED", "CANCELLED"]),
  })
  .openapi({ title: "CompanyApplication" });

const forbidden = error("Acesso negado.");
const unauthorized = error("Token inválido ou expirado.");

// ─── Perfil ────────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/company/profile",
  tags: [TAG],
  security: bearerAuth,
  summary: "Retorna o perfil da empresa do membro autenticado.",
  responses: {
    200: {
      description: "Perfil da empresa.",
      content: { "application/json": { schema: successResponse(companyProfileSchema) } },
    },
    401: unauthorized,
    403: forbidden,
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/profile",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza name, description e/ou phone (somente ADMIN). CNPJ não é editável.",
  request: { body: jsonBody(updateCompanyProfileSchema) },
  responses: {
    200: {
      description: "Perfil atualizado.",
      content: { "application/json": { schema: successResponse(companyProfileSchema) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
    403: forbidden,
  },
});

// ─── Dados próprios do membro ──────────────────────────────────────────────────────
registry.registerPath({
  method: "patch",
  path: "/company/me",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza os próprios dados (name, phone e/ou email).",
  request: { body: jsonBody(updateMyProfileSchema) },
  responses: {
    200: {
      description: "Dados atualizados.",
      content: { "application/json": { schema: successResponse(memberSchema) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
    409: error("E-mail já está em uso."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/me/password",
  tags: [TAG],
  security: bearerAuth,
  summary: "Altera a própria senha (exige a senha atual).",
  request: { body: jsonBody(changePasswordSchema) },
  responses: {
    200: {
      description: "Senha alterada.",
      content: { "application/json": { schema: successResponse(z.null()) } },
    },
    400: error("Dados inválidos."),
    401: error("Senha atual incorreta."),
  },
});

// ─── Membros (somente ADMIN) ───────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/company/members",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista os membros da empresa (somente ADMIN).",
  responses: {
    200: {
      description: "Membros da empresa.",
      content: { "application/json": { schema: successResponse(z.array(memberSchema)) } },
    },
    401: unauthorized,
    403: forbidden,
  },
});

registry.registerPath({
  method: "post",
  path: "/company/members",
  tags: [TAG],
  security: bearerAuth,
  summary: "Cria um membro da empresa (somente ADMIN).",
  request: { body: jsonBody(createMemberSchema) },
  responses: {
    201: {
      description: "Membro criado.",
      content: { "application/json": { schema: successResponse(memberSchema) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
    403: forbidden,
    409: error("E-mail ou CPF já cadastrado."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/members/{memberId}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza name, phone e/ou role de um membro (somente ADMIN, não pode alterar a si mesmo).",
  request: { params: memberIdParamsSchema, body: jsonBody(updateMemberSchema) },
  responses: {
    200: {
      description: "Membro atualizado.",
      content: { "application/json": { schema: successResponse(memberSchema) } },
    },
    400: error("Você não pode alterar seus próprios dados de acesso."),
    401: unauthorized,
    403: forbidden,
    404: error("Membro não encontrado."),
  },
});

registry.registerPath({
  method: "delete",
  path: "/company/members/{memberId}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Desativa um membro — soft delete (somente ADMIN, não pode desativar a si mesmo).",
  request: { params: memberIdParamsSchema },
  responses: {
    204: { description: "Membro desativado." },
    400: error("Você não pode alterar seus próprios dados de acesso."),
    401: unauthorized,
    403: forbidden,
    404: error("Membro não encontrado."),
  },
});

registry.registerPath({
  method: "post",
  path: "/company/members/{memberId}/totp/reset",
  tags: [TAG],
  security: bearerAuth,
  summary: "Reseta o TOTP de um membro (somente ADMIN, não pode resetar o próprio).",
  request: { params: memberIdParamsSchema },
  responses: {
    200: {
      description: "TOTP resetado.",
      content: { "application/json": { schema: successResponse(z.null()) } },
    },
    400: error("Você não pode alterar seus próprios dados de acesso."),
    401: unauthorized,
    403: forbidden,
    404: error("Membro não encontrado."),
  },
});

// ─── Vagas ───────────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/company/jobs",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista as vagas da empresa (paginado).",
  request: { query: paginationQuerySchema },
  responses: {
    200: {
      description: "Lista paginada de vagas.",
      content: { "application/json": { schema: paginatedResponse(jobSchema) } },
    },
    401: unauthorized,
    403: forbidden,
  },
});

registry.registerPath({
  method: "post",
  path: "/company/jobs",
  tags: [TAG],
  security: bearerAuth,
  summary: "Cria uma vaga.",
  request: { body: jsonBody(createJobSchema) },
  responses: {
    201: {
      description: "Vaga criada.",
      content: { "application/json": { schema: successResponse(jobSchema) } },
    },
    400: error("Dados inválidos ou curso não encontrado."),
    401: unauthorized,
    403: forbidden,
  },
});

registry.registerPath({
  method: "get",
  path: "/company/jobs/{jobId}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Detalha uma vaga da empresa.",
  request: { params: jobIdParamsSchema },
  responses: {
    200: {
      description: "Vaga encontrada.",
      content: { "application/json": { schema: successResponse(jobSchema) } },
    },
    401: unauthorized,
    403: forbidden,
    404: error("Vaga não encontrada."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/jobs/{jobId}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza uma vaga.",
  request: { params: jobIdParamsSchema, body: jsonBody(updateJobSchema) },
  responses: {
    200: {
      description: "Vaga atualizada.",
      content: { "application/json": { schema: successResponse(jobSchema) } },
    },
    400: error("Dados inválidos."),
    401: unauthorized,
    403: forbidden,
    404: error("Vaga não encontrada."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/jobs/{jobId}/status",
  tags: [TAG],
  security: bearerAuth,
  summary: "Altera o status da vaga (CLOSED é terminal).",
  request: { params: jobIdParamsSchema, body: jsonBody(changeJobStatusSchema) },
  responses: {
    200: {
      description: "Status atualizado.",
      content: { "application/json": { schema: successResponse(jobSchema) } },
    },
    400: error("Transição de status inválida."),
    401: unauthorized,
    403: forbidden,
    404: error("Vaga não encontrada."),
  },
});

// ─── Candidaturas ─────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/company/jobs/{jobId}/applications",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista candidaturas de uma vaga (paginado).",
  request: { params: jobIdParamsSchema, query: paginationQuerySchema },
  responses: {
    200: {
      description: "Lista paginada de candidaturas.",
      content: { "application/json": { schema: paginatedResponse(applicationSchema) } },
    },
    401: unauthorized,
    403: forbidden,
    404: error("Vaga não encontrada."),
  },
});

registry.registerPath({
  method: "patch",
  path: "/company/jobs/{jobId}/applications/{id}/status",
  tags: [TAG],
  security: bearerAuth,
  summary: "Atualiza o status de uma candidatura (ANALYSING | APPROVED | REJECTED).",
  request: { params: jobApplicationParamsSchema, body: jsonBody(changeApplicationStatusSchema) },
  responses: {
    200: {
      description: "Status da candidatura atualizado.",
      content: { "application/json": { schema: successResponse(applicationSchema) } },
    },
    400: error("Transição de status inválida."),
    401: unauthorized,
    403: forbidden,
    404: error("Candidatura não encontrada."),
  },
});
