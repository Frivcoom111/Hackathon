import { bearerAuth, errorResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import {
  companyResponseSchema,
  loginSchema,
  registerCompanySchema,
  registerStudentSchema,
  studentResponseSchema,
  totpCodeSchema,
} from "./auth.schema";

const TAG = "Auth";

const jsonBody = (schema: Parameters<typeof successResponse>[0]) => ({
  content: { "application/json": { schema } },
});

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

// Corpo enviado em JSON no cadastro de estudante.
const registerStudentBody = registerStudentSchema.openapi({ title: "RegisterStudentBody" });

const tokenResponse = z.object({ token: z.string() }).openapi({ title: "TokenResponse" });
const loginResponse = z
  .object({
    type: z.enum(["AUTHENTICATED", "TOTP_SETUP", "TOTP_REQUIRED"]),
    token: z.string().optional(),
    tempToken: z.string().optional(),
    qrCode: z.string().optional(),
    requiresSetup: z.boolean().optional(),
    requiresVerification: z.boolean().optional(),
  })
  .openapi({ title: "LoginResult" });

// ─── POST /auth/register/student ──────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/auth/register/student",
  tags: [TAG],
  summary: "Registra um estudante.",
  description: "Cria User(STUDENT) + Student + StudentCourse em transação. Não requer autenticação.",
  request: {
    body: jsonBody(registerStudentBody),
  },
  responses: {
    201: {
      description: "Cadastro realizado com sucesso.",
      content: { "application/json": { schema: successResponse(studentResponseSchema) } },
    },
    400: error("Dados inválidos."),
    409: error("E-mail, RA ou CPF já cadastrado."),
  },
});

// ─── POST /auth/register/company ──────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/auth/register/company",
  tags: [TAG],
  summary: "Registra uma empresa e seu membro administrador.",
  description:
    "Cria Address + Company(PENDING) + User(COMPANY, isActive:false) + CompanyMember(ADMIN). " +
    "Não retorna token: a empresa aguarda aprovação.",
  request: { body: jsonBody(registerCompanySchema) },
  responses: {
    201: {
      description: "Cadastro enviado. Aguardando aprovação.",
      content: { "application/json": { schema: successResponse(companyResponseSchema) } },
    },
    400: error("Dados inválidos."),
    409: error("E-mail, CNPJ ou CPF já cadastrado."),
  },
});

// ─── POST /auth/login ─────────────────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/auth/login",
  tags: [TAG],
  summary: "Autentica e inicia o fluxo de acesso.",
  description:
    "STUDENT/ADMIN recebem JWT completo. COMPANY recebe um tempToken (5min) e segue para o " +
    "fluxo TOTP. No primeiro acesso (TOTP_SETUP) o QR code ja vem na resposta e a empresa " +
    "confirma em /totp/setup/confirm; nos acessos seguintes (TOTP_REQUIRED) basta /totp/verify.",
  request: { body: jsonBody(loginSchema) },
  responses: {
    200: {
      description: "Login processado.",
      content: { "application/json": { schema: successResponse(loginResponse) } },
    },
    401: error("Credenciais inválidas."),
    403: error("Empresa aguardando aprovação ou conta desativada."),
  },
});

// ─── POST /auth/totp/setup/confirm ────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/auth/totp/setup/confirm",
  tags: [TAG],
  security: bearerAuth,
  summary: "Confirma o setup do TOTP e ativa a MFA.",
  request: { body: jsonBody(totpCodeSchema) },
  responses: {
    200: {
      description: "TOTP configurado; retorna JWT completo.",
      content: { "application/json": { schema: successResponse(tokenResponse) } },
    },
    400: error("TOTP não iniciado."),
    401: error("Código inválido."),
  },
});

// ─── POST /auth/totp/verify ───────────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/auth/totp/verify",
  tags: [TAG],
  security: bearerAuth,
  summary: "Verifica o código TOTP em logins subsequentes.",
  request: { body: jsonBody(totpCodeSchema) },
  responses: {
    200: {
      description: "Verificação concluída; retorna JWT completo.",
      content: { "application/json": { schema: successResponse(tokenResponse) } },
    },
    400: error("TOTP não iniciado."),
    401: error("Código inválido."),
  },
});

// ─── GET /auth/me ─────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/auth/me",
  tags: [TAG],
  security: bearerAuth,
  summary: "Retorna o perfil do usuário autenticado.",
  description: "Conteúdo varia por role (STUDENT, COMPANY, ADMIN). Nunca expõe password/totpSecret.",
  responses: {
    200: {
      description: "Perfil do usuário.",
      content: { "application/json": { schema: successResponse(z.unknown()) } },
    },
    401: error("Token inválido ou expirado."),
    404: error("Perfil não encontrado."),
  },
});
