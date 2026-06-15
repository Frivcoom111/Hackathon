import cors from "cors";
import express, { type Express } from "express";
import helmet from "helmet";
import path from "node:path";
import { env } from "./config/env";
import { setupDocs } from "./docs/docs";
import addressRoutes from "./modules/address/address.routes";
import authRoutes from "./modules/auth/auth.routes";
import companiesRoutes from "./modules/companies/companies.routes";
import companyRoutes from "./modules/company/company.routes";
import coursesRoutes from "./modules/courses/courses.routes";
import jobsRoutes from "./modules/jobs/jobs.routes";
import notificationRoutes from "./modules/notification/notification.routes";
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
  // Em desenvolvimento aceita qualquer origem para facilitar testes locais
  app.use(
    cors({
      origin: env.NODE_ENV === "development" ? "*" : env.FRONTEND_URL,
      methods: ["GET", "POST", "PATCH", "PUT", "DELETE"],
    }),
  );

  app.use(globalRateLimiter);
  app.use("/uploads", express.static(path.resolve("uploads")));

  app.use("/auth", authRoutes);
  app.use("/courses", coursesRoutes);
  app.use("/jobs", jobsRoutes);
  app.use("/companies", companiesRoutes);
  app.use("/company", companyRoutes);
  app.use("/student", studentRoutes);
  app.use("/address", addressRoutes);
  app.use("/notifications", notificationRoutes);
  app.use("/users", usersRoutes);

  setupDocs(app);

  app.use(errorHandler);

  return app;
};
