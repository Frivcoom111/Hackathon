# Relatorio extremamente detalhado do projeto

Este relatorio foi feito para explicar o projeto inteiro do Portal de Estagios UniALFA de forma didatica, como material de estudo e apresentacao para o grupo.

Importante: alguns arquivos do projeto sao muito grandes, repetitivos, gerados automaticamente ou de biblioteca. Entao este relatorio usa tres niveis de detalhe:

1. Arquivos principais pequenos: explicacao linha por linha.
2. Arquivos principais grandes: explicacao por blocos de linhas, mantendo a ordem real do arquivo.
3. Arquivos gerados, vendor, Bootstrap minificado, Postman/Insomnia e documentacao extensa: explicacao por responsabilidade, porque explicar cada linha desses arquivos nao ajuda a entender a regra de negocio.

Arquivos como `node_modules`, `vendor`, `api/src/generated`, `bootstrap.min.css`, `bootstrap.bundle.min.js`, colecoes Postman/Insomnia e PDFs nao foram explicados linha por linha porque sao bibliotecas ou arquivos gerados.

---

## 1. Visao geral do sistema

O projeto possui tres grandes partes:

```text
api                    API Node.js com Express, Prisma e MySQL
web                    Portal PHP que o aluno e a empresa acessam pelo navegador
portal-desktop-swing   Sistema Java Swing usado como painel administrativo/backoffice
```

O fluxo principal do sistema web e:

```text
Usuario abre o navegador
PHP renderiza a pagina
PHP chama a API Node.js
API valida dados e regras
API usa Prisma
Prisma acessa MySQL/MariaDB
API retorna JSON
PHP mostra o resultado na tela
```

O ponto mais importante de arquitetura e:

```text
O PHP nao acessa o banco diretamente.
O PHP consome a API Node.js por HTTP.
```

---

## 2. Estrutura de pastas

### Raiz do projeto

```text
api/
web/
portal-desktop-swing/
docker-compose.yml
EXPLICACAO_UPLOAD_IMAGENS.md
```

### `api/`

Responsavel pela API REST.

Principais pastas:

```text
api/src/app.ts                  monta o Express e registra rotas
api/src/server.ts               inicia o servidor
api/src/config/env.ts           valida variaveis de ambiente
api/src/lib/prisma.ts           cria o cliente Prisma
api/src/modules/                modulos de negocio
api/src/shared/                 middlewares, schemas e utilitarios comuns
api/prisma/schema.prisma        modelo do banco
api/prisma/migrations/          historico de alteracoes do banco
api/uploads/                    arquivos enviados pelo usuario
```

### `web/`

Responsavel pelo portal PHP.

Principais pastas:

```text
web/src/index.php               entrada principal do site
web/src/api.php                 ponte entre PHP e API Node.js
web/src/classes/                classes POO do PHP
web/src/layouts/                header e footer
web/src/pages/                  paginas do portal
web/src/css/style.css           visual principal local
web/src/assets/                 imagens e Bootstrap local
```

### `portal-desktop-swing/`

Responsavel pelo sistema administrativo Java.

Principais pastas:

```text
config/                         conexao com banco
dao/                            consultas SQL
service/                        regras de negocio
model/                          entidades Java
gui/                            telas Swing
util/                           validacoes, senha, sessao e relatorios
```

---

## 3. Banco de dados

Arquivo principal:

```text
api/prisma/schema.prisma
```

### Linhas 1 a 8

```prisma
generator client
datasource db
```

Define que o Prisma vai gerar o cliente em `api/src/generated/prisma` e que o banco usado e MySQL.

### Linhas 12 a 55: enums

`Role` define o tipo do usuario:

```text
ADMIN
COMPANY
STUDENT
```

`CompanyStatus` define o status da empresa:

```text
PENDING    pendente
ANALYSING  em analise
APPROVED   aprovada
BLOCKED    bloqueada
```

`CompanyMemberRole` define papel do usuario dentro da empresa:

```text
ADMIN
RECRUITER
```

`JobStatus` define status da vaga:

```text
ACTIVE
PAUSED
CLOSED
```

`Modality` define modalidade:

```text
PRESENCIAL
REMOTE
HYBRID
```

`ApplicationStatus` define status da candidatura:

```text
PENDING
ANALYSING
APPROVED
REJECTED
CANCELLED
```

`StudentCourseStatus` define status do curso do aluno:

```text
ACTIVE
COMPLETED
CANCELLED
```

### Linhas 58 a 71: model `Address`

Guarda endereco reutilizavel.

Campos:

```text
id          identificador unico
street      rua
number      numero
complement  complemento opcional
district    bairro
city        cidade
state       UF com 2 letras
zipCode     CEP
createdAt   data de criacao
updatedAt   data de atualizacao
student     relacao opcional com aluno
company     relacao opcional com empresa
```

### Linhas 74 a 87: model `User`

Guarda dados de login.

Campos:

