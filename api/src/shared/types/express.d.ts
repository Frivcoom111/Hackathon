import "express";

declare global {
  namespace Express {
    // Augmenta o `Express.User` usado tanto pelo Passport quanto pelo `req.user`.
    interface User {
      id: string;
      email: string;
      role: "USER" | "ADMIN";
      mfaVerified: boolean;
    }
  }
}
