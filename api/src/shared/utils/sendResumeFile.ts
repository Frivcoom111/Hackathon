import fs from "node:fs";
import path from "node:path";
import type { Response } from "express";
import { NotFoundError } from "../errors/AppError";

// Pasta única onde os currículos vivem. Resolver tudo a partir daqui evita
// path traversal: usamos só o basename do que veio do banco.
const RESUME_DIR = path.resolve("uploads", "resumes");

const CONTENT_TYPE_BY_EXT: Record<string, string> = {
  ".pdf": "application/pdf",
  ".jpg": "image/jpeg",
  ".jpeg": "image/jpeg",
  ".png": "image/png",
};

/**
 * Envia com segurança um currículo salvo em uploads/resumes.
 * - Usa apenas o basename do resumePath (descarta qualquer "../").
 * - Confirma que o caminho resolvido permanece dentro de RESUME_DIR.
 * - 404 se o arquivo não existir em disco.
 */
export function sendResumeFile(res: Response, resumePath: string): void {
  const fileName = path.basename(resumePath);
  const absolutePath = path.resolve(RESUME_DIR, fileName);

  if (!absolutePath.startsWith(RESUME_DIR + path.sep)) {
    throw new NotFoundError("Currículo não encontrado.");
  }

  if (!fs.existsSync(absolutePath)) {
    throw new NotFoundError("Arquivo do currículo não encontrado.");
  }

  const ext = path.extname(absolutePath).toLowerCase();
  const contentType = CONTENT_TYPE_BY_EXT[ext] ?? "application/octet-stream";

  res.setHeader("Content-Type", contentType);
  res.setHeader("Content-Disposition", `inline; filename="${fileName}"`);
  res.sendFile(absolutePath);
}