```text
id             identificador
email          email unico
password       senha criptografada
role           ADMIN, COMPANY ou STUDENT
isActive       define se pode acessar
totpSecret     segredo do Authenticator
totpEnabled    se o Authenticator ja foi ativado
createdAt      criacao
updatedAt      atualizacao
student        relacao com aluno
companyMember  relacao com membro da empresa
notifications  notificacoes do usuario
```

### Linhas 90 a 100: model `Course`

Guarda cursos.

Campos:

```text
id        identificador
name      nome unico
code      codigo opcional unico
periods   quantidade de periodos
isActive  se aparece no portal
createdAt criacao
updatedAt atualizacao
```

### Linhas 103 a 124: model `Student`

Guarda perfil do aluno.

Campos principais:

```text
id                identificador
userId            liga ao User
addressId         liga ao Address
name              nome
ra                registro academico unico
cpf               CPF unico
phone             telefone
headline          titulo profissional curto do perfil
summary           resumo/descricao do perfil
profilePhotoPath  caminho da foto de perfil
coverPhotoPath    caminho da capa do perfil
isEligible        se esta apto a se candidatar
resumePath        caminho do curriculo
createdAt         criacao
updatedAt         atualizacao
```

Observacao importante:

```text
profilePhotoPath e coverPhotoPath guardam o caminho do arquivo.
Nao guardam a imagem em binario dentro do banco.
```

### Linhas 127 a 137: model `Certificate`

Guarda certificados do aluno.

Campos:

```text
id          identificador
studentId   aluno dono do certificado
name        nome do certificado
institution instituicao
issuedAt    data de emissao
filePath    caminho do arquivo se existir
```

### Linhas 140 a 153: model `StudentCourse`

Liga aluno ao curso.

Campos:

```text
studentId   aluno
courseId    curso
status      ACTIVE, COMPLETED ou CANCELLED
startedAt   data de inicio
finishedAt  data de conclusao se finalizado
```

`@@unique([studentId, courseId])` impede o mesmo aluno de ter o mesmo curso duplicado.

### Linhas 156 a 169: model `Company`

Guarda empresa.

Campos:

```text
id          identificador
addressId   endereco
name        nome
cnpj        CNPJ unico
description descricao
phone       telefone
status      PENDING, ANALYSING, APPROVED ou BLOCKED
members     usuarios ligados a empresa
jobs        vagas da empresa
```

### Linhas 172 a 185: model `CompanyMember`

Guarda responsavel/recrutador da empresa.

Campos:

```text
companyId liga a empresa
userId    liga ao User de login
role      ADMIN ou RECRUITER
name      nome do responsavel
cpf       CPF unico
phone     telefone
```

### Linhas 189 a 207: model `Job`

Guarda vagas.

Campos:

```text
companyId    empresa dona da vaga
courseId     curso relacionado
title        titulo
description  descricao
area         area da vaga
requirements requisitos
salary       bolsa/salario
location     localizacao
modality     modalidade
status       status
deletedAt    exclusao logica
applications candidaturas
```

### Linhas 210 a 223: model `Application`

Guarda candidatura.

Campos:

```text
studentId  aluno candidato
jobId      vaga
status     andamento da candidatura
resumePath curriculo usado
deletedAt  cancelamento/exclusao logica
```

`@@unique([studentId, jobId])` impede duas candidaturas do mesmo aluno na mesma vaga.

### Linhas 226 a 235: model `Notification`

Guarda notificacoes.

Campos:

```text
userId    dono da notificacao
title     titulo
message   texto
type      tipo
isRead    lida ou nao
createdAt criacao
```

---

## 4. API Node.js

### 4.1 `api/src/app.ts`

Arquivo que monta a aplicacao Express.

Linha por linha:

```text
1 importa cors, usado para permitir chamadas de outro front-end
2 importa express e o tipo Express
3 importa helmet, seguranca HTTP basica
4 importa path, usado para resolver caminho da pasta uploads
5 importa env, variaveis ja validadas
6 importa setupDocs, documentacao OpenAPI/Scalar
7 importa rotas de address
8 importa rotas de auth
9 importa rotas publicas de companies
10 importa rotas protegidas de company
11 importa rotas de courses
12 importa rotas de jobs
13 importa rotas de notifications
14 importa rotas de student
15 importa rotas de users
16 importa middleware global de erro
17 importa rate limiter global
19 declara appBuild, funcao que cria e devolve o Express
20 cria app com express()
21 ativa leitura de JSON no corpo das requisicoes
23 a 27 configura helmet; em desenvolvimento desliga CSP para facilitar testes
29 a 34 configura CORS; em desenvolvimento aceita qualquer origem
36 ativa limite global de requisicoes
37 expoe a pasta uploads como arquivos estaticos
39 registra /auth
40 registra /courses
41 registra /jobs
42 registra /companies
43 registra /company
44 registra /student
45 registra /address
46 registra /notifications
47 registra /users
49 ativa documentacao em desenvolvimento
51 registra handler de erro por ultimo
53 retorna o app pronto
```

### 4.2 `api/src/server.ts`

Linha por linha:

