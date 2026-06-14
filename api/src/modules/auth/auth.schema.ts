import { z } from "../../lib/zod";
import { passwordSchema } from "../../shared/schemas/common.schema";

const onlyDigits = (value: string) => value.replace(/\D/g, "");

export const loginSchema = z.object({
  email: z.email("E-mail invalido."),
  password: z.string().min(1, "Senha obrigatoria."),
});

export const registerStudentSchema = z.object({
  email: z.email("E-mail invalido."),
  password: passwordSchema,
  name: z.string().min(2, "Informe o nome completo."),
  ra: z.string().min(1, "Informe o RA."),
  cpf: z.string().transform(onlyDigits).pipe(z.string().length(11, "CPF deve ter 11 digitos.")),
  phone: z
    .string()
    .optional()
    .transform((value) => (value ? onlyDigits(value) : undefined)),
  courseId: z.uuid("Curso invalido."),
  status: z.enum(["ACTIVE", "COMPLETED", "CANCELLED"]).default("ACTIVE"),
  startedAt: z.coerce.date(),
  finishedAt: z.preprocess((value) => (value === "" ? undefined : value), z.coerce.date().optional()),
  resumePath: z.string().optional(),
});

export const registerCompanySchema = z.object({
  email: z.email("E-mail invalido."),
  password: passwordSchema,
  name: z.string().min(2, "Informe o nome da empresa."),
  cnpj: z.string().transform(onlyDigits).pipe(z.string().length(14, "CNPJ deve ter 14 digitos.")),
  description: z.string().optional(),
  phone: z
    .string()
    .optional()
    .transform((value) => (value ? onlyDigits(value) : undefined)),
  address: z.object({
    street: z.string().min(1, "Informe a rua."),
    number: z.string().min(1, "Informe o numero."),
    complement: z.string().optional(),
    district: z.string().min(1, "Informe o bairro."),
    city: z.string().min(1, "Informe a cidade."),
    state: z.string().length(2, "UF deve ter 2 letras.").transform((value) => value.toUpperCase()),
    zipCode: z.string().min(8, "Informe o CEP."),
  }),
  member: z.object({
    name: z.string().min(2, "Informe o responsavel."),
    cpf: z.string().transform(onlyDigits).pipe(z.string().length(11, "CPF do responsavel deve ter 11 digitos.")),
    phone: z
      .string()
      .optional()
      .transform((value) => (value ? onlyDigits(value) : undefined)),
  }),
});

export type LoginInput = z.infer<typeof loginSchema>;
export type RegisterStudentInput = z.infer<typeof registerStudentSchema>;
export type RegisterCompanyInput = z.infer<typeof registerCompanySchema>;
