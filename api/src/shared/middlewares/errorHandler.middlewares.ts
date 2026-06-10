import type { NextFunction, Request, Response } from "express";
import { MulterError } from "multer";
import { ZodError } from "zod";
import { AppError } from "../errors/AppError";
import { response } from "../utils/response";

export const errorHandler = (error: Error, _req: Request, res: Response, _next: NextFunction) => {
  if (error instanceof AppError) {
    res.status(error.statusCode).json(response.error(error.message));
    return;
  }

  if (error instanceof MulterError) {
    const message =
      error.code === "LIMIT_FILE_SIZE"
        ? "Arquivo excede o tamanho máximo de 5MB."
        : "Erro no upload do arquivo.";
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
