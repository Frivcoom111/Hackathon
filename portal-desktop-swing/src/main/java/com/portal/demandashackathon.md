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

## Gestão de Alunos

- [ ] Cadastrar aluno
- [ ] Editar aluno
- [ ] Consultar alunos (listagem com busca por nome/RA)
- [ ] Controlar aptidão para estágio (`isEligible`)
- [ ] Importar alunos via arquivo `.txt`
- [ ] `StudentDAO.java` — CRUD completo
- [ ] `StudentService.java` — regras de negócio
- [ ] `StudentListPanel.java` — tabela com busca
- [ ] `StudentFormDialog.java` — formulário cadastro/edição
- [ ] `StudentImportDialog.java` — importar `.txt`
- [ ] `FileImportUtil.java` — parser do arquivo

---

## Gestão de Vagas (consulta)

- [ ] Consultar vagas cadastradas (vindas do sistema PHP/Node)
- [ ] Filtrar por status, empresa, curso
- [ ] Visualizar detalhes da vaga
- [ ] `JobDAO.java` — listar + filtros
- [ ] `JobListPanel.java` — tabela com filtros
- [ ] `JobDetailDialog.java` — visualizar detalhes

---

## Gestão de Candidaturas (consulta)

- [ ] Consultar candidaturas realizadas pelos alunos
- [ ] Visualizar status de cada candidatura
- [ ] `ApplicationDAO.java` — listar com JOINs em Job e Student
- [ ] `ApplicationListPanel.java` — tabela com filtros por status
- [ ] `ApplicationDetailDialog.java` — visualizar dados da candidatura

---

## Relatórios (.txt) — Obrigatórios

- [ ] Relatório de empresas cadastradas
- [ ] Relatório de alunos cadastrados
- [ ] Relatório de vagas disponíveis
- [ ] Relatório de candidaturas e status
- [ ] `ReportService.java` — consultas agrupadas
- [ ] `ReportExporter.java` — escrever arquivos `.txt` formatados
- [ ] `ReportPanel.java` — tela para escolher e gerar relatório

---

## Diferenciais (se houver tempo)

- [x] Sistema de autenticação para acesso ao Back Office
- [ ] Controle de perfis de acesso (ADMIN, COORDENADOR, OPERADOR)
- [ ] Exportação de relatórios em CSV
- [ ] Exportação de relatórios em PDF (iText ou Apache PDFBox)
- [ ] Aplicação de Design Patterns
- [ ] Dashboard com contadores (empresas pendentes, candidaturas abertas)
