import { z } from "../../lib/zod";
import { cpfSchema, passwordSchema } from "../../shared/schemas/common.schema";

// ─── Input Schemas ────────────────────────────────────────────────────────────

const studentCourseStatusSchema = z.enum(["ACTIVE", "COMPLETED", "CANCELLED"]);

export const registerStudentSchema = z
  .object({
    email: z.email("E-mail inválido."),
    password: passwordSchema,
    name: z
      .string()
      .min(2, "O nome deve ter no mínimo 2 caracteres.")
      .max(100, "O nome deve ter no máximo 100 caracteres."),
    ra: z
      .string()
      .min(1, "O RA é obrigatório.")
      .max(20, "O RA deve ter no máximo 20 caracteres."),
    cpf: cpfSchema,
    phone: z.string().min(8).max(20).optional(),
    resumePath: z.string().optional(),
    courseId: z.uuid("Curso inválido."),
    status: studentCourseStatusSchema.default("ACTIVE"),
    startedAt: z.coerce.date(),
    finishedAt: z.coerce.date().optional(),
  })
  .openapi({ title: "RegisterStudent" });

// ─── Response Schema ──────────────────────────────────────────────────────────

export const studentResponseSchema = z
  .object({
    userId: z.uuid(),
    name: z.string(),
    ra: z.string(),
    phone: z.string().nullable(),
    resumePath: z.string().nullable(),
  })
  .openapi({ title: "StudentResponse" });

// ─── Inferência de Types ────────────────────────────────────────────────────────

export type RegisterStudentInput = z.infer<typeof registerStudentSchema>;
export type StudentResponse = z.infer<typeof studentResponseSchema>;
