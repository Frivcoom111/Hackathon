Contexto do Projeto:

Hackathon pedido:
PORTAL DE ESTÁGIOS UNIALFA
Conectando Talentos às Oportunidades Locais
Imagine um ambiente onde possamos criar uma ponte direta entre os alunos
da Faculdade UniALFA e as empresas da nossa região que buscam novos
talentos. O objetivo é que a proposta seja uma plataforma simples e objetiva, sem
processos burocráticos dispersos, na qual o “atrativo principal” — as oportunidades
de estágio — sejam visíveis de forma clara e acessível.
Esse é o desafio deste hackathon institucional: criar um Portal de Estágios
interativo para organizar, promover e gerenciar vagas de estágio e candidaturas.
Seu time será responsável por projetar uma solução onde:
● As empresas locais poderão se cadastrar e gerenciar suas vagas de estágio
com facilidade através de um painel exclusivo.
● Os alunos poderão visualizar as vagas disponíveis, realizar candidaturas e
acompanhar o status de seus processos.
● O sistema possuirá notificações claras sobre o andamento das candidaturas.
● Foco em usabilidade, performance e experiência do usuário são essenciais.
O sistema proposto adota uma arquitetura distribuída, onde cada disciplina do
semestre representará uma peça fundamental para o funcionamento do
ecossistema do projeto.
Visão Geral da Arquitetura
POO Java (Back Office Institucional): Esta é a camada corporativa da solução, voltada para a
equipe da própria Faculdade (UniALFA). Através de uma aplicação desktop, a instituição fará a
gestão administrativa do portal, como a aprovação de empresas cadastradas e o controle de
alunos aptos a estagiar.
Node.js (API RESTful): O motor do sistema. Uma API robusta que centraliza as regras de
negócio, fornecendo rotas para vagas, candidaturas e notificações, servindo como ponte de
comunicação entre o banco de dados e as interfaces web.
POO PHP (Front-end Web - Alunos e Empresas): A camada pública e de parceiros, onde
alunos e empresas acessam a plataforma. O front-end em PHP consome a API Node.JS para
renderizar o Portal do Aluno (vagas e candidaturas) e o Painel da Empresa (gestão de suas
próprias vagas).
Requisitos por Disciplina
POO (Java Orientado a Objetos)
Para a camada de Back Office Institucional, deve ser construída uma aplicação Java de uso
corporativo (que rode em uma infraestrutura local da faculdade).
● Tecnologias: A aplicação deve ser construída usando Maven e suas informações devem
ser persistidas no Banco de Dados MySQL. Para estabelecer a conexão, adicione a
dependência do mysql-connector utilizando o Maven.
● Interface (GUI): A aplicação contará com uma interface gráfica principal e janelas
auxiliares desenvolvidas com a biblioteca Java Swing. A interface deve priorizar clareza,
organização e facilidade de utilização pelos colaboradores da instituição.
● Modelagem de Domínio: É obrigatória a criação e utilização de classes orientadas a
objetos para representar as entidades centrais do sistema, no mínimo: (Aluno, Empresa,
Vaga e Candidatura). Outras entidades complementares poderão ser criadas conforme a
necessidade da solução proposta.
● Funcionalidades: A equipe institucional precisa gerenciar o ecossistema. Cabe aos
alunos desenvolver rotinas como: aprovar/bloquear o cadastro de Empresas, gerenciar
alunos (ex: importar matriculados) ou gerar relatórios de candidaturas.
● Funcionalidades : A aplicação deverá disponibilizar funcionalidades administrativas para
gerenciamento do ecossistema do portal, incluindo:
○ Gestão de Empresas:
■ Aprovar empresas cadastradas.
■ Bloquear ou desativar empresas.
■ Consultar informações cadastrais.
○ Gestão de Alunos:
■ Cadastrar, editar e consultar alunos.
■ Importar alunos matriculados através de arquivos texto (.txt).
■ Controlar quais alunos estão aptos a participar dos processos de estágio..
○ Gestão de Vagas e Candidaturas:
■ Consultar vagas cadastradas.
■ Consultar candidaturas realizadas pelos alunos.
■ Visualizar status das candidaturas.
■ Gerar relatórios gerenciais relacionados às vagas e candidaturas..
○ Relatórios (.txt):
■ Empresas cadastradas.
■ Alunos cadastrados.
■ Vagas disponíveis.
■ Candidaturas realizadas e seus respectivos status..
● Arquitetura e Boas Práticas: O projeto deverá seguir uma arquitetura organizada e
baseada nos princípios da Programação Orientada a Objetos, contendo uma estrutura de
pacotes semelhante a (gui, model, service, dao, util). É obrigatório o uso adequado dos
conceitos de: Encapsulamento, Herança, Polimorfismo, Abstração, Separação de
responsabilidades. O código deverá ser organizado, reutilizável, legível e de fácil
manutenção.
● Requisitos Desejáveis (Diferenciais):
○ Sistema de autenticação para acesso ao Back Office Institucional.
○ Controle de perfis de acesso (administrador, coordenador, operador, etc.).
○ Geração de relatórios em formatos adicionais (PDF, CSV).
○ Importação e exportação de dados em formatos complementares.
○ Aplicação de padrões de projeto (Design Patterns) quando apropriado.
POO (PHP Orientado a Objetos)
Para a camada de interação, deve ser construída uma aplicação web em PHP que consuma a
API Node.JS. O rigor na modelagem orientada a objetos é essencial.
● Modelagem de Domínio: É obrigatória a criação e utilização de classes bem definidas
para representar as entidades centrais do negócio, no mínimo: Aluno, Empresa, Vaga e
Candidatura.
● Painel da Empresa: Desenvolver uma área restrita (Back Office) onde a empresa possa
realizar o CRUD (Criar, Ler, Atualizar, Excluir) de suas vagas e visualizar a lista de alunos
candidatos a cada uma delas.
● Portal do Aluno: Desenvolver a interface onde o aluno visualiza a listagem de vagas
disponíveis (consumidas da API) e um formulário para submeter sua candidatura.
● Integração: A aplicação PHP não deve acessar o banco de dados diretamente; todas as
operações de leitura e escrita devem ser feitas através de requisições HTTP à API
Node.JS.
● Boas Práticas: É fundamental o uso dos conceitos de POO (encapsulamento, herança,
polimorfismo, separação de responsabilidades). O código deve ser organizado e limpo. A
não aplicação destes fundamentos possui caráter eliminatório.
Node.js
O coração do ecossistema. Vocês deverão desenvolver a API responsável por centralizar toda a
lógica de negócio e fornecer os dados para os demais módulos da plataforma.
● Rotas e Endpoints: Desenvolver uma API RESTful completa utilizando Node.js,
responsável pelo gerenciamento de Vagas, Candidaturas, e um sistema de Notificações
(por exemplo, informar quando uma candidatura tiver seu status alterado).
● CRUD e Persistência: Implementar todas as operações de criação, consulta, atualização
e remoção de dados (CRUD), realizando a persistência das informações em banco de
dados relacional.
● Arquitetura Modular: A aplicação deverá seguir uma arquitetura organizada e escalável,
separando as responsabilidades em camadas distintas como, (Controllers): responsáveis
por receber e responder às requisições HTTP, (Services): responsáveis pelas regras de
negócio, (Repositories/Modules): responsáveis pelo acesso e manipulação dos dados.
● Integração com o Front-end PHP: Toda comunicação entre os sistemas web
desenvolvidos em PHP e o banco de dados deverá ocorrer exclusivamente por meio da
API Node.js. Os módulos PHP não poderão realizar consultas diretas ao banco de dados,
devendo consumir os endpoints disponibilizados pela API através de requisições HTTP.
A estrutura do banco de dados deverá ser controlada por meio de migrations, garantindo
o versionamento das alterações realizadas. Também deverão ser criadas seeds para a
carga inicial e parametrização dos dados necessários ao funcionamento da aplicação.
● Padronização das Respostas: A API deverá retornar respostas em formato JSON,
utilizando códigos HTTP apropriados para indicar sucesso, falhas de validação, recursos
não encontrados e erros internos do servidor.
● Segurança e Validação: As validações de entrada de dados deverão ser implementadas
utilizando Zod, juntamente com mecanismos de tratamento de erros e validações de
negócio que garantam a integridade e consistência das informações processadas pelos
endpoints.
DevOps
Durante o Hackathon, as equipes deverão adotar práticas modernas de desenvolvimento e
operações (DevOps), demonstrando organização, colaboração e boas práticas de mercado.
Controle de Versão
Todo o desenvolvimento deverá ser realizado utilizando o Git e hospedado no GitHub. O
repositório deverá apresentar:
● Histórico consistente de commits, evidenciando a evolução do projeto;
● Utilização adequada de branches para desenvolvimento de funcionalidades;
● Uso de Pull Requests para integração de código e revisão entre membros da equipe;
● Organização e clareza nas mensagens de commit.
Documentação
O projeto deverá conter um arquivo README.md completo e atualizado, contemplando:
● Descrição do problema proposto e da solução desenvolvida;
● Objetivos do projeto;
● Tecnologias e ferramentas utilizadas;
● Instruções para instalação e execução local;
● Estrutura do projeto;
● Integrantes da equipe e suas respectivas contribuições;
● Evidências de testes realizados e funcionalidades implementadas.
Colaboração e Organização
As equipes deverão demonstrar trabalho colaborativo por meio do uso adequado das
ferramentas de versionamento, registro das atividades desenvolvidas e divisão equilibrada
das responsabilidades entre os participantes.
Boas Práticas de Desenvolvimento
Serão considerados na avaliação:
● Organização do código-fonte;
● Padronização e legibilidade do código;
● Modularização da aplicação;
● Tratamento adequado de erros;
User Experience (UX) Design
A primeira impressão e a facilidade de uso determinam o sucesso da plataforma.
● Guia de estilo e Identidade Visual: Escolher tipografias e paletas de cores que garantam
boa legibilidade e acessibilidade (contraste entre texto e fundo), crie um guia de estilo com
a documentação de cores e tipografias escolhidas, defina variáveis para usar em seus
protótipos.
● Protótipos de alta e baixa fidelidade: Desenvolver um protótipo com um fluxo/jornada
de alta fidelidade focando especificamente no fluxo de candidatura (desde a tela inicial
até a confirmação da candidatura do aluno) e desenvolver protótipos de baixa fidelidade
de todas as telas web.
● Experiência: O design deve ser atrativo, intuitivo e minimizar o atrito (menos cliques para
chegar ao objetivo), proporcionando uma experiência visual agradável e funcional tanto
para o estudante buscando o primeiro emprego quanto para o RH da empresa.

