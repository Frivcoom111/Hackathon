import { randomUUID } from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import multer from "multer";
import { BadRequestError } from "../errors/AppError";

const RESUMES_DIR = path.resolve("uploads", "resumes");

// Garante que a pasta de destino exista no carregamento do módulo.
fs.mkdirSync(RESUMES_DIR, { recursive: true });

// Tipos aceitos: PDF, JPEG e PNG.
const ALLOWED_MIME_TYPES = new Set([
  "application/pdf",
  "image/jpeg", 
  "image/png",
]);

// Definição da onde ficará armazenado o arquivo e com qual nome.
const storage = multer.diskStorage({
  destination: (_req, _file, cb) => {
    cb(null, RESUMES_DIR);
  },
  filename: (_req, file, cb) => {
    cb(null, `${randomUUID()}${path.extname(file.originalname)}`);
  },
});

// Middleware para usar na request da API.
export const uploadResume = multer({
  storage,
  limits: {
    fileSize: 5 * 1024 * 1024, // 5MB
  },
  fileFilter: (_req, file, cb) => {
    if (!ALLOWED_MIME_TYPES.has(file.mimetype)) {
      cb(new BadRequestError("Tipo de arquivo inválido. Envie PDF, JPG ou PNG."));
      return;
    }

    console.log(file.originalname, file.mimetype);

    cb(null, true);
  },
});
