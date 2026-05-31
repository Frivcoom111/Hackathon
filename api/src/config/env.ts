import { z } from "zod";
import "dotenv/config";

const envSchema = z.object({
  PORT: z.coerce.number().default(3000),
  NODE_ENV: z.enum(["development", "production"]).default("development"),
  DATABASE_URL: z.string().url("DATABASE_URL inválida."),
  JWT_SECRET: z.string().min(32, "JWT_SECRET mínimo de 32 caracteres."),
  JWT_EXPIRES_IN: z.string().default("1d"),
  SALT: z.coerce.number().default(10),
  CORS_ORIGIN: z.string().url("CORS_ORIGIN inválido.").default("*"),
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