Banco já definido:
📊 Análise do Banco de Dados — Portal de Estágios UniALFA

📋 Índice

Visão Geral

Tabelas e Estrutura

Relacionamentos

Fluxos Principais

Integridade Referencial

Considerações de Design

Visão Geral

O banco segue um padrão relacional normalizado com 11 tabelas, focando em:

✅ Usuários (User) com controle de papel (ADMIN, COMPANY, STUDENT)

✅ Estudantes com perfil expandido (Student)

✅ Empresas com equipe de recrutadores (Company, CompanyMember)

✅ Ofertas de trabalho (Job)

✅ Candidaturas de estudantes (Application)

✅ Endereços reutilizáveis (Address)

✅ Certificados e histórico acadêmico

Tabelas e Estrutura

1️⃣ Address (Endereços)

Propósito: Reutilizável para Students, Companies e CompanyMembers

┌─ Address ────────────────────┐
├─ id (PK, UUID)              │
├─ street                      │
├─ number                      │
├─ complement (nullable)       │
├─ district                    │
├─ city                        │
├─ state (2 chars)            │
├─ zipCode                     │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Usada por 3 entidades (normalizando dados de endereço)

Endereços podem ser opcionais (nullable em alguns casos)

Auditoria temporal com createdAt e updatedAt

