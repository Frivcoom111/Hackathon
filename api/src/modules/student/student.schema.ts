import { z } from "../../lib/zod";
import { phoneSchema } from "../../shared/schemas/common.schema";

// ─── Input Schemas ──────────────────────────────────────────────────────────

// name/phone vivem em Student; email vive em User. O service atualiza ambos numa transação.
export const updateStudentProfileSchema = z
  .object({
    name: z.string().min(2, "O nome deve ter no mínimo 2 caracteres.").max(100).optional(),
    phone: phoneSchema.optional(),
    email: z.email("E-mail inválido.").optional(),
  })
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateStudentProfile" });

// ─── Inferência de Types ──────────────────────────────────────────────────────

export type UpdateStudentProfileInput = z.infer<typeof updateStudentProfileSchema>;
