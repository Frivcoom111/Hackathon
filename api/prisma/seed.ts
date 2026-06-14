import { prisma } from "../src/lib/prisma";

// Seed idempotente do portal: só insere quando a respectiva tabela está vazia.
// Rodar com `pnpm db:seed` (ou `prisma db seed`). Não roda no startup da API.

const defaultCourses = [
  { name: "Analise e Desenvolvimento de Sistemas", code: "ADS", periods: 5 },
  { name: "Administracao", code: "ADM", periods: 8 },
  { name: "Marketing", code: "MKT", periods: 4 },
];

const defaultCompanies = [
  {
    name: "Tech Local",
    cnpj: "12345678000190",
    description: "Empresa de tecnologia focada em sistemas web e automacao.",
    phone: "44999991000",
    status: "APPROVED" as const,
  },
  {
    name: "Agencia Alfa",
    cnpj: "22345678000191",
    description: "Agencia de comunicacao, marketing digital e producao de conteudo.",
    phone: "44999992000",
    status: "APPROVED" as const,
  },
  {
    name: "Winfo",
    cnpj: "32345678000192",
    description: "Suporte em TI, infraestrutura e consultoria para empresas locais.",
    phone: "44999993000",
    status: "APPROVED" as const,
  },
];

export async function ensurePortalSeed() {
  const courseCount = await prisma.course.count();
  if (courseCount === 0) {
    await prisma.course.createMany({ data: defaultCourses });
  }

  const companyCount = await prisma.company.count();
  if (companyCount === 0) {
    await prisma.company.createMany({ data: defaultCompanies });
  }

  const jobCount = await prisma.job.count();
  if (jobCount > 0) return;

  const [ads, marketing] = await Promise.all([
    prisma.course.findFirst({ where: { code: "ADS" } }),
    prisma.course.findFirst({ where: { code: "MKT" } }),
  ]);

  const [techLocal, agenciaAlfa, winfo] = await Promise.all([
    prisma.company.findFirst({ where: { cnpj: "12345678000190" } }),
    prisma.company.findFirst({ where: { cnpj: "22345678000191" } }),
    prisma.company.findFirst({ where: { cnpj: "32345678000192" } }),
  ]);

  if (!techLocal || !agenciaAlfa || !winfo) return;

  await prisma.job.createMany({
    data: [
      {
        companyId: techLocal.id,
        courseId: ads?.id,
        title: "Estagio Backend Jr",
        description: "Apoio no desenvolvimento de APIs, manutencao de sistemas internos e integracoes.",
        area: "Tecnologia",
        requirements: "PHP ou JavaScript basico, logica de programacao e vontade de aprender.",
        salary: 1200,
        location: "Umuarama, PR",
        modality: "HYBRID",
        status: "ACTIVE",
      },
      {
        companyId: agenciaAlfa.id,
        courseId: marketing?.id,
        title: "Marketing Digital",
        description: "Criacao de conteudos, apoio em campanhas e acompanhamento de metricas digitais.",
        area: "Marketing",
        requirements: "Boa escrita, criatividade e familiaridade com redes sociais.",
        salary: 900,
        location: "Umuarama, PR",
        modality: "PRESENCIAL",
        status: "ACTIVE",
      },
      {
        companyId: winfo.id,
        courseId: ads?.id,
        title: "Suporte TI",
        description: "Atendimento a usuarios, configuracao de computadores e apoio a infraestrutura.",
        area: "Tecnologia",
        requirements: "Conhecimento basico em redes, Windows e atendimento ao usuario.",
        salary: 1100,
        location: "Umuarama, PR",
        modality: "REMOTE",
        status: "ACTIVE",
      },
    ],
  });
}

ensurePortalSeed()
  .then(() => console.log("Seed do portal concluido."))
  .catch((error) => {
    console.error("Falha no seed do portal:", error);
    process.exitCode = 1;
  })
  .finally(() => prisma.$disconnect());
