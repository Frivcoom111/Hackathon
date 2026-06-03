import type { NextFunction, Request, Response } from "express";
import jwt from "jsonwebtoken";
import { env } from "../../config/env";
import { AppError } from "../errors/AppError";

export const authMiddleware = async (req: Request, _res: Response, next: NextFunction) => {
  try {
    const heardersAuthorization = req.headers.authorization as string;

    if (!heardersAuthorization?.startsWith("Bearer ")) {
      throw new AppError("Token não fornecido", 401);
    }

    const token = heardersAuthorization.split(" ")[1] as string;

    const decoded = jwt.verify(token, env.JWT_SECRET) as jwt.JwtPayload;

    req.user = {
      id: decoded.id,
    };

    next();
  } catch (error) {
    next(error);
  }
};
