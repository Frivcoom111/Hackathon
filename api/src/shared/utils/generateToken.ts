import jwt, { type SignOptions } from "jsonwebtoken";
import { env } from "../../config/env";
import type { Role } from "../../generated/prisma/enums";

export interface JwtPayload {
  sub: string;
  email: string;
  role: Role;
  mfaVerified: boolean;
}

export const generateToken = (payload: JwtPayload): string => {
  return jwt.sign(payload, env.JWT_SECRET, {
    expiresIn: env.JWT_EXPIRES_IN as SignOptions["expiresIn"],
  });
};
