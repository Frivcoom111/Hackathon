# Contratos de API consumidos pelo PHP

Contrato usado pela camada PHP para integrar com o backend Node.js do Portal de Estagios UniALFA.

Padrao de resposta do backend:

```json
{
  "success": true,
  "message": "Operacao realizada com sucesso.",
  "data": {}
}
```

Listas paginadas retornam:

```json
{
  "success": true,
  "data": [],
  "meta": {
    "page": 1,
    "limit": 10,
    "total": 0,
    "totalPages": 0
  }
}
```

O `ApiClient` do PHP aceita o envelope e entrega `data` para os services.

## Auth

### POST /auth/login

Payload:

```json
{
  "email": "aluno@unialfa.edu.br",
  "password": "Senha@123"
}
```

Resposta autenticada:

```json
{
  "type": "AUTHENTICATED",
  "token": "jwt"
}
```

Depois do token final, o PHP chama `GET /auth/me` para montar a sessao com usuario e perfil de acesso.

### GET /auth/me

Requer `Authorization: Bearer <token>`.

Retorna perfil de aluno ou membro de empresa com o `user.role`.

### POST /auth/register/student

Payload enviado pelo PHP:

```json
{
  "email": "aluno@unialfa.edu.br",
  "password": "Senha@123",
  "name": "Aluno",
  "ra": "123456",
  "cpf": "00000000000",
  "phone": "44999999999",
  "courseId": "uuid-do-curso",
  "status": "ACTIVE",
  "startedAt": "2026-06-12"
}
```

`courseId` vem do formulario ou de `DEFAULT_COURSE_ID` no `.env`.

### POST /auth/register/company

Payload enviado pelo PHP:

```json
{
  "email": "empresa@email.com",
  "password": "Senha@123",
  "name": "Empresa",
  "cnpj": "00000000000000",
  "description": "Descricao da empresa",
  "phone": "44999999999",
  "address": {
    "street": "Rua",
    "number": "123",
    "district": "Centro",
    "city": "Umuarama",
    "state": "PR",
    "zipCode": "87500000"
  },
  "member": {
    "name": "Responsavel",
    "cpf": "00000000000",
    "phone": "44999999999"
  }
}
```

## Vagas publicas

### GET /jobs

Filtros aceitos pelo backend:

- `page`
- `limit`
- `courseId`
- `area`
- `modality`: `PRESENCIAL`, `REMOTE`, `HYBRID`
- `search`

O backend retorna somente vagas `ACTIVE` de empresas `APPROVED`.

### GET /jobs/:jobId

Retorna detalhe publico da vaga.

### POST /jobs/:jobId/apply

Requer aluno autenticado.

Payload:

```json
{
  "coverLetter": "Texto opcional"
}
```

## Aluno

### GET /student/applications

Lista candidaturas do aluno autenticado.

### DELETE /student/applications/:id

Cancela candidatura.

## Empresa

Todas as rotas exigem empresa autenticada.

### GET /company/jobs

Lista vagas da propria empresa.

### POST /company/jobs

Payload principal:

```json
{
  "title": "Estagio em Desenvolvimento",
  "description": "Descricao da vaga",
  "area": "Tecnologia",
  "requirements": "PHP\nMySQL\nGit",
  "salary": 1200,
  "location": "Umuarama, PR",
  "modality": "HYBRID",
  "courseId": "uuid-opcional"
}
```

### GET /company/jobs/:jobId

Retorna vaga da propria empresa.

### PATCH /company/jobs/:jobId

Atualiza dados da vaga.

### PATCH /company/jobs/:jobId/status

Atualiza status da vaga para `ACTIVE`, `PAUSED` ou `CLOSED`.

O botao "Excluir" do PHP usa essa rota com `CLOSED`.

### GET /company/jobs/:jobId/applications

Lista candidaturas de uma vaga.

### PATCH /company/jobs/:jobId/applications/:id/status

Atualiza status da candidatura para `ANALYSING`, `APPROVED` ou `REJECTED`.

## Rotas em modo demonstracao

Algumas telas publicas usam dados locais quando `USE_MOCK_DATA=true`, como empresas parceiras, notificacoes e cards de destaque. Com mock desligado, esses pontos dependem da evolucao do backend.
