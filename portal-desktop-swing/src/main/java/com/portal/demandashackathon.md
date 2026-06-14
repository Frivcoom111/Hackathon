# Demandas do Hackathon — Java Desktop (Back Office UniALFA)

---

## Infraestrutura e Arquitetura

- [x] Maven + MySQL (mysql-connector via dependência)
- [x] Interface gráfica com Java Swing
- [x] Estrutura de pacotes: `gui`, `model`, `service`, `dao`, `util`
- [x] `DatabaseConfig.java` com pool de conexões (HikariCP)
- [x] `BaseDAO.java` base para acesso ao banco
- [x] `PasswordUtil.java` para hash de senhas
- [x] Enums espelhando o banco: `Role`, `CompanyStatus`, `ApplicationStatus`, `JobStatus`, `JobModality`, `StudentCourseStatus`
- [x] Models OO para todas as entidades: `User`, `Student`, `Company`, `CompanyMember`, `Job`, `Application`, `Course`, `StudentCourse`, `Certificate`, `Address`, `Notification`

---

## Autenticação (Diferencial)

- [x] `LoginFrame.java` — tela de login (email + senha)
- [x] `AuthService.java` — validação de credencial contra `User` (role=ADMIN)
- [x] `Session.java` — controle de sessão do usuário logado

---

## Dashboard

- [x] `DashboardFrame.java` — janela principal com menu lateral e `CardLayout`

---

## Gestão de Cursos *(extra — base para vincular vagas)*

- [x] `CourseDAO.java` — CRUD completo
- [x] `CourseService.java` — criar, editar, toggle ativo
- [x] `CoursePanel.java` — listagem com filtro e ações
- [x] `CourseFormDialog.java` — formulário criar/editar

---

## Gestão de Empresas ✅

- [x] Aprovar empresas cadastradas
- [x] Bloquear / desativar empresas
- [x] Mover status: PENDING → ANALYSING → APPROVED / BLOCKED
- [x] Consultar informações cadastrais (nome, CNPJ, telefone, descrição)
- [x] `CompanyDAO.java` — `findAll`, `findByStatus`, `findById`, `updateStatus`
- [x] `CompanyService.java` — `analisar`, `aprovar`, `bloquear`
- [x] `CompanyListPanel.java` — tabela com filtro por status + badges coloridos
- [x] `CompanyDetailDialog.java` — detalhes + botões de ação com confirmação

---

## Gestão de Alunos ✅

- [x] Cadastrar aluno
- [x] Editar aluno
- [x] Consultar alunos (listagem com busca por nome/RA)
- [x] Controlar aptidão para estágio (`isEligible`)
- [x] Importar alunos via arquivo `.txt`
- [x] `StudentDAO.java` — CRUD completo (transação User+Student)
- [x] `StudentService.java` — regras de negócio
- [x] `StudentListPanel.java` — tabela com busca e badge de aptidão
- [x] `StudentFormDialog.java` — formulário cadastro/edição
- [x] `StudentImportDialog.java` — importar `.txt` com preview
- [x] `FileImportUtil.java` — parser do arquivo

---

## Gestão de Vagas (consulta) ✅

- [x] Consultar vagas cadastradas (vindas do sistema PHP/Node)
- [x] Filtrar por status (ACTIVE, PAUSED, CLOSED)
- [x] Visualizar detalhes da vaga
- [x] `JobDAO.java` — findAll implementado
- [x] `JobListPanel.java` — tabela com filtro e badge colorido por status
- [x] `JobDetailDialog.java` — visualizar detalhes

---

## Gestão de Candidaturas (consulta) ✅

- [x] Consultar candidaturas realizadas pelos alunos
- [x] Visualizar status de cada candidatura
- [x] `ApplicationDAO.java` — findAll com JOIN em Student e Job
- [x] `ApplicationListPanel.java` — tabela com filtros por status e badge colorido
- [x] `ApplicationDetailDialog.java` — visualizar dados da candidatura

---

## Relatórios (.txt) — Obrigatórios ✅

- [x] Relatório de empresas cadastradas
- [x] Relatório de alunos cadastrados
- [x] Relatório de vagas disponíveis
- [x] Relatório de candidaturas e status
- [x] `ReportService.java` — consultas agrupadas com timestamp
- [x] `ReportExporter.java` — arquivos `.txt` formatados
- [x] `ReportPanel.java` — cards coloridos + seleção de pasta + abre Explorer

---

## Diferenciais (se houver tempo)

- [x] Sistema de autenticação para acesso ao Back Office
- [ ] Controle de perfis de acesso (ADMIN, COORDENADOR, OPERADOR)
- [ ] Exportação de relatórios em CSV
- [ ] Exportação de relatórios em PDF (iText ou Apache PDFBox)
- [x] Aplicação de Design Patterns (Singleton, DAO, Factory Method, Facade, Template Method)
- [x] Dashboard com contadores (empresas pendentes, vagas ativas, candidaturas abertas, alunos aptos)
