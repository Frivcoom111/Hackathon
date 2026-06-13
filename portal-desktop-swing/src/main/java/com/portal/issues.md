# Issues — Portal de Estágios UniALFA

Cada seção abaixo é uma instrução para o Copilot criar uma issue no GitHub.
Após a issue ser criada, a branch é aberta e o nome é passado para commit e push.

---

## Issue 1 — Gestão de Alunos e Relatórios TXT

**Instrução para o Copilot:**

Crie uma issue com as seguintes informações:

**Título:**
`feat(java): Gestão de Alunos e Geração de Relatórios TXT`

**Descrição:**

```
## O que foi implementado

### Gestão de Alunos
Módulo completo de administração de alunos no Back Office Java (Swing).

**Fluxo:**
- Admin cadastra aluno (cria User + Student em transação única)
- Admin edita dados do aluno
- Admin marca aluno como Apto ou Inapto para estágio (isEligible)
- Admin importa lista de alunos via arquivo .txt

**Arquivos criados/alterados:**
- `model/Student.java` — adicionados userId, email, phone
- `dao/StudentDAO.java` — findAll (JOIN com User), findByTerm, existsByRa, existsByCpf, saveWithUser (transação), update, toggleEligible
- `dao/UserDAO.java` — adicionados save() e existsByEmail()
- `service/StudentService.java` — listar, buscar, criar, editar, toggleEligivel, importar
- `gui/students/StudentListPanel.java` — tabela com busca por nome/RA, badge de aptidão colorido
- `gui/students/StudentFormDialog.java` — formulário criar/editar (senha inicial = CPF)
- `gui/students/StudentImportDialog.java` — seleção de .txt, preview antes de importar
- `util/FileImportUtil.java` — parser atualizado (formato: nome;ra;cpf;email;periodo;curso)

---

### Geração de Relatórios TXT
Painel com 4 tipos de relatório, com seleção de pasta de destino via JFileChooser.

**Relatórios disponíveis:**
- Empresas cadastradas (nome, CNPJ formatado, status, telefone)
- Alunos cadastrados (nome, RA, CPF, aptidão)
- Vagas disponíveis (título, área, modalidade, status, salário)
- Candidaturas (nome do aluno, vaga, status)

**Arquivos criados/alterados:**
- `dao/JobDAO.java` — findAll (vagas não deletadas)
- `dao/ApplicationDAO.java` — findAll com JOIN em Student e Job
- `model/Application.java` — adicionados studentName e jobTitle
- `service/ReportService.java` — orquestra os 4 relatórios com timestamp no nome do arquivo
- `util/ReportExporter.java` — atualizado para exibir nomes legíveis nas candidaturas e CNPJ formatado
- `gui/reports/ReportPanel.java` — 4 cards coloridos, seleção de pasta, abre o Explorer após gerar
- `gui/dashboard/DashboardFrame.java` — item Relatórios adicionado ao menu lateral

## Fora do escopo (próximas issues)
- Consulta de Vagas (JobListPanel, JobDetailDialog)
- Consulta de Candidaturas (ApplicationListPanel, ApplicationDetailDialog)
- Exportação em PDF e CSV
```

**Branch:** `feat/vinicius/alunos-relatorios`

---
