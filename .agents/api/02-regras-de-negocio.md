# API Node.js — Regras de Negócio (o documento mais importante)

> Todas as rotas e regras, **conferidas arquivo por arquivo** no código (`routes` + `service`).
> É a fonte da verdade para entender "por que o sistema faz o que faz".

---

## 🔐 Auth (`/auth`) — cadastro, login e 2FA

**Rotas (de `auth.routes.ts`):**
- `POST /auth/login` — autentica. **Público.**
- `POST /auth/register/student` — cadastra `User(STUDENT)` + `Student` + vínculo de curso (`StudentCourse`). **Público.** (Corpo JSON, **sem** upload de currículo.)
- `POST /auth/register/company` — cadastra `User(COMPANY)` + `Address` + `Company(PENDING)` + `CompanyMember(ADMIN)`. **Público.**
- `POST /auth/totp/setup/confirm` — confirma o 2FA no 1º acesso (exige token temporário). Ativa `totpEnabled`.
- `POST /auth/totp/verify` — valida o código do 2FA nos acessos seguintes.

**Regras (de `auth.service.ts`):**
- Senha **sempre** com hash (bcrypt); token JWT só é emitido no login/confirmação.
- **Só `STUDENT`** recebe o JWT completo no login. **`ADMIN` e `COMPANY` recebem um `tempToken`** e seguem para o TOTP.
- No 1º acesso de empresa/admin, o **QR Code já vem na resposta do login** (`type: "TOTP_SETUP"`).
- `register/*` **não** retorna token — cadastrar **não** loga.
- O login bloqueia usuário inexistente, com senha errada ou **inativo** (`isActive = false`).
- Cadastro duplicado (email/CPF/CNPJ já existentes) → erro **409**.

> **Sobre a empresa "PENDENTE":** a empresa é criada com `Company.status = PENDING`. Enquanto não estiver
> `APPROVED`, **as vagas dela não aparecem na vitrine** (ver módulo jobs). A aprovação/bloqueio e a ativação
> do acesso são feitas pelo **Admin no app Java**.

---

## 🎓 Student (`/student`) — perfil e candidaturas do aluno

**Rotas (de `student.routes.ts` — todas exigem aluno autenticado):**
- `GET /student/profile` · `PATCH /student/profile` — vê/edita o perfil (nome/telefone no `Student`; email no `User`, em transação).
- `PATCH /student/password` — troca de senha (exige a senha atual).
- `PATCH /student/resume` — upload de currículo (multipart, campo `resume`).
- `GET /student/resume/download` — baixa o próprio currículo.
- `GET /student/applications` — lista candidaturas (paginado).
- `DELETE /student/applications/:id` — cancela candidatura.

**Regras (de `student.service.ts`):**
- Todas as rotas exigem **aluno autenticado** (`requireStudent`).
- **Cancelamento:** só com status `PENDING` ou `ANALYSING`; é **soft delete** (vira `CANCELLED` + `deletedAt`).
- Só o **dono** cancela a própria candidatura (verificação por `studentId` → senão 403).
- Cancelar **notifica a empresa** dona da vaga (tipo `APPLICATION_CANCELLED`).
- Upload de currículo exige um arquivo (PDF/JPG/PNG) — sem arquivo → **400**.
- O endereço do aluno fica no módulo `address` (`/address/me`).

---

## 🏢 Company (`/company`) — gestão pela empresa

**Rotas (de `company.routes.ts` — todas exigem `COMPANY` com MFA):**
- `GET /company/profile` · `PATCH /company/profile` (**ADMIN**) — perfil da empresa.
- `PATCH /company/me` · `PATCH /company/me/password` — dados do próprio membro.
- `GET|POST /company/members`, `PATCH|DELETE /company/members/:memberId`, `POST /company/members/:memberId/totp/reset` — membros (**só ADMIN**).
- `GET|POST /company/jobs`, `GET|PATCH /company/jobs/:jobId`, `PATCH /company/jobs/:jobId/status` — vagas.
- `GET /company/jobs/:jobId/applications` — lista candidaturas da vaga.
- `GET /company/jobs/:jobId/applications/:id/resume` — baixa o currículo do candidato.
- `PATCH /company/jobs/:jobId/applications/:id/status` — muda o status da candidatura.

**Regras (de `company.service.ts`):**
- Todas exigem `COMPANY` com **MFA verificada** (`requireCompany`); ações de perfil/membros exigem **ADMIN** (`requireCompanyAdmin`).
- Um membro **não** pode alterar/desativar/resetar **a si mesmo**; o alvo precisa ser da **mesma empresa**.
- A empresa só enxerga e mexe nas **próprias** vagas (validação de "ownership" da vaga).
- **Status da vaga** (`JOB_STATUS_TRANSITIONS`): `ACTIVE → [PAUSED, CLOSED]`, `PAUSED → [ACTIVE, CLOSED]`, `CLOSED → []` (**terminal, irreversível**).
- **Status da candidatura:** só muda a partir de `PENDING`/`ANALYSING`. `APPROVED`/`REJECTED` só são permitidos **a partir de `ANALYSING`**. Estados finais são **imutáveis**.
- Mudar o status da candidatura para `ANALYSING`/`APPROVED`/`REJECTED` **notifica o aluno** (tipo `APPLICATION_STATUS`).
- Baixar currículo do candidato: usa o `resumePath` da candidatura e, se não houver, cai para o `resumePath` do **perfil do aluno**.

---

## 💼 Jobs (`/jobs`) — vitrine de vagas e candidatura

