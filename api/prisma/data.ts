// Dados estáticos usados pelo seed. Nenhuma lógica de banco aqui —
// apenas constantes que o seed.ts consome para montar as queries.

// ─── Tipos ────────────────────────────────────────────────────────────────────

export type SeedAddress = {
  street: string;
  number: string;
  complement?: string;
  district: string;
  city: string;
  state: string;
  zipCode: string;
};

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
  companyCnpj: string;
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

export type SeedCompanyMember = {
  email: string;
  plainPassword: string;
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
  address: SeedAddress;
  courseCode: string;
  courseStartedAt: Date;
  certificates: {
    name: string;
    institution?: string;
    issuedAt: Date;
  }[];
};

export type SeedApplication = {
  studentRa: string;
  jobTitle: string;
  status: "PENDING" | "ANALYSING" | "APPROVED" | "REJECTED" | "CANCELLED";
};

export type SeedNotification = {
  userEmail: string;
  title: string;
  message: string;
  type: string;
  isRead: boolean;
};

// ─── TOTP de desenvolvimento ──────────────────────────────────────────────────
// Secret Base32 fixo para admin e membros de empresa.
// Adicione manualmente no Google Authenticator / Authy para logar no dev.
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
    companyCnpj: "12345678000190",
    courseCode: "ADS",
    title: "Estagio Frontend Jr",
    description: "Desenvolvimento de interfaces web responsivas com HTML, CSS e JavaScript.",
    area: "Tecnologia",
    requirements: "Conhecimento basico em HTML, CSS e logica de programacao.",
    salary: 1100,
    location: "Umuarama, PR",
    modality: "REMOTE",
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
    companyCnpj: "22345678000191",
    courseCode: "ADM",
    title: "Assistente Administrativo",
    description: "Apoio em rotinas administrativas, controle de documentos e atendimento interno.",
    area: "Administracao",
    requirements: "Organizacao, proatividade e conhecimento basico em Excel.",
    salary: 850,
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
  {
    companyCnpj: "32345678000192",
    courseCode: "ADS",
    title: "Analista de Dados Jr",
    description: "Coleta, tratamento e visualizacao de dados para apoio a decisoes estrategicas.",
    area: "Tecnologia",
    requirements: "Excel intermediario, nocoes de SQL e curiosidade por dados.",
    salary: 1300,
    location: "Umuarama, PR",
    modality: "HYBRID",
    status: "ACTIVE",
  },
];

// ─── Usuários ─────────────────────────────────────────────────────────────────

export const seedAdmin: SeedAdmin = {
  email: "admin@unialfa.com",
  plainPassword: "Admin@123",
};

export const seedCompanyMembers: SeedCompanyMember[] = [
  {
    // Admin da Tech Local
    email: "empresa@techlocal.com",
    plainPassword: "Empresa@123",
    companyCnpj: "12345678000190",
    memberRole: "ADMIN",
    member: {
      name: "Ana Gestora",
      cpf: "55566677788",
      phone: "44999001111",
    },
  },
  {
    // Recruiter da Tech Local
    email: "recruiter@techlocal.com",
    plainPassword: "Recruit@123",
    companyCnpj: "12345678000190",
    memberRole: "RECRUITER",
    member: {
      name: "Carlos Recruiter",
      cpf: "11122233344",
      phone: "44988881111",
    },
  },
  {
    // Admin da Agencia Alfa
    email: "empresa@agenciaalfa.com",
    plainPassword: "Empresa@123",
    companyCnpj: "22345678000191",
    memberRole: "ADMIN",
    member: {
      name: "Bruno Gestor",
      cpf: "22233344455",
      phone: "44999002222",
    },
  },
  {
    // Admin da Winfo
    email: "empresa@winfo.com",
    plainPassword: "Empresa@123",
    companyCnpj: "32345678000192",
    memberRole: "ADMIN",
    member: {
      name: "Paula Gestora",
      cpf: "33344455566",
      phone: "44999003333",
    },
  },
];