```text
1 importa appBuild
2 importa env
4 cria funcao main
5 chama appBuild para montar o Express
6 app.listen sobe o servidor na porta do .env
7 console.log mostra a URL da API
10 chama main()
11 catch captura erro ao iniciar
12 console.error exibe erro
13 process.exit(1) encerra com falha
```

### 4.3 `api/src/config/env.ts`

Explicacao por blocos:

```text
1 a 2 importa zod e dotenv
4 a 19 define todas as variaveis obrigatorias/opcionais
21 valida process.env com safeParse
23 a 29 se estiver errado, mostra os campos com problema e encerra
31 exporta env ja validado
32 exporta o tipo Env
```

Variaveis importantes:

```text
DATABASE_URL      conexao Prisma
DATABASE_HOST     host MySQL/MariaDB
DATABASE_PORT     porta
DATABASE_USER     usuario
DATABASE_PASSWORD senha
DATABASE_NAME     banco
JWT_SECRET        segredo do token
JWT_EXPIRES_IN    validade do token
FRONTEND_URL      origem permitida em producao
```

### 4.4 `api/src/lib/prisma.ts`

Linha por linha:

```text
1 importa PrismaMariaDb
2 importa PrismaClient
3 importa env
5 a 12 cria adapter MariaDB usando host, porta, usuario, senha e banco
14 cria instancia do PrismaClient
16 exporta prisma para as rotas usarem
```

### 4.5 `api/src/shared/utils/response.ts`

Padroniza respostas JSON.

```text
1 a 6 define metadados de paginacao
8 cria objeto response
9 success retorna { success: true, message, data }
13 paginated retorna { success: true, data, meta }
17 error retorna { success: false, message, details?, code? }
```

### 4.6 `api/src/shared/utils/bcryptUtils.ts`

```text
1 importa bcrypt
2 importa env
4 generateHash criptografa senha com salt configurado
8 compareHash compara senha digitada com hash do banco
```

### 4.7 `api/src/shared/utils/generateToken.ts`

```text
1 importa jsonwebtoken
2 importa env
3 importa tipos Role e CompanyMemberRole
5 a 12 define o conteudo do JWT: sub, email, role, mfaVerified, companyMemberRole
15 a 20 gera token assinado
17 permite sobrescrever expiracao, usado no token temporario do TOTP
```

### 4.8 `api/src/shared/middlewares/auth.middlewares.ts`

Arquivo que protege rotas.

```text
1 a 7 imports
9 inicia authMiddleware
11 pega header Authorization
13 a 15 exige formato Bearer token
17 extrai token
19 valida JWT com JWT_SECRET
21 a 24 busca no banco se o usuario continua ativo
26 a 28 bloqueia conta inativa
30 a 36 coloca dados do usuario em req.user
38 chama next
39 a 44 trata token invalido ou erro geral
48 a 66 requireCompanyAdmin permite acao apenas para COMPANY com MFA e role ADMIN
69 a 89 requireRole valida role e MFA quando necessario
92 exporta requireAdmin
93 exporta requireCompany
94 exporta requireStudent
```

### 4.9 `api/src/shared/middlewares/upload.middleware.ts`

Controla arquivos enviados.

```text
7 define pasta de curriculos
8 define pasta de fotos de perfil
9 define pasta de capas
11 a 13 cria pastas se nao existirem
15 a 19 mapeia MIME de curriculo para extensao
21 a 24 mapeia MIME de imagem para extensao
27 a 35 cria storage do multer com pasta e nome unico
37 a 50 cria upload com limite de 5MB e filtro de tipo
53 exporta uploadResume
59 exporta uploadProfilePhoto
65 exporta uploadCoverPhoto
```

### 4.10 `api/src/shared/middlewares/errorHandler.middlewares.ts`

```text
1 importa fs para apagar upload se der erro
2 a 6 imports de Express, Multer, Zod, AppError e response
8 inicia errorHandler
10 a 13 apaga arquivos enviados caso a request falhe
15 a 18 se for AppError, responde com status especifico
20 a 25 se for erro do Multer, retorna mensagem de upload
27 a 30 se for ZodError, retorna dados invalidos
32 console.error mostra erro inesperado
33 retorna erro 500
```

---

## 5. Modulo Auth da API

### 5.1 `api/src/modules/auth/auth.routes.ts`

```text
1 a 7 imports
9 cria router
10 cria AuthService com prisma
12 POST /login
13 valida body com loginSchema
14 chama service.login
16 responde sucesso
19 POST /register/student com uploadResume.single("resume")
20 a 23 junta req.body com caminho do arquivo
24 chama service.registerStudent
26 responde aluno cadastrado
29 POST /register/company
30 valida body
31 chama registerCompany
33 responde empresa cadastrada
36 GET /totp/setup exige authMiddleware
37 chama setupTotp
39 retorna QR Code
42 POST /totp/setup/confirm
43 valida codigo
44 confirma Authenticator
46 retorna sucesso
49 POST /totp/verify
50 valida codigo
51 verifica codigo
53 retorna token final
56 exporta router
```

### 5.2 `api/src/modules/auth/auth.schema.ts`

