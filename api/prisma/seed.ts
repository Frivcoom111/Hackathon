import { prisma } from "../src/lib/prisma";
import { generateHash } from "../src/shared/utils/bcryptUtils";
import {
  TOTP_DEV_SECRET,
  seedAdmin,
  seedCompanies,
  seedCourses,
  seedJobs,
  seedRecruiter,
  seedStudent,
} from "./data";

// Seed idempotente: verifica se o admin já existe antes de rodar.
// Rodar com `pnpm db:seed`.

async function main() {
  const alreadySeeded = await prisma.user.findFirst({
    where: { email: seedAdmin.email },
  });

  if (alreadySeeded) {
    console.log("Seed ja executado anteriormente. Nenhuma alteracao feita.");
    return;
  }

  console.log("Iniciando seed...");

  // Hashes feitos fora da transacao para nao prolongar o lock no banco.
  const [adminHash, recruiterHash, studentHash] = await Promise.all([
    generateHash(seedAdmin.plainPassword),
    generateHash(seedRecruiter.plainPassword),
    generateHash(seedStudent.plainPassword),
  ]);

  await prisma.$transaction(async (tx) => {
    // ─── 1. Cursos ───────────────────────────────────────────────────────────
    const courses = await Promise.all(
      seedCourses.map((c) =>
        tx.course.upsert({
          where: { code: c.code },
          update: {},
          create: c,
        }),
      ),
    );

    // Mapa code → id para resolver referências abaixo
    const courseById = Object.fromEntries(courses.map((c) => [c.code!, c.id]));

    // ─── 2. Empresas ─────────────────────────────────────────────────────────
    const companies = await Promise.all(
      seedCompanies.map((c) =>
        tx.company.upsert({
          where: { cnpj: c.cnpj },
          update: {},
          create: c,
        }),
      ),
    );

    // Mapa cnpj → id para resolver referências abaixo
    const companyById = Object.fromEntries(companies.map((c) => [c.cnpj, c.id]));

    // ─── 3. Admin da plataforma ───────────────────────────────────────────────
    await tx.user.create({
      data: {
        email: seedAdmin.email,
        password: adminHash,
        role: "ADMIN",
        totpSecret: TOTP_DEV_SECRET,
        totpEnabled: true,
      },
    });

    // ─── 4. Recruiter (membro de empresa) ────────────────────────────────────
    const recruiterUser = await tx.user.create({
      data: {
        email: seedRecruiter.email,
        password: recruiterHash,
        role: "COMPANY",
        totpSecret: TOTP_DEV_SECRET,
        totpEnabled: true,
      },
    });

    await tx.companyMember.create({
      data: {
        userId: recruiterUser.id,
        companyId: companyById[seedRecruiter.companyCnpj],
        role: seedRecruiter.memberRole,
        name: seedRecruiter.member.name,
        cpf: seedRecruiter.member.cpf,
        phone: seedRecruiter.member.phone,
      },
    });

    // ─── 5. Aluno ─────────────────────────────────────────────────────────────
    const studentUser = await tx.user.create({
      data: {
        email: seedStudent.email,
        password: studentHash,
        role: "STUDENT",
      },
    });

    const student = await tx.student.create({
      data: {
        userId: studentUser.id,
        name: seedStudent.student.name,
        ra: seedStudent.student.ra,
        cpf: seedStudent.student.cpf,
        phone: seedStudent.student.phone,
      },
    });

    await tx.studentCourse.create({
      data: {
        studentId: student.id,
        courseId: courseById[seedStudent.courseCode],
        status: "ACTIVE",
        startedAt: seedStudent.courseStartedAt,
      },
    });

    // ─── 6. Vagas ─────────────────────────────────────────────────────────────
    await tx.job.createMany({
      data: seedJobs.map(({ companyCnpj, courseCode, ...rest }) => ({
        ...rest,
        companyId: companyById[companyCnpj],
        courseId: courseCode ? courseById[courseCode] : undefined,
      })),
    });
  });

  console.log("");
  console.log("Seed concluido com sucesso!");
  console.log("");
  console.log("Credenciais de acesso:");
  console.log("  Admin      →  admin@unialfa.com         /  Admin@123");
  console.log("  Recruiter  →  recruiter@techlocal.com   /  Recruit@123");
  console.log("  Aluno      →  joao@aluno.com            /  Aluno@1234");
  console.log("");
  console.log("TOTP (admin e recruiter):");
  console.log(`  Secret Base32: ${TOTP_DEV_SECRET}`);
  console.log("  Adicione-o manualmente no Google Authenticator / Authy.");
  console.log("");
}

main()
  .catch((error) => {
    console.error("Falha no seed:", error);
    process.exitCode = 1;
  })
  .finally(() => prisma.$disconnect());