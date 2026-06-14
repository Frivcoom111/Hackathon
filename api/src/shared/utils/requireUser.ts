import type { Request } from "express";
import { UnauthorizedError } from "../errors/AppError";

// authMiddleware já popula req.user; este guard satisfaz o tipo e é defesa em profundidade.
export function requireUser(req: Request) {
  if (!req.user) throw new UnauthorizedError("Token não fornecido.");
  return req.user;
}
