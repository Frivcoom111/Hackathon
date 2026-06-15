# Portal de Estágios UniALFA

> Conectando talentos às oportunidades locais.

Plataforma para aproximar os alunos da Faculdade UniALFA das empresas da região
que buscam estagiários. Empresas publicam e gerenciam vagas, alunos visualizam
as oportunidades e se candidatam, e a instituição administra todo o ecossistema
por um back office desktop.

Projeto desenvolvido para o **Hackathon Institucional UniALFA** com uma
arquitetura distribuída: uma API central e três interfaces que a consomem (ou,
no caso do desktop, acessam o banco diretamente).

---

## 📋 Índice

- [Arquitetura](#-arquitetura)
- [Tecnologias](#-tecnologias)
- [Pré-requisitos](#-pré-requisitos)
- [Como rodar (passo a passo)](#-como-rodar-passo-a-passo)
  - [1. Banco de dados (Docker)](#1-banco-de-dados-docker)
  - [2. API Node.js](#2-api-nodejs)
  - [3. Front-end Web (PHP)](#3-front-end-web-php)
  - [4. Back Office Desktop (Java Swing)](#4-back-office-desktop-java-swing)
- [Estrutura do projeto](#-estrutura-do-projeto)
- [Modelo de dados](#-modelo-de-dados)
- [Documentação da API](#-documentação-da-api)
- [Solução de problemas](#-solução-de-problemas)

---

## 🏗 Arquitetura

```
                         ┌──────────────────────┐
                         │   MySQL 8.0 (3306)    │
                         │   Docker / phpMyAdmin │
                         └──────────┬───────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │ Prisma              │ JDBC direto          │
              ▼                     │                      ▼
   ┌─────────────────────┐         │           ┌─────────────────────┐
   │  API Node.js (3000) │         │           │  Back Office Java   │
   │  Express + Prisma   │         └───────────│  Swing Desktop      │
   └──────────┬──────────┘                     │  (admin UniALFA)    │
              │ HTTP/JSON                       └─────────────────────┘
              ▼
   ┌─────────────────────┐
   │  Front-end Web PHP   │
   │  Aluno + Empresa     │
   └─────────────────────┘
```

| Camada | Pasta | Quem usa | Como acessa os dados |
|---|---|---|---|
| **API REST** | `api/` | Todos | Prisma → MySQL |
| **Web (Aluno/Empresa)** | `web/` | Alunos e empresas | Apenas via HTTP na API |
| **Back Office** | `portal-desktop-swing/` | Equipe UniALFA | JDBC direto no MySQL |
| **Banco** | `docker-compose.yml` | — | MySQL + phpMyAdmin |

> A API é o **coração do sistema**. O PHP nunca acessa o banco diretamente — só
> consome a API. O desktop Java conecta direto no MySQL (rede interna da faculdade).

---

## 🛠 Tecnologias

- **API:** Node.js, TypeScript, Express 5, Prisma 7, MySQL, Zod, JWT, bcrypt, TOTP (2FA), multer
- **Web:** PHP 8 (POO), Composer, Guzzle, Bootstrap 5
- **Desktop:** Java 17, Maven, Swing, HikariCP, MySQL Connector/J, dotenv-java, bcrypt
- **Infra:** Docker, Docker Compose, MySQL 8.0, phpMyAdmin

---

## ✅ Pré-requisitos

Instale antes de começar:

- [Docker Desktop](https://www.docker.com/) (para o banco)
- [Node.js 20+](https://nodejs.org/) e [pnpm](https://pnpm.io/) (`npm install -g pnpm`)
- [PHP 8.1+](https://www.php.net/downloads) e [Composer](https://getcomposer.org/)
- [Java JDK 17+](https://adoptium.net/) e [Maven](https://maven.apache.org/)

> Você não precisa instalar MySQL na máquina — o Docker cuida disso.

---

## 🚀 Como rodar (passo a passo)

A ordem importa: **banco → API → web/desktop**.

### 1. Banco de dados (Docker)

Na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe dois containers:

- **MySQL** em `localhost:3306` (banco `hackathon`, usuário `root`, senha `root`)
- **phpMyAdmin** em [http://localhost:8081](http://localhost:8081) (servidor: `mysql`, usuário: `root`, senha: `root`)

### 2. API Node.js

```bash
cd api
pnpm install
```

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

Preencha o `.env` com os valores abaixo (compatíveis com o Docker acima):

```env
# Server
PORT=3000
NODE_ENV=development

# Database
DATABASE_URL="mysql://root:root@localhost:3306/hackathon"
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_USER=root
DATABASE_PASSWORD=root
DATABASE_NAME=hackathon

# Auth (JWT_SECRET precisa ter no mínimo 32 caracteres)
JWT_SECRET=troque-este-segredo-por-uma-string-bem-longa-123456
JWT_EXPIRES_IN=1d
SALT=10

# CORS
CORS_ORIGIN=*

# Rate limit
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX=100
```

Gere o client do Prisma, rode as migrations e popule os dados iniciais (seed):

```bash
pnpm db:generate
pnpm db:migrate:deploy
pnpm db:seed
```

Suba a API em modo de desenvolvimento:

```bash
pnpm dev
```

A API estará em **[http://localhost:3000](http://localhost:3000)** e a documentação
interativa em **[http://localhost:3000/docs](http://localhost:3000/docs)**.

**Scripts úteis da API:**

| Comando | O que faz |
|---|---|
| `pnpm dev` | Sobe a API com hot-reload |
| `pnpm db:migrate` | Cria/aplica migrations em desenvolvimento |
| `pnpm db:migrate:deploy` | Aplica migrations existentes (uso comum) |
| `pnpm db:seed` | Popula cursos, empresas e vagas iniciais |
| `pnpm db:studio` | Abre o Prisma Studio (visualizar o banco) |
| `pnpm check` | Roda lint + format (Biome) |

### 3. Front-end Web (PHP)

Com a **API rodando**, em outro terminal:

```bash
cd web
composer install
```

A URL da API já vem configurada como `http://localhost:3000` em
[web/app/Config/Config.php](web/app/Config/Config.php). Suba o servidor embutido
do PHP a partir da pasta `web/src`:

```bash
php -S localhost:8000 -t src
```

Acesse **[http://localhost:8000](http://localhost:8000)**.

Páginas principais (via `?page=`):

- `?page=home` — início
- `?page=vagas` — listagem de vagas (Portal do Aluno)
- `?page=login` / `?page=cadastro` — autenticação
- `?page=empresa-dashboard` — painel da empresa (Back Office web)

### 4. Back Office Desktop (Java Swing)

Aplicação administrativa da UniALFA. Conecta **direto** no MySQL.

```bash
cd portal-desktop-swing
```

Crie um arquivo `.env` na pasta `portal-desktop-swing/` com as credenciais do banco:

```env
DB_URL=jdbc:mysql://localhost:3306/hackathon
DB_USER=root
DB_PASSWORD=root
```

Compile e execute:

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.portal.Main"
```

> Alternativa: abra o projeto na sua IDE (IntelliJ/Eclipse) e rode a classe
> [`com.portal.Main`](portal-desktop-swing/src/main/java/com/portal/Main.java).

---

## 📁 Estrutura do projeto

```
Hackathon/
├── docker-compose.yml         # MySQL + phpMyAdmin
├── api/                       # API REST (Node.js + TypeScript + Prisma)
│   ├── prisma/                # schema, migrations e seed
│   ├── src/
│   │   ├── app.ts             # configuração do Express
│   │   ├── server.ts          # ponto de entrada
│   │   ├── config/            # validação de env (Zod)
│   │   ├── modules/           # auth, student, company, jobs, address,
│   │   │                      #   notification, course (controller/service/repo)
│   │   ├── shared/            # middlewares, erros, utils
│   │   └── docs/              # OpenAPI / Scalar
│   └── docs/                  # documentação por módulo (markdown)
├── web/                       # Front-end PHP (POO)
│   ├── app/                   # Api, Auth, Config, Services
│   ├── src/
│   │   ├── index.php          # roteador (?page=)
│   │   ├── classes/           # Aluno, Empresa, Vaga, Candidatura
│   │   ├── pages/             # telas (publico, aluno, empresa, auth)
│   │   └── assets/            # css, imagens, Bootstrap
│   └── composer.json
└── portal-desktop-swing/      # Back Office Java Swing (Maven)
    ├── pom.xml
    └── src/main/java/com/portal/
        ├── Main.java          # ponto de entrada
        ├── config/            # pool de conexão (HikariCP)
        ├── gui/               # telas Swing
        ├── model/             # entidades de domínio
        ├── service/           # regras de negócio
        ├── dao/               # acesso a dados (JDBC)
        └── util/              # utilitários
```

---

## 🗃 Modelo de dados

O banco tem **11 tabelas** (MySQL, normalizado em 3NF):

| Tabela | Responsabilidade |
|---|---|
| `Address` | Endereço reutilizável (aluno, empresa, membro) |
| `User` | Autenticação base — email, senha, role, 2FA (TOTP) |
| `Course` | Cursos da universidade |
| `Student` | Perfil do aluno — RA, CPF, currículo, elegibilidade |
| `Certificate` | Certificados do aluno (1:N) |
| `StudentCourse` | Histórico acadêmico (N:N aluno↔curso) |
| `Company` | Empresa parceira — CNPJ, status de aprovação |
| `CompanyMember` | Recrutadores/admins vinculados à empresa |
| `Job` | Vagas publicadas (soft delete) |
| `Application` | Candidaturas dos alunos (soft delete) |
| `Notification` | Notificações geradas pelo sistema |

**Papéis (`User.role`):** `ADMIN`, `COMPANY`, `STUDENT`.

**Fluxo principal:** `Student` → `Application` → `Job` ← `Company`.

> Detalhes completos do schema estão em
> [api/prisma/schema.prisma](api/prisma/schema.prisma).

---

## 📚 Documentação da API

Com a API rodando em modo `development`, acesse a referência interativa
(Scalar/OpenAPI) em:

**[http://localhost:3000/docs](http://localhost:3000/docs)**

Rotas principais:

| Prefixo | Módulo |
|---|---|
| `/auth` | Login, cadastro, 2FA |
| `/student` | Alunos e currículos |
| `/company` | Empresas e membros |
| `/jobs` | Vagas |
| `/courses` | Cursos |
| `/address` | Endereços |
| `/notifications` | Notificações |

Há também uma coleção do Insomnia em
[api/docs/requests/](api/docs/requests/) e docs por módulo em
[api/docs/modules/](api/docs/modules/).

---

## 🩺 Solução de problemas

| Problema | Causa provável | Solução |
|---|---|---|
| `Environments incompleto` ao subir a API | `.env` faltando campos | Confira o passo 2 — `JWT_SECRET` precisa de 32+ caracteres |
| API não conecta no banco | MySQL não está pronto | Aguarde o healthcheck do Docker ou rode `docker compose ps` |
| Web mostra erro de conexão | API não está rodando | Suba a API (passo 2) antes do PHP |
| `class not found` no Java | `.env` ausente ou deps faltando | Crie o `.env` e rode `mvn clean compile` |
| Porta 3306/3000/8000 em uso | Outro serviço ocupando | Pare o serviço ou troque a porta |

Para resetar o banco do zero:

```bash
docker compose down -v   # apaga o volume do MySQL
docker compose up -d
# depois rode novamente as migrations e o seed da API
```
</content>
</invoke>
