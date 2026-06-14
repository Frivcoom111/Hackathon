# Módulos da API

Resumo de cada módulo: o que faz e quais regras de negócio aplica. Leitura rápida antes de ir
ao código. Detalhes de request/response estão no OpenAPI (`/docs` em desenvolvimento).

| Módulo | Prefixo | Resumo |
|--------|---------|--------|
| [auth](./auth.md) | `/auth` | Cadastro, login e MFA (TOTP) |
| [company](./company.md) | `/company` | Empresa, membros, vagas e candidaturas |
| [student](./student.md) | `/student` | Perfil, currículo e candidaturas do estudante |
| [jobs](./jobs.md) | `/jobs` | Listagem de vagas e candidatura |
| [address](./address.md) | `/address` | Endereço de estudante e empresa |
| [notification](./notification.md) | `/notifications` | Notificações do usuário |
| [course](./course.md) | `/courses` | Listagem de cursos |

## Convenções
- Camadas: `schema` (validação de entrada) → `repository` (Prisma) → `service` (regra de
  negócio) → `controller` (HTTP) → `routes` (wiring + guards).
- Respostas tipadas por interfaces em `<modulo>.types.ts`; schemas Zod só validam entrada.
- Erros via classes de `shared/errors/AppError`; respostas via `shared/utils/response`.
- Guard de usuário: `requireUser` (`shared/utils`); guards de papel em `shared/middlewares/auth.middlewares`.
