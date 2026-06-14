import { z } from "../../lib/zod";
import { addressSchema, cnpjSchema, cpfSchema, passwordSchema, phoneSchema } from "../../shared/schemas/common.schema";

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
    ra: z.string().min(1, "O RA é obrigatório.").max(20, "O RA deve ter no máximo 20 caracteres."),
    cpf: cpfSchema,
    phone: z.string().min(8).max(20).optional(),
    resumePath: z.string().optional(),
    courseId: z.uuid("Curso inválido."),
    status: studentCourseStatusSchema.default("ACTIVE"),
    startedAt: z.coerce.date(),
    finishedAt: z.coerce.date().optional(),
  })
  .openapi({ title: "RegisterStudent" });

export const registerCompanySchema = z
  .object({
    email: z.email("E-mail inválido."),
    password: passwordSchema,
    name: z.string().min(2, "O nome deve ter no mínimo 2 caracteres.").max(150),
    cnpj: cnpjSchema,
    description: z.string().min(1, "A descrição é obrigatória.").max(2000),
    phone: phoneSchema,
    address: addressSchema,
    member: z.object({
      name: z.string().min(2, "O nome do responsável é obrigatório.").max(100),
      cpf: cpfSchema,
      phone: phoneSchema.optional(),
    }),
  })
  .openapi({ title: "RegisterCompany" });

export const loginSchema = z
  .object({
    email: z.email("E-mail inválido."),
    password: z.string().min(1, "A senha é obrigatória."),
  })
  .openapi({ title: "Login" });

export const totpCodeSchema = z
  .object({
    code: z.string().regex(/^\d{6}$/, "O código deve conter 6 dígitos."),
  })
  .openapi({ title: "TotpCode" });

// ─── Response Schemas ─────────────────────────────────────────────────────────

export const studentResponseSchema = z
  .object({
    userId: z.uuid(),
    name: z.string(),
    ra: z.string(),
    phone: z.string().nullable(),
    resumePath: z.string().nullable(),
  })
  .openapi({ title: "StudentResponse" });

export const companyResponseSchema = z
  .object({
    id: z.uuid(),
    name: z.string(),
    cnpj: z.string(),
    status: z.enum(["PENDING", "ANALYSING", "APPROVED", "BLOCKED"]),
    members: z.array(
      z.object({
        userId: z.uuid(),
        name: z.string(),
        role: z.enum(["ADMIN", "RECRUITER"]),
      }),
    ),
  })
  .openapi({ title: "CompanyResponse" });

// ─── Inferência de Types ────────────────────────────────────────────────────────

export type RegisterStudentInput = z.infer<typeof registerStudentSchema>;
export type RegisterCompanyInput = z.infer<typeof registerCompanySchema>;
export type LoginInput = z.infer<typeof loginSchema>;
export type TotpCodeInput = z.infer<typeof totpCodeSchema>;
export type StudentResponse = z.infer<typeof studentResponseSchema>;
export type CompanyResponse = z.infer<typeof companyResponseSchema>;
