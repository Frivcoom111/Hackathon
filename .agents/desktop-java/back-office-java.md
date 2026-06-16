# Back Office Desktop (Java Swing)

> Pasta: `portal-desktop-swing/`. É o sistema **administrativo** usado pela equipe da faculdade.
> Diferente do PHP, ele acessa o **banco direto** via JDBC (roda na rede interna da UniALFA).

## Para que serve

A equipe da UniALFA usa o desktop para administrar o portal:

- **Empresas:** aprovar, bloquear e consultar cadastros.
- **Alunos:** cadastrar, editar, consultar, **importar de arquivo `.txt`/CSV** e marcar aptidão (elegibilidade).
- **Vagas e candidaturas:** consultar e acompanhar status.
- **Relatórios:** exportar em `.txt`.
- **Dashboard:** contadores em tempo real (empresas pendentes, vagas ativas, etc.).
- **Login:** autenticação com senha em hash (bcrypt) e controle de sessão.

---

## Tecnologias

- **Java 17 + Swing** — interface gráfica desktop.
- **Maven** — build e dependências.
- **MySQL 8** — mesmo banco do resto do projeto.
- **HikariCP** — pool de conexões.
- **dotenv-java** — lê o `.env`.
- **bcrypt** — confere a senha (mesmo algoritmo da API).

---

## Estrutura do projeto

```text
src/main/java/com/portal/
├── Main.java          # ponto de entrada (abre a tela de login)
├── config/            # DatabaseConfig (pool de conexões — Singleton)
├── model/             # entidades do domínio (Student, Company, Job, ...)
│   └── enums/         # status e papéis (Role, JobStatus, ...)
├── dao/               # acesso ao banco (1 DAO por entidade, todos estendem BaseDAO)
├── service/           # regras de negócio (Fachada entre a GUI e os DAOs)
├── util/              # apoio (Session, validações, exportar relatório, botões)
└── gui/               # telas Swing (login, dashboard, listas e formulários)
```

---

## As camadas (como o Java se organiza)

Mesma ideia da API: separar responsabilidades.

```text
GUI (telas Swing)  →  Service (regras)  →  DAO (SQL/banco)  →  MySQL
```

- A **GUI** nunca fala direto com o banco — fala com o **Service**.
- O **Service** valida regras e chama o **DAO**.
- O **DAO** executa o SQL.

### DAOs (acesso ao banco)
`BaseDAO` (base comum) + um DAO por entidade: `UserDAO`, `StudentDAO`, `CompanyDAO`, `CompanyMemberDAO`, `CourseDAO`, `JobDAO`, `ApplicationDAO`, `DashboardDAO`, `NotificationDAO`.

Exemplo — `StudentDAO`: listar, buscar por termo, verificar RA/CPF duplicado, salvar aluno + usuário **em transação**, atualizar, alternar aptidão.

### Services (regras de negócio)
`AuthService` (login admin), `StudentService`, `CompanyService`, `CourseService`, `JobService`, `ApplicationService`, `ReportService`.

Exemplo — `StudentService`: listar, buscar, criar, editar, alternar elegibilidade e **importar alunos de CSV/TXT** validando nome, RA, CPF e duplicidade.

### GUI (telas)
`LoginFrame`, `DashboardFrame`, `DashboardHomePanel`, `StudentListPanel`, `StudentFormDialog`, `StudentImportDialog`, `CompanyListPanel`, `CompanyDetailDialog` (aprovar/bloquear), `CoursePanel`, `CourseFormDialog`, `JobListPanel`, `ApplicationListPanel`, `ReportPanel`, etc.

### Models
`User`, `Student`, `Address`, `Company`, `CompanyMember`, `Course`, `StudentCourse`, `Job`, `Application`, `Certificate`, `Notification`, `DashboardStats` — cada um com atributos privados, construtores, getters e setters.

---

## Padrões de projeto aplicados (ótimo para a banca)

| Padrão | Onde | Para quê |
|--------|------|----------|
| **Singleton** | `config/DatabaseConfig` | Um único pool de conexões na aplicação inteira |
| **DAO** | `dao/BaseDAO` + DAOs | Isolar o acesso ao banco por entidade |
| **Template Method** | `dao/BaseDAO` | Reúso de `getConnection()`, `now()` e `mapAddress()` |
| **Factory Method** | `util/ButtonFactory` | Botões padronizados (primary/secondary/danger) |
| **Facade** | `service/*` | A GUI fala com os services, não direto com os DAOs |

---

## Papel do Java no sistema (importante)

O desktop é a **camada institucional / de governança**. É por ele que a empresa **passa de `PENDING` para `APPROVED`**
(e que se controla o acesso). Ou seja:

- O **cadastro** da empresa acontece no site PHP (via API).
- **Quem libera/bloqueia** a empresa é o **Admin pelo desktop Java**.
- Enquanto a empresa não está `APPROVED`, **as vagas dela não aparecem** para os alunos na vitrine.

Isso fecha o ciclo de governança do portal.
