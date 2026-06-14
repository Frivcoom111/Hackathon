# Issues — Portal de Estágios UniALFA

Cada seção abaixo é uma instrução para o Copilot criar uma issue no GitHub.
Após a issue ser criada, a branch é aberta e o nome é passado para commit e push.

---

## Issue 3 — Dashboard com contadores e Design Patterns

**Instrução para o Copilot:**

Crie uma issue com as seguintes informações:

**Título:**
`feat(java): Dashboard com contadores e aplicação de Design Patterns`

**Descrição:**

```
## O que foi implementado

### Dashboard com contadores (DashboardHomePanel)
Painel inicial exibido ao fazer login, com visão geral do sistema em tempo real.

**Contadores:**
- Empresas Pendentes — aguardando análise do admin
- Vagas Ativas — publicadas e disponíveis
- Candidaturas Abertas — com status PENDING ou ANALYSING
- Alunos Aptos — com isEligible = true

**Características:**
- 4 cards padronizados em azul (UX consistente)
- Botão "Atualizar" recarrega os dados do banco sem reabrir a tela
- Saudação com e-mail do usuário logado
- Item "Início" adicionado ao menu lateral como entrada padrão ao logar

**Arquivos criados:**
- `model/DashboardStats.java` — POJO com os 4 contadores
- `dao/DashboardDAO.java` — query única com 4 subqueries paralelas
- `gui/dashboard/DashboardHomePanel.java` — painel de contadores

**Arquivos alterados:**
- `gui/dashboard/DashboardFrame.java` — painel "home" registrado e definido como padrão

---

### Design Patterns aplicados
Cinco padrões documentados e implementados no projeto:

| Pattern | Onde | Descrição |
|---|---|---|
| Singleton | `config/DatabaseConfig.java` | Pool HikariCP único; construtor privado adicionado |
| DAO | `dao/BaseDAO.java` + DAOs | Isolamento de acesso ao banco por entidade |
| Factory Method | `util/ButtonFactory.java` | Criação padronizada de botões Swing (primary, secondary, danger) |
| Facade | `service/*.java` | Services expõem operações de alto nível para a GUI |
| Template Method | `dao/BaseDAO.java` | `getConnection()` reutilizado por todos os DAOs concretos |

**Arquivos criados:**
- `util/ButtonFactory.java` — Factory Method para botões padronizados

**Arquivos alterados:**
- `config/DatabaseConfig.java` — construtor privado + javadoc Singleton
- `misc/md/ESTUTURA.md` — tabela de Design Patterns documentada

---

### Correção de UX
Todos os elementos de interface padronizados em azul (0x1565C0):
- Cards do dashboard: fundo azul claro, texto e borda azul
- Header do DashboardHomePanel: azul padrão
- Títulos dos cards de relatório: azul padrão

## Fora do escopo (próximas issues)
- Exportação de relatórios em CSV
- Exportação de relatórios em PDF
- Controle de perfis de acesso (ADMIN, COORDENADOR, OPERADOR)
```

**Branch:** `feat/vinicius/dashboard-patterns`

---
