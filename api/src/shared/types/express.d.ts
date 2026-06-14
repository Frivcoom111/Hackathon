import "express";
import type { CompanyMemberRole, Role } from "../../generated/prisma/enums";

declare global {
  namespace Express {
    interface Request {
      user?: {
        id: string;
        email: string;
        role: Role;
        mfaVerified: boolean;
        companyMemberRole?: CompanyMemberRole;
      };
    }
  }
}
