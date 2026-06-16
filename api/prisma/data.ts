// Dados estáticos usados pelo seed. Nenhuma lógica de banco aqui —
// apenas constantes que o seed.ts consome para montar as queries.

// ─── Tipos ────────────────────────────────────────────────────────────────────

export type SeedCourse = {
  name: string;
  code: string;
  periods: number;
};

export type SeedCompany = {
  cnpj: string;
  name: string;
  description: string;
  phone: string;
  status: "PENDING" | "ANALYSING" | "APPROVED" | "BLOCKED";
};

export type SeedJob = {
  /** Referencia a SeedCompany.cnpj — resolvido para companyId no seed */
  companyCnpj: string;
  /** Referencia a SeedCourse.code — resolvido para courseId no seed */
  courseCode?: string;
  title: string;
  description: string;
  area: string;
  requirements?: string;
  salary?: number;
  location: string;
  modality: "PRESENCIAL" | "REMOTE" | "HYBRID";
  status: "ACTIVE" | "PAUSED" | "CLOSED";
};

export type SeedAdmin = {
  email: string;
  plainPassword: string;
};

export type SeedRecruiter = {
  email: string;
  plainPassword: string;
  /** CNPJ da empresa à qual pertence */
  companyCnpj: string;
  memberRole: "ADMIN" | "RECRUITER";
  member: {
    name: string;
    cpf: string;
    phone?: string;
  };
};

export type SeedStudent = {
  email: string;
  plainPassword: string;
  student: {
    name: string;
    ra: string;
    cpf: string;
    phone?: string;
  };
  /** Code do curso (SeedCourse.code) em que o aluno está matriculado */
  courseCode: string;
  courseStartedAt: Date;
};

// ─── TOTP de desenvolvimento ──────────────────────────────────────────────────
// Secret Base32 fixo para admin e recruiter. Adicione-o manualmente no seu
// app autenticador (Google Authenticator, Authy etc.) para logar no dev.
export const TOTP_DEV_SECRET = "JBSWY3DPEHPK3PXP";

// ─── Cursos ───────────────────────────────────────────────────────────────────

export const seedCourses: SeedCourse[] = [
  { name: "Analise e Desenvolvimento de Sistemas", code: "ADS", periods: 5 },
  { name: "Administracao", code: "ADM", periods: 8 },
  { name: "Marketing", code: "MKT", periods: 4 },
];

// ─── Empresas ─────────────────────────────────────────────────────────────────

export const seedCompanies: SeedCompany[] = [
  {
    cnpj: "12345678000190",
    name: "Tech Local",
    description: "Empresa de tecnologia focada em sistemas web e automacao.",
    phone: "44999991000",
    status: "APPROVED",
  },
  {
    cnpj: "22345678000191",
    name: "Agencia Alfa",
    description: "Agencia de comunicacao, marketing digital e producao de conteudo.",
    phone: "44999992000",
    status: "APPROVED",
  },
  {
    cnpj: "32345678000192",
    name: "Winfo",
    description: "Suporte em TI, infraestrutura e consultoria para empresas locais.",
    phone: "44999993000",
    status: "APPROVED",
  },
];

// ─── Vagas ────────────────────────────────────────────────────────────────────

export const seedJobs: SeedJob[] = [
  {
    companyCnpj: "12345678000190",
    courseCode: "ADS",
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
    companyCnpj: "22345678000191",
    courseCode: "MKT",
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
    companyCnpj: "32345678000192",
    courseCode: "ADS",
    title: "Suporte TI",
    description: "Atendimento a usuarios, configuracao de computadores e apoio a infraestrutura.",
    area: "Tecnologia",
    requirements: "Conhecimento basico em redes, Windows e atendimento ao usuario.",
    salary: 1100,
    location: "Umuarama, PR",
    modality: "REMOTE",
    status: "ACTIVE",
  },
];

// ─── Usuários de seed ─────────────────────────────────────────────────────────

export const seedAdmin: SeedAdmin = {
  email: "admin@unialfa.com",
  plainPassword: "Admin@123",
};

export const seedRecruiter: SeedRecruiter = {
  email: "recruiter@techlocal.com",
  plainPassword: "Recruit@123",
  companyCnpj: "12345678000190", // Tech Local
  memberRole: "RECRUITER",
  member: {
    name: "Carlos Recruiter",
    cpf: "11122233344",
    phone: "44988881111",
  },
};

export const seedStudent: SeedStudent = {
  email: "joao@aluno.com",
  plainPassword: "Aluno@1234",
  student: {
    name: "Joao Silva",
    ra: "2024001",
    cpf: "99988877766",
    phone: "44977776666",
  },
  courseCode: "ADS",
  courseStartedAt: new Date("2024-02-01"),
};