export const seedStudents: SeedStudent[] = [
  {
    email: "joao@aluno.com",
    plainPassword: "Aluno@1234",
    student: {
      name: "Joao Silva",
      ra: "2024001",
      cpf: "99988877766",
      phone: "44977776666",
    },
    address: {
      street: "Rua das Flores",
      number: "123",
      district: "Centro",
      city: "Umuarama",
      state: "PR",
      zipCode: "87501001",
    },
    courseCode: "ADS",
    courseStartedAt: new Date("2024-02-01"),
    certificates: [
      { name: "Logica de Programacao", institution: "Curso em Video", issuedAt: new Date("2023-06-01") },
      { name: "PHP do Zero", institution: "Udemy", issuedAt: new Date("2024-01-10") },
    ],
  },
  {
    email: "maria@aluno.com",
    plainPassword: "Aluno@1234",
    student: {
      name: "Maria Santos",
      ra: "2024002",
      cpf: "88877766655",
      phone: "44966665555",
    },
    address: {
      street: "Avenida Parana",
      number: "456",
      district: "Zona 1",
      city: "Umuarama",
      state: "PR",
      zipCode: "87501002",
    },
    courseCode: "MKT",
    courseStartedAt: new Date("2024-02-01"),
    certificates: [
      { name: "Google Analytics", institution: "Google", issuedAt: new Date("2023-12-01") },
      { name: "Marketing de Conteudo", institution: "Rock Content", issuedAt: new Date("2024-03-15") },
    ],
  },
  {
    email: "lucas@aluno.com",
    plainPassword: "Aluno@1234",
    student: {
      name: "Lucas Oliveira",
      ra: "2024003",
      cpf: "77766655544",
      phone: "44955554444",
    },
    address: {
      street: "Rua Sete de Setembro",
      number: "789",
      district: "Zona 2",
      city: "Umuarama",
      state: "PR",
      zipCode: "87501003",
    },
    courseCode: "ADM",
    courseStartedAt: new Date("2023-08-01"),
    certificates: [{ name: "Excel Avancado", institution: "Senai", issuedAt: new Date("2024-02-20") }],
  },
];

// ─── Candidaturas ─────────────────────────────────────────────────────────────

export const seedApplications: SeedApplication[] = [
  { studentRa: "2024001", jobTitle: "Estagio Backend Jr", status: "ANALYSING" },
  { studentRa: "2024001", jobTitle: "Estagio Frontend Jr", status: "PENDING" },
  { studentRa: "2024001", jobTitle: "Suporte TI", status: "PENDING" },
  { studentRa: "2024002", jobTitle: "Marketing Digital", status: "APPROVED" },
  { studentRa: "2024002", jobTitle: "Assistente Administrativo", status: "PENDING" },
  { studentRa: "2024003", jobTitle: "Assistente Administrativo", status: "ANALYSING" },
  { studentRa: "2024003", jobTitle: "Analista de Dados Jr", status: "REJECTED" },
];

// ─── Notificações ─────────────────────────────────────────────────────────────

export const seedNotifications: SeedNotification[] = [
  {
    userEmail: "joao@aluno.com",
    title: "Candidatura recebida",
    message: "Sua candidatura para Estagio Backend Jr foi recebida e esta em analise.",
    type: "application",
    isRead: false,
  },
  {
    userEmail: "joao@aluno.com",
    title: "Candidatura recebida",
    message: "Sua candidatura para Estagio Frontend Jr foi recebida.",
    type: "application",
    isRead: true,
  },
  {
    userEmail: "maria@aluno.com",
    title: "Candidatura aprovada!",
    message: "Parabens! Sua candidatura para Marketing Digital foi aprovada. Aguarde o contato da empresa.",
    type: "application",
    isRead: false,
  },
  {
    userEmail: "maria@aluno.com",
    title: "Candidatura recebida",
    message: "Sua candidatura para Assistente Administrativo foi recebida.",
    type: "application",
    isRead: true,
  },
  {
    userEmail: "lucas@aluno.com",
    title: "Candidatura em analise",
    message: "Sua candidatura para Assistente Administrativo esta sendo avaliada pela empresa.",
    type: "application",
    isRead: false,
  },
  {
    userEmail: "lucas@aluno.com",
    title: "Candidatura nao aprovada",
    message: "Infelizmente sua candidatura para Analista de Dados Jr nao foi aprovada desta vez.",
    type: "application",
    isRead: false,
  },
];
