import { z } from "../../lib/zod";

// ─── Id Params Schema ──────────────────────────────────────────────────────────

export const idParamsSchema = z
  .object({
    id: z.uuid(),
  })
  .openapi({ title: "IdParams" });

// ─── Paginated Schema ──────────────────────────────────────────────────────────

export const paginationQuerySchema = z
  .object({
    page: z.coerce.number().min(1).default(1),
    limit: z.coerce.number().min(1).max(100).default(10),
  })
  .openapi({ title: "PaginationQuery" });

export type IdParams = z.infer<typeof idParamsSchema>;
export type PaginationQuery = z.infer<typeof paginationQuerySchema>;
