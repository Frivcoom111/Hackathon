import fs from "node:fs";
import type { NextFunction, Request, Response } from "express";
import { MulterError } from "multer";
import { ZodError } from "zod";
import { AppError } from "../errors/AppError";
import { response } from "../utils/response";

export const errorHandler = (error: Error, req: Request, res: Response, _next: NextFunction) => {
  // Remove arquivos já salvos pelo multer quando a request falha (evita órfãos em disco).
  const uploaded = req.file ? [req.file] : Object.values(req.files ?? {}).flat();
  for (const file of uploaded) {
    if (file?.path) fs.unlink(file.path, () => {});
  }

  if (error instanceof AppError) {
    res.status(error.statusCode).json(response.error(error.message));
    return;
  }

  if (error instanceof MulterError) {
    const message =
      error.code === "LIMIT_FILE_SIZE" ? "Arquivo excede o tamanho máximo de 5MB." : "Erro no upload do arquivo.";
    res.status(400).json(response.error(message));
    return;
  }

  if (error instanceof ZodError) {
    res.status(400).json(response.error("Dados inválidos.", error.issues));
    return;
  }

  console.error(error);
  res.status(500).json(response.error("Erro interno do servidor."));
};