```text
1 importa zod local
2 importa passwordSchema
4 onlyDigits remove tudo que nao for numero
6 a 9 loginSchema valida email e senha
11 a 13 totpCodeSchema exige 6 digitos
15 a 29 registerStudentSchema valida cadastro de aluno
31 a 60 registerCompanySchema valida cadastro de empresa
62 a 80 schemas de resposta usados na documentacao
83 a 88 exporta tipos TypeScript inferidos pelo zod
```

### 5.3 `api/src/modules/auth/auth.service.ts`

Explicacao por blocos:

```text
1 a 7 imports
9 declara AuthService
10 recebe prisma no construtor
12 a 57 login
59 a 78 setupTotp
80 a 94 confirmTotp
96 a 110 verifyTotp
112 a 156 registerStudent
158 a 203 registerCompany
205 a 208 ensureCourseExists
210 a 232 findUserForTotp
234 a 240 checkTotpCode
242 a 256 makeToken
258 a 271 publicUser e handleUniqueError
```

Detalhe do `login`:

```text
13 busca usuario por email
14 a 22 inclui student e companyMember/company
25 bloqueia usuario inexistente ou inativo
29 compara senha
30 a 32 bloqueia senha errada
34 a 40 se for STUDENT, retorna token completo
42 gera token temporario sem MFA
44 a 51 se nao tem TOTP, retorna TOTP_SETUP
53 a 57 se ja tem TOTP, retorna TOTP_REQUIRED
```

Detalhe de `setupTotp`:

```text
60 busca usuario para TOTP
61 usa secret existente ou gera um novo
63 a 68 salva secret se ainda nao existia
70 a 74 monta URI do Authenticator
75 gera QR Code base64
77 retorna qrCode e otpauth
```

Detalhe de `confirmTotp`:

```text
81 busca usuario
82 valida codigo
84 a 87 marca totpEnabled true
89 a 93 retorna token final
```

Detalhe de `registerStudent`:

```text
113 valida se curso existe
114 criptografa senha
116 abre try
117 inicia transacao
118 a 125 cria User
126 a 135 cria Student
137 a 148 cria StudentCourse
150 retorna user, student e studentCourse
153 trata duplicidade
```

Detalhe de `registerCompany`:

```text
159 criptografa senha
162 inicia transacao
163 a 170 cria User COMPANY
172 a 174 cria Address
176 a 185 cria Company
187 a 197 cria CompanyMember ADMIN
199 retorna dados criados
202 trata duplicidade
```

---

## 6. Rotas publicas e de vagas

### 6.1 `api/src/modules/courses/courses.routes.ts`

```text
1 importa Router
2 importa prisma
3 importa response
4 importa seed demo
6 cria router
8 GET /
9 garante cursos/vagas iniciais se banco estiver vazio
11 a 14 busca cursos ativos ordenados
16 retorna { courses }
19 exporta router
```

### 6.2 `api/src/modules/companies/companies.routes.ts`

```text
1 a 4 imports
6 cria router
8 GET /
9 garante seed
11 a 20 busca empresas com endereco e vagas ativas
22 retorna { companies }
25 exporta router
```

### 6.3 `api/src/modules/jobs/jobs.routes.ts`

```text
1 a 7 imports
9 cria router
11 GET /jobs
13 garante seed
15 pega status da query
16 pega limit da query
18 a 29 busca vagas com empresa e curso
31 retorna { jobs }
37 GET /jobs/:jobId
39 pega id da rota
40 a 49 busca vaga
51 a 53 se nao achar, NotFound
55 retorna vaga
61 POST /jobs/:jobId/apply
64 a 66 busca aluno pelo userId do token
68 a 70 se nao encontrar aluno, erro
72 a 78 busca vaga ativa
80 a 82 se nao achar, erro
84 a 90 verifica candidatura duplicada
92 a 94 se ja candidatou, erro de conflito
96 a 102 cria candidatura
104 retorna candidatura enviada
110 exporta router
```

---

## 7. Area da empresa na API

### `api/src/modules/company/company.routes.ts`

Este arquivo e a rota realmente usada pelo PHP local para empresa.

Blocos:

```text
1 a 5 imports
7 cria router
9 protege tudo com authMiddleware + requireCompany
11 a 29 getCompanyByUser encontra a empresa pelo usuario logado
31 a 41 parseSalary transforma salario em numero ou null
44 a 50 GET /company/profile
53 a 74 GET /company/jobs
77 a 99 GET /company/jobs/:jobId
102 a 132 POST /company/jobs
135 a 167 PATCH /company/jobs/:jobId
170 a 208 GET /company/jobs/:jobId/applications
211 a 254 PATCH /company/jobs/:jobId/applications/:applicationId/status
257 exporta router
```

Detalhes importantes:

```text
Todas as rotas exigem empresa logada.
A empresa so acessa as vagas dela.
Ao listar candidaturas, a API inclui dados do aluno.
Ao atualizar status, valida se a candidatura pertence a vaga da empresa.
```

---

## 8. Area do aluno na API

### `api/src/modules/student/student.routes.ts`

Arquivo usado pelo perfil estilo LinkedIn.

Blocos:

