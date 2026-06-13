import cors from "cors";
import express, { type Express } from "express";
import helmet from "helmet";
import { env } from "./config/env";
import { setupDocs } from "./docs/docs";
import authRoutes from "./modules/auth/auth.routes";
import companyRoutes from "./modules/company/company.routes";
import jobsRoutes from "./modules/jobs/jobs.routes";
import studentRoutes from "./modules/student/student.routes";
import usersRoutes from "./modules/users/users.routes";
import { errorHandler } from "./shared/middlewares/errorHandler.middlewares";
import { globalRateLimiter } from "./shared/middlewares/rateLimit.middleware";

export const appBuild = async (): Promise<Express> => {
  const app = express();
  app.use(express.json());

  app.use(
    helmet({
      contentSecurityPolicy: env.NODE_ENV === "development" ? false : undefined,
    }),
  );
  app.use(
    cors({
      origin: env.FRONTEND_URL,
      methods: ["GET", "POST", "PATCH", "PUT", "DELETE"],
    }),
  );

  app.use(globalRateLimiter);

  app.use("/auth", authRoutes);
  app.use("/users", usersRoutes);
  app.use("/company", companyRoutes);
  app.use("/student", studentRoutes);
  app.use("/jobs", jobsRoutes);

  setupDocs(app);

  app.use(errorHandler);

  return app;
};
