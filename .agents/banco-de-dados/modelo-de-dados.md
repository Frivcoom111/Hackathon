# Banco de Dados — Modelo de Dados

> Fonte da verdade: `api/prisma/schema.prisma`. Banco **MySQL 8.0**, normalizado (3NF), com **11 tabelas**.
> Este documento foi conferido campo a campo contra o schema real.

## As 11 tabelas (resumo)

| Tabela | Responsabilidade |
|--------|------------------|
| `Address` | Endereço reutilizável (aluno e empresa) |
| `User` | Autenticação base — email, senha, papel (role) e 2FA |
| `Course` | Cursos da universidade |
| `Student` | Perfil do aluno — RA, CPF, telefone, currículo, elegibilidade |
| `Certificate` | Certificados do aluno (1 aluno → N certificados) |
| `StudentCourse` | Histórico acadêmico (liga aluno e curso) |
| `Company` | Empresa parceira — CNPJ, status de aprovação |
| `CompanyMember` | Recrutadores/admins vinculados à empresa |
| `Job` | Vagas publicadas (com soft delete) |
| `Application` | Candidaturas dos alunos (com soft delete) |
| `Notification` | Notificações geradas pelo sistema |

**Fluxo principal:** `Student` → `Application` → `Job` ← `Company`.

---

## Os enums (os "valores fixos" do sistema)

Enums são listas fechadas de valores válidos. O banco recusa qualquer valor fora da lista.

| Enum | Valores | Para que serve |
|------|---------|----------------|
| `Role` | `ADMIN`, `COMPANY`, `STUDENT` | Tipo de usuário |
| `CompanyStatus` | `PENDING`, `ANALYSING`, `APPROVED`, `BLOCKED` | Situação da empresa |
| `CompanyMemberRole` | `ADMIN`, `RECRUITER` | Papel da pessoa dentro da empresa |
| `JobStatus` | `ACTIVE`, `PAUSED`, `CLOSED` | Situação da vaga |
| `Modality` | `PRESENCIAL`, `REMOTE`, `HYBRID` | Modalidade da vaga |
| `ApplicationStatus` | `PENDING`, `ANALYSING`, `APPROVED`, `REJECTED`, `CANCELLED` | Andamento da candidatura |
| `StudentCourseStatus` | `ACTIVE`, `COMPLETED`, `CANCELLED` | Situação do curso do aluno |

---

## Detalhe de cada tabela (campos exatos do schema)

### 1. `Address` (endereços)
`id`, `street`, `number`, `complement?`, `district`, `city`, `state` (2 letras), `zipCode`, datas.
- **Reutilizado por:** `Student` e `Company` (relação 1:1 em cada). O membro da empresa **não** tem endereço próprio.
- **Decisão:** tabela isolada. Quem tem endereço guarda um `addressId`. Se o endereço for apagado, o dono **não** é apagado junto (`onDelete: SetNull`).

### 2. `User` (login)
`id`, `email` (único), `password` (hash), `role` (default `STUDENT`), `isActive` (default **true**), `totpSecret?`, `totpEnabled` (default false), datas.
- Autenticação **pura** — sem nome, sem CPF. Dados pessoais ficam em `Student` ou `CompanyMember`.
- **2FA (TOTP):** `totpSecret` e `totpEnabled` ficam aqui.

### 3. `Course` (cursos)
`id`, `name` (único), `code?` (único), `periods` (nº de semestres), `isActive` (default true), datas.
- Cadastrados pelo Admin da UniALFA (no desktop Java).

### 4. `Student` (aluno)
`id`, `userId` (único), `addressId?` (único), `name`, `ra` (único), `cpf` (único), `phone?`, `isEligible` (default true), `resumePath?`, datas.
- Estende o `User` (1:1).
- O currículo é um **caminho de arquivo** (`resumePath`), não o arquivo dentro do banco.

### 5. `Certificate` (certificados)
`id`, `studentId`, `name`, `institution?`, `issuedAt`, `filePath?`, datas.
- 1 aluno → muitos certificados. Aluno apagado → certificados apagados (`Cascade`).
- ℹ️ Existe no banco, mas **a API atual não expõe rotas** para gerenciar certificados.

