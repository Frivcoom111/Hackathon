# company (`/company`)

Gestão da empresa pelo membro autenticado: perfil, membros, vagas e candaturas.

## Rotas
- `GET /company/profile` · `PATCH /company/profile` (ADMIN) — perfil da empresa (CNPJ não editável).
- `PATCH /company/me` · `PATCH /company/me/password` — dados do próprio membro.
- `GET|POST|PATCH|DELETE /company/members` (ADMIN) + `POST /members/:id/totp/reset` — membros.
- `GET|POST /company/jobs`, `GET|PATCH /company/jobs/:jobId`, `PATCH /company/jobs/:jobId/status` — vagas.
- `GET /company/jobs/:jobId/applications` · `PATCH .../applications/:id/status` — candidaturas.

## Regras de negócio
- Todas as rotas exigem COMPANY com MFA verificada (`requireCompany`); ações de membros/perfil exigem ADMIN (`requireCompanyAdmin`).
- Membro não pode alterar/desativar/resetar a si mesmo; alvo precisa ser da mesma empresa.
- Status da vaga: `ACTIVE↔PAUSED`, ambos → `CLOSED` (terminal, irreversível).
- Status da candidatura: `PENDING → ANALYSING → APPROVED|REJECTED`; estados finais são imutáveis; APPROVED/REJECTED só a partir de ANALYSING.
- Mudança de status notifica o estudante (ver [notification](./notification.md)).
- Desativação de membro é soft delete (`User.isActive=false`).
