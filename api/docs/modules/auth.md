# auth (`/auth`)

Cadastro, autenticação e MFA (TOTP).

## Rotas
- `POST /auth/register/student` — cadastra User(STUDENT) + Student + vínculo de curso. Público.
- `POST /auth/register/company` — cadastra Company(PENDING) + User(COMPANY, inativo) + CompanyMember(ADMIN). Público.
- `POST /auth/login` — autentica.
- `GET /auth/totp/setup` · `POST /auth/totp/setup/confirm` · `POST /auth/totp/verify` — fluxo MFA.
- `GET /auth/me` — dados do usuário autenticado (varia por papel).

## Regras de negócio
- Senha sempre com hash (`bcryptUtils`); token JWT só no login (`generateToken`).
- STUDENT/ADMIN recebem JWT completo no login. COMPANY recebe `tempToken` (5min) e segue para o TOTP.
- Empresa nasce PENDING e `User.isActive=false`; só acessa rotas protegidas após aprovação (feita pelo app Java).
- `register/*` não retorna token; cadastro não loga.
- `me` nunca expõe `password`/`totpSecret`.
