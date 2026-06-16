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

## Índice

- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Como rodar (passo a passo)](#como-rodar-passo-a-passo)
  - [1. Banco de dados (Docker)](#1-banco-de-dados-docker)
  - [2. API Node.js](#2-api-nodejs)
  - [3. Front-end Web (PHP)](#3-front-end-web-php)
  - [4. Back Office Desktop (Java Swing)](#4-back-office-desktop-java-swing)
- [Credenciais de acesso (seed)](#credenciais-de-acesso-seed)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Modelo de dados](#modelo-de-dados)
- [Documentação da API](#documentação-da-api)
- [Solução de problemas](#solução-de-problemas)

---

## Arquitetura

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

## Tecnologias

- **API:** Node.js, TypeScript, Express 5, Prisma 7, MySQL, Zod, JWT, bcrypt, TOTP (2FA), multer
- **Web:** PHP 8 (POO), Composer, Guzzle, Bootstrap 5
- **Desktop:** Java 17, Maven, Swing, HikariCP, MySQL Connector/J, dotenv-java, bcrypt
- **Infra:** Docker, Docker Compose, MySQL 8.0, phpMyAdmin

---

## Pré-requisitos

Instale antes de começar:

- [Docker Desktop](https://www.docker.com/) — para o banco de dados
- [Node.js 20+](https://nodejs.org/) e [pnpm](https://pnpm.io/) (`npm install -g pnpm`)
- [PHP 8.1+](https://www.php.net/downloads) e [Composer](https://getcomposer.org/)
- [Java JDK 17+](https://adoptium.net/) e [Maven](https://maven.apache.org/)

> Você não precisa instalar MySQL na máquina — o Docker cuida disso.

---

## Como rodar (passo a passo)

A ordem importa: **banco → API → web / desktop**.

### 1. Banco de dados (Docker)

Na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe dois containers:

- **MySQL** em `localhost:3306` — banco `hackathon`, usuário `root`, senha `root`
- **phpMyAdmin** em [http://localhost:8081](http://localhost:8081) — servidor: `mysql`, usuário: `root`, senha: `root`

Aguarde o healthcheck do MySQL ficar `healthy` antes de continuar:

```bash
docker compose ps
```

---

### 2. API Node.js

```bash
cd api
pnpm install
```

Crie o arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

Edite o `.env` com os valores abaixo (compatíveis com o Docker acima):

```env
# Server
PORT=3000
NODE_ENV=development

# Database
DATABASE_URL=mysql://root:root@localhost:3306/hackathon
DATABASE_HOST=localhost
DATABASE_PORT=3306
DATABASE_USER=root
DATABASE_PASSWORD=root
DATABASE_NAME=hackathon

# Auth — JWT_SECRET precisa ter no mínimo 32 caracteres
JWT_SECRET=troque-este-segredo-por-uma-string-bem-longa-123456
JWT_EXPIRES_IN=1d
SALT=10

# CORS
FRONTEND_URL=*

# Rate limit
RATE_LIMIT_WINDOW_MS=900000
RATE_LIMIT_MAX=100
```

Gere o client do Prisma, aplique as migrations e popule o banco:

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
| `pnpm db:generate` | Gera o Prisma Client a partir do schema |
| `pnpm db:migrate` | Cria e aplica uma nova migration (dev) |
| `pnpm db:migrate:deploy` | Aplica migrations existentes sem criar novas |
| `pnpm db:seed` | Popula o banco com dados iniciais |
| `pnpm db:studio` | Abre o Prisma Studio (visualizar/editar o banco) |
| `pnpm check` | Roda lint + format (Biome) |

---

### 3. Front-end Web (PHP)

Com a **API rodando**, em outro terminal:

```bash
cd web
composer install
```

A URL da API já vem configurada como `http://localhost:3000` em
[web/app/Config/Config.php](web/app/Config/Config.php) — não é necessário nenhum `.env`.

Suba o servidor embutido do PHP a partir da pasta `web/src`:

```bash
php -S localhost:8000 -t src
```

Acesse **[http://localhost:8000](http://localhost:8000)**.

Páginas principais (via `?page=`):

| Página | URL |
|---|---|
| Início | `http://localhost:8000` |
| Lista de vagas | `?page=vagas` |
| Login | `?page=login` |
| Cadastro | `?page=cadastro` |
| Painel da empresa | `?page=empresa-dashboard` |
| Perfil do aluno | `?page=aluno-perfil` |
| Notificações | `?page=notificacoes` |

---

### 4. Back Office Desktop (Java Swing)

Aplicação administrativa da UniALFA. Conecta **diretamente** no MySQL (sem passar pela API).

```bash
cd portal-desktop-swing
```

Crie um arquivo `.env` dentro de `portal-desktop-swing/` com as credenciais do banco:

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

> Alternativa: abra o projeto na sua IDE (IntelliJ / Eclipse) e rode a classe
> [`com.portal.Main`](portal-desktop-swing/src/main/java/com/portal/Main.java).

---

## Credenciais de acesso (seed)

Após rodar `pnpm db:seed` as seguintes contas estarão disponíveis:

| Tipo | E-mail | Senha |
|---|---|---|
| Admin UniALFA | `admin@unialfa.com` | `Admin@123` |
| Empresa Admin (Tech Local) | `empresa@techlocal.com` | `Empresa@123` |
| Empresa Admin (Agência Alfa) | `empresa@agenciaalfa.com` | `Empresa@123` |
| Empresa Admin (Winfo) | `empresa@winfo.com` | `Empresa@123` |
| Recruiter (Tech Local) | `recruiter@techlocal.com` | `Recruit@123` |
| Aluno | `joao@aluno.com` | `Aluno@1234` |
| Aluno | `maria@aluno.com` | `Aluno@1234` |
| Aluno | `lucas@aluno.com` | `Aluno@1234` |

> **Atenção — 2FA (TOTP):** As contas de Admin e Empresa possuem autenticação de
> dois fatores obrigatória. Para logar, adicione a chave abaixo no **Google
> Authenticator** ou **Authy** e use o código gerado:
>
> ```
> JBSWY3DPEHPK3PXP
> ```
>
> Contas de Aluno **não** exigem 2FA.

---

## Estrutura do projeto

```
Hackathon/
├── docker-compose.yml              # MySQL + phpMyAdmin
├── api/                            # API REST (Node.js + TypeScript + Prisma)
│   ├── .env.example                # modelo de variáveis de ambiente
│   ├── prisma/
│   │   ├── schema.prisma           # definição do banco
│   │   ├── migrations/             # histórico de migrations
│   │   ├── data.ts                 # constantes estáticas do seed
│   │   └── seed.ts                 # script de seed (transacional)
│   └── src/
│       ├── server.ts               # ponto de entrada
│       ├── app.ts                  # configuração do Express
│       ├── config/                 # validação de env (Zod)
│       ├── modules/                # auth, student, company, jobs,
│       │                           #   address, notification, course
│       ├── shared/                 # middlewares, erros, utils
│       └── docs/                   # OpenAPI / Scalar
├── web/                            # Front-end PHP (POO)
│   ├── app/
│   │   ├── Api/                    # ApiClient + Api (facade)
│   │   ├── Auth/                   # JwtManager, Guard
│   │   ├── Config/                 # Config.php (URL da API)
│   │   └── Services/               # AuthService, VagaService, etc.
│   ├── src/
│   │   ├── index.php               # roteador (?page=)
│   │   ├── layouts/                # header.php, footer.php
│   │   ├── classes/                # Aluno, Empresa, Vaga, Candidatura
│   │   ├── pages/                  # telas (publico, aluno, empresa, auth)
│   │   └── assets/                 # css, imagens, Bootstrap
│   └── composer.json
└── portal-desktop-swing/           # Back Office Java Swing (Maven)
    ├── pom.xml
    └── src/main/java/com/portal/
        ├── Main.java               # ponto de entrada
        ├── config/                 # pool de conexão (HikariCP)
        ├── gui/                    # telas Swing
        ├── model/                  # entidades de domínio
        ├── service/                # regras de negócio
        ├── dao/                    # acesso a dados (JDBC)
        └── util/                   # utilitários
```

---

## Modelo de dados

O banco tem **11 tabelas** (MySQL, normalizado em 3NF):

| Tabela | Responsabilidade |
|---|---|
| `Address` | Endereço reutilizável (aluno, empresa) |
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

## Documentação da API

Com a API rodando em modo `development`, acesse a referência interativa
(Scalar / OpenAPI) em:

**[http://localhost:3000/docs](http://localhost:3000/docs)**

Rotas principais:

| Prefixo | Módulo |
|---|---|
| `/auth` | Login, cadastro (aluno e empresa), 2FA |
| `/student` | Alunos e currículos |
| `/company` | Empresas e membros |
| `/jobs` | Vagas |
| `/courses` | Cursos |
| `/address` | Endereços |
| `/notifications` | Notificações |

---

## Solução de problemas

| Problema | Causa provável | Solução |
|---|---|---|
| `Environments incompleto` ao subir a API | `.env` faltando ou com campos errados | Confira o passo 2 — `JWT_SECRET` precisa de 32+ caracteres; use `FRONTEND_URL`, não `CORS_ORIGIN` |
| API não conecta no banco | MySQL ainda inicializando | Aguarde o healthcheck: `docker compose ps` deve mostrar `healthy` |
| Web mostra erro de conexão com a API | API não está rodando | Suba a API (passo 2) antes do PHP |
| Seed já executado (nada criado) | Seed é idempotente — já existe o admin no banco | Reset o banco abaixo ou use as contas existentes |
| `class not found` no Java | `.env` ausente ou dependências não baixadas | Crie o `.env` e rode `mvn clean compile` |
| Porta 3306 / 3000 / 8000 em uso | Outro serviço ocupando a porta | Pare o serviço conflitante ou troque a porta no `.env` / comando PHP |
| Login de admin/empresa não funciona | 2FA não configurado no app autenticador | Adicione a chave `JBSWY3DPEHPK3PXP` no Google Authenticator / Authy |

**Resetar o banco do zero:**

```bash
docker compose down -v   # apaga o volume do MySQL
docker compose up -d
# aguarde o healthcheck e rode novamente:
cd api
pnpm db:migrate:deploy
pnpm db:seed
```