# student (`/student`)

Perfil e candidaturas do estudante autenticado.

## Rotas
- `GET|PATCH /student/profile` — perfil (name/phone no Student; email no User, em transação).
- `PATCH /student/password` — troca de senha (exige senha atual).
- `PATCH /student/resume` — upload de currículo (multipart, campo `resume`).
- `GET /student/applications` — candidaturas (paginado).
- `DELETE /student/applications/:id` — cancela candidatura.

## Regras de negócio
- Todas as rotas exigem STUDENT autenticado (`requireStudent`).
- Cancelamento só com status `PENDING` ou `ANALYSING`; é soft delete (status `CANCELLED` + `deletedAt`).
- Só o dono cancela a própria candidatura (ownership por `studentId`).
- Cancelar notifica a empresa dona da vaga (ver [notification](./notification.md)).
- Endereço do estudante fica no módulo [address](./address.md) (`/address/me`).
