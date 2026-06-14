# Portal de Estágios UniALFA — Back Office Institucional (Java Swing)

## Visão Geral

Aplicação desktop Java para a equipe administrativa da UniALFA gerenciar o ecossistema do portal:
aprovar empresas, gerenciar alunos, consultar vagas e candidaturas e gerar relatórios.

---

## Estrutura de Pacotes

```
src/main/java/com/portal/
│
├── Main.java                         ← Ponto de entrada da aplicação
│
├── gui/                              ← Todas as telas Swing
│   ├── login/
│   │   └── LoginFrame.java           ← Tela de login
│   ├── dashboard/
│   │   └── DashboardFrame.java       ← Janela principal (menu + painel central)
│   ├── companies/
│   │   ├── CompanyListPanel.java     ← Listagem de empresas com filtros
│   │   └── CompanyDetailDialog.java  ← Detalhes / aprovar / bloquear empresa
│   ├── students/
│   │   ├── StudentListPanel.java     ← Listagem de alunos
│   │   ├── StudentFormDialog.java    ← Cadastrar / editar aluno
│   │   └── StudentImportDialog.java  ← Importar alunos via .txt
│   ├── jobs/
│   │   ├── JobListPanel.java         ← Consultar vagas cadastradas
│   │   └── JobDetailDialog.java      ← Visualizar detalhes da vaga
│   ├── applications/
│   │   ├── ApplicationListPanel.java ← Consultar candidaturas
│   │   └── ApplicationDetailDialog.java ← Visualizar status da candidatura
│   ├── reports/
│   │   └── ReportPanel.java          ← Geração de relatórios
│   └── components/
│       ├── RoundedButton.java        ← Componentes reutilizáveis de UI
│       ├── StatusBadge.java
│       └── SearchField.java
│
├── model/                            ← Entidades de domínio (POO)
│   ├── User.java
│   ├── Student.java                  ← extends / associa User
│   ├── Company.java
│   ├── CompanyMember.java
│   ├── Job.java
│   ├── Application.java
│   ├── Course.java
│   ├── StudentCourse.java
│   ├── Certificate.java
│   ├── Address.java
│   ├── Notification.java
│   └── enums/
│       ├── Role.java                 ← ADMIN, COMPANY, STUDENT
│       ├── CompanyStatus.java        ← PENDING, ANALYSING, APPROVED, BLOCKED
│       ├── ApplicationStatus.java    ← PENDING, ANALYSING, APPROVED, REJECTED, CANCELLED
│       ├── JobStatus.java            ← ACTIVE, PAUSED, CLOSED
│       ├── JobModality.java          ← PRESENCIAL, REMOTE, HYBRID
│       └── StudentCourseStatus.java  ← ACTIVE, COMPLETED, CANCELLED
│
├── dao/                              ← Acesso ao banco de dados (JDBC)
│   ├── BaseDAO.java                  ← Classe abstrata com conexão e helpers
│   ├── UserDAO.java
│   ├── StudentDAO.java
│   ├── CompanyDAO.java
│   ├── JobDAO.java
│   ├── ApplicationDAO.java
│   ├── CourseDAO.java
│   └── NotificationDAO.java
│
├── service/                          ← Regras de negócio
│   ├── AuthService.java              ← Login / hash de senha
│   ├── CompanyService.java           ← Aprovar / bloquear / listar empresas
│   ├── StudentService.java           ← CRUD aluno + controle isEligible
│   ├── JobService.java               ← Consulta de vagas
│   ├── ApplicationService.java       ← Consulta de candidaturas
│   └── ReportService.java            ← Geração de relatórios .txt (e extras)
│
└── util/                             ← Utilitários
    ├── DatabaseConfig.java           ← HikariCP pool (já existe)
    ├── PasswordUtil.java             ← Hash BCrypt ou SHA-256
    ├── ValidationUtil.java           ← CPF, CNPJ, RA, email
    ├── FileImportUtil.java           ← Leitura de .txt para importar alunos
    └── ReportExporter.java           ← Escreve arquivos de relatório
```

---

## Metas por Módulo

### M1 — Infraestrutura Base
- [ ] Configurar `DatabaseConfig.java` com HikariCP + dotenv (parcialmente feito)
- [ ] Criar `BaseDAO.java` com helpers de CRUD genérico
- [ ] Criar `PasswordUtil.java` para hash de senhas
- [ ] Criar `Main.java` com inicialização do Look & Feel (FlatLaf ou Nimbus)

### M2 — Autenticação
- [ ] `LoginFrame.java` — tela de login com email + senha
- [ ] `AuthService.java` — validar credencial contra tabela `User` (role=ADMIN)
- [ ] Controle de sessão simples (objeto `Session` estático ou singleton)

### M3 — Gestão de Empresas

**Contexto:** Empresas se cadastram pelo portal PHP com status inicial `PENDING`.
O admin do Back Office Java aprova ou bloqueia. Só empresas `APPROVED` conseguem publicar vagas.

**Fluxo de status:**
```
PENDING → ANALYSING → APPROVED
                    → BLOCKED
```

**O que o admin pode fazer:**
- Ver lista de todas as empresas com filtro por status
- Abrir detalhes: nome, CNPJ, telefone, descrição, endereço
- Mover para ANALYSING (começou a analisar)
- Aprovar empresa (APPROVED) → libera publicação de vagas
- Bloquear empresa (BLOCKED) → impede acesso ao painel