```text
1 a 6 imports
8 cria router
10 protege tudo com authMiddleware + requireStudent
12 a 22 getStudentOrThrow busca aluno pelo userId
24 a 52 profileInclude define o que vem no perfil completo
54 a 65 getFullProfile busca perfil completo
67 a 73 GET /student/profile
76 a 103 PATCH /student/profile
106 a 140 PATCH /student/address
143 a 197 PATCH /student/course
200 a 228 POST /student/certificates
231 a 248 POST /student/profile/photo
251 a 268 POST /student/profile/cover
271 a 288 POST /student/resume
291 a 313 GET /student/applications
316 exporta router
```

Pontos importantes:

```text
Foto de perfil vai para uploads/profiles e salva profilePhotoPath.
Capa vai para uploads/covers e salva coverPhotoPath.
Curriculo vai para uploads/resumes e salva resumePath.
Curso finalizado exige finishedAt.
Perfil retorna curso, certificados, endereco e candidaturas.
```

---

## 9. PHP Web

### 9.1 `web/src/index.php`

Arquivo de entrada do site.

Linha por linha:

```text
1 abre PHP
3 inicia sessao para guardar login
4 inicia buffer de saida
7 define BASE como vazio
10 a 20 define mapa de rotas
23 a 34 define titulos das paginas
37 pega pagina da query string ou usa home
39 a 43 se for logout, destroi sessao e redireciona para login
46 a 48 se rota nao existe, usa 404
50 define titulo da pagina
53 inclui header
56 abre main
57 a 62 mostra 404 se necessario
63 a 65 inclui pagina real
68 inclui footer
```

### 9.2 `web/src/router.php`

```text
2 pega caminho da URL
3 remove barras
6 lista paginas aceitas por URL amigavel
8 se esta na lista, define $_GET['page']
10 carrega index.php
12 se nao reconhece, deixa servidor PHP servir arquivo normal
```

### 9.3 `web/src/api.php`

Arquivo mais importante da integracao PHP com Node.

Blocos:

```text
3 a 6 api_base_url retorna URL da API
8 a 15 api_decode_response decodifica JSON
18 a 70 api_request faz requisicao HTTP
73 a 76 api_get chama GET
79 a 82 api_post_json chama POST com JSON
85 a 88 api_patch_json chama PATCH com JSON
91 a 94 api_post_form chama POST multipart/form-data
97 a 109 api_has_file detecta arquivo
112 a 128 api_prepare_curl_files prepara CURLFile
131 a 155 api_build_multipart monta multipart manual
158 a 168 api_items extrai listas do retorno
171 a 178 demo_courses fallback de cursos
181 a 216 demo_companies fallback de empresas
219 a 292 demo_jobs fallback de vagas
295 a 347 demo_students fallback de alunos
```

Detalhe do `api_request`:

```text
Monta URL completa.
Se existir cURL, usa cURL.
Se nao existir cURL, usa file_get_contents.
Inclui Authorization: Bearer quando token e passado.
Envia JSON quando body nao tem arquivo.
Envia multipart quando body tem arquivo.
Retorna array PHP decodificado do JSON.
```

### 9.4 `web/src/layouts/header.php`

```text
1 a 12 abre HTML, head, CSS e fontes
13 abre body
16 verifica se usuario esta logado
17 pega role da sessao
18 decide se deve esconder navbar em login/cadastro
21 se nao for login/cadastro, mostra navbar
24 a 26 logo
28 a 32 botao mobile
35 a 45 links centrais
36 a 40 se empresa, mostra menu de empresa
42 a 44 se publico/aluno, mostra Inicio, Empresas, Vagas
48 a 52 se nao logado, mostra Entrar/Cadastrar
53 a 66 se logado, mostra perfil/minha empresa e sair
70 fecha condicao da navbar
```

### 9.5 `web/src/layouts/footer.php`

```text
1 define se deve esconder footer em login/cadastro
3 se nao deve esconder, mostra footer
10 a 14 logo
17 a 21 links do footer
24 a 26 copyright
34 a 35 carrega Bootstrap JS
37 a 38 fecha body/html
```

---

## 10. Classes PHP POO

### `web/src/classes/Vaga.php`

Representa uma vaga no PHP.

Blocos:

```text
3 declara classe Vaga
5 a 11 constantes de modalidade/status
14 a 29 atributos privados/publicos
31 a 48 construtor recebe array da API e preenche objeto
50 a 63 getters
67 a 74 setters
78 a 86 helpers de status
89 a 94 formata salario
97 a 105 traduz modalidade
108 a 122 transforma objeto em array para API
```

### `web/src/classes/Empresa.php`

```text
3 declara classe
5 a 8 constantes de status
10 a 18 atributos
20 a 32 construtor
34 a 42 getters
46 a 49 setters
53 a 60 verifica aprovada/bloqueada
64 a 70 formata CNPJ
72 a 82 retorna array
```

### `web/src/classes/Aluno.php`

```text
3 declara classe
4 a 14 atributos
16 a 30 construtor
32 a 42 getters
46 a 49 setters
56 a 70 toArray
73 a 76 formata CPF
```

