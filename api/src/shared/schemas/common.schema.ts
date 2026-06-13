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

// ─── CPF / CNPJ / Telefone / Endereço ────────────────────────────────────────────────

export const cpfSchema = z.string().regex(/^\d{11}$/, "CPF deve conter 11 dígitos numéricos.");

export const cnpjSchema = z.string().regex(/^\d{14}$/, "CNPJ deve conter 14 dígitos numéricos.");

export const phoneSchema = z.string().regex(/^\d{10,11}$/, "Telefone deve conter 10 ou 11 dígitos numéricos.");

export const addressSchema = z
  .object({
    street: z.string().min(1, "A rua é obrigatória.").max(150),
    number: z.string().min(1, "O número é obrigatório.").max(10),
    complement: z.string().max(100).optional(),
    district: z.string().min(1, "O bairro é obrigatório.").max(100),
    city: z.string().min(1, "A cidade é obrigatória.").max(100),
    state: z.string().length(2, "A UF deve ter 2 letras."),
    zipCode: z.string().regex(/^\d{8}$/, "CEP deve conter 8 dígitos numéricos."),
  })
  .openapi({ title: "Address" });

// ─── Inferência de Types ──────────────────────────────────────────────────────────

export type IdParams = z.infer<typeof idParamsSchema>;
export type PaginationQuery = z.infer<typeof paginationQuerySchema>;
