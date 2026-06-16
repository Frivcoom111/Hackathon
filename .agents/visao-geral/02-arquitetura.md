# Visão Geral — Arquitetura

## Visão em diagrama

```text
                         ┌──────────────────────┐
                         │   MySQL 8.0 (3306)    │
                         │   Docker / phpMyAdmin │
                         └──────────┬───────────┘
                                    │
              ┌─────────────────────┼─────────────────────┐
              │ Prisma              │ JDBC direto          │
              ▼                     │                      ▼
   ┌─────────────────────┐         │           ┌─────────────────────┐
   │  API Node.js (3000) │         │           │  Back Office Java   │
   │  Express + Prisma   │         └───────────│  Swing Desktop      │
   └──────────┬──────────┘                     │  (admin UniALFA)    │
              │ HTTP/JSON                       └─────────────────────┘
              ▼
   ┌─────────────────────┐
   │  Front-end Web PHP   │
   │  Aluno + Empresa     │
   └─────────────────────┘
```

## Tabela das camadas

| Camada | Pasta | Quem usa | Como acessa os dados |
|--------|-------|----------|----------------------|
| **API REST** | `api/` | Todos (indiretamente) | Prisma → MySQL |
| **Web (Aluno/Empresa)** | `web/` | Alunos e empresas | **Apenas via HTTP na API** |
| **Back Office** | `portal-desktop-swing/` | Equipe UniALFA | JDBC direto no MySQL |
| **Banco** | `docker-compose.yml` | — | MySQL + phpMyAdmin |

> A API é o **coração**. O PHP nunca acessa o banco diretamente — só consome a API.
> O desktop Java conecta direto no MySQL (rede interna da faculdade).

---

## O caminho de uma requisição (do clique ao banco)

Quando um aluno clica em "ver vagas" no navegador:

```text
1. Usuário abre o navegador            (cliente)
2. PHP renderiza a página              (web/)
3. PHP chama a API Node.js por HTTP    (web/app/Api + classes)
4. API valida dados e regras           (api/src/modules/.../service)
5. API usa o Prisma                    (api/src/lib/prisma.ts)
6. Prisma acessa o MySQL
7. API retorna JSON
8. PHP transforma o JSON em objetos e mostra na tela
```

O ponto-chave: **o PHP nunca pula etapas**. Ele sempre passa pela API.

---

## As rotas que a API expõe (confirmado em `api/src/app.ts`)

```text
/auth            cadastro, login e 2FA
/company         área da empresa (perfil, membros, vagas, candidaturas)
/student         área do aluno (perfil, currículo, candidaturas)
/jobs            vitrine de vagas e candidatura
/address         endereço de aluno e empresa
/notifications   notificações do usuário
/courses         listagem de cursos (público)
```

> Em `development`, a documentação interativa (Scalar/OpenAPI) fica em `http://localhost:3000/docs`.

---

## Tecnologias por parte

### API (`api/`)
- **Node.js + TypeScript**, **Express 5**, **Prisma 7**, **MySQL**.
- **Zod** (validação), **JWT** (token), **bcrypt** (hash de senha), **otplib + qrcode** (2FA/TOTP), **multer** (upload).
- **Helmet + CORS + Rate limit** (segurança HTTP).

### Web (`web/`)
- **PHP 8 (Orientado a Objetos)**, **Composer + Guzzle** (cliente HTTP), **Bootstrap 5** + CSS próprio.

### Desktop (`portal-desktop-swing/`)
- **Java 17 + Swing**, **Maven**, **HikariCP** (pool), **dotenv-java**, **bcrypt**.

### Infra
- **Docker + Docker Compose**, **MySQL 8.0**, **phpMyAdmin**.

---

## Por que essa arquitetura é boa (justificativa)

- **Uma regra, um lugar.** Toda lógica de negócio mora na API. Mudou a regra? Muda só na API; o site e o desktop continuam funcionando.
- **Times independentes.** Enquanto uma pessoa faz a API, outra faz o site, outra faz o desktop — todos combinando pelo "contrato" da API/banco.
- **Segurança concentrada.** Autenticação, validação e permissões ficam na API, não espalhadas pelo front.
- **Troca de front sem dor.** Amanhã dá para fazer um app mobile que consome a mesma API, sem reescrever regra nenhuma.

> Quem conecta onde: **Carlos** (API Node, via Prisma) · **Vinícius** (Java, JDBC direto na porta 3306) · **Japa** (PHP, só via API Node).
