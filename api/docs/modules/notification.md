# notification (`/notifications`)

Notificações por usuário. Criadas por outros módulos; consumidas pelo dono.

## Rotas
- `GET /notifications` — lista as do usuário autenticado (paginado; filtro `?unread=true`).
- `PATCH /notifications/read-all` — marca todas como lidas.
- `PATCH /notifications/:id/read` — marca uma como lida.

## Gatilhos (quem dispara)
- **Status da candidatura** (`company.service.changeApplicationStatus`) → notifica o **estudante**
  em ANALYSING/APPROVED/REJECTED. Tipo `APPLICATION_STATUS`.
- **Nova candidatura** (`jobs.service.apply`) → notifica os **membros da empresa**. Tipo `NEW_APPLICATION`.
- **Cancelamento** (`student.service.cancelApplication`) → notifica os **membros da empresa**. Tipo `APPLICATION_CANCELLED`.

## Regras de negócio
- Qualquer usuário autenticado acessa apenas as próprias notificações (`:id` de outro → 403).
- Notificação para empresa é fan-out: uma por membro (`createForCompanyMembers`).
- Falha ao notificar nunca quebra a ação principal (envolto em try/catch).