2️⃣ User (Usuários Base)

Propósito: Autenticação e autorização central

┌─ User ───────────────────────┐
├─ id (PK, UUID)              │
├─ email (UNIQUE)             │
├─ password (hashed)          │
├─ role (ENUM)                │
│  └─ ADMIN, COMPANY, STUDENT │
├─ isActive (boolean)         │
├─ totpSecret / totpEnabled   │ ← 2FA
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Base de autenticação para todo sistema

Roles definem tipo de usuário

Suporta 2FA via TOTP

Email único (garante identificação)

3️⃣ Course (Cursos)

Propósito: Cursos oferecidos pela universidade

┌─ Course ─────────────────────┐
├─ id (PK, UUID)              │
├─ name (UNIQUE)              │
├─ code (UNIQUE, nullable)    │
├─ periods (INT)              │ ← Semestres
├─ isActive (boolean)         │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Identifica cada curso da universidade

periods = quantos semestres tem o curso

Pode estar inativo mas manter histórico

4️⃣ Student (Estudantes)

Propósito: Perfil expandido do estudante

┌─ Student ────────────────────┐
├─ id (PK, UUID)              │
├─ userId (FK → User) [UNIQUE]│
├─ addressId (FK → Address)   │
├─ name                       │
├─ ra (RA único)              │
├─ cpf (UNIQUE)               │
├─ phone                      │
├─ period (INT)               │ ← Semestre atual
├─ isEligible (boolean)       │ ← Pode estagiar?
├─ resumePath (file path)     │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Extends User (1:1 relationship)