### `web/src/classes/Candidatura.php`

```text
3 declara classe
5 a 9 constantes de status
11 a 19 atributos
21 a 33 construtor
35 a 43 getters
47 a 49 setters
53 a 56 helpers booleanos de status
61 a 71 getStatusLabel
74 a 84 getStatusBadgeClass
87 a 96 toArray
```

---

## 11. Paginas PHP

### 11.1 Login: `web/src/pages/auth/login.php`

Blocos:

```text
1 a 2 abre PHP e carrega api.php
4 a 9 variaveis de estado
11 a 14 limpar_totp limpa dados temporarios
16 a 20 redirecionar troca pagina
22 a 31 preparar_totp salva tempToken e busca QR Code
33 a 34 pega mensagem de sucesso
36 a 38 se clicar voltar, limpa TOTP
40 a 90 processa POST de login ou TOTP
92 em diante monta HTML visual do login
218 a 229 JavaScript para mostrar/esconder senha
231 a 236 JavaScript para focar codigo Authenticator
```

Fluxo do login:

```text
Aluno: email/senha -> /auth/login -> token -> sessao -> home.
Empresa: email/senha -> tempToken -> QR Code -> codigo -> token final -> dashboard.
```

### 11.2 Cadastro: `web/src/pages/auth/cadastro.php`

Blocos:

```text
1 a 2 carrega api.php
4 a 5 estado inicial
7 a 80 processa POST
13 valida confirmacao de senha
15 a 41 cadastro de aluno
43 a 75 cadastro de empresa
83 a 87 busca cursos na API ou fallback demo
89 em diante HTML do cadastro
303 a 308 JS troca aba aluno/empresa
310 a 320 JS mostra data de conclusao quando status e COMPLETED
```

### 11.3 Home: `web/src/pages/publico/home.php`

```text
2 a 4 carrega Vaga e api.php
7 inicializa lista
8 busca vagas em /jobs
9 a 11 usa demo se API falhar
13 a 15 transforma arrays em objetos Vaga
18 em diante renderiza banner, vagas em destaque e blocos do portal
```

### 11.4 Vagas: `web/src/pages/publico/vagas.php`

```text
2 a 3 carrega Vaga e api.php
5 a 6 mensagens
8 a 21 processa candidatura
9 a 12 exige login para candidatar
15 envia POST /jobs/:id/apply
24 a 28 le filtros da URL
30 busca vagas na API
31 a 33 fallback demo
35 a 60 filtra e cria objetos
63 em diante renderiza hero, filtros, cards e modal
275 JavaScript abre modal da vaga
```

### 11.5 Empresas: `web/src/pages/publico/empresas.php`

```text
2 a 4 carrega Empresa e api.php
7 inicializa array
8 busca /companies
9 a 11 fallback demo
13 a 15 cria objetos Empresa
18 em diante renderiza empresas parceiras
```

### 11.6 Perfil aluno: `web/src/pages/aluno/perfil.php`

Blocos:

```text
1 a 2 carrega api.php
4 a 7 bloqueia acesso sem login ou empresa
9 pega token
11 a 15 voltar_perfil redireciona
17 a 25 perfil_salvar_resposta salva mensagens na sessao
27 a 107 processa formularios POST
30 a 38 atualiza perfil
40 a 48 atualiza endereco
50 a 58 atualiza curso
60 a 67 adiciona certificado
70 a 79 envia foto de perfil
82 a 89 envia capa
92 a 99 envia curriculo
110 busca /student/profile
113 a 138 cria dados demo se API nao retornar
141 busca cursos
146 a 149 pega mensagens
150 a 165 prepara variaveis para renderizar
167 a 242 funcoes auxiliares de iniciais, URL, datas e status
244 em diante HTML do perfil estilo LinkedIn
714 JavaScript mostra data de conclusao quando curso finalizado
```

### 11.7 Menu da empresa: `web/src/pages/empresa/_empresa_menu.php`

```text
3 a 11 empresa_exigir_login exige token e role empresa
13 a 29 empresa_iniciais cria sigla do avatar
31 a 34 empresa_mapear reutiliza mapas de status
36 a 64 traduz status para label/classe
66 a 70 formata data curta
72 a 87 resume texto longo
90 a 107 renderiza menu da empresa
```

### 11.8 Dashboard empresa: `web/src/pages/empresa/dashboard.php`

```text
1 a 5 carrega classes, api e menu
7 exige login de empresa
9 busca perfil da empresa
12 busca vagas
15 a 17 normaliza retorno
19 a 24 inicializa contadores
26 a 42 percorre vagas e soma candidatos
44 a 45 prepara totais
48 em diante renderiza dashboard, cards, lista de vagas e painel lateral
```

### 11.9 Formulario de vaga: `web/src/pages/empresa/vaga-form.php`

```text
1 a 4 carrega dependencias
6 exige login
7 pega vaga_id se for edicao
11 a 16 busca vaga existente
18 define modo edicao
19 a 24 busca cursos
26 a 47 processa POST criando ou editando vaga
51 a 59 prepara valores do formulario
62 em diante renderiza formulario
```

