import jwt, { type SignOptions } from "jsonwebtoken";
import { env } from "../../config/env";

export const generateToken = (id: string) => {
  return jwt.sign({ id: id }, env.JWT_SECRET, {
    expiresIn: env.JWT_EXPIRES_IN as SignOptions["expiresIn"],
  });
};
