import { randomUUID } from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import multer from "multer";
import { BadRequestError } from "../errors/AppError";

const RESUMES_DIR = path.resolve("uploads", "resumes");

// Garante que o diretório de destino exista (multer não cria recursivamente).
fs.mkdirSync(RESUMES_DIR, { recursive: true });

// Tipos aceitos e a extensão usada ao salvar (derivada do mimetype validado,
// nunca do nome enviado pelo cliente). As chaves funcionam como allowlist.
const MIME_TO_EXT: Record<string, string> = {
  "application/pdf": ".pdf",
  "image/jpeg": ".jpg",
  "image/png": ".png",
};

// Definição de onde ficará armazenado o arquivo e com qual nome.
const storage = multer.diskStorage({
  destination: (_req, _file, cb) => {
    cb(null, RESUMES_DIR);
  },
  filename: (_req, file, cb) => {
    cb(null, `${randomUUID()}${MIME_TO_EXT[file.mimetype]}`);
  },
});

// Middleware para usar na request da API.
export const uploadResume = multer({
  storage,
  limits: {
    fileSize: 5 * 1024 * 1024, // 5MB
  },
  fileFilter: (_req, file, cb) => {
    if (!(file.mimetype in MIME_TO_EXT)) {
      cb(new BadRequestError("Tipo de arquivo inválido. Envie PDF, JPG ou PNG."));
      return;
    }

    cb(null, true);
  },
});
