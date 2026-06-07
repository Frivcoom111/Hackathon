import express, { type Express } from "express";
import usersRoutes from "./modules/users/users.routes";
import { errorHandler } from "./shared/middlewares/errorHandler.middlewares";

export const appBuild = async (): Promise<Express> => {
  const app = express();
  app.use(express.json());

  app.use("/users", usersRoutes);

  app.use(errorHandler);

  return app;
};
