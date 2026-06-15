import { bearerAuth, errorResponse, successResponse } from "../../docs/helpers";
import { registry } from "../../docs/registry";
import { z } from "../../lib/zod";

const TAG = "Courses";

const courseOut = z
  .object({
    id: z.uuid(),
    name: z.string(),
    code: z.string().nullable(),
    periods: z.number(),
  })
  .openapi({ title: "Course" });

registry.registerPath({
  method: "get",
  path: "/courses",
  tags: [TAG],
  security: bearerAuth,
  summary: "Lista os cursos ativos.",
  responses: {
    200: {
      description: "Cursos ativos.",
      content: { "application/json": { schema: successResponse(z.array(courseOut)) } },
    },
    401: { description: "Token inválido ou expirado.", content: { "application/json": { schema: errorResponse } } },
  },
});
