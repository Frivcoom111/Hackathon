# course (`/courses`)

Listagem de cursos para o front (ex.: selects de cadastro/criação de vaga).

## Rotas
- `GET /courses` — lista os cursos ativos (id, name, code, periods), ordenados por nome.

## Regras de negócio
- Exige autenticação (qualquer papel).
- Retorna apenas cursos com `isActive=true`.
- Somente leitura; criação/edição de cursos não é exposta por esta API.
