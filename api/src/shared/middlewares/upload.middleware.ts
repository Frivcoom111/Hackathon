import { randomUUID } from "node:crypto";
import fs from "node:fs";
import path from "node:path";
import multer from "multer";
import { BadRequestError } from "../errors/AppError";

const COVER_DIR = path.join("uploads", "covers");
const RESUME_DIR = path.join("uploads", "resumes");

for (const dir of [COVER_DIR, RESUME_DIR]) {
  fs.mkdirSync(dir, { recursive: true });
}

const IMAGE_MIME_TO_EXT: Record<string, string> = {
  "image/jpeg": ".jpg",
  "image/png": ".png",
  "image/webp": ".webp",
};

// Currículo aceita PDF além das imagens.
const RESUME_MIME_TO_EXT: Record<string, string> = {
  "application/pdf": ".pdf",
  "image/jpeg": ".jpg",
  "image/png": ".png",
};

const makeStorage = (destination: string, mimeMap: Record<string, string>) =>
  multer.diskStorage({
    destination: (_req, _file, cb) => {
      cb(null, destination);
    },
    filename: (_req, file, cb) => {
      cb(null, `${randomUUID()}${mimeMap[file.mimetype]}`);
    },
  });

const makeUpload = (destination: string, mimeMap: Record<string, string>, errorMessage: string) =>
  multer({
    storage: makeStorage(destination, mimeMap),
    limits: {
      fileSize: 5 * 1024 * 1024,
    },
    fileFilter: (_req, file, cb) => {
      if (!(file.mimetype in mimeMap)) {
        cb(new BadRequestError(errorMessage));
        return;
      }

      cb(null, true);
    },
  });

export const uploadCoverPhoto = makeUpload(
  COVER_DIR,
  IMAGE_MIME_TO_EXT,
  "Tipo de imagem invalido. Envie JPG, PNG ou WEBP.",
);

export const uploadResume = makeUpload(
  RESUME_DIR,
  RESUME_MIME_TO_EXT,
  "Tipo de arquivo invalido. Envie PDF, JPG ou PNG.",
);
