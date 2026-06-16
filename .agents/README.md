# 📚 Documentação do Projeto — Portal de Estágios UniALFA

> Esta pasta `.agents/` concentra **toda a documentação do projeto**, organizada por tema.
> Serve para dois objetivos:
>
> 1. **Orientar a IA** (Claude, Copilot, etc.) a entender o projeto rapidamente.
> 2. **Apresentar e estudar** o projeto — explicado de forma simples, para qualquer pessoa entender.
>
> ✅ **Todo o conteúdo foi verificado contra o código real da API** (`api/src`, `api/prisma/schema.prisma`),
> que é a fonte da verdade. Onde os `.md` antigos divergiam do código, valeu o **código**.

---

## O que é o projeto, em uma frase

Um **portal de estágios** que conecta os **alunos da Faculdade UniALFA** às **empresas da região**
que oferecem vagas. A faculdade administra tudo por um sistema desktop interno.

---

## Mapa da documentação (pastas)

```text
.agents/
├── README.md                     ← você está aqui (índice geral)
├── visao-geral/
│   ├── 01-o-projeto.md           O que é, quem usa e para quê
│   └── 02-arquitetura.md         As 3 partes (API, Web, Desktop) e como conversam
├── banco-de-dados/
│   └── modelo-de-dados.md        As 11 tabelas, enums e relacionamentos
├── api/
│   ├── 01-estrutura.md           Como a API Node.js é montada (o "cérebro")
│   └── 02-regras-de-negocio.md   TODAS as rotas e regras (essencial p/ apresentar)
├── frontend-web-php/
│   └── front-php.md              O site PHP (POO) que aluno e empresa acessam
├── desktop-java/
│   └── back-office-java.md       O sistema administrativo Java (back office)
├── fluxos/
│   └── fluxos-completos.md       Passo a passo de cada ação (login, candidatura...)
└── operacao/
    ├── 01-como-rodar.md          Subir o projeto, credenciais e troubleshooting
    └── 02-guia-apresentacao.md   Roteiro pronto para apresentar
```

### Ordem de leitura sugerida
1. `visao-geral/01-o-projeto.md` → `visao-geral/02-arquitetura.md`
2. `banco-de-dados/modelo-de-dados.md`
3. `api/01-estrutura.md` → `api/02-regras-de-negocio.md`
4. `frontend-web-php/front-php.md` → `desktop-java/back-office-java.md`
5. `fluxos/fluxos-completos.md`
6. `operacao/01-como-rodar.md` → `operacao/02-guia-apresentacao.md`

---

## Resumo ultrarrápido (elevator pitch)

- **3 partes:** uma **API** (Node.js) que guarda todas as regras, um **site** (PHP) para alunos e empresas,
  e um **sistema desktop** (Java) para a faculdade.
- **A API é o coração.** O site PHP **nunca** acessa o banco direto — pede tudo pela API por HTTP.
  O desktop Java acessa o banco direto (rede interna da faculdade).
- **Banco:** MySQL com 11 tabelas normalizadas (3NF).
- **Segurança:** senhas com hash (bcrypt), login por token (JWT) e **2FA (autenticador)** para Empresa e Admin.
- **Fluxo principal:** o aluno **se candidata** a uma **vaga** que a **empresa publicou**, e a empresa **acompanha** os candidatos.

---

## Para a IA (leia antes de editar código)

- A **fonte da verdade das regras** é a **API** (`api/src/modules/`). O PHP e o Java apenas refletem essas regras.
- O banco é definido em `api/prisma/schema.prisma`. Ao mudar o schema, rode `pnpm db:generate`.
- Arquitetura de cada módulo da API: `schema` (valida entrada) → `repository` (Prisma) → `service` (regra) → `controller` (HTTP) → `routes` (rotas + guards).
- Mantenha a explicação **simples**. O time pediu textos claros, sem complexidade desnecessária.