### 11.10 Candidatos: `web/src/pages/empresa/candidatos.php`

```text
1 a 3 carrega dependencias
6 exige login
7 pega vaga_id
11 a 109 se nao tem vaga_id, mostra lista de vagas
111 a 124 processa mudanca de status de candidatura
128 busca dados da vaga
132 busca candidaturas
136 a 149 conta status
152 em diante renderiza candidatos da vaga
```

---

## 12. CSS

Arquivo:

```text
web/src/css/style.css
```

Esse arquivo tem mais de mil linhas. Ele controla todo o visual local.

Principais responsabilidades:

```text
variaveis de cor
tipografia
navbar
footer
home/banner
cards de vaga
login e cadastro
perfil estilo LinkedIn
area da empresa
responsividade mobile
```

Ponto importante:

```text
O visual local foi mantido.
A estrutura do GitHub foi sincronizada, mas o CSS local continua sendo o principal.
```

---

## 13. Java Desktop

O Java desktop e uma aplicacao Swing separada do PHP. Ele acessa banco diretamente via JDBC/DAO.

### 13.1 `Main.java`

```text
1 define pacote
3 importa LoginFrame
4 importa SwingUtilities
6 declara Main
7 main inicia aplicacao Swing
8 chama LoginFrame dentro da thread correta do Swing
```

### 13.2 `config/DatabaseConfig.java`

```text
1 pacote
3 a 6 imports
8 classe DatabaseConfig
10 carrega .env
11 a 13 le DB_URL, DB_USER, DB_PASSWORD
14 guarda pool Hikari
16 a 51 configura pool
53 getConnection devolve conexao
66 closePool fecha pool
```

### 13.3 DAOs

DAOs sao classes que falam com o banco.

```text
BaseDAO              fornece getConnection
UserDAO              salva e busca usuarios
StudentDAO           lista, cria, edita e alterna aptidao de alunos
CompanyDAO           lista empresas e muda status
CompanyMemberDAO     lista membros da empresa
CourseDAO            CRUD de cursos
JobDAO               lista vagas
ApplicationDAO       lista candidaturas
DashboardDAO         busca contadores do dashboard
NotificationDAO      placeholder para notificacoes
```

Exemplo `StudentDAO`:

```text
18 findAll lista alunos
37 findByTerm busca aluno por termo
61 existsByRa verifica RA duplicado
72 existsByCpf verifica CPF duplicado
83 saveWithUser cria User e Student em transacao
106 update atualiza aluno
121 toggleEligible alterna aptidao
130 insertStudent insere aluno
149 map transforma ResultSet em Student
```

### 13.4 Services

Services validam regras antes de chamar DAO.

```text
AuthService          login administrativo
StudentService       regras de aluno/importacao
CompanyService       regras de empresa
CourseService        regras de curso
JobService           regras de vagas
ApplicationService   candidaturas
ReportService        relatorios
```

Exemplo `StudentService`:

```text
16 listar chama dao.findAll
20 buscar escolhe findAll ou findByTerm
25 criar valida e salva aluno
37 editar valida e atualiza
46 toggleEligivel alterna aptidao
55 importar le CSV/TXT e salva alunos validos
88 validar confere nome, RA, CPF e duplicidade
```

### 13.5 GUI Swing

```text
LoginFrame                       tela de login admin
DashboardFrame                   janela principal
DashboardHomePanel               cards/resumo do painel
StudentListPanel                 lista alunos
StudentFormDialog                formulario aluno
StudentImportDialog              importacao de alunos
CompanyListPanel                 lista empresas
CompanyDetailDialog              aprovar/bloquear/analisar empresa
CoursePanel                      lista cursos
CourseFormDialog                 formulario curso
JobListPanel                     lista vagas
JobDetailDialog                  detalhe vaga
ApplicationListPanel             lista candidaturas
ApplicationDetailDialog          detalhe candidatura
ReportPanel                      gera relatorios TXT
```

### 13.6 Models

Models representam dados.

```text
User
Student
Address
Company
CompanyMember
Course
StudentCourse
Job
Application
Certificate
Notification
DashboardStats
```

Cada model normalmente tem:

```text
atributos privados
construtor vazio
construtor completo
getters
setters
```

---

## 14. Fluxos completos do sistema

### Login aluno

```text
web/src/pages/auth/login.php
POST /auth/login
api/src/modules/auth/auth.routes.ts
AuthService.login
Prisma busca User
bcrypt compara senha
API retorna token
PHP salva token em $_SESSION
PHP redireciona para home
```

### Login empresa com Authenticator

```text
Empresa digita email/senha
PHP chama /auth/login
API retorna tempToken
PHP chama /auth/totp/setup
API retorna QR Code
Empresa digita codigo
PHP chama /auth/totp/setup/confirm ou /auth/totp/verify
API retorna token final com mfaVerified true
PHP salva token e role empresa
Empresa acessa dashboard
```

### Cadastro aluno

