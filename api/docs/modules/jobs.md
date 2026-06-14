# jobs (`/jobs`)

Vitrine de vagas e candidatura do estudante.

## Rotas
- `GET /jobs` — lista vagas (paginado; filtros: courseId, area, modality, search).
- `GET /jobs/:jobId` — detalhe da vaga.
- `POST /jobs/:jobId/apply` — candidatura (STUDENT; multipart com `resume` opcional).

## Regras de negócio
- Todas as rotas exigem autenticação; `apply` exige STUDENT (`requireStudent`).
- Só vagas `ACTIVE` de empresas `APPROVED` são visíveis/candidatáveis.
- Para se candidatar, o estudante precisa ser elegível, ter **endereço** e **currículo** cadastrados.
- Currículo: usa o arquivo enviado no apply; se ausente, usa o `resumePath` do perfil; sem nenhum → 400.
- Candidatura duplicada (mesmo student+job) é bloqueada (409).
- Candidatura criada notifica a empresa dona da vaga (ver [notification](./notification.md)).
