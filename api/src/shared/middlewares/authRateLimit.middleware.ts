import rateLimit from "express-rate-limit";
import { response } from "../utils/response";

// Rate limit dedicado a rotas sensíveis de autenticação (login, register, TOTP).
// Mais restritivo que o global: 10 requisições por IP a cada 15 minutos.
export const authRateLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 10,
  handler: (_req, res) => {
    res.status(429).json(response.error("Muitas tentativas. Tente novamente em alguns minutos."));
  },
});
