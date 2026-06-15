import { bearerAuth, errorResponse, paginatedResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";
import { jobIdParamsSchema, listJobsQuerySchema } from "./jobs.schema";

const TAG = "Jobs";

const error = (description: string) => ({
  description,
  content: { "application/json": { schema: errorResponse } },
});

// ─── Doc schemas (saída) ──────────────────────────────────────────────────────

const jobListItem = z
  .object({
    id: z.uuid(),
    title: z.string(),
    area: z.string(),
    location: z.string(),
    modality: z.enum(["PRESENCIAL", "REMOTE", "HYBRID"]),
    salary: z.number().nullable(),
    company: z.object({ id: z.uuid(), name: z.string() }),
    course: z.object({ id: z.uuid(), name: z.string() }).nullable(),
  })
  .openapi({ title: "PublicJobListItem" });

const jobDetail = z
  .object({
    id: z.uuid(),
    title: z.string(),
    description: z.string(),
    area: z.string(),
    requirements: z.string().nullable(),
    salary: z.number().nullable(),
    location: z.string(),
    modality: z.enum(["PRESENCIAL", "REMOTE", "HYBRID"]),
    company: z.object({ id: z.uuid(), name: z.string(), description: z.string() }),
  })
  .openapi({ title: "PublicJobDetail" });

const applicationOut = z.object({ id: z.uuid(), status: z.string() }).openapi({ title: "JobApplicationResult" });

// ─── GET /jobs ─────────────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/jobs",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista vagas (ACTIVE de empresas APPROVED).",
  description: "Filtros opcionais: courseId, area, modality, search. Requer autenticação.",
  request: { query: listJobsQuerySchema },
  responses: {
    200: {
      description: "Lista paginada de vagas.",
      content: { "application/json": { schema: paginatedResponse(jobListItem) } },
    },
    401: error("Token inválido ou expirado."),
  },
});

// ─── GET /jobs/:jobId ──────────────────────────────────────────────────────────
registry.registerPath({
  method: "get",
  path: "/jobs/{jobId}",
  tags: [TAG],
  security: bearerAuth,
  summary: "Detalha uma vaga.",
  request: { params: jobIdParamsSchema },
  responses: {
    200: {
      description: "Vaga encontrada.",
      content: { "application/json": { schema: successResponse(jobDetail) } },
    },
    401: error("Token inválido ou expirado."),
    404: error("Vaga não encontrada."),
  },
});

// ─── POST /jobs/:jobId/apply ───────────────────────────────────────────────────
registry.registerPath({
  method: "post",
  path: "/jobs/{jobId}/apply",
  tags: [TAG],
  security: bearerAuth,
  summary: "Candidata o estudante autenticado a uma vaga.",
  description: "Requer role STUDENT com endereco cadastrado. A candidatura usa os dados do perfil do aluno.",
  request: { params: jobIdParamsSchema },
  responses: {
    201: {
      description: "Candidatura enviada.",
      content: { "application/json": { schema: successResponse(applicationOut) } },
    },
    400: error("Vaga indisponivel ou endereco ausente."),
    401: error("Token inválido ou expirado."),
    403: error("Estudante inelegível."),
    409: error("Você já se candidatou a esta vaga."),
  },
});
