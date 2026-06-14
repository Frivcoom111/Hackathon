import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { response } from "../../shared/utils/response";
import { ensurePortalSeed } from "../portalSeed";

const router = Router();

router.get("/", async (_req, res) => {
  await ensurePortalSeed(prisma);

  const companies = await prisma.company.findMany({
    include: {
      address: true,
      jobs: {
        where: { status: "ACTIVE", deletedAt: null },
        select: { id: true, title: true },
      },
    },
    orderBy: { createdAt: "desc" },
  });

  res.status(200).json(response.success({ companies }));
});

export default router;
