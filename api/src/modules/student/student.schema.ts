import { z } from "../../lib/zod";
import { addressSchema, phoneSchema } from "../../shared/schemas/common.schema";

// ─── Input Schemas ──────────────────────────────────────────────────────────

export const updateStudentProfileSchema = z
  .object({
    name: z.string().min(2, "O nome deve ter no mínimo 2 caracteres.").max(100).optional(),
    phone: phoneSchema.optional(),
  })
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateStudentProfile" });

// PUT /student/address faz upsert; reaproveita o schema de endereço compartilhado.
export const updateAddressSchema = addressSchema;

// ─── Inferência de Types ──────────────────────────────────────────────────────

export type UpdateStudentProfileInput = z.infer<typeof updateStudentProfileSchema>;
export type UpdateAddressInput = z.infer<typeof updateAddressSchema>;
