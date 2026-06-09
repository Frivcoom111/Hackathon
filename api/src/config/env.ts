import { z } from "zod";
import "dotenv/config";

const envSchema = z.object({
  PORT: z.coerce.number().default(3000),
  NODE_ENV: z.enum(["development", "production"]).default("development"),
  DATABASE_URL: z.string().url("DATABASE_URL inválida"),
  DATABASE_HOST: z.string().min(1, "DATABASE_HOST é obrigatório."),
  DATABASE_USER: z.string().min(1, "DATABASE_USER é obrigatório."),
  DATABASE_PASSWORD: z.string().min(1, "DATABASE_PASSWORD é obrigatório."),
  DATABASE_NAME: z.string().min(1, "DATABASE_NAME é obrigatório."),
  JWT_SECRET: z.string().min(32, "JWT_SECRET mínimo de 32 caracteres."),
  JWT_EXPIRES_IN: z.string().default("1d"),
  SALT: z.coerce.number().default(10),
  FRONTEND_URL: z.string().default("*"),
});

const parsed = envSchema.safeParse(process.env);

if (!parsed.success) {
  console.error({
    message: "Environments incompleto",
    error: parsed.error.flatten().fieldErrors,
  });
  process.exit(1);
}

export const env = parsed.data;
export type Env = z.infer<typeof envSchema>;
