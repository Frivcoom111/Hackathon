# Portal de Estagios UniALFA - PHP POO

Aplicacao PHP orientada a objetos para o Portal de Estagios e Empregos UniALFA. O projeto entrega uma camada web para alunos e empresas, consumindo uma API externa para autenticacao, vagas, candidaturas e gestao de oportunidades.

## Funcionalidades

- Aplicacao PHP 8.2+ em MVC simples.
- Composer com autoload PSR-4.
- Front controller em `public/index.php`.
- Cliente HTTP proprio em `App\Http\ApiClient`.
- Models de dominio com encapsulamento.
- Services para consumir API Node.js sem acesso direto ao banco.
- Sessao PHP com token JWT, usuario e role.
- Portal do aluno: vagas, candidatura, dashboard, candidaturas e notificacoes.
- Painel da empresa: dashboard, CRUD de vagas e candidatos por vaga.
- Paginas publicas: home, empresas, conteudos, sobre, FAQ, vagas e detalhe da vaga.
- Modo demonstracao com dados mockados quando a API Node nao estiver ativa.
- Interface responsiva com identidade visual UniALFA.

## Como rodar

```bash
composer install
php -S 127.0.0.1:8080 -t public
```

Acesse:

```text
http://127.0.0.1:8080
```

Se a porta 8080 estiver ocupada, use a 8081:

```bash
php -S 127.0.0.1:8081 -t public
```

## Docker

```bash
docker compose up --build
```

## Variaveis

Copie `.env.example` para `.env` se quiser configurar localmente.

```text
APP_URL=http://127.0.0.1:8080
API_BASE_URL=http://127.0.0.1:3000
USE_MOCK_DATA=true
DEFAULT_COURSE_ID=
```

Com `USE_MOCK_DATA=true`, a aplicacao funciona mesmo sem a API Node.js.

`DEFAULT_COURSE_ID` deve receber o UUID de um curso existente no backend. Esse valor e usado no cadastro de aluno quando o formulario nao recebe um curso explicitamente.

## API consumida

Repositorio de referencia do backend: `https://github.com/Frivcoom111/Hackathon`

Endpoints principais consumidos:

- `POST /auth/login`
- `GET /auth/me`
- `POST /auth/register/student`
- `POST /auth/register/company`
- `GET /jobs`
- `GET /jobs/{jobId}`
- `POST /jobs/{jobId}/apply`
- `GET /student/applications`
- `DELETE /student/applications/{id}`
- `GET /company/jobs`
- `POST /company/jobs`
- `GET /company/jobs/{jobId}`
- `PATCH /company/jobs/{jobId}`
- `PATCH /company/jobs/{jobId}/status`
- `GET /company/jobs/{jobId}/applications`
- `PATCH /company/jobs/{jobId}/applications/{id}/status`

Para testar em modo demonstracao:

- Email contendo `empresa` entra como empresa.
- Qualquer outro email entra como aluno.

## Rotas principais

### Publicas

- `GET /`
- `GET /empresas`
- `GET /conteudos`
- `GET /sobre`
- `GET /faq`
- `GET /login`
- `POST /login`
- `GET /cadastro/aluno`
- `POST /cadastro/aluno`
- `GET /cadastro/empresa`
- `POST /cadastro/empresa`
- `GET /vagas`
- `GET /vagas/{id}`
- `POST /vagas/{id}/candidatar`

### Aluno

- `GET /aluno`
- `GET /aluno/dashboard`
- `GET /aluno/candidaturas`
- `POST /aluno/candidaturas/{id}/cancelar`
- `GET /aluno/notificacoes`
- `POST /aluno/notificacoes/{id}/ler`

### Empresa

- `GET /empresa`
- `GET /empresa/dashboard`
- `GET /empresa/vagas`
- `GET /empresa/vagas/nova`
- `POST /empresa/vagas`
- `GET /empresa/vagas/{id}/editar`
- `POST /empresa/vagas/{id}`
- `POST /empresa/vagas/{id}/excluir`
- `GET /empresa/vagas/{id}/candidatos`
- `POST /empresa/candidaturas/{id}/status`

## Estrutura

```text
app/
  Controllers/
  Core/
  Http/
  Models/
  Services/
  Support/
  Views/
config/
docs/
docker/
public/
```

## Classes principais

- `Router`: resolve rotas e parametros.
- `Controller`: base para views, redirects e controle de acesso.
- `Session`: token, usuario, role e mensagens flash.
- `ApiClient`: metodos HTTP `get`, `post`, `put`, `patch`, `delete`.
- `User`, `Student`, `Company`, `Job`, `Application`, `Notification`: dominio.
- `AuthService`, `JobService`, `ApplicationService`, `CompanyService`, `NotificationService`: integracao com API.

## Regras importantes

- O PHP nao executa SQL.
- O PHP nao abre conexao MySQL.
- Toda leitura/escrita de negocio passa pelos services e pela API Node.js.
- Segredos e variaveis sensiveis devem ficar apenas em `.env`, nunca versionados.

## Documentacao complementar

- `docs/DECISOES_TECNICAS.md`
- `docs/API_CONTRATOS.md`
- `docs/SCHEMA_DOMINIO.md`
