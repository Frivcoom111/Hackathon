import cors from "cors";
import express, { type Express } from "express";
import helmet from "helmet";
import { env } from "./config/env";
import { setupDocs } from "./docs/docs";
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

  app.use("/users", usersRoutes);

  setupDocs(app);

  app.use(errorHandler);

  return app;
};
