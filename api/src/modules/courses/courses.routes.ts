import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { response } from "../../shared/utils/response";
import { ensurePortalSeed } from "../portalSeed";

const router = Router();

router.get("/", async (_req, res) => {
  await ensurePortalSeed(prisma);

  const courses = await prisma.course.findMany({
    where: { isActive: true },
    orderBy: { name: "asc" },
  });

  res.status(200).json(response.success({ courses }));
});

export default router;