### 6. `StudentCourse` (histórico acadêmico)
`id`, `studentId`, `courseId`, `status` (default `ACTIVE`), `startedAt`, `finishedAt?`, datas.
- Tabela "pivô" que liga aluno e curso (N:N).
- `@@unique([studentId, courseId])`: o mesmo aluno não pode ter o mesmo curso duplicado.

### 7. `Company` (empresa)
`id`, `addressId?` (único), `name`, `cnpj` (único), `description?`, `phone?`, `status` (default `PENDING`), datas.
- O `status` controla a visibilidade das vagas da empresa.

### 8. `CompanyMember` (pessoa da empresa)
`id`, `companyId`, `userId` (único), `role` (default `RECRUITER`), `name`, `cpf` (único), `phone?`, datas.
- A pessoa física (recrutador/admin) que age em nome da empresa. Estende `User` (1:1).
- Um `User` pertence a **uma** empresa só (`userId` único).
- ⚠️ **Não tem `addressId`** — diferente de `Student` e `Company`.

### 9. `Job` (vaga)
`id`, `companyId`, `courseId?`, `title`, `description`, `area`, `requirements?`, `salary?`, `location`, `modality` (default `PRESENCIAL`), `status` (default `ACTIVE`), `deletedAt?`, datas.
- `courseId` nulo = vaga aberta para **qualquer** curso.

### 10. `Application` (candidatura)
`id`, `studentId`, `jobId`, `status` (default `PENDING`), `resumePath?`, `deletedAt?`, datas.
- `@@unique([studentId, jobId])`: o aluno não pode se candidatar 2x à mesma vaga.
- ⚠️ **Não existe campo `coverLetter`** no schema atual.

### 11. `Notification` (notificação)
`id`, `userId`, `title`, `message`, `type` (texto livre), `isRead` (default false), `createdAt`.
- **Imutável:** não tem `updatedAt`. Só muda `isRead` para `true`.

---

## Relacionamentos (quem se liga a quem)

```text
User         1:1  Student
User         1:1  CompanyMember
User         1:N  Notification
Student      1:N  Certificate
Student      N:N  Course          (via StudentCourse)
Company      1:N  CompanyMember
Company      1:N  Job
Job          1:N  Application
Student      1:N  Application
Address      1:1  Student
Address      1:1  Company
```

---

## Regras de integridade (o que acontece ao apagar)

| Quando você apaga... | Acontece com os filhos | Por quê |
|----------------------|------------------------|---------|
| Um **aluno** (`Student`) | Apaga candidaturas, certificados e histórico (`Cascade`) | Não faz sentido manter dados órfãos |
| Uma **empresa** | Apaga vagas e membros (`Cascade`) | Idem |
| Um **usuário** (`User`) | Apaga `Student`/`CompanyMember`/notificações (`Cascade`) | A conta sumiu |
| Uma **vaga** | Apaga candidaturas (`Cascade`) | Candidatura sem vaga não existe |
| Um **curso** | Bloqueado se houver vínculo (FK `Course` sem cascade) | Protege o histórico acadêmico |
| Um **endereço** | A referência do dono vira `NULL` (`SetNull`) | O dono continua existindo, só sem endereço |

---

## Decisões de design importantes (para explicar na banca)

- **Soft delete** em `Job` e `Application`: o registro **não some** do banco, só ganha `deletedAt`. As consultas filtram `deletedAt: null`. Vantagem: histórico preservado e possibilidade de recuperar.
- **Auditoria temporal**: quase toda tabela tem `createdAt`/`updatedAt`.
- **UNIQUEs críticos**: `User.email`, `Student.cpf`, `Student.ra`, `Company.cnpj` e o par `Application(studentId, jobId)` impedem duplicidades no próprio banco (não dependem só do código).
- **Validação no banco com ENUM**: estados inválidos são recusados pelo MySQL, não só pela aplicação.
- **`User` separado dos dados pessoais**: autenticação enxuta; nome/CPF ficam em `Student`/`CompanyMember`.

---

## Onde os arquivos ficam guardados

Currículo (PDF/JPG/PNG) **não** fica no banco. É salvo no **filesystem da API** (pasta `api/uploads/resumes/`),
e o banco guarda só o **caminho**:

```text
Banco:        resumePath = "uploads/resumes/uuid.pdf"
Arquivo real: api/uploads/resumes/uuid.pdf
```

Biblioteca de upload: **multer**. Limite: **5 MB** por arquivo.