**Tabela `Company` no banco:**
| Campo | Tipo | Observação |
|---|---|---|
| id | UUID | PK |
| name | VARCHAR | Nome da empresa |
| cnpj | VARCHAR | Único |
| description | LONGTEXT | Apresentação |
| phone | VARCHAR | Nullable |
| status | ENUM | PENDING, ANALYSING, APPROVED, BLOCKED |
| addressId | FK → Address | Nullable |
| createdAt / updatedAt | DATETIME | — |

**Arquivos a criar:**
- [ ] `CompanyDAO.java` — `findAll()`, `findByStatus()`, `updateStatus()`
- [ ] `CompanyService.java` — `analisar()`, `aprovar()`, `bloquear()`
- [ ] `CompanyListPanel.java` — tabela com filtro por status no topo
- [ ] `CompanyDetailDialog.java` — dados completos + botões de ação

**Observação:** A tabela `Address` é separada. Para exibir o endereço no detalhe da empresa, precisamos de um JOIN ou busca adicional em `Address` pelo `addressId`.

### M4 — Gestão de Alunos
- [ ] `StudentDAO.java` — CRUD completo
- [ ] `StudentService.java` — criar/editar/ativar/desativar aptidão
- [ ] `StudentListPanel.java` — tabela com busca por nome/RA
- [ ] `StudentFormDialog.java` — formulário de cadastro/edição
- [ ] `StudentImportDialog.java` — importar lista via .txt (formato definido abaixo)
- [ ] `FileImportUtil.java` — parser do .txt

### M5 — Consulta de Vagas
- [ ] `JobDAO.java` — listar, filtrar por status/empresa/curso
- [ ] `JobListPanel.java` — tabela com filtros
- [ ] `JobDetailDialog.java` — visualizar dados completos da vaga

### M6 — Consulta de Candidaturas
- [ ] `ApplicationDAO.java` — listar com joins em Job e Student
- [ ] `ApplicationListPanel.java` — tabela com filtros por status
- [ ] `ApplicationDetailDialog.java` — visualizar dados da candidatura

### M7 — Relatórios
- [ ] `ReportService.java` — consultas agrupadas para relatórios
- [ ] `ReportExporter.java` — escrever arquivos .txt formatados
- [ ] `ReportPanel.java` — escolher tipo + clique para gerar
- [ ] Relatórios obrigatórios:
  - Empresas cadastradas
  - Alunos cadastrados
  - Vagas disponíveis
  - Candidaturas e status

### M8 — Diferenciais (se houver tempo)
- [ ] Controle de perfis (ADMIN, COORDENADOR, OPERADOR) com permissões distintas
- [ ] Exportação CSV
- [ ] Exportação PDF (iText ou Apache PDFBox)
- [ ] Dashboard com contadores (empresas pendentes, candidaturas abertas)

---

## Formato do Arquivo .txt para Importação de Alunos

```
# Uma linha por aluno, campos separados por ;
# nome;ra;cpf;email;periodo;curso
João Silva;20240001;123.456.789-00;joao@unialfa.edu.br;3;Sistemas de Informação
Maria Souza;20240002;987.654.321-00;maria@unialfa.edu.br;5;Engenharia de Software
```

---

## Design Patterns Aplicados

| Pattern | Onde | Descrição |
|---|---|---|
| **Singleton** | `config/DatabaseConfig.java` | Pool HikariCP inicializado uma única vez via static block; construtor privado impede instanciação externa. |
| **DAO (Data Access Object)** | `dao/BaseDAO.java` + todos os DAOs | Isola o acesso ao banco de dados da lógica de negócio. Cada entidade tem seu próprio DAO. |
| **Factory Method** | `util/ButtonFactory.java` | Centraliza a criação de botões Swing padronizados (`primary`, `secondary`, `danger`), garantindo consistência visual sem duplicação. |
| **Facade** | `service/*.java` | Cada Service é uma Facade: expõe operações de alto nível para a GUI sem que ela conheça os DAOs ou SQL. |
| **Template Method** | `dao/BaseDAO.java` | Define o método `getConnection()` como template reutilizado por todos os DAOs concretos. |

---

## Convenções de Código

- Cada DAO recebe uma `Connection` ou usa o pool via `DatabaseConfig.getConnection()`
- Services não acessam o banco diretamente — sempre via DAO
- Diálogos (`JDialog`) são modais e recebem o frame pai como parâmetro
- Painéis (`JPanel`) são adicionados ao `DashboardFrame` via `CardLayout`
- Enums espelham exatamente os ENUMs do banco MySQL

---

## Prioridade de Entrega

| Prioridade | Módulo                   | Justificativa                          |
|------------|--------------------------|----------------------------------------|
| 1          | M1 — Infraestrutura      | Base de tudo                           |
| 2          | M2 — Autenticação        | Requisito desejável + ponto de entrada |
| 3          | M3 — Gestão de Empresas  | Funcionalidade obrigatória             |
| 4          | M4 — Gestão de Alunos    | Funcionalidade obrigatória (+ import)  |
| 5          | M5 — Vagas               | Funcionalidade obrigatória             |
| 6          | M6 — Candidaturas        | Funcionalidade obrigatória             |
| 7          | M7 — Relatórios          | Funcionalidade obrigatória             |
| 8          | M8 — Diferenciais        | Bonus se houver tempo                  |
