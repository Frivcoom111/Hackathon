Arquivos gerados

prisma/schema.prisma — Prisma 7, pronto para uso

portal_estagios_schema.sql — DDL para importar no MySQL Workbench

Tabelas (11)

| Tabela | Responsabilidade |
|---|---|
| Address | Endereço reutilizável — Student, Company, CompanyMember |
| User | Auth base — email, senha, role, TOTP |
| Course | Cursos cadastrados pelo Admin da UniALFA |
| Student | Perfil do aluno — RA, CPF, período, currículo |
| Certificate | Certificados do aluno (1:N) |
| StudentCourse | Histórico de cursos do aluno (N:N com Course) |
| Company | Empresa parceira — CNPJ, status de aprovação |
| CompanyMember | Recrutadores vinculados à empresa — CPF, role |
| Job | Vagas publicadas pelas empresas |
| Application | Candidaturas dos alunos às vagas |
| Notification | Notificações geradas pelo sistema |

Enums

Role: ADMIN | COMPANY | STUDENT

CompanyStatus: PENDING → ANALYSING → APPROVED | BLOCKED

CompanyMemberRole: ADMIN | RECRUITER

JobStatus: ACTIVE | PAUSED | CLOSED

Modality: PRESENCIAL | REMOTE | HYBRID

ApplicationStatus: PENDING → ANALYSING → APPROVED | REJECTED | CANCELLED

StudentCourseStatus: ACTIVE | COMPLETED | CANCELLED

Decisões de design

Address — tabela isolada, sem FK própria. Student, Company e CompanyMember têm addressId. ON DELETE SET NULL — endereço deletado não derruba o dono.

User — auth puro. Sem nome, sem CPF. Dados pessoais ficam em Student ou CompanyMember.

TOTP — totpSecret e totpEnabled em User. Habilitado para ADMIN e COMPANY.

CompanyMember — pessoa física da empresa. Tem CPF, endereço e nome próprios. Um User pertence a uma empresa só (userId @unique).

StudentCourse — tabela pivot com status e datas. Regra "só 1 ACTIVE por vez" no service. @@unique([studentId, courseId]) impede duplicata.

Job.courseId — nullable. Null = vaga aberta para qualquer curso. ON DELETE SET NULL — curso removido não derruba a vaga.

Soft delete — deletedAt em Job e Application. Queries sempre filtram WHERE deletedAt IS NULL.

Application — @@unique([studentId, jobId]) impede candidatura duplicada no banco.

Notification — imutável. Sem updatedAt. Só marca isRead = true.

Relacionamentos

User         1:1  Student
User         1:1  CompanyMember
User         1:N  Notification
Student      1:N  Certificate
Student      N:N  Course         (via StudentCourse)
Company      1:N  CompanyMember
Company      1:N  Job
Job          1:N  Application
Student      1:N  Application
Address      1:1  Student
Address      1:1  Company
Address      1:1  CompanyMember

Conexões ao banco

| Membro | Como conecta |
|---|---|
| Carlos (Node.js) | Prisma via DATABASE_URL |
| Vinícius (Java) | JDBC direto — porta 3306 |
| Japa (PHP) | Apenas via API Node.js |

Storage de arquivos

Currículo (PDF/DOCX) salvo no filesystem local da API.
Banco guarda só o caminho (ex: /uploads/resumes/uuid.pdf).
Lib: multer. Limite: 5MB.

este é o fluxo do banco de dados

-- ─────────────────────────────────────────────────────────────────────────────
-- Portal de Estágios UniALFA — Schema completo
-- MySQL 8.0
-- ─────────────────────────────────────────────────────────────────────────────


SET FOREIGN_KEY_CHECKS = 0;


-- ─── Address ──────────────────────────────────────────────────────────────────


