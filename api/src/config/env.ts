import { z } from "zod";
import "dotenv/config";

const envSchema = z.object({
  PORT: z.coerce.number().default(3000),
  NODE_ENV: z.enum(["development", "production"]).default("development"),
  DATABASE_URL: z.string().url("DATABASE_URL invalida."),
  DATABASE_HOST: z.string().min(1, "DATABASE_HOST obrigatorio."),
  DATABASE_PORT: z.coerce.number().default(3306),
  DATABASE_USER: z.string().min(1, "DATABASE_USER obrigatorio."),
  DATABASE_PASSWORD: z.string().default(""),
  DATABASE_NAME: z.string().min(1, "DATABASE_NAME obrigatorio."),
  JWT_SECRET: z.string().min(32, "JWT_SECRET precisa ter pelo menos 32 caracteres."),
  JWT_EXPIRES_IN: z.string().default("1d"),
  SALT: z.coerce.number().default(10),
  FRONTEND_URL: z.string().default("*"),
  RATE_LIMIT_WINDOW_MS: z.coerce.number().default(15 * 60 * 1000),
  RATE_LIMIT_MAX: z.coerce.number().default(100),
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
