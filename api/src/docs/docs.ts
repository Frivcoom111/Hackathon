import { apiReference } from "@scalar/express-api-reference";
import type { Router } from "express";
import { generateSpec } from "./openapi";

export function setupDocs(app: Router) {
  const spec = generateSpec();
  app.use("/docs", apiReference({ spec: { content: spec } }));
}
