import { Router } from "express";
import { prisma } from "../../lib/prisma";
import { BadRequestError, NotFoundError } from "../../shared/errors/AppError";
import { authMiddleware, requireStudent } from "../../shared/middlewares/auth.middlewares";
import { uploadCoverPhoto } from "../../shared/middlewares/upload.middleware";
import { response } from "../../shared/utils/response";

const router = Router();

router.use(authMiddleware, requireStudent);

const getStudentOrThrow = async (userId: string) => {
  const student = await prisma.student.findUnique({
    where: { userId },
  });

  if (!student) {
    throw new NotFoundError("Perfil do aluno nao encontrado.");
  }

  return student;
};

const profileInclude = {
  user: {
    select: {
      email: true,
    },
  },
  address: true,
  courses: {
    include: {
      course: true,
    },
    orderBy: { createdAt: "desc" as const },
  },
  certificates: {
    orderBy: { issuedAt: "desc" as const },
  },
  applications: {
    where: { deletedAt: null },
    include: {
      job: {
        include: {
          company: true,
        },
      },
    },
    orderBy: { createdAt: "desc" as const },
    take: 5,
  },
};

const getFullProfile = async (userId: string) => {
  const student = await prisma.student.findUnique({
    where: { userId },
    include: profileInclude,
  });

  if (!student) {
    throw new NotFoundError("Perfil do aluno nao encontrado.");
  }

  return student;
};

router.get("/profile", async (req, res, next) => {
  try {
    const student = await getFullProfile(req.user!.id);
    res.status(200).json(response.success(student));
  } catch (error) {
    next(error);
  }
});

router.patch("/profile", async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);
    const name = String(req.body.name ?? student.name).trim();

    if (name.length < 2) {
      throw new BadRequestError("Informe o nome completo.");
    }

    const isEligible =
      req.body.isEligible === undefined ? student.isEligible : ["1", "true", "on"].includes(String(req.body.isEligible));

    await prisma.student.update({
      where: { id: student.id },
      data: {
        name,
        phone: String(req.body.phone ?? "").replace(/\D/g, "") || null,
        headline: String(req.body.headline ?? "").trim() || null,
        summary: String(req.body.summary ?? "").trim() || null,
        isEligible,
      },
    });

    const updated = await getFullProfile(req.user!.id);
    res.status(200).json(response.success(updated, "Perfil atualizado com sucesso."));
  } catch (error) {
    next(error);
  }
});

router.patch("/address", async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);
    const data = {
      street: String(req.body.street ?? "").trim(),
      number: String(req.body.number ?? "").trim(),
      complement: String(req.body.complement ?? "").trim() || null,
      district: String(req.body.district ?? "").trim(),
      city: String(req.body.city ?? "").trim(),
      state: String(req.body.state ?? "").trim().toUpperCase(),
      zipCode: String(req.body.zipCode ?? "").replace(/\D/g, ""),
    };

    if (!data.street || !data.number || !data.district || !data.city || data.state.length !== 2 || data.zipCode.length < 8) {
      throw new BadRequestError("Preencha o endereco corretamente.");
    }

    if (student.addressId) {
      await prisma.address.update({
        where: { id: student.addressId },
        data,
      });
    } else {
      const address = await prisma.address.create({ data });
      await prisma.student.update({
        where: { id: student.id },
        data: { addressId: address.id },
      });
    }

    const updated = await getFullProfile(req.user!.id);
    res.status(200).json(response.success(updated, "Endereco atualizado com sucesso."));
  } catch (error) {
    next(error);
  }
});

router.patch("/course", async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);
    const courseId = String(req.body.courseId ?? "").trim();
    const status = String(req.body.status ?? "ACTIVE").trim();
    const startedAt = new Date(req.body.startedAt ?? Date.now());
    const finishedAt = req.body.finishedAt ? new Date(req.body.finishedAt) : null;

    if (!courseId) {
      throw new BadRequestError("Informe o curso.");
    }

    if (!["ACTIVE", "COMPLETED", "CANCELLED"].includes(status)) {
      throw new BadRequestError("Status do curso invalido.");
    }

    if (Number.isNaN(startedAt.getTime())) {
      throw new BadRequestError("Data de inicio do curso invalida.");
    }

    if (status === "COMPLETED" && (!finishedAt || Number.isNaN(finishedAt.getTime()))) {
      throw new BadRequestError("Informe a data de conclusao do curso.");
    }

    const course = await prisma.course.findUnique({ where: { id: courseId } });
    if (!course) {
      throw new NotFoundError("Curso nao encontrado.");
    }

    await prisma.studentCourse.upsert({
      where: {
        studentId_courseId: {
          studentId: student.id,
          courseId,
        },
      },
      create: {
        studentId: student.id,
        courseId,
        status: status as "ACTIVE" | "COMPLETED" | "CANCELLED",
        startedAt,
        finishedAt: status === "COMPLETED" ? finishedAt : null,
      },
      update: {
        status: status as "ACTIVE" | "COMPLETED" | "CANCELLED",
        startedAt,
        finishedAt: status === "COMPLETED" ? finishedAt : null,
      },
    });

    const updated = await getFullProfile(req.user!.id);
    res.status(200).json(response.success(updated, "Curso atualizado com sucesso."));
  } catch (error) {
    next(error);
  }
});

router.post("/certificates", async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);
    const name = String(req.body.name ?? "").trim();
    const institution = String(req.body.institution ?? "").trim() || null;
    const issuedAt = new Date(req.body.issuedAt ?? "");

    if (name.length < 2) {
      throw new BadRequestError("Informe o nome do certificado.");
    }

    if (Number.isNaN(issuedAt.getTime())) {
      throw new BadRequestError("Informe a data do certificado.");
    }

    await prisma.certificate.create({
      data: {
        studentId: student.id,
        name,
        institution,
        issuedAt,
      },
    });

    const updated = await getFullProfile(req.user!.id);
    res.status(201).json(response.success(updated, "Certificado adicionado ao perfil."));
  } catch (error) {
    next(error);
  }
});

router.post("/profile/cover", uploadCoverPhoto.single("image"), async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);

    if (!req.file) {
      throw new BadRequestError("Envie uma imagem.");
    }

    await prisma.student.update({
      where: { id: student.id },
      data: { coverPhotoPath: req.file.path.replace(/\\/g, "/") },
    });

    const updated = await getFullProfile(req.user!.id);
    res.status(200).json(response.success(updated, "Capa atualizada."));
  } catch (error) {
    next(error);
  }
});

router.get("/applications", async (req, res, next) => {
  try {
    const student = await getStudentOrThrow(req.user!.id);

    const applications = await prisma.application.findMany({
      where: {
        studentId: student.id,
        deletedAt: null,
      },
      include: {
        job: {
          include: {
            company: true,
          },
        },
      },
      orderBy: { createdAt: "desc" },
    });

    res.status(200).json(response.success({ applications }));
  } catch (error) {
    next(error);
  }
});

export default router;
