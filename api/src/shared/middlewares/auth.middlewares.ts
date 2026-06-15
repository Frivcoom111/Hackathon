import type { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { env } from "../../config/env";
import { CompanyMemberRole, Role } from "../../generated/prisma/enums";
import { prisma } from "../../lib/prisma";
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

    const dbUser = await prisma.user.findUnique({
      where: { id: decoded.sub },
      select: { isActive: true },
    });

    if (!dbUser?.isActive) {
      throw new ForbiddenError("Conta inativa. Aguarde a aprovacao da empresa.");
    }

    req.user = {
      id: decoded.sub,
      email: decoded.email,
      role: decoded.role,
      mfaVerified: decoded.mfaVerified,
      companyMemberRole: decoded.companyMemberRole,
    };

    next();
  } catch (error) {
    if (error instanceof jwt.JsonWebTokenError) {
      return next(new UnauthorizedError("Token inválido ou expirado."));
    }
    next(error);
  }
};

// Exige role específica e, opcionalmente, MFA verificada.
export const requireCompanyAdmin = (req: Request, _res: Response, next: NextFunction) => {
  try {
    if (!req.user) {
      throw new UnauthorizedError("Token nao fornecido.");
    }

    if (req.user.role !== Role.COMPANY || !req.user.mfaVerified) {
      throw new ForbiddenError();
    }

    if (req.user.companyMemberRole !== CompanyMemberRole.ADMIN) {
      throw new ForbiddenError("Apenas administradores da empresa podem executar esta acao.");
    }

    next();
  } catch (error) {
    next(error);
  }
};

const requireRole = (role: Role, options: { mfa: boolean }) => {
  return (req: Request, _res: Response, next: NextFunction) => {
    try {
      if (!req.user) {
        throw new UnauthorizedError("Token não fornecido.");
      }

      if (req.user.role !== role) {
        throw new ForbiddenError();
      }

      if (options.mfa && !req.user.mfaVerified) {
        throw new ForbiddenError("Verificação MFA obrigatória.");
      }

      next();
    } catch (error) {
      next(error);
    }
  };
};

export const requireAdmin = requireRole(Role.ADMIN, { mfa: true });
export const requireCompany = requireRole(Role.COMPANY, { mfa: true });
export const requireStudent = requireRole(Role.STUDENT, { mfa: false });
