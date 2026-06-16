# Operação — Guia de Apresentação (roteiro)

> Roteiro pronto para explicar o projeto de forma clara. Tempo sugerido: **8 a 12 minutos**.
> A ideia é contar uma **história**, não listar tecnologias.

---

## Estrutura sugerida da fala

### 1. Abertura — o problema (30s)
> "Alunos da UniALFA precisam de estágio e empresas da região precisam de estagiários, mas hoje
> isso é desorganizado. Criamos um **Portal de Estágios** que junta alunos, empresas e a faculdade
> em um só lugar."

### 2. As 3 partes (1 min)
Mostre o diagrama (`visao-geral/02-arquitetura.md`) e diga:
> "São três aplicações que dividem o mesmo banco:
> - uma **API em Node.js**, que é o cérebro e guarda todas as regras;
> - um **site em PHP**, que o aluno e a empresa usam no navegador;
> - um **sistema desktop em Java**, que a faculdade usa para administrar."

**Frase de efeito:**
> "A regra de ouro do projeto é: **o PHP nunca acessa o banco direto** — ele sempre passa pela API.
> Assim, toda regra fica em um lugar só."

### 3. O banco (1 min)
Mostre `banco-de-dados/modelo-de-dados.md`:
> "São 11 tabelas, normalizadas em 3ª forma normal. O fluxo central é:
> **Aluno → Candidatura → Vaga ← Empresa**. Usamos soft delete em vagas e candidaturas, para nunca
> perder histórico."

### 4. Demonstração ao vivo (4-5 min) — o ponto alto
Siga esta ordem (conta a história inteira):

1. **Admin (Java)**: aprova uma empresa que estava `PENDING`.
2. **Empresa (site)**: faz login **com o 2FA** (mostre o QR Code / Google Authenticator) e **cria uma vaga**.
3. **Aluno (site)**: faz login (sem 2FA), encontra a vaga e **se candidata**.
4. **Empresa (site)**: abre os candidatos, coloca **em análise** e depois **aprova**.
5. **Aluno (site)**: mostra a **notificação** que chegou.

> Mostra os 3 perfis, a integração e as regras funcionando juntas.

### 5. Destaques técnicos (1-2 min)
Escolha 3 ou 4:
- **Segurança**: senha com hash (bcrypt), login por token (JWT) e **2FA obrigatório** para Empresa e Admin.
- **POO no PHP**: classes `Aluno`, `Empresa`, `Vaga`, `Candidatura` com encapsulamento (exigência eliminatória — atendida).
- **Padrões de projeto no Java**: Singleton, DAO, Facade, Factory, Template Method.
- **API em camadas**: schema → repository → service → controller → routes.
- **Documentação automática** da API (Scalar/OpenAPI) em `/docs`.

### 6. Fechamento (30s)
> "Resumindo: um portal de estágios com três interfaces, uma API central com todas as regras, banco
> normalizado e segurança real. A arquitetura permite, no futuro, até um app mobile usando a mesma API,
> sem reescrever nada."

---

## Perguntas que a banca pode fazer (e respostas curtas e CORRETAS)

| Pergunta | Resposta curta |
|----------|----------------|
| Por que separar API do PHP? | Para centralizar a regra de negócio em um lugar só e poder trocar/adicionar fronts sem reescrever a lógica. |
| Por que o Java acessa o banco direto? | É um sistema interno da faculdade, na rede interna; back office administrativo. |
| Onde ficam os currículos? | No filesystem da API (`uploads/resumes/`); o banco guarda só o caminho. Mais simples que salvar binário no banco. |
| Como garantem que ninguém se candidata 2x? | Regra `@@unique([studentId, jobId])` no banco + checagem na API (erro 409). |
| O que é soft delete e por quê? | Marcar `deletedAt` em vez de apagar. Preserva histórico e permite recuperação. |
| Como funciona o 2FA? | Padrão TOTP: a empresa escaneia um QR Code (que vem na resposta do login) e digita um código de 6 dígitos. |
| O que impede uma empresa não aprovada de aparecer? | A vitrine só mostra vagas `ACTIVE` de empresas `APPROVED`. A aprovação é feita pelo Admin no desktop Java. |
| Precisa de currículo para se candidatar? | Não é obrigatório no envio da candidatura. É obrigatório ter **endereço** e ser **elegível**. |
| Validação fica só no front? | Não. Fica na API (Zod) e também no banco (ENUMs, UNIQUEs, FKs). |
| Quem pode mudar o status da candidatura? | A empresa dona da vaga; e só vai para APROVADA/REJEITADA a partir de "em análise". |

---

## Checklist antes de apresentar

- [ ] Docker do banco está `healthy` (`docker compose ps`).
- [ ] API rodando em `http://localhost:3000` (testar `/docs`).
- [ ] Site PHP rodando em `http://localhost:8000`.
- [ ] Desktop Java abre e loga com `admin@unialfa.com`.
- [ ] Google Authenticator com a chave `JBSWY3DPEHPK3PXP` configurada no celular.
- [ ] Uma empresa `PENDING` separada para aprovar ao vivo (efeito melhor).
- [ ] Contas de teste anotadas (ver `operacao/01-como-rodar.md`).

---

## Mapa rápido "onde está cada coisa" (se travar na demo)

| Quero mostrar... | Vá em |
|------------------|-------|
| Regras de negócio | `api/02-regras-de-negocio.md` |
| Passo a passo de uma ação | `fluxos/fluxos-completos.md` |
| Modelo do banco | `banco-de-dados/modelo-de-dados.md` |
| Como a API é feita | `api/01-estrutura.md` |
| Como o site PHP é feito | `frontend-web-php/front-php.md` |
| Como o desktop Java é feito | `desktop-java/back-office-java.md` |
