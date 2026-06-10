import rateLimit from "express-rate-limit";
import { env } from "../../config/env";
import { response } from "../utils/response";

// Rate limit global: limita requisições por IP em uma janela de tempo.
export const globalRateLimiter = rateLimit({
  windowMs: env.RATE_LIMIT_WINDOW_MS,
  max: env.RATE_LIMIT_MAX,
  handler: (_req, res) => {
    res.status(429).json(response.error("Muitas requisições. Tente novamente mais tarde."));
  },
});