isEligible: controla se pode fazer estágio

resumePath: onde está armazenado o currículo

Mantém RA (Registro Acadêmico)

5️⃣ Certificate (Certificados)

Propósito: Certificações extras do estudante

┌─ Certificate ────────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ name                       │
├─ institution                │
├─ issuedAt (data)            │
├─ filePath                   │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Many-to-One com Student (um aluno, muitos certificados)

Cascata: deleta certificados ao deletar estudante

Armazena arquivo do certificado

6️⃣ StudentCourse (Inscrição em Cursos)

Propósito: Histórico acadêmico — qual estudante fez qual curso

┌─ StudentCourse ──────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ courseId (FK → Course)     │
├─ status (ENUM)              │
│  └─ ACTIVE, COMPLETED,      │
│     CANCELLED               │
├─ startedAt (data)           │
├─ finishedAt (nullable)      │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (studentId,courseId)│
└──────────────────────────────┘

Características:

Junção Many-to-Many entre Student e Course

Restrição: um aluno não pode estar 2x no mesmo curso

Aluno deletado → registros cascateiam

Curso deletado → bloqueia (RESTRICT) se tem alunos ativos

7️⃣ Company (Empresas)

Propósito: Empresa que oferece estágios

┌─ Company ────────────────────┐
├─ id (PK, UUID)              │
├─ addressId (FK → Address)   │
├─ name                       │
├─ cnpj (UNIQUE)              │
├─ description (LONGTEXT)     │
├─ phone                      │
├─ status (ENUM)              │
│  └─ PENDING, ANALYSING,     │
│     APPROVED, BLOCKED       │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Status controla se pode publicar vagas

CNPJ único (empresa não pode repetir)

Opcional ter endereço (SET NULL se deletado)

Descrição em LONGTEXT para apresentação

8️⃣ CompanyMember (Membros da Empresa)

Propósito: Recrutadores/admins que atuam em nome da company

┌─ CompanyMember ──────────────┐
├─ id (PK, UUID)              │
├─ companyId (FK → Company)   │
├─ userId (FK → User)[UNIQUE] │
├─ addressId (FK → Address)   │
├─ role (ENUM)                │
│  └─ ADMIN, RECRUITER        │
├─ name                       │
├─ cpf (UNIQUE)               │
├─ phone                      │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (companyId, userId) │
└──────────────────────────────┘

Características:

Extends User (1:1 por user)

Um user não pode estar em 2 companies (UNIQUE)

Company deletada → todos membros deletados (CASCADE)

Diferencia ADMIN (gerencia) de RECRUITER (publica vagas)

9️⃣ Job (Vagas de Estágio)

Propósito: Oportunidade de trabalho publicada por company

┌─ Job ────────────────────────┐
├─ id (PK, UUID)              │
├─ companyId (FK → Company)   │
├─ courseId (FK → Course)     │
├─ title                      │
├─ description (LONGTEXT)     │
├─ area (VARCHAR)             │
├─ requirements (LONGTEXT)    │
├─ salary (DOUBLE, nullable)  │
├─ location                   │
├─ modality (ENUM)            │
│  └─ PRESENCIAL, REMOTE,     │
│     HYBRID                  │
├─ status (ENUM)              │
│  └─ ACTIVE, PAUSED, CLOSED  │
├─ deletedAt (soft delete)    │
├─ createdAt / updatedAt      │
└──────────────────────────────┘

Características:

Vinculada a uma Company (obrigatório)

Opcionalmente vinculada a um Course (para direcionar)

deletedAt: soft delete (não remove do BD)

