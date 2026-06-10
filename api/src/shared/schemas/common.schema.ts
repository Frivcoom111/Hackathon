import { z } from "../../lib/zod";

// ─── Id Params Schema ──────────────────────────────────────────────────────────

export const idParamsSchema = z
  .object({
    id: z.string().uuid(),
  })
  .openapi({ title: "IdParams" });

// ─── Paginated Schema ──────────────────────────────────────────────────────────

export const paginationQuerySchema = z
  .object({
    page: z.coerce.number().min(1).default(1),
    limit: z.coerce.number().min(1).max(100).default(10),
  })
  .openapi({ title: "PaginationQuery" });

// ─── Password Schema ──────────────────────────────────────────────────────────

export const passwordSchema = z
  .string()
  .min(8, "A senha deve ter no mínimo 8 caracteres.")
  .max(100, "A senha deve ter no máximo 100 caracteres.")
  .regex(
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]+$/,
    "A senha deve conter letras maiúsculas, minúsculas, números e um caractere especial (@$!%*?&#).",
  );

// ─── Inferência de Types ──────────────────────────────────────────────────────────

export type IdParams = z.infer<typeof idParamsSchema>;
export type PaginationQuery = z.infer<typeof paginationQuerySchema>;
