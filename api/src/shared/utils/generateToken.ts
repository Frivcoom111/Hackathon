import jwt, { type SignOptions } from "jsonwebtoken";
import { env } from "../../config/env";

export interface JwtPayload {
  sub: string;
  email: string;
  role: "USER" | "ADMIN";
  mfaVerified: boolean;
}

export const generateToken = (payload: JwtPayload): string => {
  return jwt.sign(payload, env.JWT_SECRET, {
    expiresIn: env.JWT_EXPIRES_IN as SignOptions["expiresIn"],
  });
};
