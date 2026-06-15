import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { ConflictError, NotFoundError } from "../../shared/errors/AppError";
import { authMiddleware, requireStudent } from "../../shared/middlewares/auth.middlewares";
import { response } from "../../shared/utils/response";
import { ensurePortalSeed } from "../portalSeed";

const router = Router();

router.get("/", async (req, res, next) => {
  try {
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
  } catch (error) {
    next(error);
  }
});

router.get("/:jobId", async (req, res, next) => {
  try {
    const jobId = String(req.params.jobId);
    const job = await prisma.job.findFirst({
      where: {
        id: jobId,
        deletedAt: null,
      },
      include: {
        company: true,
        course: true,
      },
    });

    if (!job) {
      throw new NotFoundError("Vaga nao encontrada.");
    }

    res.status(200).json(response.success(job));
  } catch (error) {
    next(error);
  }
});

router.post("/:jobId/apply", authMiddleware, requireStudent, async (req, res, next) => {
  try {
    const jobId = String(req.params.jobId);
    const student = await prisma.student.findUnique({
      where: { userId: req.user!.id },
    });

    if (!student) {
      throw new NotFoundError("Aluno nao encontrado.");
    }

    const job = await prisma.job.findFirst({
      where: {
        id: jobId,
        status: "ACTIVE",
        deletedAt: null,
      },
    });

    if (!job) {
      throw new NotFoundError("Vaga nao encontrada ou encerrada.");
    }

    const jaCandidatado = await prisma.application.findFirst({
      where: {
        studentId: student.id,
        jobId: job.id,
        deletedAt: null,
      },
    });

    if (jaCandidatado) {
      throw new ConflictError("Voce ja se candidatou para esta vaga.");
    }

    const application = await prisma.application.create({
      data: {
        studentId: student.id,
        jobId: job.id,
      },
    });

    res.status(201).json(response.success(application, "Candidatura enviada com sucesso."));
  } catch (error) {
    next(error);
  }
});

export default router;
