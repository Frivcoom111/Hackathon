# Fluxos Completos (passo a passo de cada ação)

> Cada ação importante de ponta a ponta: do clique do usuário até o banco.
> **Conferido contra as rotas/services reais da API.** Ótimo para demonstrar ao vivo.

---

## Login do aluno

```text
web/src/pages/auth/login.php
  → POST /auth/login
  → AuthService.login (API)
  → Prisma busca o User → bcrypt compara a senha
  → como role = STUDENT, a API retorna o token COMPLETO ("AUTHENTICATED")
  → PHP salva o token em $_SESSION → redireciona para a home
```

## Login da empresa (com autenticador / 2FA)

```text
Empresa digita email/senha
  → PHP chama /auth/login
  → API retorna um tempToken e:
       • 1º acesso → "TOTP_SETUP" com o QR Code JÁ na resposta
       • próximos  → "TOTP_REQUIRED"
  → Empresa escaneia (1º acesso) e digita o código de 6 dígitos
  → PHP chama /auth/totp/setup/confirm (1º acesso) ou /auth/totp/verify (demais)
  → API retorna o token FINAL (mfaVerified = true)
  → PHP salva o token e o papel "empresa" → dashboard
```
> O **Admin** também passa por 2FA, mas normalmente entra pelo **desktop Java**, não pelo site.

---

## Cadastro do aluno

```text
PHP busca os cursos em /courses (rota PÚBLICA)
  → Aluno preenche o formulário
  → PHP envia JSON para /auth/register/student
  → API valida com Zod e confere se o curso existe
  → API criptografa a senha (bcrypt)
  → API cria User + Student + StudentCourse (tudo em uma transação)
  → PHP mostra sucesso e manda para o login
```
> Cadastrar **não** loga e **não** envia currículo. O currículo é enviado **depois**, já logado, em `PATCH /student/resume`.

## Cadastro da empresa

```text
Empresa preenche o formulário
  → PHP envia JSON para /auth/register/company
  → API cria User(COMPANY) + Address + Company(PENDING) + CompanyMember(ADMIN)
  → A empresa fica com status PENDING (aguardando a faculdade)
```
> Enquanto a empresa não for `APPROVED` pelo **Admin no desktop Java**, suas vagas **não aparecem** na vitrine.

---

## Perfil do aluno

```text
Aluno abre o perfil
  → PHP chama /student/profile
  → API retorna os dados do aluno + candidaturas
  → Aluno edita dados (PATCH /student/profile) ou troca senha (PATCH /student/password)
  → API valida e salva no banco
```

## Upload e download de currículo

```text
PHP envia o currículo para PATCH /student/resume (multipart, campo "resume")
  → multer (API) salva o arquivo em api/uploads/resumes/
  → API guarda o caminho em Student.resumePath
  → para baixar: GET /student/resume/download (aluno) ou
    GET /company/jobs/:jobId/applications/:id/resume (empresa, no candidato)
```
> O banco guarda **só o caminho**; o arquivo físico mora na API.

---

## Empresa cria uma vaga

```text
Empresa abre o formulário de vaga
  → PHP busca os cursos
  → Empresa envia o formulário → POST /company/jobs
  → API valida que é uma empresa logada (com MFA)
  → API cria o Job ligado ao companyId (status ACTIVE)
  → o dashboard passa a listar a vaga
```

## Aluno se candidata a uma vaga

```text
Aluno clica em "candidatar" (currículo é OPCIONAL no envio)
  → PHP chama POST /jobs/:jobId/apply
  → API valida: a vaga está ACTIVE e a empresa APPROVED?
  → API valida: o aluno é elegível? tem endereço cadastrado?
  → API verifica candidatura duplicada (mesmo aluno + vaga → 409)
  → API cria a Application (status PENDING)
  → API notifica a empresa dona da vaga (NEW_APPLICATION)
```

## Empresa analisa um candidato

```text
Empresa abre "candidatos" de uma vaga
  → PHP chama GET /company/jobs/:jobId/applications
  → API retorna as candidaturas
  → Empresa muda o status: PENDING → ANALYSING → APPROVED/REJECTED
  → PHP chama PATCH /company/jobs/:jobId/applications/:id/status
  → API valida que a vaga pertence àquela empresa
  → API só deixa APPROVED/REJECTED a partir de ANALYSING
  → API atualiza o status e notifica o aluno (APPLICATION_STATUS)
```

## Aluno cancela a candidatura

```text
Aluno abre "minhas candidaturas"
  → PHP chama DELETE /student/applications/:id
  → API valida que a candidatura é do próprio aluno (senão 403)
  → API só permite cancelar se o status for PENDING ou ANALYSING
  → API faz soft delete (status CANCELLED + deletedAt)
  → API notifica a empresa (APPLICATION_CANCELLED)
```

---

## Ciclo de vida completo de uma vaga (a história inteira)

```text
1. Admin aprova a empresa (PENDING → APPROVED)         [desktop Java]
2. Empresa publica a vaga (status ACTIVE)              [site PHP → API]
3. A vaga passa a aparecer na vitrine                  [API filtra ACTIVE + empresa APPROVED]
4. Aluno encontra a vaga e se candidata (PENDING)      [site PHP → API]
5. Empresa coloca em análise (ANALYSING)               [site PHP → API]
6. Empresa aprova ou rejeita (APPROVED/REJECTED)       [site PHP → API]
7. Aluno é notificado a cada passo                     [API → Notification]
8. Quando não quer mais candidatos: CLOSED (terminal)  [site PHP → API]
```
