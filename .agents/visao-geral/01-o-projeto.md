# Visão Geral — O Projeto

## O problema que o projeto resolve

Alunos da **Faculdade UniALFA** precisam de estágio. Empresas da região precisam de estagiários.
Hoje isso acontece de forma desorganizada (mural, indicação, boca a boca).

O **Portal de Estágios UniALFA** resolve isso colocando todo mundo em um só lugar:

- A **empresa** publica e gerencia suas vagas.
- O **aluno** vê as vagas e se candidata.
- A **faculdade** administra e fiscaliza tudo (aprova empresas, cadastra alunos e cursos, gera relatórios).

> Projeto desenvolvido para o **Hackathon Institucional UniALFA**.

---

## Quem usa o sistema (os 3 perfis)

| Perfil | Quem é | O que faz | Por onde acessa |
|--------|--------|-----------|-----------------|
| **Aluno** (`STUDENT`) | Estudante da UniALFA | Vê vagas, se candidata, edita seu perfil e currículo | Site PHP (navegador) |
| **Empresa** (`COMPANY`) | Recrutador / responsável | Publica vagas, analisa candidatos | Site PHP (navegador) |
| **Admin** (`ADMIN`) | Equipe da faculdade | Aprova/bloqueia empresas, gerencia alunos e cursos, relatórios | Sistema desktop Java |

---

## As 3 grandes partes do projeto

```text
api/                    → API Node.js (Express + Prisma + MySQL). É o CÉREBRO do sistema.
web/                    → Site PHP que o aluno e a empresa acessam pelo navegador.
portal-desktop-swing/   → Sistema Java Swing usado pela faculdade (back office / administrativo).
```

E na raiz existe o `docker-compose.yml`, que sobe o **banco MySQL** + **phpMyAdmin** prontos para uso.

---

## A regra de ouro da arquitetura

```text
O PHP NÃO acessa o banco de dados diretamente.
O PHP conversa com a API Node.js por HTTP (pede e recebe JSON).
```

Isso é importante porque centraliza **todas as regras de negócio na API**. Se a regra muda, muda em um lugar só.
O único que acessa o banco direto é o **desktop Java** (porque roda na rede interna da faculdade).

---

## O fluxo principal do sistema (a história central)

```text
1. A faculdade (Admin) aprova uma empresa e cadastra alunos/cursos.   [desktop Java]
2. A empresa publica uma vaga.                                        [site PHP → API]
3. O aluno vê a vaga no site e se candidata.                          [site PHP → API]
4. A empresa analisa o candidato e muda o status.                     [site PHP → API]
5. O aluno recebe uma notificação a cada mudança.                     [API]
```

Em diagrama de entidades:

```text
Aluno  →  Candidatura  →  Vaga  ←  Empresa
```

---

## O que torna o projeto interessante (destaques para a banca)

- **Arquitetura distribuída**: 3 aplicações diferentes (Node, PHP, Java) usando o **mesmo banco**, cada uma no seu papel.
- **API como contrato único**: o site PHP é "burro" de propósito — toda regra está na API.
- **Segurança real**: senha com hash, login por token JWT e **2FA com autenticador** para Empresa e Admin.
- **POO de verdade no PHP**: classes `Aluno`, `Empresa`, `Vaga`, `Candidatura` com encapsulamento.
- **Padrões de projeto no Java**: Singleton, DAO, Template Method, Factory, Facade.
- **Banco normalizado (3NF)** com soft delete e auditoria de datas.

---

## ⚠️ O que o sistema NÃO faz (escopo atual — para não prometer demais)

A API atual cobre o essencial do fluxo de estágio. **Não** estão implementados (ficam como evolução futura):

- Foto de perfil / capa do aluno e gerenciamento de certificados **pela API** (o schema foca em dados + currículo).
- Carta de apresentação (`coverLetter`) na candidatura.
- Endereço próprio para o membro da empresa (endereço existe para **aluno** e **empresa**).

> Saber o limite do escopo evita prometer na apresentação algo que o código ainda não faz.
