import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { BadRequestError, NotFoundError } from "../../shared/errors/AppError";
import { authMiddleware, requireCompany } from "../../shared/middlewares/auth.middlewares";
import { response } from "../../shared/utils/response";

const router = Router();

router.use(authMiddleware, requireCompany);

const getCompanyByUser = async (userId: string) => {
  const member = await prisma.companyMember.findUnique({
    where: { userId },
    include: {
      company: {
        include: {
          address: true,
          members: true,
        },
      },
    },
  });

  if (!member) {
    throw new NotFoundError("Empresa nao encontrada para este usuario.");
  }

  return member.company;
};

const parseSalary = (value: unknown) => {
  if (value === undefined || value === null || value === "") {
    return null;
  }

  const salary = Number(value);
  if (!Number.isFinite(salary)) {
    throw new BadRequestError("Bolsa invalida.");
  }

  return salary;
};

router.get("/profile", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    res.status(200).json(response.success(company));
  } catch (error) {
    next(error);
  }
});

router.get("/jobs", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const limit = Number(req.query.limit ?? 50);

    const jobs = await prisma.job.findMany({
      where: {
        companyId: company.id,
        deletedAt: null,
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

router.get("/jobs/:jobId", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const job = await prisma.job.findFirst({
      where: {
        id: req.params.jobId,
        companyId: company.id,
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

router.post("/jobs", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const title = String(req.body.title ?? "").trim();
    const description = String(req.body.description ?? "").trim();
    const area = String(req.body.area ?? "").trim();
    const location = String(req.body.location ?? "").trim();

    if (!title || !description || !area || !location) {
      throw new BadRequestError("Preencha titulo, descricao, area e localizacao.");
    }

    const job = await prisma.job.create({
      data: {
        companyId: company.id,
        courseId: req.body.courseId || null,
        title,
        description,
        area,
        requirements: req.body.requirements || null,
        salary: parseSalary(req.body.salary),
        location,
        modality: req.body.modality ?? "PRESENCIAL",
        status: req.body.status ?? "ACTIVE",
      },
    });

    res.status(201).json(response.success(job, "Vaga criada com sucesso."));
  } catch (error) {
    next(error);
  }
});

router.patch("/jobs/:jobId", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const exists = await prisma.job.findFirst({
      where: {
        id: req.params.jobId,
        companyId: company.id,
        deletedAt: null,
      },
    });

    if (!exists) {
      throw new NotFoundError("Vaga nao encontrada.");
    }

    const job = await prisma.job.update({
      where: { id: req.params.jobId },
      data: {
        title: req.body.title,
        description: req.body.description,
        area: req.body.area,
        requirements: req.body.requirements ?? null,
        salary: parseSalary(req.body.salary),
        location: req.body.location,
        modality: req.body.modality,
        status: req.body.status,
      },
    });

    res.status(200).json(response.success(job, "Vaga atualizada com sucesso."));
  } catch (error) {
    next(error);
  }
});

router.get("/jobs/:jobId/applications", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const jobId = String(req.params.jobId);

    const job = await prisma.job.findFirst({
      where: {
        id: jobId,
        companyId: company.id,
        deletedAt: null,
      },
    });

    if (!job) {
      throw new NotFoundError("Vaga nao encontrada.");
    }

    const applications = await prisma.application.findMany({
      where: {
        jobId,
        deletedAt: null,
      },
      include: {
        student: {
          include: {
            courses: {
              include: { course: true },
            },
          },
        },
      },
      orderBy: { createdAt: "desc" },
      take: 50,
    });

    res.status(200).json(response.success({ applications }));
  } catch (error) {
    next(error);
  }
});

router.patch("/jobs/:jobId/applications/:applicationId/status", async (req, res, next) => {
  try {
    const company = await getCompanyByUser(req.user!.id);
    const jobId = String(req.params.jobId);
    const applicationId = String(req.params.applicationId);
    const status = String(req.body.status ?? "");

    const job = await prisma.job.findFirst({
      where: {
        id: jobId,
        companyId: company.id,
        deletedAt: null,
      },
    });

    if (!job) {
      throw new NotFoundError("Vaga nao encontrada.");
    }

    if (!["PENDING", "ANALYSING", "APPROVED", "REJECTED", "CANCELLED"].includes(status)) {
      throw new BadRequestError("Status de candidatura invalido.");
    }

    const application = await prisma.application.findFirst({
      where: {
        id: applicationId,
        jobId,
        deletedAt: null,
      },
    });

    if (!application) {
      throw new NotFoundError("Candidatura nao encontrada.");
    }

    const updated = await prisma.application.update({
      where: { id: applicationId },
      data: { status: status as "PENDING" | "ANALYSING" | "APPROVED" | "REJECTED" | "CANCELLED" },
    });

    res.status(200).json(response.success(updated, "Status da candidatura atualizado."));
  } catch (error) {
    next(error);
  }
});

export default router;
