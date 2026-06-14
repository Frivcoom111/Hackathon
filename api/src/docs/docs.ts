import { apiReference } from "@scalar/express-api-reference";
import type { Router } from "express";
import { env } from "../config/env";

// Importa os módulos de documentação para registrar suas rotas no `registry`.
import "../modules/address/address.docs";
import "../modules/auth/auth.docs";
import "../modules/company/company.docs";
import "../modules/jobs/jobs.docs";
import "../modules/student/student.docs";
import { generateSpec } from "./openapi";

export function setupDocs(app: Router) {
  // Documentação disponível apenas em ambiente de desenvolvimento.
  if (env.NODE_ENV !== "development") return;

  const spec = generateSpec();
  app.use("/docs", apiReference({ spec: { content: spec } }));
}
