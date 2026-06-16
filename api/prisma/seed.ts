import { prisma } from "../src/lib/prisma";
import { generateHash } from "../src/shared/utils/bcryptUtils";
import {
  seedAdmin,
  seedApplications,
  seedCompanies,
  seedCompanyMembers,
  seedCourses,
  seedJobs,
  seedNotifications,
  seedStudents,
} from "./data";

// Seed idempotente: se o admin já existir, nada é feito.
// Rodar com: pnpm db:seed

async function main() {
  const alreadySeeded = await prisma.user.findFirst({
    where: { email: seedAdmin.email },
  });

  if (alreadySeeded) {
    console.log("Seed ja executado. Nenhuma alteracao feita.");
    return;
  }

  console.log("Iniciando seed...\n");

  // Bcrypt fora da transação para não segurar o lock do banco
  const memberHashes = await Promise.all(
    seedCompanyMembers.map((m) => generateHash(m.plainPassword)),
  );
  const studentHashes = await Promise.all(
    seedStudents.map((s) => generateHash(s.plainPassword)),
  );
  const adminHash = await generateHash(seedAdmin.plainPassword);

  await prisma.$transaction(
    async (tx) => {
      // ─── 1. Cursos ─────────────────────────────────────────────────────────
      console.log("  → Cursos...");
      const courses = await Promise.all(
        seedCourses.map((c) =>
          tx.course.upsert({
            where: { code: c.code },
            update: {},
            create: c,
          }),
        ),
      );
      // Mapa code → id
      const courseById: Record<string, string> = Object.fromEntries(
        courses.map((c) => [c.code!, c.id]),
      );

      // ─── 2. Empresas ───────────────────────────────────────────────────────
      console.log("  → Empresas...");
      const companies = await Promise.all(
        seedCompanies.map((c) =>
          tx.company.upsert({
            where: { cnpj: c.cnpj },
            update: {},
            create: c,
          }),
        ),
      );
      // Mapa cnpj → id
      const companyById: Record<string, string> = Object.fromEntries(
        companies.map((c) => [c.cnpj, c.id]),
      );

      // ─── 3. Vagas ──────────────────────────────────────────────────────────
      console.log("  → Vagas...");
      const jobs = await Promise.all(
        seedJobs.map(({ companyCnpj, courseCode, ...rest }) =>
          tx.job.create({
            data: {
              ...rest,
              companyId: companyById[companyCnpj],
              courseId: courseCode ? courseById[courseCode] : undefined,
            },
          }),
        ),
      );
      // Mapa title → id (títulos únicos no seed)
      const jobById: Record<string, string> = Object.fromEntries(
        jobs.map((j) => [j.title, j.id]),
      );

      // ─── 4. Admin da plataforma ────────────────────────────────────────────
      console.log("  → Admin...");
      await tx.user.create({
        data: {
          email: seedAdmin.email,
          password: adminHash,
          role: "ADMIN",
        },
      });

      // ─── 5. Membros de empresa ─────────────────────────────────────────────
      console.log("  → Membros de empresa...");
      for (let i = 0; i < seedCompanyMembers.length; i++) {
        const m = seedCompanyMembers[i];
        const user = await tx.user.create({
          data: {
            email: m.email,
            password: memberHashes[i],
            role: "COMPANY"
          },
        });
        await tx.companyMember.create({
          data: {
            userId: user.id,
            companyId: companyById[m.companyCnpj],
            role: m.memberRole,
            name: m.member.name,
            cpf: m.member.cpf,
            phone: m.member.phone,
          },
        });
      }

      // ─── 6. Alunos ─────────────────────────────────────────────────────────
      console.log("  → Alunos...");
      // Mapa ra → studentId (usado nas candidaturas)
      const studentById: Record<string, string> = {};

      for (let i = 0; i < seedStudents.length; i++) {
        const s = seedStudents[i];

        const address = await tx.address.create({ data: s.address });

        const user = await tx.user.create({
          data: {
            email: s.email,
            password: studentHashes[i],
            role: "STUDENT",
          },
        });

        const student = await tx.student.create({
          data: {
            userId: user.id,
            addressId: address.id,
            name: s.student.name,
            ra: s.student.ra,
            cpf: s.student.cpf,
            phone: s.student.phone,
          },
        });

        studentById[s.student.ra] = student.id;

        await tx.studentCourse.create({
          data: {
            studentId: student.id,
            courseId: courseById[s.courseCode],
            status: "ACTIVE",
            startedAt: s.courseStartedAt,
          },
        });

        if (s.certificates.length > 0) {
          await tx.certificate.createMany({
            data: s.certificates.map((cert) => ({
              studentId: student.id,
              name: cert.name,
              institution: cert.institution,
              issuedAt: cert.issuedAt,
            })),
          });
        }
      }

      // ─── 7. Candidaturas ───────────────────────────────────────────────────
      console.log("  → Candidaturas...");
      await tx.application.createMany({
        data: seedApplications.map(({ studentRa, jobTitle, status }) => ({
          studentId: studentById[studentRa],
          jobId: jobById[jobTitle],
          status,
        })),
      });

      // ─── 8. Notificações ───────────────────────────────────────────────────
      console.log("  → Notificacoes...");
      // Mapa email → userId
      const allUsers = await tx.user.findMany({
        select: { id: true, email: true },
      });
      const userIdByEmail: Record<string, string> = Object.fromEntries(
        allUsers.map((u) => [u.email, u.id]),
      );

      await tx.notification.createMany({
        data: seedNotifications.map(({ userEmail, ...rest }) => ({
          userId: userIdByEmail[userEmail],
          ...rest,
        })),
      });
    },
    { timeout: 30000 },
  );

  console.log("\nSeed concluido com sucesso!\n");
  console.log("┌─────────────────────────────────────────────────────────┐");
  console.log("│                  CREDENCIAIS DE ACESSO                 │");
  console.log("├──────────────┬──────────────────────────────┬──────────┤");
  console.log("│ Tipo         │ E-mail                       │ Senha    │");
  console.log("├──────────────┼──────────────────────────────┼──────────┤");
  console.log("│ Admin        │ admin@unialfa.com            │ Admin@123│");
  console.log("│ Empresa ADM  │ empresa@techlocal.com        │ Empresa@1│");
  console.log("│ Empresa ADM  │ empresa@agenciaalfa.com      │ Empresa@1│");
  console.log("│ Empresa ADM  │ empresa@winfo.com            │ Empresa@1│");
  console.log("│ Recruiter    │ recruiter@techlocal.com      │ Recruit@1│");
  console.log("│ Aluno        │ joao@aluno.com               │ Aluno@123│");
  console.log("│ Aluno        │ maria@aluno.com              │ Aluno@123│");
  console.log("│ Aluno        │ lucas@aluno.com              │ Aluno@123│");
  console.log("└──────────────┴──────────────────────────────┴──────────┘");
  console.log("\nOBS: O TOTP é configurado no primeiro login de cada usuário.\n");
}

main()
  .catch((error) => {
    console.error("\nFalha no seed:", error);
    process.exitCode = 1;
  })
  .finally(() => prisma.$disconnect());