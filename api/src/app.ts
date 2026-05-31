import express, { type Express } from "express";
import { errorHandler } from "./shared/middlewares/errorHandler";

export const appBuild = async (): Promise<Express> => {
  const app = express();
  app.use(express.json());

  app.use(errorHandler);

  return app;
};