**Rotas (de `jobs.routes.ts`):**
- `GET /jobs` — lista vagas (paginado; filtros: `courseId`, `area`, `modality`, `search`). Exige autenticação.
- `GET /jobs/:jobId` — detalhe da vaga. Exige autenticação.
- `POST /jobs/:jobId/apply` — candidatura (**aluno**; multipart com `resume` **opcional**).

**Regras (de `jobs.service.ts` + `jobs.repository.ts`):**
- Todas exigem autenticação; `apply` exige aluno (`requireStudent`).
- **Só aparecem na vitrine** vagas com `status = ACTIVE`, `deletedAt = null` e **empresa `APPROVED`**.
- Para se candidatar, a vaga precisa estar `ACTIVE` e a empresa `APPROVED`; senão → **400**.
- O aluno precisa ser **elegível** (`isEligible`) → senão **403**; e ter **endereço** cadastrado → senão **400**.
- **Currículo na candidatura é OPCIONAL.** Se enviar um arquivo, ele é salvo na candidatura; se não enviar, a candidatura é criada sem `resumePath`. *(Não há bloqueio por falta de currículo no apply.)*
- Candidatura **duplicada** (mesmo aluno + mesma vaga) é bloqueada (**409**).
- Criar candidatura **notifica a empresa** dona da vaga (tipo `NEW_APPLICATION`).

---

## 📍 Address (`/address`) — endereço de aluno e empresa

**Rotas (de `address.routes.ts`):**
- `POST|GET|PATCH|DELETE /address/me` — endereço do **aluno** (`requireStudent`).
- `POST|GET|PUT|DELETE /address/company` — endereço da **empresa**; **leitura** por qualquer membro, **escrita só ADMIN**.

**Regras:**
- **Um endereço por dono:** `POST` → 409 se já existe; `GET`/atualizar/`DELETE` → 404 se não existe.
- `DELETE` remove a linha de `Address`; a FK do dono vira `NULL` (relação opcional → `SetNull`).
- O endereço da empresa é resolvido pela empresa do **membro autenticado**.
- O endereço do aluno é **exigido** para se candidatar a vagas.
- ⚠️ Atenção ao verbo: o do **aluno** atualiza com **`PATCH`**; o da **empresa** atualiza com **`PUT`**.

---

## 🔔 Notification (`/notifications`) — notificações

**Rotas (de `notification.routes.ts` — exigem autenticação):**
- `GET /notifications` — lista as do usuário (paginado; filtro `?unread=true`).
- `PATCH /notifications/read-all` — marca todas como lidas.
- `PATCH /notifications/:id/read` — marca uma como lida.

**Quem dispara notificações:**
- **Mudança de status da candidatura** (`company.service`) → notifica o **aluno** (em `ANALYSING`/`APPROVED`/`REJECTED`). Tipo `APPLICATION_STATUS`.
- **Nova candidatura** (`jobs.service`) → notifica os **membros da empresa**. Tipo `NEW_APPLICATION`.
- **Cancelamento** (`student.service`) → notifica os **membros da empresa**. Tipo `APPLICATION_CANCELLED`.

**Regras:**
- Cada usuário só vê **as próprias** notificações.
- Notificação para empresa é **fan-out**: uma por membro.
- Falha ao notificar **nunca** quebra a ação principal (envolto em try/catch).

---

## 📚 Course (`/courses`) — cursos

**Rota (de `course.routes.ts`):**
- `GET /courses` — lista cursos **ativos**, ordenados por nome.

**Regras:**
- ⚠️ **A rota é PÚBLICA** (não exige token). Motivo: o formulário de cadastro precisa carregar os cursos **antes** do login.
- Retorna apenas cursos com `isActive = true`.
- Somente leitura — criar/editar curso **não** é exposto por esta API (isso é feito no desktop Java).

---

## 🧭 Máquinas de estado (as transições permitidas)

### Empresa (`CompanyStatus`)
```text
PENDING ──► ANALYSING ──► APPROVED   (vagas passam a aparecer na vitrine)
                      └──► BLOCKED    (bloqueada)
```
> Quem move a empresa nesse fluxo é o **Admin no desktop Java**.

### Vaga (`JobStatus`)  — confirmado em `JOB_STATUS_TRANSITIONS`
```text
ACTIVE ◄──► PAUSED
   │          │
   └────► CLOSED ◄────┘     (CLOSED é terminal: não volta atrás)
```

### Candidatura (`ApplicationStatus`)
```text
PENDING ──► ANALYSING ──► APPROVED   (final, imutável)
                      └──► REJECTED   (final, imutável)

PENDING/ANALYSING ──► CANCELLED       (só o aluno cancela a própria)
```
> `APPROVED`/`REJECTED` só são permitidos **a partir de `ANALYSING`** (não direto de `PENDING`).

---

## 🔒 Resumo das permissões (quem pode o quê)

| Ação | Quem pode |
|------|-----------|
| Listar cursos | **Qualquer um** (rota pública) |
| Ver vagas / detalhe | Qualquer usuário autenticado |
| Se candidatar | Aluno elegível e com endereço (currículo opcional) |
| Cancelar candidatura | Só o aluno dono dela (status PENDING/ANALYSING) |
| Publicar/editar vaga | Empresa com MFA |
| Editar perfil da empresa / gerenciar membros | Apenas **ADMIN** da empresa |
| Mudar status de candidatura | Empresa dona da vaga |
| Aprovar/bloquear empresa | **Admin da faculdade** (desktop Java) |
| Cadastrar cursos | **Admin da faculdade** (desktop Java) |
