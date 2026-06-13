# Issues — Portal de Estágios UniALFA

Cada seção abaixo é uma instrução para o Copilot criar uma issue no GitHub.
Após a issue ser criada, a branch é aberta e o nome é passado para commit e push.

c
---

## Issue 2 — Consulta de Vagas, Candidaturas e Regras de Negócio de Empresa

**Instrução para o Copilot:**

Crie uma issue com as seguintes informações:

**Título:**
`feat(java): Consulta de Vagas e Candidaturas + Regras de negócio de empresa`

**Descrição:**

```
## O que foi implementado

### Consulta de Vagas
Painel de consulta somente leitura para visualizar vagas cadastradas pelo sistema PHP/Node.

- Tabela com colunas: Título, Área, Modalidade, Local, Status, Salário
- Filtro client-side por status (ACTIVE, PAUSED, CLOSED)
- Badge colorido por status: verde (ACTIVE), amarelo (PAUSED), vermelho (CLOSED)
- Duplo-clique ou botão "Ver Detalhes" abre JobDetailDialog
- Botão Atualizar recarrega do banco

**Arquivos criados:**
- `gui/jobs/JobListPanel.java`
- `gui/jobs/JobDetailDialog.java`

---

### Consulta de Candidaturas
Painel de consulta somente leitura para visualizar candidaturas realizadas pelos alunos.

- Tabela com colunas: Aluno, Vaga, Status
- Filtro client-side por status (PENDING, ANALYSING, APPROVED, REJECTED, CANCELLED)
- Badge colorido por cada status
- Duplo-clique ou botão "Ver Detalhes" abre ApplicationDetailDialog com badge inline

**Arquivos criados:**
- `gui/applications/ApplicationListPanel.java`
- `gui/applications/ApplicationDetailDialog.java`

---

### Regras de negócio: Empresa x Usuários vinculados
Implementadas as regras de ativação/desativação de usuários conforme status da empresa.

**Regras:**
- Uma empresa pode ter vários usuários (via CompanyMember)
- Um usuário pertence a no máximo uma empresa
- Usuário vinculado a empresa só pode estar ativo se a empresa estiver APPROVED
- `aprovar()` → reativa todos os usuários da empresa (`isActive = 1`)
- `bloquear()` → desativa todos os usuários da empresa (`isActive = 0`)
- `analisar()` → desativa todos os usuários da empresa (`isActive = 0`)

**Arquivos alterados:**
- `dao/UserDAO.java` — adicionado `setActiveByCompany(companyId, active)`
- `service/CompanyService.java` — `aprovar`, `bloquear` e `analisar` agora chamam `setActiveByCompany`

---

### Correções de UI
- Botão "Limpar" (Alunos) padronizado: fundo azul, letra branca
- Botão "Atualizar" (Candidaturas e Vagas) padronizado: fundo azul, letra branca
- Botões "Gerar .txt" (Relatórios) padronizados: todos azul/branco em vez de cor do card

---

### Dashboard
- Menu lateral atualizado com itens "Vagas" e "Candidaturas"
- Novos painéis registrados no CardLayout do DashboardFrame

## Fora do escopo (próximas issues)
- Dashboard com contadores (empresas pendentes, candidaturas abertas)
- Controle de perfis de acesso (ADMIN, COORDENADOR, OPERADOR)
- Exportação de relatórios em CSV/PDF
```

**Branch:** `feat/vinicius/vagas-candidaturas`

---
