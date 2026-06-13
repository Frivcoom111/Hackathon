import jwt, { type SignOptions } from "jsonwebtoken";
import { env } from "../../config/env";
import type { CompanyMemberRole, Role } from "../../generated/prisma/enums";

export interface JwtPayload {
  sub: string;
  email: string;
  role: Role;
  mfaVerified: boolean;
  companyMemberRole?: CompanyMemberRole;
}

// expiresIn permite emitir tokens temporários (ex.: fluxo TOTP usa "5m");
// quando omitido, usa o padrão do ambiente (JWT_EXPIRES_IN).
export const generateToken = (
  payload: JwtPayload,
  expiresIn: SignOptions["expiresIn"] = env.JWT_EXPIRES_IN as SignOptions["expiresIn"],
): string => {
  return jwt.sign(payload, env.JWT_SECRET, { expiresIn });
};
