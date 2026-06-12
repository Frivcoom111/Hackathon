# Schema de dominio usado como referencia

O PHP nao executa SQL nem usa PDO/MySQL. Este resumo serve apenas para alinhar models e nomes de campos com a API.

## Entidades principais

- `Address`: endereco compartilhado por alunos e empresas.
- `User`: autenticacao, email, role e status de acesso.
- `Course`: cursos da instituicao.
- `Student`: dados academicos do aluno, RA, periodo e aptidao.
- `Company`: cadastro da empresa e status institucional.
- `CompanyMember`: responsavel ou recrutador da empresa.
- `Job`: vaga publicada pela empresa.
- `Application`: candidatura do aluno a uma vaga.
- `Notification`: avisos de status e recomendacoes.

## Enums relevantes

### User.role

- `ADMIN`
- `COMPANY`
- `STUDENT`

### Company.status

- `PENDING`
- `ANALYSING`
- `APPROVED`
- `BLOCKED`

### Job.modality

- `PRESENCIAL`
- `REMOTE`
- `HYBRID`

### Job.status

- `ACTIVE`
- `PAUSED`
- `CLOSED`

### Application.status

- `PENDING`
- `ANALYSING`
- `APPROVED`
- `REJECTED`
- `CANCELLED`
