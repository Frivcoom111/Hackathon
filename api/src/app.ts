import cors from "cors";
import express, { type Express } from "express";
import helmet from "helmet";
import { env } from "./config/env";
import { setupDocs } from "./docs/docs";
import usersRoutes from "./modules/users/users.routes";
import { errorHandler } from "./shared/middlewares/errorHandler.middlewares";

export const appBuild = async (): Promise<Express> => {
  const app = express();
  app.use(express.json());

  app.use(helmet());
  app.use(
    cors({
      origin: env.FRONTEND_URL,
      methods: ["GET", "POST", "PATCH", "PUT", "DELETE"],
    }),
  );

  app.use("/users", usersRoutes);

  setupDocs(app);

  app.use(errorHandler);

  return app;
};
