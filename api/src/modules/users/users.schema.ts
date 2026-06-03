import { z } from "../../lib/zod";

const passwordSchema = z
  .string()
  .min(8, "A senha deve ter no mínimo 8 caracteres.")
  .max(100, "A senha deve ter no máximo 100 caracteres.")
  .regex(
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#])[A-Za-z\d@$!%*?&#]+$/,
    "A senha deve conter letras maiúsculas, minúsculas, números e um caractere especial (@$!%*?&#).",
  );

export const createUserSchema = z
  .object({
    name: z
      .string()
      .min(2, "O nome deve ter no mínimo 2 caracteres.")
      .max(100, "O nome deve ter no máximo 100 caracteres."),
    email: z.email("E-mail inválido."),
    password: passwordSchema,
    role: z.enum(["ADMIN", "USER"]).default("USER"),
  })
  .openapi({ title: "CreateUser" });

export const updateUserSchema = z
  .object({
    name: z
      .string()
      .min(2, "O nome deve ter no mínimo 2 caracteres.")
      .max(100, "O nome deve ter no máximo 100 caracteres.")
      .optional(),
    email: z.email("E-mail inválido.").optional(),
  })
  .openapi({ title: "UpdateUser" });

export const changePasswordSchema = z
  .object({
    currentPassword: z.string().min(1, "A senha atual é obrigatória."),
    newPassword: passwordSchema,
    confirmPassword: z.string().min(1, "A confirmação de senha é obrigatória."),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "As senhas não coincidem.",
    path: ["confirmPassword"],
  })
  .refine((data) => data.newPassword !== data.currentPassword, {
    message: "A nova senha não pode ser igual à senha atual.",
    path: ["newPassword"],
  })
  .openapi({ title: "ChangePassword" });

export const updateRoleSchema = z
  .object({
    role: z.enum(["ADMIN", "USER"]),
  })
  .openapi({ title: "UpdateRole" });

export const updateStatusSchema = z
  .object({
    isActive: z.boolean(),
  })
  .openapi({ title: "UpdateStatus" });

export const userResponseSchema = z
  .object({
    id: z.uuid(),
    name: z.string(),
    email: z.email(),
    role: z.enum(["ADMIN", "USER"]),
    isActive: z.boolean(),
    createdAt: z.date(),
    updatedAt: z.date(),
  })
  .openapi({ title: "UserResponse" });

export type CreateUserInput = z.infer<typeof createUserSchema>;
export type UpdateUserInput = z.infer<typeof updateUserSchema>;
export type ChangePasswordInput = z.infer<typeof changePasswordSchema>;
export type UpdateRoleInput = z.infer<typeof updateRoleSchema>;
export type UpdateStatusInput = z.infer<typeof updateStatusSchema>;
export type UserResponse = z.infer<typeof userResponseSchema>;