Status controla visibilidade: ACTIVE (visível), PAUSED (oculta), CLOSED (finalizada)

🔟 Application (Candidaturas)

Propósito: Quando um estudante se candidata a uma vaga

┌─ Application ────────────────┐
├─ id (PK, UUID)              │
├─ studentId (FK → Student)   │
├─ jobId (FK → Job)           │
├─ status (ENUM)              │
│  └─ PENDING, ANALYSING,     │
│     APPROVED, REJECTED,     │
│     CANCELLED               │
├─ resumePath (nullable)      │
├─ coverLetter (LONGTEXT)     │
├─ deletedAt (soft delete)    │
├─ createdAt / updatedAt      │
│                             │
├─ UNIQUE (studentId, jobId)  │
└──────────────────────────────┘

Características:

Estudante não pode se candidatar 2x à mesma vaga

Armazena currículo específico da candidatura (pode diferir do resumePath do Student)

Status acompanha o andamento: PENDING → ANALYSING → APPROVED/REJECTED

Soft delete preserva histórico

1️⃣1️⃣ Notification (Notificações)

Propósito: Sistema de notificações para usuários

┌─ Notification ───────────────┐
├─ id (PK, UUID)              │
├─ userId (FK → User)         │
├─ title                      │
├─ message (LONGTEXT)         │
├─ type (VARCHAR)             │ ← Tipo de notif
├─ isRead (boolean)           │
├─ createdAt (sem updatedAt)  │
└──────────────────────────────┘

Características:

Simples e direta

Sem updatedAt (notificação é imutável)

type classifica: 'job-published', 'application-status', etc.

Cascata ao deletar usuário

Relacionamentos

📍 Mapa de ForeignKeys

                          ┌─────────────────┐
                          │       User      │
                          │   (base auth)   │
                          └────────┬────────┘
                                   │
                  ┌────────────┬────┴────┬──────────────┐
                  │            │         │              │
                  ▼            ▼         ▼              ▼
            ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────┐
            │ Student │  │ Company  │  │Notif...  │  │CompanyMember│
            │ (1:1)   │  │ Member   │  │(1:many)  │  │  (1:1 ext)  │
            └────┬────┘  │ (1:1)    │  └──────────┘  └──────┬──────┘
                 │       └────┬─────┘                       │
                 │            │                             │
          ┌──────▼──────┐     ▼                      ┌──────▼──────┐
          │   Address   │  Company                   │   Address   │
          │(reutiliz.)  │ (1:many)                   │(reutiliz.)  │
          └─────────────┘     │                      └─────────────┘
                              │
                         ┌────▼────┐
                         │   Job   │
                         │(1:many) │
                         └────┬────┘
                              │
                         ┌────▼─────────┐
                         │ Application   │
                         │(N:M indirect) │
                         └───────────────┘

🔗 Relações Detalhadas

De Para Tipo Ação Delete Descrição Student User 1:1 CASCADE Estudante extends User Student Address 1:N SET NULL Endereço pode ser deletado StudentCourse Student N:1 CASCADE Deleta histórico ao deletar aluno StudentCourse Course N:1 RESTRICT Impede deletar curso com alunos Company Address 1:N SET NULL Endereço pode ser deletado CompanyMember Company N:1 CASCADE Deleta membro ao deletar company CompanyMember User 1:1 CASCADE Deleta membro ao deletar user CompanyMember Address 1:N SET NULL Endereço pode ser deletado Job Company N:1 CASCADE Deleta vagas ao deletar company Job Course N:1 SET NULL Vaga pode perder vínculo com course Application Student N:1 CASCADE Deleta candidaturas ao deletar aluno Application Job N:1 CASCADE Deleta candidaturas ao deletar vaga Certificate Student N:1 CASCADE Deleta certificados ao deletar aluno Notification User N:1 CASCADE Deleta notificações ao deletar user

Fluxos Principais

🎓 Fluxo 1: Estudante se Candidata a Vaga

1. User (role=STUDENT) cria conta
   ↓
2. Student preenche perfil (RA, CPF, período, resumePath)
   ↓
3. Student visualiza Jobs (status = ACTIVE)
   ↓
4. Student cria Application (studentId, jobId)
   ↓
5. Application vai para status = PENDING
   ↓
6. CompanyMember (recruiter) analisa → APPROVED/REJECTED
   ↓