CREATE TABLE `Address` (
  `id`         VARCHAR(36)  NOT NULL,
  `street`     VARCHAR(191) NOT NULL,
  `number`     VARCHAR(20)  NOT NULL,
  `complement` VARCHAR(191) NULL,
  `district`   VARCHAR(191) NOT NULL,
  `city`       VARCHAR(191) NOT NULL,
  `state`      VARCHAR(2)   NOT NULL,
  `zipCode`    VARCHAR(10)  NOT NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── User ─────────────────────────────────────────────────────────────────────


CREATE TABLE `User` (
  `id`          VARCHAR(36)  NOT NULL,
  `email`       VARCHAR(191) NOT NULL,
  `password`    VARCHAR(191) NOT NULL,
  `role`        ENUM('ADMIN', 'COMPANY', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
  `isActive`    TINYINT(1)   NOT NULL DEFAULT 1,
  `totpSecret`  VARCHAR(191) NULL,
  `totpEnabled` TINYINT(1)   NOT NULL DEFAULT 0,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `User_email_key` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Course ───────────────────────────────────────────────────────────────────


CREATE TABLE `Course` (
  `id`        VARCHAR(36)  NOT NULL,
  `name`      VARCHAR(191) NOT NULL,
  `code`      VARCHAR(50)  NULL,
  `periods`   INT          NOT NULL,
  `isActive`  TINYINT(1)   NOT NULL DEFAULT 1,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `Course_name_key` (`name`),
  UNIQUE KEY `Course_code_key` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Student ──────────────────────────────────────────────────────────────────


CREATE TABLE `Student` (
  `id`         VARCHAR(36)  NOT NULL,
  `userId`     VARCHAR(36)  NOT NULL,
  `addressId`  VARCHAR(36)  NULL,
  `name`       VARCHAR(191) NOT NULL,
  `ra`         VARCHAR(191) NOT NULL,
  `cpf`        VARCHAR(14)  NOT NULL,
  `phone`      VARCHAR(20)  NULL,
  `isEligible` TINYINT(1)   NOT NULL DEFAULT 1,
  `resumePath` VARCHAR(191) NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `Student_userId_key`    (`userId`),
  UNIQUE KEY `Student_ra_key`        (`ra`),
  UNIQUE KEY `Student_cpf_key`       (`cpf`),
  UNIQUE KEY `Student_addressId_key` (`addressId`),
  CONSTRAINT `Student_userId_fkey`
    FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `Student_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Certificate ──────────────────────────────────────────────────────────────


CREATE TABLE `Certificate` (
  `id`          VARCHAR(36)  NOT NULL,
  `studentId`   VARCHAR(36)  NOT NULL,
  `name`        VARCHAR(191) NOT NULL,
  `institution` VARCHAR(191) NULL,
  `issuedAt`    DATETIME(3)  NOT NULL,
  `filePath`    VARCHAR(191) NULL,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  KEY `Certificate_studentId_idx` (`studentId`),
  CONSTRAINT `Certificate_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── StudentCourse ────────────────────────────────────────────────────────────


CREATE TABLE `StudentCourse` (
  `id`         VARCHAR(36)  NOT NULL,
  `studentId`  VARCHAR(36)  NOT NULL,
  `courseId`   VARCHAR(36)  NOT NULL,
  `status`     ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
  `startedAt`  DATETIME(3)  NOT NULL,
  `finishedAt` DATETIME(3)  NULL,
  `createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `StudentCourse_studentId_courseId_key` (`studentId`, `courseId`),
  KEY `StudentCourse_courseId_idx` (`courseId`),
  CONSTRAINT `StudentCourse_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `StudentCourse_courseId_fkey`
    FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Company ──────────────────────────────────────────────────────────────────


CREATE TABLE `Company` (
  `id`          VARCHAR(36)  NOT NULL,
  `addressId`   VARCHAR(36)  NULL,
  `name`        VARCHAR(191) NOT NULL,
  `cnpj`        VARCHAR(18)  NOT NULL,
  `description` LONGTEXT     NULL,
  `phone`       VARCHAR(20)  NULL,
  `status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `Company_cnpj_key`      (`cnpj`),
  UNIQUE KEY `Company_addressId_key` (`addressId`),
  CONSTRAINT `Company_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── CompanyMember ────────────────────────────────────────────────────────────


CREATE TABLE `CompanyMember` (
  `id`        VARCHAR(36)  NOT NULL,
  `companyId` VARCHAR(36)  NOT NULL,
  `userId`    VARCHAR(36)  NOT NULL,
  `addressId` VARCHAR(36)  NULL,
  `role`      ENUM('ADMIN', 'RECRUITER') NOT NULL DEFAULT 'RECRUITER',
  `name`      VARCHAR(191) NOT NULL,
  `cpf`       VARCHAR(14)  NOT NULL,
  `phone`     VARCHAR(20)  NULL,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `CompanyMember_userId_key`           (`userId`),
  UNIQUE KEY `CompanyMember_cpf_key`              (`cpf`),
  UNIQUE KEY `CompanyMember_addressId_key`        (`addressId`),
  UNIQUE KEY `CompanyMember_companyId_userId_key` (`companyId`, `userId`),
  KEY `CompanyMember_companyId_idx` (`companyId`),
  CONSTRAINT `CompanyMember_companyId_fkey`
    FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `CompanyMember_userId_fkey`
    FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `CompanyMember_addressId_fkey`
    FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Job ──────────────────────────────────────────────────────────────────────


CREATE TABLE `Job` (
  `id`           VARCHAR(36)  NOT NULL,
  `companyId`    VARCHAR(36)  NOT NULL,
  `courseId`     VARCHAR(36)  NULL,
  `title`        VARCHAR(191) NOT NULL,
  `description`  LONGTEXT     NOT NULL,
  `area`         VARCHAR(191) NOT NULL,
  `requirements` LONGTEXT     NULL,
  `salary`       DOUBLE       NULL,
  `location`     VARCHAR(191) NOT NULL,
  `modality`     ENUM('PRESENCIAL', 'REMOTE', 'HYBRID') NOT NULL DEFAULT 'PRESENCIAL',
  `status`       ENUM('ACTIVE', 'PAUSED', 'CLOSED')     NOT NULL DEFAULT 'ACTIVE',
  `deletedAt`    DATETIME(3)  NULL,
  `createdAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  KEY `Job_companyId_idx` (`companyId`),
  KEY `Job_courseId_idx`  (`courseId`),
  CONSTRAINT `Job_companyId_fkey`
    FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
  CONSTRAINT `Job_courseId_fkey`
    FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Application ──────────────────────────────────────────────────────────────


CREATE TABLE `Application` (
  `id`          VARCHAR(36)  NOT NULL,
  `studentId`   VARCHAR(36)  NOT NULL,
  `jobId`       VARCHAR(36)  NOT NULL,
  `status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
  `resumePath`  VARCHAR(191) NULL,
  `coverLetter` LONGTEXT     NULL,
  `deletedAt`   DATETIME(3)  NULL,
  `createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  UNIQUE KEY `Application_studentId_jobId_key` (`studentId`, `jobId`),
  KEY `Application_jobId_idx` (`jobId`),
  CONSTRAINT `Application_studentId_fkey`
    FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `Application_jobId_fkey`
    FOREIGN KEY (`jobId`)     REFERENCES `Job`(`id`)     ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─── Notification ─────────────────────────────────────────────────────────────


CREATE TABLE `Notification` (
  `id`        VARCHAR(36)  NOT NULL,
  `userId`    VARCHAR(36)  NOT NULL,
  `title`     VARCHAR(191) NOT NULL,
  `message`   LONGTEXT     NOT NULL,
  `type`      VARCHAR(191) NOT NULL,
  `isRead`    TINYINT(1)   NOT NULL DEFAULT 0,
  `createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),


  PRIMARY KEY (`id`),
  KEY `Notification_userId_idx` (`userId`),
  CONSTRAINT `Notification_userId_fkey`
    FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ─────────────────────────────────────────────────────────────────────────────


SET FOREIGN_KEY_CHECKS = 1;


siga com base nisso



📊 Análise do Banco de Dados — Portal de Estágios UniALFA

📋 Índice

Visão Geral

Tabelas e Estrutura

Relacionamentos

Fluxos Principais

Integridade Referencial

Considerações de Design

Visão Geral

O banco segue um padrão relacional normalizado com 11 tabelas, focando em:

✅ Usuários (User) com controle de papel (ADMIN, COMPANY, STUDENT)

✅ Estudantes com perfil expandido (Student)

✅ Empresas com equipe de recrutadores (Company, CompanyMember)

✅ Ofertas de trabalho (Job)

✅ Candidaturas de estudantes (Application)

✅ Endereços reutilizáveis (Address)

✅ Certificados e histórico acadêmico

Tabelas e Estrutura

1️⃣ Address (Endereços)

Propósito: Reutilizável para Students, Companies e CompanyMembers

┌─ Address ────────────────────┐
├─ id (PK, UUID)              │
├─ street                      │
├─ number                      │
├─ complement (nullable)       │
├─ district                    │
├─ city                        │
├─ state (2 chars)            │
├─ zipCode                     │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Usada por 3 entidades (normalizando dados de endereço)

Endereços podem ser opcionais (nullable em alguns casos)

Auditoria temporal com createdAt e updatedAt

2️⃣ User (Usuários Base)

Propósito: Autenticação e autorização central

┌─ User ───────────────────────┐
├─ id (PK, UUID)              │
├─ email (UNIQUE)             │
├─ password (hashed)          │
├─ role (ENUM)                │
│  └─ ADMIN, COMPANY, STUDENT │
├─ isActive (boolean)         │
├─ totpSecret / totpEnabled   │ ← 2FA
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Base de autenticação para todo sistema

Roles definem tipo de usuário

Suporta 2FA via TOTP

Email único (garante identificação)

3️⃣ Course (Cursos)

Propósito: Cursos oferecidos pela universidade

┌─ Course ─────────────────────┐
├─ id (PK, UUID)              │
├─ name (UNIQUE)              │
├─ code (UNIQUE, nullable)    │
├─ periods (INT)              │ ← Semestres
├─ isActive (boolean)         │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Identifica cada curso da universidade

periods = quantos semestres tem o curso

Pode estar inativo mas manter histórico

4️⃣ Student (Estudantes)

Propósito: Perfil expandido do estudante

┌─ Student ────────────────────┐
├─ id (PK, UUID)              │
├─ userId (FK → User) [UNIQUE]│
├─ addressId (FK → Address)   │
├─ name                       │
├─ ra (RA único)              │
├─ cpf (UNIQUE)               │
├─ phone                      │
├─ period (INT)               │ ← Semestre atual
├─ isEligible (boolean)       │ ← Pode estagiar?
├─ resumePath (file path)     │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Extends User (1:1 relationship)

isEligible: controla se pode fazer estágio

resumePath: onde está armazenado o currículo

Mantém RA (Registro Acadêmico)

5️⃣ Certificate (Certificados)

Propósito: Certificações extras do estudante

┌─ Certificate ────────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ name                       │
├─ institution                │
├─ issuedAt (data)            │
├─ filePath                   │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Many-to-One com Student (um aluno, muitos certificados)

Cascata: deleta certificados ao deletar estudante

Armazena arquivo do certificado

6️⃣ StudentCourse (Inscrição em Cursos)

Propósito: Histórico acadêmico — qual estudante fez qual curso

┌─ StudentCourse ──────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ courseId (FK → Course)     │
├─ status (ENUM)              │
│  └─ ACTIVE, COMPLETED,      │
│     CANCELLED               │
├─ startedAt (data)           │
├─ finishedAt (nullable)      │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (studentId,courseId)│
└──────────────────────────────┘

Características:

Junção Many-to-Many entre Student e Course

Restrição: um aluno não pode estar 2x no mesmo curso

Aluno deletado → registros cascateiam

Curso deletado → bloqueia (RESTRICT) se tem alunos ativos

7️⃣ Company (Empresas)

Propósito: Empresa que oferece estágios

┌─ Company ────────────────────┐
├─ id (PK, UUID)              │
├─ addressId (FK → Address)   │
├─ name                       │
├─ cnpj (UNIQUE)              │
├─ description (LONGTEXT)     │
├─ phone                      │
├─ status (ENUM)              │
│  └─ PENDING, ANALYSING,     │
│     APPROVED, BLOCKED       │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Status controla se pode publicar vagas

CNPJ único (empresa não pode repetir)

Opcional ter endereço (SET NULL se deletado)

Descrição em LONGTEXT para apresentação

8️⃣ CompanyMember (Membros da Empresa)

Propósito: Recrutadores/admins que atuam em nome da company

┌─ CompanyMember ──────────────┐
├─ id (PK, UUID)              │
├─ companyId (FK → Company)   │
├─ userId (FK → User)[UNIQUE] │
├─ addressId (FK → Address)   │
├─ role (ENUM)                │
│  └─ ADMIN, RECRUITER        │
├─ name                       │
├─ cpf (UNIQUE)               │
├─ phone                      │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (companyId, userId) │
└──────────────────────────────┘

Características:

Extends User (1:1 por user)

Um user não pode estar em 2 companies (UNIQUE)

Company deletada → todos membros deletados (CASCADE)

Diferencia ADMIN (gerencia) de RECRUITER (publica vagas)

9️⃣ Job (Vagas de Estágio)

Propósito: Oportunidade de trabalho publicada por company

┌─ Job ────────────────────────┐
├─ id (PK, UUID)              │
├─ companyId (FK → Company)   │
├─ courseId (FK → Course)     │
├─ title                      │
├─ description (LONGTEXT)     │
├─ area (VARCHAR)             │
├─ requirements (LONGTEXT)    │
├─ salary (DOUBLE, nullable)  │
├─ location                   │
├─ modality (ENUM)            │
│  └─ PRESENCIAL, REMOTE,     │
│     HYBRID                  │
├─ status (ENUM)              │
│  └─ ACTIVE, PAUSED, CLOSED  │
├─ deletedAt (soft delete)    │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Vinculada a uma Company (obrigatório)

Opcionalmente vinculada a um Course (para direcionar)

deletedAt: soft delete (não remove do BD)

Status controla visibilidade: ACTIVE (visível), PAUSED (oculta), CLOSED (finalizada)

🔟 Application (Candidaturas)

Propósito: Quando um estudante se candidata a uma vaga

┌─ Application ────────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ jobId (FK → Job)           │
├─ status (ENUM)              │
│  └─ PENDING, ANALYSING,     │
│     APPROVED, REJECTED,     │
│     CANCELLED               │
├─ resumePath (nullable)      │
├─ coverLetter (LONGTEXT)     │
├─ deletedAt (soft delete)    │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (studentId, jobId)  │
└──────────────────────────────┘

Características:

Estudante não pode se candidatar 2x à mesma vaga

Armazena currículo específico da candidatura (pode diferir do resumePath do Student)

Status acompanha o andamento: PENDING → ANALYSING → APPROVED/REJECTED

Soft delete preserva histórico

1️⃣1️⃣ Notification (Notificações)

Propósito: Sistema de notificações para usuários

┌─ Notification ───────────────┐
├─ id (PK, UUID)              │
├─ userId (FK → User)         │
├─ title                      │
├─ message (LONGTEXT)         │
├─ type (VARCHAR)             │ ← Tipo de notif
├─ isRead (boolean)           │
├─ createdAt (sem updatedAt)  │
└──────────────────────────────┘

Características:

Simples e direta

Sem updatedAt (notificação é imutável)

type classifica: 'job-published', 'application-status', etc.

Cascata ao deletar usuário

Relacionamentos

📍 Mapa de ForeignKeys

                          ┌─────────────────┐
                          │       User      │
                          │   (base auth)   │
                          └────────┬────────┘
                                   │
                  ┌────────────┬────┴────┬──────────────┐
                  │            │         │              │
                  ▼            ▼         ▼              ▼
            ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────┐
            │ Student │  │ Company  │  │Notif...  │  │CompanyMember│
            │ (1:1)   │  │ Member   │  │(1:many)  │  │  (1:1 ext)  │
            └────┬────┘  │ (1:1)    │  └──────────┘  └──────┬──────┘
                 │       └────┬─────┘                       │
                 │            │                             │
          ┌──────▼──────┐     ▼                      ┌──────▼──────┐
          │   Address   │  Company                   │   Address   │
          │(reutiliz.)  │ (1:many)                   │(reutiliz.)  │
          └─────────────┘     │                      └─────────────┘
                              │
                         ┌────▼────┐
                         │   Job   │
                         │(1:many) │
                         └────┬────┘
                              │
                         ┌────▼─────────┐
                         │ Application   │
                         │(N:M indirect) │
                         └───────────────┘

🔗 Relações Detalhadas

De Para Tipo Ação Delete Descrição Student User 1:1 CASCADE Estudante extends User Student Address 1:N SET NULL Endereço pode ser deletado StudentCourse Student N:1 CASCADE Deleta histórico ao deletar aluno StudentCourse Course N:1 RESTRICT Impede deletar curso com alunos Company Address 1:N SET NULL Endereço pode ser deletado CompanyMember Company N:1 CASCADE Deleta membro ao deletar company CompanyMember User 1:1 CASCADE Deleta membro ao deletar user CompanyMember Address 1:N SET NULL Endereço pode ser deletado Job Company N:1 CASCADE Deleta vagas ao deletar company Job Course N:1 SET NULL Vaga pode perder vínculo com course Application Student N:1 CASCADE Deleta candidaturas ao deletar aluno Application Job N:1 CASCADE Deleta candidaturas ao deletar vaga Certificate Student N:1 CASCADE Deleta certificados ao deletar aluno Notification User N:1 CASCADE Deleta notificações ao deletar user

Fluxos Principais

🎓 Fluxo 1: Estudante se Candidata a Vaga

1. User (role=STUDENT) cria conta
   ↓
2. Student preenche perfil (RA, CPF, período, resumePath)
   ↓
3. Student visualiza Jobs (status = ACTIVE)
   ↓
4. Student cria Application (studentId, jobId)
   ↓
5. Application vai para status = PENDING
   ↓
6. CompanyMember (recruiter) analisa → APPROVED/REJECTED
   ↓
7. Notification enviada ao Student

🏢 Fluxo 2: Empresa Publica Vaga

1. User (role=COMPANY) cria conta
   ↓
2. Company cria perfil (CNPJ, status=PENDING)
   ↓
3. Admin aprova Company (status=APPROVED)
   ↓
4. CompanyMember (admin) cria vagas (Job, status=ACTIVE)
   ↓
5. Estudantes veem a vaga
   ↓
6. Recruiter analisa candidaturas (Application)

📚 Fluxo 3: Histórico Acadêmico

1. Student em um Course específico
   ↓
2. Cria StudentCourse (status=ACTIVE)
   ↓
3. Ao fim: StudentCourse (status=COMPLETED)
   ↓
4. Student adiciona Certificate (comprovante)
   ↓
5. Resumo: cursos + certificados = Perfil completo

Integridade Referencial

✅ Constraints Importantes

CASCATA (CASCADE)

Estudante deletado → Applications, Certificates, StudentCourses deletados

Company deletada → Jobs, CompanyMembers deletados

User deletado → Student, CompanyMember, Notifications deletados

Job deletado → Applications deletadas

BLOQUEIA (RESTRICT)

Curso com alunos registrados → não pode deletar

Protege integridade do histórico acadêmico

SET NULL

Address deletado → referências viram NULL

Permite manter user sem endereço

⚠️ Pontos de Atenção

Soft Deletes em Job e Application

deletedAt permite recuperação de dados

Queries precisam filtrar WHERE deletedAt IS NULL

UNIQUEs Críticos

User.email → impossível 2 usuários com mesmo email

Student.cpf, Student.ra → identidade do aluno

Company.cnpj → identidade da empresa

Application (studentId, jobId) → impede candidatura duplicada

ENUM Roles e Status

Validação acontece no BD, não só na app

Impede estados inválidos

Considerações de Design

💡 O que Está Bem Feito

✅ Normalização adequada

Address reutilizável (3NF respeitada)

StudentCourse quebra Many-to-Many corretamente

Dados duplicados minimizados

✅ Auditoria temporal

createdAt/updatedAt em quase todas tabelas

Rastreabilidade completa

✅ Soft Deletes

Job e Application usam deletedAt

Histórico preservado

✅ Roles de Usuário

ADMIN, COMPANY, STUDENT bem separados

Extensões específicas (Student, CompanyMember)

✅ Flexibilidade

Campos nullable bem pensados (complement, description, salary)

2FA optativo mas suportado (totpSecret, totpEnabled)

🤔 Possíveis Melhorias Futuras

Auditoria de Quem Alterou Quê

Adicionar updatedBy em tabelas críticas

Histórico de Status

Tabela separada: ApplicationStatusHistory

Rastreia quem/quando muda status

Ratings/Feedback

Tabela: ApplicationFeedback ou CompanyReview

Estudante avalia empresa, empresa avalia aluno

Skill Tags

Tabela: JobSkill, StudentSkill

Relaciona skills específicas (Python, React, etc)

Interview Schedule

Tabela: Interview

Rastreia entrevistas (data, resultado)

🎯 Resumo Executivo

Aspecto Status Tabelas 11 tabelas bem estruturadas Normalização 3NF (Terceira Forma Normal) Relacionamentos 14 Foreign Keys com integridade Cascade/Restrict Bem balanceado Auditoria createdAt/updatedAt presente Soft Deletes Job, Application Roles ADMIN, COMPANY, STUDENT Fluxo Principal Student → Application → Job ← Company

Conclusão: Schema robusto e pronto para produção! 🚀

explicação sobre o banco resumidamente

crie um front end consumindo rotas diretamente desse banco
crie por partes pergutando se pode continuar e perguntando 

aqui está todo o contexto do projeto, use isto para ciarmos o projeto juntos, utilizamdo php orientado a objetos como a linguagem backend e consumo das rotas da api via php

POO (PHP Orientado a Objetos)
Para a camada de interação, deve ser construída uma aplicação web em PHP que consuma a
API Node.JS. O rigor na modelagem orientada a objetos é essencial.
● Modelagem de Domínio: É obrigatória a criação e utilização de classes bem definidas
para representar as entidades centrais do negócio, no mínimo: Aluno, Empresa, Vaga e
Candidatura.
● Painel da Empresa: Desenvolver uma área restrita (Back Office) onde a empresa possa
realizar o CRUD (Criar, Ler, Atualizar, Excluir) de suas vagas e visualizar a lista de alunos
candidatos a cada uma delas.
● Portal do Aluno: Desenvolver a interface onde o aluno visualiza a listagem de vagas
disponíveis (consumidas da API) e um formulário para submeter sua candidatura.
● Integração: A aplicação PHP não deve acessar o banco de dados diretamente; todas as
operações de leitura e escrita devem ser feitas através de requisições HTTP à API
Node.JS.
● Boas Práticas: É fundamental o uso dos conceitos de POO (encapsulamento, herança,
polimorfismo, separação de responsabilidades). O código deve ser organizado e limpo. A
não aplicação destes fundamentos possui caráter eliminatório.

crie de formas simples de explicar nada muito complexo