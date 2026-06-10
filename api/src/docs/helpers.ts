import type { ZodType } from "zod";
import { z } from "../lib/zod";

// ─── Envelopes de Resposta ──────────────────────────────────────────────────────
// Espelham os formatos produzidos por `shared/utils/response`.

export const successResponse = (data: ZodType) =>
  z.object({
    success: z.literal(true),
    message: z.string(),
    data,
  });

export const paginatedResponse = (item: ZodType) =>
  z.object({
    success: z.literal(true),
    data: z.array(item),
    meta: z.object({
      page: z.number(),
      limit: z.number(),
      total: z.number(),
      totalPages: z.number(),
    }),
  });

export const errorResponse = z
  .object({
    success: z.literal(false),
    message: z.string(),
    details: z.unknown().optional(),
    code: z.string().optional(),
  })
  .openapi({ title: "ErrorResponse" });

export const bearerAuth = [{ bearerAuth: [] }];
