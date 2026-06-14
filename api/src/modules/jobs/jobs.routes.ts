import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { response } from "../../shared/utils/response";
import { ensurePortalSeed } from "../portalSeed";

const router = Router();

router.get("/", async (req, res) => {
  await ensurePortalSeed(prisma);

  const status = typeof req.query.status === "string" ? req.query.status : "ACTIVE";
  const limit = Number(req.query.limit ?? 50);

  const jobs = await prisma.job.findMany({
    where: {
      deletedAt: null,
      status: status === "ACTIVE" ? "ACTIVE" : undefined,
    },
    include: {
      company: true,
      course: true,
    },
    orderBy: { createdAt: "desc" },
    take: Number.isFinite(limit) ? Math.min(Math.max(limit, 1), 50) : 50,
  });

  res.status(200).json(response.success({ jobs }));
});

export default router;