7. Notification enviada ao Student

🏢 Fluxo 2: Empresa Publica Vaga

1. User (role=COMPANY) cria conta
   ↓
2. Company cria perfil (CNPJ, status=PENDING)
   ↓
3. Admin aprova Company (status=APPROVED)
   ↓
4. CompanyMember (admin) cria vagas (Job, status=ACTIVE)
   ↓
5. Estudantes veem a vaga
   ↓
6. Recruiter analisa candidaturas (Application)

📚 Fluxo 3: Histórico Acadêmico

1. Student em um Course específico
   ↓
2. Cria StudentCourse (status=ACTIVE)
   ↓
3. Ao fim: StudentCourse (status=COMPLETED)
   ↓
4. Student adiciona Certificate (comprovante)
   ↓
5. Resumo: cursos + certificados = Perfil completo

Integridade Referencial

✅ Constraints Importantes

CASCATA (CASCADE)

Estudante deletado → Applications, Certificates, StudentCourses deletados

Company deletada → Jobs, CompanyMembers deletados

User deletado → Student, CompanyMember, Notifications deletados

Job deletado → Applications deletadas

BLOQUEIA (RESTRICT)

Curso com alunos registrados → não pode deletar

Protege integridade do histórico acadêmico

SET NULL

Address deletado → referências viram NULL

Permite manter user sem endereço

⚠️ Pontos de Atenção

Soft Deletes em Job e Application

deletedAt permite recuperação de dados

Queries precisam filtrar WHERE deletedAt IS NULL

UNIQUEs Críticos

User.email → impossível 2 usuários com mesmo email

Student.cpf, Student.ra → identidade do aluno

Company.cnpj → identidade da empresa

Application (studentId, jobId) → impede candidatura duplicada

ENUM Roles e Status

Validação acontece no BD, não só na app

Impede estados inválidos

Considerações de Design

💡 O que Está Bem Feito

✅ Normalização adequada

Address reutilizável (3NF respeitada)

StudentCourse quebra Many-to-Many corretamente

Dados duplicados minimizados

✅ Auditoria temporal

createdAt/updatedAt em quase todas tabelas

Rastreabilidade completa

✅ Soft Deletes

Job e Application usam deletedAt

Histórico preservado

✅ Roles de Usuário

ADMIN, COMPANY, STUDENT bem separados

Extensões específicas (Student, CompanyMember)

✅ Flexibilidade

Campos nullable bem pensados (complement, description, salary)

2FA optativo mas suportado (totpSecret, totpEnabled)

🤔 Possíveis Melhorias Futuras

Auditoria de Quem Alterou Quê

Adicionar updatedBy em tabelas críticas

Histórico de Status

Tabela separada: ApplicationStatusHistory

Rastreia quem/quando muda status

Ratings/Feedback

Tabela: ApplicationFeedback ou CompanyReview

Estudante avalia empresa, empresa avalia aluno

Skill Tags

Tabela: JobSkill, StudentSkill

Relaciona skills específicas (Python, React, etc)

Interview Schedule

Tabela: Interview

Rastreia entrevistas (data, resultado)

🎯 Resumo Executivo

Aspecto Status Tabelas 11 tabelas bem estruturadas Normalização 3NF (Terceira Forma Normal) Relacionamentos 14 Foreign Keys com integridade Cascade/Restrict Bem balanceado Auditoria createdAt/updatedAt presente Soft Deletes Job, Application Roles ADMIN, COMPANY, STUDENT Fluxo Principal Student → Application → Job ← Company

Conclusão: Schema robusto e pronto para produção! 🚀

-- ─────────────────────────────────────────────────────────────────────────────
-- Portal de Estágios UniALFA — Schema completo
-- MySQL 8.0
-- ─────────────────────────────────────────────────────────────────────────────

SET FOREIGN_KEY_CHECKS = 0;

-- ─── Address ──────────────────────────────────────────────────────────────────

