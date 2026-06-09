import type { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { env } from "../../config/env";
import { ForbiddenError, UnauthorizedError } from "../errors/AppError";
import type { JwtPayload } from "../utils/generateToken";

export const authMiddleware = async (req: Request, _res: Response, next: NextFunction) => {
  try {
    const authorization = req.headers.authorization;

    if (!authorization?.startsWith("Bearer ")) {
      throw new UnauthorizedError("Token não fornecido.");
    }

    const token = authorization.split(" ")[1] as string;

    const decoded = jwt.verify(token, env.JWT_SECRET) as JwtPayload;

    req.user = {
      id: decoded.sub,
      email: decoded.email,
      role: decoded.role,
      mfaVerified: decoded.mfaVerified,
    };

    next();
  } catch (error) {
    if (error instanceof jwt.JsonWebTokenError) {
      return next(new UnauthorizedError("Token inválido ou expirado."));
    }
    next(error);
  }
};

// Exige usuário ADMIN com MFA (TOTP) já verificada no token.
export const requireAdmin = async (req: Request, _res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      throw new UnauthorizedError("Token não fornecido.");
    }

    if (req.user.role !== "ADMIN" || !req.user.mfaVerified) {
      throw new ForbiddenError("Acesso restrito a administradores com verificação MFA.");
    }

    next();
  } catch (error) {
    next(error);
  }
};