```text
PHP busca cursos em /courses
Aluno preenche formulario
PHP envia multipart para /auth/register/student
API valida dados com zod
API criptografa senha
API cria User
API cria Student
API cria StudentCourse
API salva curriculo se enviado
PHP mostra sucesso e redireciona para login
```

### Cadastro empresa

```text
Empresa preenche formulario
PHP envia JSON para /auth/register/company
API cria User COMPANY
API cria Address
API cria Company
API cria CompanyMember ADMIN
Empresa fica PENDING
```

### Perfil aluno

```text
Aluno abre perfil
PHP chama /student/profile
API retorna Student + User + Address + Courses + Certificates + Applications
PHP monta perfil visual
Aluno altera dados
PHP envia PATCH/POST para API
API salva no banco
```

### Upload de foto

```text
PHP envia imagem para /student/profile/photo
Multer salva em api/uploads/profiles
API salva caminho em Student.profilePhotoPath
PHP monta URL usando api_base_url()
Imagem aparece no perfil
```

### Empresa cria vaga

```text
Empresa abre formulario
PHP busca cursos
Empresa envia formulario
PHP chama POST /company/jobs
API valida empresa logada
API cria Job ligado a companyId
Dashboard passa a listar a vaga
```

### Aluno se candidata

```text
Aluno clica candidatar
PHP exige login
PHP chama POST /jobs/:jobId/apply
API valida aluno
API verifica vaga ativa
API verifica duplicidade
API cria Application
```

### Empresa analisa candidato

```text
Empresa abre candidatos
PHP chama /company/jobs/:jobId/applications
API retorna candidaturas
Empresa altera status
PHP chama PATCH /company/jobs/:jobId/applications/:applicationId/status
API valida propriedade da vaga
API atualiza Application.status
```

---

## 15. Pontos importantes encontrados

### 15.1 Campos locais que nao estavam no GitHub original

No schema local existem:

```text
headline
summary
profilePhotoPath
coverPhotoPath
```

Eles sao necessarios para o perfil estilo LinkedIn.

### 15.2 Upload nao salva imagem no banco

O banco salva somente:

```text
uploads/profiles/arquivo.png
```

A imagem fisica fica em:

```text
api/uploads/profiles
```

### 15.3 Estrutura duplicada de paginas

Existem paginas antigas:

```text
web/src/pages/login.php
web/src/pages/cadastro.php
web/src/pages/home.php
```

E paginas novas:

```text
web/src/pages/auth/login.php
web/src/pages/auth/cadastro.php
web/src/pages/publico/home.php
```

O `index.php` esta apontando para a estrutura nova. As antigas ainda estao no projeto como sobra/compatibilidade.

### 15.4 Prisma generated

Como o schema foi alterado, em ambiente de desenvolvimento e recomendado rodar:

```bash
pnpm exec prisma generate
```

Isso atualiza o cliente gerado do Prisma para refletir campos novos/removidos.

---

## 16. Verificacoes feitas anteriormente

Foram executadas verificacoes importantes:

```bash
pnpm exec tsc --noEmit
pnpm exec prisma validate
php -l web/src/index.php
php -l web/src/pages/auth/login.php
php -l web/src/pages/auth/cadastro.php
php -l web/src/pages/aluno/perfil.php
php -l web/src/pages/empresa/dashboard.php
php -l web/src/pages/empresa/vaga-form.php
php -l web/src/pages/empresa/candidatos.php
php -l web/src/pages/publico/home.php
php -l web/src/pages/publico/vagas.php
php -l web/src/pages/publico/empresas.php
```

Resultado:

```text
TypeScript sem erro
Prisma schema valido
PHP sem erro de sintaxe nas paginas testadas
```

---

## 17. Resumo para apresentar ao grupo

O projeto e um portal de estagios com tres camadas. A API Node.js concentra as regras de negocio e conversa com o banco MySQL usando Prisma. O PHP e a interface web; ele renderiza telas e consome a API por HTTP, sem acessar o banco diretamente. O Java Swing e um sistema administrativo separado, usado para backoffice.

O aluno consegue se cadastrar, fazer login, ver vagas, candidatar-se e editar um perfil parecido com LinkedIn. A empresa consegue se cadastrar, fazer login com Authenticator, criar vagas e analisar candidatos. O banco guarda usuarios, alunos, empresas, cursos, vagas, candidaturas e notificacoes. Arquivos como foto, capa e curriculo ficam na pasta `uploads`, e o banco guarda apenas o caminho.

---

## 18. Melhorias recomendadas

1. Remover paginas antigas duplicadas depois que a estrutura nova estiver validada.
2. Criar migration para os campos `headline`, `summary`, `profilePhotoPath` e `coverPhotoPath`.
3. Rodar `pnpm exec prisma generate` apos alterar schema.
4. Decidir se as imagens ficarao em `uploads` local ou em storage externo.
5. Criar testes automatizados para rotas principais.
6. Padronizar comentarios com linguagem de aluno/faculdade se for exigido na entrega.
7. Verificar se o Java desktop e a API usam exatamente o mesmo schema apos as ultimas migrations.
8. Limpar arquivos de documentacao pessoal antes de commit final.
