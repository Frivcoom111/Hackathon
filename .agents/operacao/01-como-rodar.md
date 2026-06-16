# Operação — Como Rodar o Projeto

> A ordem importa: **banco → API → web / desktop**.

## Pré-requisitos

- **Docker Desktop** — para o banco de dados.
- **Node.js 20+** e **pnpm** (`npm install -g pnpm`).
- **PHP 8.1+** e **Composer**.
- **Java JDK 17+** e **Maven**.

> Você **não** precisa instalar MySQL na máquina — o Docker cuida disso.

---

## 1. Banco de dados (Docker)

Na raiz do projeto:

```bash
docker compose up -d
```

Sobe dois containers:
- **MySQL** em `localhost:3306` — banco `hackathon`, usuário `root`, senha `root`.
- **phpMyAdmin** em `http://localhost:8081` — servidor `mysql`, usuário `root`, senha `root`.

Aguarde o MySQL ficar `healthy`:

```bash
docker compose ps
```

---

## 2. API Node.js

```bash
cd api
pnpm install
cp .env.example .env
```

Edite o `.env` (compatível com o Docker acima):

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

Prepare o banco e suba a API:

```bash
pnpm db:generate          # gera o Prisma Client
pnpm db:migrate:deploy    # aplica as migrations
pnpm db:seed              # popula o banco com dados iniciais
pnpm dev                  # sobe a API com hot-reload
```

- API: **http://localhost:3000**
- Documentação interativa: **http://localhost:3000/docs**

**Scripts úteis:**

| Comando | O que faz |
|---------|-----------|
| `pnpm dev` | Sobe a API com hot-reload |
| `pnpm db:generate` | Gera o Prisma Client a partir do schema |
| `pnpm db:migrate` | Cria e aplica uma nova migration (dev) |
| `pnpm db:migrate:deploy` | Aplica migrations existentes sem criar novas |
| `pnpm db:seed` | Popula o banco com dados iniciais |
| `pnpm db:studio` | Abre o Prisma Studio (visualizar/editar o banco) |
| `pnpm check` | Roda lint + format (Biome) |

---

## 3. Front-end Web (PHP)

Com a **API rodando**, em outro terminal:

```bash
cd web
composer install
php -S localhost:8000 -t src
```

Acesse **http://localhost:8000**. A URL da API já vem configurada em `web/app/Config/Config.php`.

---

## 4. Back Office Desktop (Java Swing)

Conecta **direto** no MySQL (sem passar pela API).

```bash
cd portal-desktop-swing
```

Crie um `.env` na pasta `portal-desktop-swing/`:

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

> Alternativa: abrir na IDE (IntelliJ/Eclipse) e rodar `com.portal.Main`.

---

## Credenciais de acesso (após o seed)

| Tipo | E-mail | Senha |
|------|--------|-------|
| Admin UniALFA | `admin@unialfa.com` | `Admin@123` |
| Empresa Admin (Tech Local) | `empresa@techlocal.com` | `Empresa@123` |
| Empresa Admin (Agência Alfa) | `empresa@agenciaalfa.com` | `Empresa@123` |
| Empresa Admin (Winfo) | `empresa@winfo.com` | `Empresa@123` |
| Recruiter (Tech Local) | `recruiter@techlocal.com` | `Recruit@123` |
| Aluno | `joao@aluno.com` | `Aluno@1234` |
| Aluno | `maria@aluno.com` | `Aluno@1234` |
| Aluno | `lucas@aluno.com` | `Aluno@1234` |

> **⚠️ 2FA (TOTP):** Admin e Empresa têm **2 fatores obrigatório**. Adicione a chave abaixo no
> **Google Authenticator** ou **Authy** e use o código gerado:
>
> ```
> JBSWY3DPEHPK3PXP
> ```
>
> Contas de **Aluno não** exigem 2FA.

---

## Solução de problemas

| Problema | Causa provável | Solução |
|----------|----------------|---------|
| `Environments incompleto` ao subir a API | `.env` faltando/errado | `JWT_SECRET` precisa de 32+ caracteres; use `FRONTEND_URL` |
| API não conecta no banco | MySQL ainda iniciando | Aguarde `docker compose ps` mostrar `healthy` |
| Web mostra erro de conexão | API não está rodando | Suba a API (passo 2) antes do PHP |
| Seed não cria nada | Seed é idempotente — já existe | Reset o banco (abaixo) ou use as contas existentes |
| `class not found` no Java | `.env` ausente ou deps não baixadas | Crie o `.env` e rode `mvn clean compile` |
| Porta 3306/3000/8000 em uso | Outro serviço na porta | Pare o conflitante ou troque a porta |
| Login admin/empresa não funciona | 2FA não configurado | Adicione a chave `JBSWY3DPEHPK3PXP` no autenticador |

**Resetar o banco do zero:**

```bash
docker compose down -v   # apaga o volume do MySQL
docker compose up -d
# aguarde o healthcheck e rode novamente:
cd api
pnpm db:migrate:deploy
pnpm db:seed
```
