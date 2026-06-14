# address (`/address`)

CRUD de endereço reutilizável para estudante e empresa. Um endereço por dono.

## Rotas
- `POST|GET|PUT|DELETE /address/me` — endereço do estudante autenticado (`requireStudent`).
- `POST|GET|PUT|DELETE /address/company` — endereço da empresa; leitura por qualquer membro,
  escrita só ADMIN (`requireCompany` + `requireCompanyAdmin` na escrita).

## Regras de negócio
- Um endereço por dono: `POST` → 409 se já existe; `GET|PUT|DELETE` → 404 se não existe.
- `DELETE` remove a linha de Address; a FK do dono é zerada (relação opcional → SetNull).
- O endereço da empresa é resolvido pela empresa do membro autenticado.
- O endereço do estudante é exigido para se candidatar a vagas (ver [jobs](./jobs.md)).
