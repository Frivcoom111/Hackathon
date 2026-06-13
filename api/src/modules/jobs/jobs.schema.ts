import { z } from "../../lib/zod";

// ─── Params ─────────────────────────────────────────────────────────────────

export const jobIdParamsSchema = z.object({ jobId: z.uuid("Vaga inválida.") }).openapi({ title: "PublicJobIdParams" });

// ─── Query (listagem pública) ─────────────────────────────────────────────────

export const listJobsQuerySchema = z
  .object({
    page: z.coerce.number().min(1).default(1),
    limit: z.coerce.number().min(1).max(100).default(10),
    courseId: z.uuid().optional(),
    area: z.string().min(1).max(100).optional(),
    modality: z.enum(["PRESENCIAL", "REMOTE", "HYBRID"]).optional(),
    search: z.string().min(1).max(150).optional(),
  })
  .openapi({ title: "ListJobsQuery" });

// ─── Body (candidatura) ───────────────────────────────────────────────────────

export const applyJobSchema = z
  .object({
    coverLetter: z.string().max(2000).optional(),
  })
  .openapi({ title: "ApplyJob" });

// ─── Inferência de Types ──────────────────────────────────────────────────────

export type ListJobsQuery = z.infer<typeof listJobsQuerySchema>;
export type ApplyJobInput = z.infer<typeof applyJobSchema>;
