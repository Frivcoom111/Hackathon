# Front-end Web (PHP Orientado a Objetos)

> Pasta: `web/`. É o site que o **aluno** e a **empresa** acessam pelo navegador.
> Regra absoluta: **o PHP não acessa o banco** — ele consome a API Node.js por HTTP.

## Por que PHP com POO?

O desafio do Hackathon **exige** Orientação a Objetos com rigor (a não aplicação é eliminatória).
Por isso o domínio do negócio é modelado em **classes**: `Aluno`, `Empresa`, `Vaga`, `Candidatura`.

Requisitos atendidos:
- **Modelagem de domínio** com classes bem definidas.
- **Painel da Empresa** (back office): CRUD de vagas + lista de candidatos por vaga.
- **Portal do Aluno**: listagem de vagas + formulário de candidatura.
- **Integração só por HTTP** (nunca banco direto).
- **Boas práticas de POO**: encapsulamento, separação de responsabilidades, código limpo.

---

## Estrutura de pastas

```text
web/
├── app/                       # camada de apoio (POO de "infra")
│   ├── Api/                   # ApiClient + Api (fachada para chamar a API)
│   ├── Auth/                  # JwtManager, Guard (controle de sessão/token)
│   ├── Config/                # Config.php (URL da API)
│   └── Services/              # AuthService, VagaService, etc.
└── src/
    ├── index.php              # roteador principal (?page=)
    ├── router.php             # URLs amigáveis
    ├── layouts/               # header.php e footer.php
    ├── classes/               # Aluno, Empresa, Vaga, Candidatura (domínio POO)
    ├── pages/                 # as telas (publico, aluno, empresa, auth)
    ├── css/                   # visual
    └── assets/                # imagens e Bootstrap local
```

---

## As classes de domínio (o coração da POO)

Ficam em `web/src/classes/`. Cada classe representa uma entidade do negócio, recebe um array vindo da API
e vira um objeto com **encapsulamento** (atributos privados + getters/setters).

- **`Vaga.php`** — constantes de modalidade/status; construtor a partir do array da API; **formata salário**, **traduz a modalidade** (`REMOTE` → "Remoto"); `toArray()`.
- **`Empresa.php`** — constantes de status; verifica aprovada/bloqueada; **formata o CNPJ**; `toArray()`.
- **`Aluno.php`** — atributos do aluno; **formata o CPF**; `toArray()`.
- **`Candidatura.php`** — constantes de status; `getStatusLabel()` (rótulo amigável), `getStatusBadgeClass()` (cor do selo); `toArray()`.

> Padrão importante: a API fala em código (`APPROVED`, `REMOTE`) e as classes **traduzem** isso para algo que o usuário entende na tela.

---

## A ponte com a API

Toda comunicação com a API passa por uma camada dedicada (em `web/app/Api/`):

- Monta a URL completa a partir da base configurada em `web/app/Config/Config.php` (`http://localhost:3000`).
- Usa um cliente HTTP (cURL/Guzzle).
- Inclui o cabeçalho `Authorization: Bearer <token>` quando o usuário está logado.
- Envia **JSON** quando não há arquivo; envia **multipart/form-data** quando há upload (currículo).
- Decodifica o JSON de resposta em array PHP, que vira objeto (`Aluno`, `Vaga`...).

> Não precisa de `.env` no PHP — a URL da API já vem no `Config.php`.

---

## As páginas (telas)

Organizadas em `web/src/pages/` por área:

### Público (qualquer um vê)
- **`publico/home.php`** — banner + vagas em destaque (busca em `/jobs`).
- **`publico/vagas.php`** — lista de vagas com filtros, cards e modal de detalhe; botão de candidatar.
- **`publico/empresas.php`** — empresas parceiras.

### Autenticação
- **`auth/login.php`** — login. Aluno entra direto; empresa segue o fluxo do QR Code (2FA).
- **`auth/cadastro.php`** — cadastro com abas: **aluno** ou **empresa**.

### Área do aluno
- **`aluno/perfil.php`** — perfil do aluno: editar dados, currículo e ver candidaturas.

### Área da empresa (back office)
- **`empresa/dashboard.php`** — painel com contadores (vagas, candidatos).
- **`empresa/vaga-form.php`** — criar/editar vaga.
- **`empresa/candidatos.php`** — lista de candidatos por vaga e mudança de status.
- **`empresa/_empresa_menu.php`** — menu da empresa (exige login de empresa).

### Outras
- **`notificacoes.php`** — notificações do usuário.
- **`download/`** — proxy para baixar currículos pela API.

> ⚠️ **Importante (alinhamento com a API):** a API atual expõe, para o aluno, **perfil + currículo + candidaturas**.
> Recursos como **foto de perfil, capa e certificados** não têm rota na API hoje. Se a tela mostrar esses campos,
> trate como **layout/evolução futura**, não como funcionalidade integrada.

---

## O roteamento (como a URL vira página)

- `index.php` inicia a sessão, lê `?page=` (ou usa `home`), monta o título e inclui `header` → a página → `footer`. Rota inexistente → 404. `?page=logout` destrói a sessão.
- `router.php` traduz URLs amigáveis (ex.: `/vagas`) para o `?page=vagas`.

| Página | URL |
|--------|-----|
| Início | `http://localhost:8000` |
| Vagas | `?page=vagas` |
| Login | `?page=login` |
| Cadastro | `?page=cadastro` |
| Painel da empresa | `?page=empresa-dashboard` |
| Perfil do aluno | `?page=aluno-perfil` |
| Notificações | `?page=notificacoes` |

---

## Como o login fica guardado

O PHP usa **`$_SESSION`** para guardar o **token JWT** e o **papel** (`role`) do usuário após o login.
A cada chamada protegida à API, ele reenvia esse token no cabeçalho `Authorization`.

---

## Upload de currículo (como funciona na prática)

```text
1. PHP envia o currículo para a API (PATCH /student/resume) em multipart.
2. A API (multer) salva o arquivo em api/uploads/resumes/.
3. A API guarda só o caminho no banco (Student.resumePath).
4. Para baixar, o PHP usa as rotas de download da API (ex.: /student/resume/download).
```

Ou seja: o **arquivo físico** mora na API; o **banco** guarda só o caminho.
