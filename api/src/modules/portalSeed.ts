import type { PrismaClient } from "../generated/prisma/client";

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

export async function ensurePortalSeed(prisma: PrismaClient) {
  for (const course of defaultCourses) {
    const existing = await prisma.course.findFirst({
      where: {
        OR: [{ name: course.name }, { code: course.code }],
      },
    });

    if (existing) {
      await prisma.course.update({
        where: { id: existing.id },
        data: {
          name: course.name,
          code: course.code,
          periods: course.periods,
          isActive: true,
        },
      });
    } else {
      await prisma.course.create({ data: course });
    }
  }

  for (const company of defaultCompanies) {
    const existing = await prisma.company.findUnique({ where: { cnpj: company.cnpj } });

    if (existing) {
      await prisma.company.update({
        where: { id: existing.id },
        data: {
          name: company.name,
          description: company.description,
          phone: company.phone,
          status: company.status,
        },
      });
    } else {
      await prisma.company.create({ data: company });
    }
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
