# API Node.js — Estrutura (o cérebro do sistema)

> Pasta: `api/`. É onde mora **toda a regra de negócio**. O site PHP e o desktop Java só refletem o que a API decide.

## Como a API é organizada

```text
api/
├── prisma/
│   ├── schema.prisma        # definição do banco (as 11 tabelas)
│   ├── migrations/          # histórico de alterações do banco
│   ├── data.ts              # constantes do seed
│   └── seed.ts              # popula o banco com dados iniciais
└── src/
    ├── server.ts            # liga o servidor (ponto de entrada)
    ├── app.ts               # monta o Express e registra as rotas
    ├── config/env.ts        # valida as variáveis de ambiente (.env) com Zod
    ├── lib/prisma.ts        # cria o cliente do Prisma (conexão com o banco)
    ├── modules/             # os módulos de negócio (auth, student, company, ...)
    ├── shared/              # middlewares, erros, utils e schemas comuns
    ├── docs/                # documentação OpenAPI / Scalar (a tela /docs)
    └── generated/prisma/    # cliente Prisma gerado (NÃO editar à mão)
```

---

## A arquitetura em camadas (o padrão de cada módulo)

Cada módulo (ex.: `auth`, `student`, `company`) segue **sempre as mesmas camadas**.
Pense numa linha de produção: o pedido entra por uma ponta e sai pela outra.

```text
routes        →  define as URLs e quem pode acessar (guards de permissão)
controller    →  recebe a requisição HTTP e devolve a resposta
service       →  AQUI ficam as REGRAS DE NEGÓCIO
repository    →  fala com o banco usando Prisma
schema        →  valida os dados que chegam (com Zod)
docs / types  →  documentação OpenAPI e tipos TypeScript
```

**Por que separar assim?** Cada arquivo tem uma única responsabilidade. Fica fácil achar onde mexer:
quer mudar uma regra? `service`. Quer mudar uma URL? `routes`. Quer mudar uma validação? `schema`.

> Como os módulos são "montados": em `routes.ts`, cria-se `repository → service → controller` e ligam-se as rotas.
> Ex.: `student.routes.ts` injeta o `NotificationService` no `StudentService` para poder notificar a empresa.

---

## Os módulos da API

| Módulo | Prefixo | O que faz |
|--------|---------|-----------|
| **auth** | `/auth` | Cadastro, login e 2FA (TOTP) |
| **student** | `/student` | Perfil, currículo e candidaturas do aluno |
| **company** | `/company` | Empresa, membros, vagas e candidaturas |
| **jobs** | `/jobs` | Vitrine de vagas e candidatura |
| **address** | `/address` | Endereço de aluno e empresa |
| **course** | `/courses` | Listagem de cursos (público) |
| **notification** | `/notifications` | Notificações do usuário |

> As **rotas e regras detalhadas** de cada módulo estão em **[02-regras-de-negocio.md](./02-regras-de-negocio.md)**.

---

## Arquivos centrais (o "esqueleto")

### `app.ts` — monta a aplicação
Registra, em ordem: leitura de JSON, segurança (**Helmet**), **CORS**, **rate limit** global, todas as rotas
(`/auth`, `/company`, `/student`, `/jobs`, `/address`, `/notifications`, `/courses`), a documentação e,
por último, o tratador de erros (`errorHandler`).

> ℹ️ A pasta `uploads` é servida pela API para o front conseguir baixar currículos pelas rotas de download.

### `server.ts` — liga o servidor
Monta o app e faz `app.listen` na porta do `.env`. Se der erro ao iniciar, encerra o processo.

### `config/env.ts` — valida o ambiente
Usa **Zod** para garantir que o `.env` está completo e correto. Se faltar algo (ex.: `JWT_SECRET` curto), a API **nem sobe** e mostra o que está errado. Evita "erro misterioso em produção".

### `lib/prisma.ts` — conexão com o banco
Cria o cliente do Prisma usando os dados do `.env`.

---

## Segurança (os middlewares de proteção)

Middlewares são "porteiros" que toda requisição atravessa.

| Arquivo | O que faz |
|---------|-----------|
| `auth.middlewares.ts` | Confere o token JWT, verifica se o usuário está **ativo** (`isActive`) e aplica as permissões por papel. |
| `upload.middleware.ts` | Configura o multer: pasta de destino, nome único de arquivo, limite de 5 MB e filtro de tipo. |
| `errorHandler.middlewares.ts` | Tratamento central de erros. Traduz erros do Zod, do multer e os `AppError` em respostas claras. |
| `rateLimit.middleware.ts` | Limita quantas requisições um cliente pode fazer (proteção contra abuso). |

### Os "guards" de permissão (em `auth.middlewares.ts`)
- `authMiddleware` — exige token válido e usuário **ativo**. Conta inativa → erro "Aguarde a aprovação da empresa".
- `requireStudent` — exige papel `STUDENT` (sem MFA).
- `requireCompany` — exige papel `COMPANY` **com MFA verificada**.
- `requireCompanyAdmin` — exige `COMPANY` + MFA + papel **ADMIN** dentro da empresa.
- `requireAdmin` — exige papel `ADMIN` + MFA.

### Utilitários de segurança
- `bcryptUtils.ts` — gera e compara hash de senha (`generateHash`, `compareHash`).
- `generateToken.ts` — cria o JWT com `sub` (id), `email`, `role`, `mfaVerified` e `companyMemberRole`.

---

## Como a autenticação funciona (confirmado no código)

```text
ALUNO (STUDENT):
  email+senha → /auth/login → recebe o TOKEN COMPLETO ("AUTHENTICATED") → já está logado.

EMPRESA e ADMIN (exigem 2FA):
  email+senha → /auth/login → recebe um tempToken (sem MFA) e:
     • 1º acesso  → resposta "TOTP_SETUP" já traz o qrCode  → confirma em /auth/totp/setup/confirm
     • próximos   → resposta "TOTP_REQUIRED"                → valida em /auth/totp/verify
  → recebe o TOKEN FINAL (mfaVerified = true) → está logado.
```

Pontos confirmados no `auth.service.ts`:
- **Só `STUDENT`** recebe o token completo direto no login. **`ADMIN` e `COMPANY` passam pelo TOTP.**
- O **QR Code** vem **na própria resposta do login** (não há rota `GET /auth/totp/setup`).
- Cadastrar (`/auth/register/*`) **não** loga e **não** retorna token.
- Senha sempre com hash (bcrypt). Token só é emitido no login/confirmação de TOTP.

---

## Documentação interativa (OpenAPI / Scalar)

Com a API em `development`, abra `http://localhost:3000/docs`. Dá para ver e testar todas as rotas — útil na demo.

---

## Dica para a IA / desenvolvedor

- Mexeu no `schema.prisma`? Rode `pnpm db:generate` para atualizar `src/generated/prisma`.
- **Nunca** coloque regra de negócio no `controller` nem no `repository` — ela vai no `service`.
- Erros de negócio usam as classes de `shared/errors/AppError`; respostas usam `shared/utils/response`.
