import cors from "cors";
import express, { type Express } from "express";
import session from "express-session";
import helmet from "helmet";
import { env } from "./config/env";
import passport from "./config/passport";
import { setupDocs } from "./docs/docs";
import authRoutes from "./modules/auth/auth.routes";
import usersRoutes from "./modules/users/users.routes";
import { errorHandler } from "./shared/middlewares/errorHandler.middlewares";

export const appBuild = async (): Promise<Express> => {
  const app = express();
  app.use(express.json());

  app.use(helmet());
  app.use(
    cors({
      origin: env.CORS_ORIGIN,
      methods: ["GET", "POST", "PATCH", "PUT", "DELETE"],
    }),
  );

  app.use(
    session({
      secret: env.SESSION_SECRET,
      resave: false,
      saveUninitialized: false,
    }),
  );
  app.use(passport.initialize());

  app.use("/auth", authRoutes);
  app.use("/users", usersRoutes);

  setupDocs(app);

  app.use(errorHandler);

  return app;
};