CREATE TABLE `Address` (
`id`         VARCHAR(36)  NOT NULL,
`street`     VARCHAR(191) NOT NULL,
`number`     VARCHAR(20)  NOT NULL,
`complement` VARCHAR(191) NULL,
`district`   VARCHAR(191) NOT NULL,
`city`       VARCHAR(191) NOT NULL,
`state`      VARCHAR(2)   NOT NULL,
`zipCode`    VARCHAR(10)  NOT NULL,
`createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── User ─────────────────────────────────────────────────────────────────────

CREATE TABLE `User` (
`id`          VARCHAR(36)  NOT NULL,
`email`       VARCHAR(191) NOT NULL,
`password`    VARCHAR(191) NOT NULL,
`role`        ENUM('ADMIN', 'COMPANY', 'STUDENT') NOT NULL DEFAULT 'STUDENT',
`isActive`    TINYINT(1)   NOT NULL DEFAULT 1,
`totpSecret`  VARCHAR(191) NULL,
`totpEnabled` TINYINT(1)   NOT NULL DEFAULT 0,
`createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `User_email_key` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Course ───────────────────────────────────────────────────────────────────

CREATE TABLE `Course` (
`id`        VARCHAR(36)  NOT NULL,
`name`      VARCHAR(191) NOT NULL,
`code`      VARCHAR(50)  NULL,
`periods`   INT          NOT NULL,
`isActive`  TINYINT(1)   NOT NULL DEFAULT 1,
`createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `Course_name_key` (`name`),
UNIQUE KEY `Course_code_key` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Student ──────────────────────────────────────────────────────────────────

CREATE TABLE `Student` (
`id`         VARCHAR(36)  NOT NULL,
`userId`     VARCHAR(36)  NOT NULL,
`addressId`  VARCHAR(36)  NULL,
`name`       VARCHAR(191) NOT NULL,
`ra`         VARCHAR(191) NOT NULL,
`cpf`        VARCHAR(14)  NOT NULL,
`phone`      VARCHAR(20)  NULL,
`isEligible` TINYINT(1)   NOT NULL DEFAULT 1,
`resumePath` VARCHAR(191) NULL,
`createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `Student_userId_key`    (`userId`),
UNIQUE KEY `Student_ra_key`        (`ra`),
UNIQUE KEY `Student_cpf_key`       (`cpf`),
UNIQUE KEY `Student_addressId_key` (`addressId`),
CONSTRAINT `Student_userId_fkey`
FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
CONSTRAINT `Student_addressId_fkey`
FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Certificate ──────────────────────────────────────────────────────────────

CREATE TABLE `Certificate` (
`id`          VARCHAR(36)  NOT NULL,
`studentId`   VARCHAR(36)  NOT NULL,
`name`        VARCHAR(191) NOT NULL,
`institution` VARCHAR(191) NULL,
`issuedAt`    DATETIME(3)  NOT NULL,
`filePath`    VARCHAR(191) NULL,
`createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
KEY `Certificate_studentId_idx` (`studentId`),
CONSTRAINT `Certificate_studentId_fkey`
FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── StudentCourse ────────────────────────────────────────────────────────────

CREATE TABLE `StudentCourse` (
`id`         VARCHAR(36)  NOT NULL,
`studentId`  VARCHAR(36)  NOT NULL,
`courseId`   VARCHAR(36)  NOT NULL,
`status`     ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
`startedAt`  DATETIME(3)  NOT NULL,
`finishedAt` DATETIME(3)  NULL,
`createdAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`  DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `StudentCourse_studentId_courseId_key` (`studentId`, `courseId`),
KEY `StudentCourse_courseId_idx` (`courseId`),
CONSTRAINT `StudentCourse_studentId_fkey`
FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
CONSTRAINT `StudentCourse_courseId_fkey`
FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Company ──────────────────────────────────────────────────────────────────

CREATE TABLE `Company` (
`id`          VARCHAR(36)  NOT NULL,
`addressId`   VARCHAR(36)  NULL,
`name`        VARCHAR(191) NOT NULL,
`cnpj`        VARCHAR(18)  NOT NULL,
`description` LONGTEXT     NULL,
`phone`       VARCHAR(20)  NULL,
`status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'BLOCKED') NOT NULL DEFAULT 'PENDING',
`createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `Company_cnpj_key`      (`cnpj`),
UNIQUE KEY `Company_addressId_key` (`addressId`),
CONSTRAINT `Company_addressId_fkey`
FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── CompanyMember ────────────────────────────────────────────────────────────

CREATE TABLE `CompanyMember` (
`id`        VARCHAR(36)  NOT NULL,
`companyId` VARCHAR(36)  NOT NULL,
`userId`    VARCHAR(36)  NOT NULL,
`addressId` VARCHAR(36)  NULL,
`role`      ENUM('ADMIN', 'RECRUITER') NOT NULL DEFAULT 'RECRUITER',
`name`      VARCHAR(191) NOT NULL,
`cpf`       VARCHAR(14)  NOT NULL,
`phone`     VARCHAR(20)  NULL,
`createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `CompanyMember_userId_key`           (`userId`),
UNIQUE KEY `CompanyMember_cpf_key`              (`cpf`),
UNIQUE KEY `CompanyMember_addressId_key`        (`addressId`),
UNIQUE KEY `CompanyMember_companyId_userId_key` (`companyId`, `userId`),
KEY `CompanyMember_companyId_idx` (`companyId`),
CONSTRAINT `CompanyMember_companyId_fkey`
FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
CONSTRAINT `CompanyMember_userId_fkey`
FOREIGN KEY (`userId`)    REFERENCES `User`(`id`)    ON DELETE CASCADE  ON UPDATE CASCADE,
CONSTRAINT `CompanyMember_addressId_fkey`
FOREIGN KEY (`addressId`) REFERENCES `Address`(`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Job ──────────────────────────────────────────────────────────────────────

CREATE TABLE `Job` (
`id`           VARCHAR(36)  NOT NULL,
`companyId`    VARCHAR(36)  NOT NULL,
`courseId`     VARCHAR(36)  NULL,
`title`        VARCHAR(191) NOT NULL,
`description`  LONGTEXT     NOT NULL,
`area`         VARCHAR(191) NOT NULL,
`requirements` LONGTEXT     NULL,
`salary`       DOUBLE       NULL,
`location`     VARCHAR(191) NOT NULL,
`modality`     ENUM('PRESENCIAL', 'REMOTE', 'HYBRID') NOT NULL DEFAULT 'PRESENCIAL',
`status`       ENUM('ACTIVE', 'PAUSED', 'CLOSED')     NOT NULL DEFAULT 'ACTIVE',
`deletedAt`    DATETIME(3)  NULL,
`createdAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`    DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
KEY `Job_companyId_idx` (`companyId`),
KEY `Job_courseId_idx`  (`courseId`),
CONSTRAINT `Job_companyId_fkey`
FOREIGN KEY (`companyId`) REFERENCES `Company`(`id`) ON DELETE CASCADE  ON UPDATE CASCADE,
CONSTRAINT `Job_courseId_fkey`
FOREIGN KEY (`courseId`)  REFERENCES `Course`(`id`)  ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Application ──────────────────────────────────────────────────────────────

CREATE TABLE `Application` (
`id`          VARCHAR(36)  NOT NULL,
`studentId`   VARCHAR(36)  NOT NULL,
`jobId`       VARCHAR(36)  NOT NULL,
`status`      ENUM('PENDING', 'ANALYSING', 'APPROVED', 'REJECTED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
`resumePath`  VARCHAR(191) NULL,
`coverLetter` LONGTEXT     NULL,
`deletedAt`   DATETIME(3)  NULL,
`createdAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
`updatedAt`   DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
UNIQUE KEY `Application_studentId_jobId_key` (`studentId`, `jobId`),
KEY `Application_jobId_idx` (`jobId`),
CONSTRAINT `Application_studentId_fkey`
FOREIGN KEY (`studentId`) REFERENCES `Student`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `Application_jobId_fkey`
FOREIGN KEY (`jobId`)     REFERENCES `Job`(`id`)     ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─── Notification ─────────────────────────────────────────────────────────────

CREATE TABLE `Notification` (
`id`        VARCHAR(36)  NOT NULL,
`userId`    VARCHAR(36)  NOT NULL,
`title`     VARCHAR(191) NOT NULL,
`message`   LONGTEXT     NOT NULL,
`type`      VARCHAR(191) NOT NULL,
`isRead`    TINYINT(1)   NOT NULL DEFAULT 0,
`createdAt` DATETIME(3)  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

PRIMARY KEY (`id`),
KEY `Notification_userId_idx` (`userId`),
CONSTRAINT `Notification_userId_fkey`
FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────────────────────

SET FOREIGN_KEY_CHECKS = 1;


Pensando no projeto, qual estutura de projetos e metas podemos definir e pensar, me ajude a estruturar o pensamento no estutura.md
