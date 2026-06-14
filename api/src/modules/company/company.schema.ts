import { z } from "../../lib/zod";
import { cpfSchema, passwordSchema, phoneSchema } from "../../shared/schemas/common.schema";

// ─── Enums ──────────────────────────────────────────────────────────────────

const modalitySchema = z.enum(["PRESENCIAL", "REMOTE", "HYBRID"]);
const jobStatusSchema = z.enum(["ACTIVE", "PAUSED", "CLOSED"]);
const memberRoleSchema = z.enum(["ADMIN", "RECRUITER"]);
const applicationStatusSchema = z.enum(["ANALYSING", "APPROVED", "REJECTED"]);

// ─── Params ─────────────────────────────────────────────────────────────────

export const memberIdParamsSchema = z
  .object({ memberId: z.uuid("Membro inválido.") })
  .openapi({ title: "MemberIdParams" });

export const jobIdParamsSchema = z.object({ jobId: z.uuid("Vaga inválida.") }).openapi({ title: "JobIdParams" });

export const jobApplicationParamsSchema = z
  .object({ jobId: z.uuid("Vaga inválida."), id: z.uuid("Candidatura inválida.") })
  .openapi({ title: "JobApplicationParams" });

// ─── Input Schemas ──────────────────────────────────────────────────────────

// CNPJ não é editável; só name, description e phone.
export const updateCompanyProfileSchema = z
  .object({
    name: z.string().min(2).max(150).optional(),
    description: z.string().min(1).max(2000).optional(),
    phone: z
      .string()
      .regex(/^\d{10,11}$/, "Telefone deve conter 10 ou 11 dígitos.")
      .optional(),
  })
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateCompanyProfile" });

// Admin cria um membro: gera o login (User) + o perfil (CompanyMember) da empresa.
export const createMemberSchema = z
  .object({
    email: z.email("E-mail inválido."),
    password: passwordSchema,
    name: z.string().min(2, "O nome deve ter no mínimo 2 caracteres.").max(100),
    cpf: cpfSchema,
    phone: phoneSchema.optional(),
    role: memberRoleSchema.default("RECRUITER"),
  })
  .openapi({ title: "CreateMember" });

// Admin atualiza outro membro (nome, telefone e/ou cargo).
export const updateMemberSchema = z
  .object({
    name: z.string().min(2).max(100).optional(),
    phone: phoneSchema.optional(),
    role: memberRoleSchema.optional(),
  })
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateMember" });

// Membro autenticado atualiza os próprios dados (nome/telefone no perfil, e-mail no login).
export const updateMyProfileSchema = z
  .object({
    name: z.string().min(2).max(100).optional(),
    phone: phoneSchema.optional(),
    email: z.email("E-mail inválido.").optional(),
  })
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateMyProfile" });

export const createJobSchema = z
  .object({
    title: z.string().min(2, "O título é obrigatório.").max(150),
    description: z.string().min(1, "A descrição é obrigatória."),
    area: z.string().min(1, "A área é obrigatória.").max(100),
    requirements: z.string().optional(),
    salary: z.number().positive("O salário deve ser positivo.").optional(),
    location: z.string().min(1, "A localização é obrigatória.").max(150),
    modality: modalitySchema.default("PRESENCIAL"),
    courseId: z.uuid("Curso inválido.").optional(),
  })
  .openapi({ title: "CreateJob" });

export const updateJobSchema = createJobSchema
  .partial()
  .refine((data) => Object.keys(data).length > 0, { message: "Nenhum dado para atualizar." })
  .openapi({ title: "UpdateJob" });

export const changeJobStatusSchema = z.object({ status: jobStatusSchema }).openapi({ title: "ChangeJobStatus" });

export const changeApplicationStatusSchema = z
  .object({ status: applicationStatusSchema })
  .openapi({ title: "ChangeApplicationStatus" });

// ─── Inferência de Types ──────────────────────────────────────────────────────

export type UpdateCompanyProfileInput = z.infer<typeof updateCompanyProfileSchema>;
export type CreateMemberInput = z.infer<typeof createMemberSchema>;
export type UpdateMemberInput = z.infer<typeof updateMemberSchema>;
export type UpdateMyProfileInput = z.infer<typeof updateMyProfileSchema>;
export type CreateJobInput = z.infer<typeof createJobSchema>;
export type UpdateJobInput = z.infer<typeof updateJobSchema>;
export type ChangeJobStatusInput = z.infer<typeof changeJobStatusSchema>;
export type ChangeApplicationStatusInput = z.infer<typeof changeApplicationStatusSchema>;
