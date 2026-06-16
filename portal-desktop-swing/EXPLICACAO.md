# 📘 EXPLICAÇÃO COMPLETA DO PROJETO — Portal de Estágios UniALFA (Back Office Java Swing)

> **Leitura de ônibus 🚌.** Linguagem simples, direto ao ponto, com analogias do dia a dia.
> Aqui você entende **o que é**, **como funciona por dentro** e **por que foi feito assim** — sem precisar abrir o código.
> Pode ler do começo ao fim, ou pular para a seção que te interessa pelo índice abaixo.

---

## 🗂️ Índice

1. O que é esse projeto, em uma frase
2. O quadro grande: onde esse projeto se encaixa
3. As tecnologias (e por que cada uma existe)
4. Como rodar o projeto (passo a passo)
5. A arquitetura em camadas (a parte mais importante)
6. Detalhando cada camada com exemplos do código real
7. Os Padrões de Projeto (Design Patterns)
8. O banco de dados (as 11 tabelas)
9. Passo a passo de uma ação real: o LOGIN
10. Passo a passo: CADASTRAR um aluno (com transação)
11. Como uma tela de lista funciona por dentro (StudentListPanel)
12. As regras de negócio mais espertas (empresas, status em cascata)
13. O Dashboard e seus contadores em tempo real
14. A importação de alunos em lote
15. As validações de CPF e CNPJ
16. As cores e o visual (identidade azul)
17. Segurança: como as senhas são guardadas
18. Pontos honestos: o que está pronto e o que é "esqueleto"
19. Como o projeto está organizado nas pastas
20. Glossário rápido
21. Perguntas e respostas para defender o projeto
22. Resumo final

---

## 1. O que é esse projeto, em uma frase

É um **programa de computador (desktop)** que a **equipe da faculdade UniALFA** usa para administrar um Portal de Estágios: aprovar empresas, cadastrar alunos, ver vagas, acompanhar candidaturas e gerar relatórios.

Pense nele como o **"painel do gerente"** de um sistema de estágios. O aluno e a empresa **não** usam esse programa — quem usa é o **funcionário interno da faculdade** (o administrador).

Três jeitos fáceis de visualizar:
- É como o **painel de administração** de uma loja virtual — o cliente vê a loja, mas o dono usa um painel separado para gerenciar tudo.
- É como o **sistema do caixa do banco**: o cliente usa o app no celular, mas o funcionário tem um programa interno bem diferente.
- É um programa que abre **uma janela no Windows**, igual ao Bloco de Notas ou à calculadora — não roda no navegador.

---

## 2. O quadro grande: onde esse projeto se encaixa

O Portal de Estágios completo é formado por **três partes** que conversam com **o mesmo banco de dados**:

| Parte | Tecnologia | Quem usa | Papel |
|---|---|---|---|
| **API** | Node.js | Ninguém usa direto | O "cérebro" que serve dados pela internet |
| **Site (web)** | PHP | Alunos e empresas | A parte pública, no navegador |
| **Back Office (ESTE projeto)** | **Java + Swing** | **Funcionários da faculdade** | A administração interna |

👉 **Este repositório é só a terceira parte: o back office em Java.** Ele é o trabalho de **Programação Orientada a Objetos (POO)** — a "camada institucional".

Os três compartilham o **mesmo banco de dados MySQL**. Por isso, quando o admin aprova uma empresa aqui no Java, essa empresa **já aparece aprovada** no site PHP. Todos olham para a mesma "fonte da verdade".

**Por que isso é importante?** Porque mostra que o projeto não vive isolado — ele faz parte de um ecossistema. O banco de dados é o "ponto de encontro" das três aplicações. Cada uma fala uma linguagem diferente (Node, PHP, Java), mas todas leem e escrevem nas mesmas tabelas.

```
   Alunos/Empresas                 Funcionário da faculdade
        │                                   │
        ▼                                   ▼
  ┌───────────┐    ┌───────────┐     ┌──────────────────┐
  │ Site PHP  │    │ API Node  │     │ Back Office JAVA │  ← este projeto
  └─────┬─────┘    └─────┬─────┘     └─────────┬────────┘
        │                │                     │
        └────────────────┴──────────┬──────────┘
                                     ▼
                          ┌────────────────────┐
                          │   Banco MySQL 8     │  ← a "fonte da verdade"
                          └────────────────────┘
```

---

## 3. As tecnologias (e por que cada uma existe)

| Tecnologia | O que é | Por que usamos |
|---|---|---|
| **Java 17** | Linguagem de programação | É a linguagem do projeto (POO) |
| **Swing** | Kit para criar janelas, botões, tabelas | É o que desenha a interface gráfica |
| **Maven** | Gerenciador de build/dependências | Baixa as bibliotecas e compila o projeto |
| **MySQL 8** | Banco de dados | Onde tudo fica guardado (alunos, vagas...) |
| **HikariCP** | "Pool de conexões" | Reaproveita conexões com o banco (rápido) |
| **dotenv-java** | Leitor de arquivo `.env` | Guarda a senha do banco fora do código |
| **bcrypt** | Criptografia de senha | Guarda senhas de forma segura (com hash) |
| **slf4j-simple** | Sistema de logs | O HikariCP usa para escrever mensagens |

### Entendendo cada peça com uma analogia

- **Java + Swing:** o Java é o "motor" e o Swing é a "carroceria" — as janelas, botões e tabelas que você vê e clica. Swing é uma tecnologia antiga, mas estável e que roda em qualquer computador com Java instalado, sem precisar de internet.

- **Maven:** é como uma **lista de compras automática**. Você diz "preciso do MySQL, do HikariCP e do bcrypt" no arquivo `pom.xml`, e o Maven vai ao mercado (a internet), compra tudo e monta o projeto pronto. Sem ele, você teria que baixar cada biblioteca na mão.

- **HikariCP (pool de conexões):** abrir uma conexão com o banco é como **ligar para alguém** — leva um tempinho para a ligação completar. O pool mantém **várias linhas já abertas** e prontas. Quando o programa precisa, pega uma linha pronta, usa, e devolve. Muito mais rápido do que discar de novo a cada vez.

- **dotenv-java:** lê um arquivo `.env` que guarda dados sensíveis (usuário e senha do banco) **fora do código**. Assim, a senha não fica escrita no meio do programa, onde qualquer um veria.

- **bcrypt:** transforma a senha em um "embaralhado" impossível de desfazer. Falaremos mais na seção 17.

### O que está no `pom.xml`
O `pom.xml` é o "documento de identidade" do projeto para o Maven. Ele declara:
- O Java 17 como versão (`maven.compiler.source/target = 17`).
- A codificação UTF-8 (para acentos funcionarem).
- As 5 dependências: MySQL Connector 8.0.33, HikariCP 5.1.0, slf4j-simple 1.7.36, dotenv-java 3.0.0 e bcrypt 0.10.2.

---

## 4. Como rodar o projeto (passo a passo)

1. **Ter o MySQL rodando** (versão 8).

2. **Criar o banco** rodando os scripts da pasta `db/`:
   ```bash
   mysql -u root -p portal < db/schema.sql   # cria as 11 tabelas
   mysql -u root -p portal < db/seed.sql     # popula dados de teste (opcional)
   ```
   - O `schema.sql` cria a estrutura (as tabelas vazias).
   - O `seed.sql` enche o banco com dados falsos para você testar (alunos, empresas, vagas de mentira).

3. **Configurar o arquivo `.env`** na raiz (use o `.env.example` como base):
   ```
   DB_URL=jdbc:mysql://localhost:3306/hackathon
   DB_USER=root
   DB_PASSWORD=root
   ```
   - `DB_URL` = endereço do banco. `localhost:3306` = "na minha própria máquina, porta 3306".
   - `DB_USER` e `DB_PASSWORD` = login do MySQL.

4. **Executar**:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass=com.portal.Main
   ```
   Ou abrir no IntelliJ e dar "Run" na classe `com.portal.Main`.

### Usuários de teste já criados pelo `seed.sql`
| E-mail | Senha | Perfil |
|---|---|---|
| `admin@unialfa.com` | `Perigoso@2019` | ADMIN (entra no sistema) |
| `joao.silva@aluno.unialfa.edu.br` (e demais `@aluno...`) | `AlunoRandom@10` | STUDENT (não entra — é só aluno) |

> ⚠️ **Importante:** só **ADMIN** consegue logar nesse programa. Se um aluno tentar, leva a mensagem "Acesso restrito à equipe UniALFA". Isso é proposital — é um sistema interno da faculdade.

---

## 5. A arquitetura em camadas (a parte mais importante de entender)

O código é organizado em **camadas**, como os andares de um prédio. Cada andar tem **uma única responsabilidade** e só conversa com o andar vizinho. Isso deixa o código organizado e fácil de manter.

```
┌─────────────────────────────────────────────────┐
│  GUI    (telas, botões, tabelas)                 │  ← o usuário VÊ e CLICA
├─────────────────────────────────────────────────┤
│  SERVICE  (regras de negócio)                    │  ← decide se PODE
│           "pode fazer isso? valida, organiza"    │
├─────────────────────────────────────────────────┤
│  DAO    (acesso ao banco) — escreve e lê SQL     │  ← FALA com o banco
├─────────────────────────────────────────────────┤
│  MODEL  (os dados: Aluno, Vaga, Empresa...)      │  ← os OBJETOS que circulam
└─────────────────────────────────────────────────┘
                       ↕
                 🗄️ Banco MySQL
```

**Analogia do restaurante:**
- **GUI** = o salão e o garçom (onde o cliente pede e recebe).
- **SERVICE** = o gerente (confere se o pedido faz sentido, aplica as regras).
- **DAO** = o cozinheiro que vai à despensa (pega/guarda os ingredientes no banco).
- **MODEL** = o prato em si (o dado: um aluno, uma vaga).

**A regra de ouro:** a GUI **nunca** fala direto com o banco. Ela **sempre** passa pelo Service. Isso evita bagunça e centraliza as regras num lugar só.

**Por que separar em camadas dá tanto trabalho — e mesmo assim vale a pena?**
- **Organização:** cada coisa tem seu lugar. Você sabe exatamente onde mexer.
- **Manutenção:** se mudar uma regra (ex.: "agora todo aluno precisa de telefone"), você mexe só no Service, sem tocar na tela nem no banco.
- **Reuso:** vários lugares podem usar o mesmo Service.
- **Teste:** dá para testar a regra de negócio sem abrir uma janela.

---

## 6. Detalhando cada camada com exemplos do código real

### 6.1 — MODEL (os dados)
São classes simples que representam coisas do mundo real. Exemplo: a classe `Student` guarda nome, RA, CPF, e-mail, telefone, se está apto, e o endereço dele. São como **fichas de papel**: só guardam informação, não tomam decisões.

Entidades principais: `Student` (aluno), `Company` (empresa), `Job` (vaga), `Application` (candidatura), `Course` (curso), `User` (login), `Address` (endereço), `CompanyMember` (funcionário da empresa), `Certificate` (certificado), `Notification` (notificação) e `DashboardStats` (os contadores da tela inicial).

#### Os "enums" (listas fixas de opções)
Um **enum** é uma lista fechada de valores possíveis. Em vez de escrever "APROVADO" como texto solto (e arriscar erro de digitação como "APROVDO"), usamos uma lista oficial e curta:

- `Role`: **ADMIN**, **COMPANY**, **STUDENT** (tipo de usuário)
- `CompanyStatus`: **PENDING**, **ANALYSING**, **APPROVED**, **BLOCKED** (situação da empresa)
- `JobStatus`: **ACTIVE**, **PAUSED**, **CLOSED** (situação da vaga)
- `JobModality`: **PRESENCIAL**, **REMOTE**, **HYBRID** (tipo de trabalho)
- `ApplicationStatus`: **PENDING**, **ANALYSING**, **APPROVED**, **REJECTED**, **CANCELLED** (situação da candidatura)
- `StudentCourseStatus`: **ACTIVE**, **COMPLETED**, **CANCELLED** (situação do aluno no curso)
- `CompanyMemberRole`: **ADMIN**, **RECRUITER** (papel do funcionário dentro da empresa)

**Por que enums são bons?** Porque o computador garante que só esses valores existem. É impossível salvar uma empresa com status "talvez". Menos erro, mais segurança.

### 6.2 — DAO (acesso ao banco) — "Data Access Object"
É a **única** camada que escreve **SQL**. Cada entidade tem o seu: `StudentDAO`, `CompanyDAO`, `JobDAO`, `ApplicationDAO`, `CourseDAO`, `UserDAO`, `DashboardDAO`, etc. Todos herdam de uma classe-mãe chamada **`BaseDAO`**, que guarda o que é comum a todos:

- `getConnection()` → pega uma conexão do pool (HikariCP).
- `now()` → devolve a data/hora atual **no fuso de São Paulo** (`America/Sao_Paulo`), pronta para gravar.
- `mapAddress()` → monta um objeto `Address` a partir das colunas que vieram do banco; devolve `null` quando o aluno/empresa não tem endereço.

**Por que herdar de `BaseDAO`?** Para não repetir código. Em vez de cada DAO ter o seu próprio "pegar conexão" e o seu próprio "que horas são", todos pegam de **um lugar só**. Se um dia precisar mudar (ex.: trocar o fuso), muda em **um único ponto**.

Um detalhe importante do código: os DAOs usam **`PreparedStatement`** (consultas com `?` no lugar dos valores). Isso protege contra **SQL Injection** — um truque malicioso em que alguém digita comandos no lugar de um nome. Com `PreparedStatement`, o que o usuário digita é tratado sempre como **texto**, nunca como comando.

Outro detalhe: os DAOs usam **`try-with-resources`** (`try (Connection conn = ...)`). Isso garante que a conexão é **devolvida ao pool automaticamente** ao final, mesmo se der erro. É como pegar um carrinho no mercado que volta sozinho para o lugar.

### 6.3 — SERVICE (as regras de negócio) — o "gerente"
Aqui ficam as **decisões**. Exemplo real do `StudentService.criar()`:
1. Valida se o nome não está vazio.
2. Valida se o RA é válido (5 a 20 dígitos numéricos).
3. Valida se o CPF é válido (cálculo dos dígitos verificadores).
4. Confere se já não existe aluno com o mesmo RA ou o mesmo CPF.
5. Confere se já não existe um login (`User`) com o mesmo e-mail.
6. Só então manda o DAO salvar.

👉 A GUI só clica "Salvar". **Quem decide se pode salvar é o Service.** Essa separação é o coração da organização do projeto.

Há um Service para cada área: `AuthService` (login), `StudentService` (alunos), `CompanyService` (empresas), `JobService` (vagas), `CourseService` (cursos), `ReportService` (relatórios) e `ApplicationService` (candidaturas).

Quando algo dá errado, o Service lança uma exceção (`ServiceException` ou `AuthException`) com uma mensagem clara. A GUI captura essa mensagem e mostra um popup vermelho. É um "telefone sem fio" bem feito: o erro nasce lá embaixo e chega na tela como um aviso amigável.

### 6.4 — GUI (as telas Swing)
São as janelas que o usuário vê. Cada funcionalidade tem o seu painel ou diálogo:
- `LoginFrame` → tela de login (a primeira a abrir).
- `DashboardFrame` → tela principal (com menu lateral e área central).
- `DashboardHomePanel` → a tela de "Início" com os contadores.
- `StudentListPanel`, `CompanyListPanel`, `JobListPanel`, `ApplicationListPanel`, `CoursePanel`, `UserListPanel`, `ReportPanel` → as listas/telas de cada área.
- `StudentFormDialog`, `CompanyDetailDialog`, `JobDetailDialog`, `ApplicationDetailDialog`, `UserDetailDialog`, `CourseFormDialog`, `StudentImportDialog` → os formulários e janelas de detalhe.

**Convenção:** os arquivos terminados em `Panel` são "telas" que ficam dentro do dashboard; os terminados em `Frame` são janelas independentes; e os terminados em `Dialog` são janelinhas que abrem por cima (popups de formulário).

### 6.5 — UTIL (ferramentas de apoio)
- `ValidationUtil` → valida e formata CPF, CNPJ, e-mail e telefone.
- `PasswordUtil` → confere senhas com bcrypt.
- `ButtonFactory` → fabrica botões padronizados (azul primário, cinza secundário, vermelho de perigo).
- `FileImportUtil` → lê arquivo `.txt` para importar alunos em lote.
- `ReportExporter` → exporta relatórios em `.txt`.
- `StatusCellRenderer` → pinta os status com cores nas tabelas.

### 6.6 — CONFIG (a conexão com o banco)
A classe `DatabaseConfig` cuida de conectar ao MySQL usando o HikariCP. Ela é um **Singleton** (explicado na próxima seção). Os números que ela configura:
- **Máximo de 10 conexões** simultâneas no pool.
- **Mínimo de 2 conexões** sempre prontas.
- Espera no máximo **30 segundos** por uma conexão livre.
- Fecha conexões ociosas após **10 minutos**.
- Renova cada conexão após **30 minutos** de vida.

Quando o programa inicia, ela imprime no console: `HikariCP Pool inicializado com sucesso!`.

---

## 7. Os Padrões de Projeto (Design Patterns) — explicados de verdade

Padrões de projeto são **soluções prontas e conhecidas** para problemas comuns. Como receitas testadas e aprovadas que todo programador reconhece. O projeto usa cinco, e isso costuma ser muito cobrado em apresentação:

### 🔹 Singleton — `DatabaseConfig`
**Problema:** abrir conexão com o banco é caro e lento. Não dá para criar um novo pool a cada clique.
**Solução:** ter **um único** pool de conexões na aplicação inteira, criado uma vez só.
**Como o código faz:** o construtor é privado (`private DatabaseConfig() {}`), então ninguém consegue criar uma cópia. O pool é criado num "bloco estático" que roda **uma vez** quando a classe é carregada pela JVM.
**Analogia:** é o **quadro de chaves do prédio**. Existe só um. Todo mundo pega uma chave (conexão), usa, e devolve. Ninguém faz um quadro de chaves novo a cada vez.

### 🔹 DAO (Data Access Object) — `BaseDAO` + todos os DAOs
**Problema:** não queremos SQL espalhado pelo programa todo, misturado com botões e telas.
**Solução:** isolar **todo** o acesso ao banco em classes próprias (uma por entidade).
**Benefício:** se um dia trocar o MySQL por outro banco, você mexe só nos DAOs. A tela e as regras nem ficam sabendo.

### 🔹 Template Method — dentro do `BaseDAO`
**Problema:** vários DAOs precisam das mesmas coisinhas (pegar conexão, saber a hora, montar endereço).
**Solução:** a classe-mãe `BaseDAO` já implementa esses métodos comuns, e os filhos (`StudentDAO`, etc.) apenas reusam.
**Analogia:** um **formulário com partes já preenchidas**. O cabeçalho já vem pronto; cada DAO só completa o que é específico dele.

### 🔹 Factory Method — `ButtonFactory`
**Problema:** queremos botões sempre com a mesma cara (cor certa, fonte Segoe UI, mãozinha ao passar o mouse).
**Solução:** uma "fábrica" com três métodos prontos: `primary()` (azul), `secondary()` (cinza) e `danger()` (vermelho).
**Como funciona:** em vez de configurar 7 propriedades de cada botão na mão, você escreve `ButtonFactory.primary("Salvar")` e pronto.
**Benefício:** consistência visual e quase zero código repetido. Se quiser mudar a cor de todos os botões azuis, muda em **um lugar**.

### 🔹 Facade (Fachada) — as classes `service/*`
**Problema:** a GUI não deveria conhecer os detalhes internos (DAOs, validações, transações, banco).
**Solução:** os Services oferecem uma "porta de entrada" simples. A GUI só chama `criar(aluno)` e não sabe o que acontece por trás.
**Analogia:** o **balcão de atendimento**. Você pede uma coisa no balcão; não precisa entrar na cozinha, na despensa e no estoque.

### Resumo dos padrões em uma tabela
| Padrão | Onde | Para quê |
|---|---|---|
| **Singleton** | `config/DatabaseConfig` | Um único pool de conexões na aplicação |
| **DAO** | `dao/BaseDAO` + DAOs | Isolar o acesso ao banco por entidade |
| **Template Method** | `dao/BaseDAO` | Reúso de `getConnection()`, `now()` e `mapAddress()` |
| **Factory Method** | `util/ButtonFactory` | Botões padronizados (primary/secondary/danger) |
| **Facade** | `service/*` | A GUI fala com os services, não direto com os DAOs |

---

## 8. O banco de dados (as 11 tabelas)

O banco é a memória permanente do sistema. São **11 tabelas**:

| Tabela | Guarda | Detalhe importante |
|---|---|---|
| `Address` | Endereços | Compartilhado por aluno e empresa |
| `User` | Logins (e-mail + senha + papel) | Senha em **hash bcrypt**; e-mail é único |
| `Course` | Cursos da faculdade | Nome único e nº de períodos |
| `Student` | Alunos | Ligado a um `User` (1 aluno = 1 login) |
| `Certificate` | Certificados dos alunos | — |
| `StudentCourse` | Qual aluno faz qual curso | Liga aluno ↔ curso |
| `Company` | Empresas | Tem status (pendente/aprovada/bloqueada) |
| `CompanyMember` | Funcionários da empresa | Admin ou recrutador |
| `Job` | Vagas de estágio | Modalidade e status |
| `Application` | Candidaturas | Liga aluno ↔ vaga, com status |
| `Notification` | Notificações | — |

### Detalhes técnicos das tabelas (que mostram cuidado)
- Todas as chaves primárias são **VARCHAR(36)** — ou seja, **UUIDs** (identificadores aleatórios como `3f2a9c7e-...`), não números sequenciais. Isso é melhor para um sistema distribuído: as três aplicações podem gerar IDs sem colidir.
- Toda tabela tem **`createdAt`** e **`updatedAt`** automáticos — o banco preenche a data de criação e atualiza sozinho a data de modificação.
- Há colunas **`deletedAt`** em algumas tabelas (Job, Application) — isso é **"soft delete"** (exclusão suave): o registro não some de verdade, só é marcado como apagado. As consultas filtram `deletedAt IS NULL` para ignorar os "apagados".
- Há várias regras de integridade (**FOREIGN KEY**): por exemplo, se um aluno é apagado, seus certificados são apagados junto (`ON DELETE CASCADE`).

### Como as tabelas se conectam (o essencial)
- Todo **aluno** tem um **User** (o login dele). São tabelas separadas: `User` cuida de login/senha/papel; `Student` cuida dos dados acadêmicos (nome, RA, CPF).
- Um aluno tem **no máximo um endereço** (relação 1 para 1 — a coluna `addressId` é única).
- Uma **candidatura** (`Application`) conecta um aluno a uma vaga.
- Uma **vaga** (`Job`) pertence a uma empresa.
- Um **funcionário** (`CompanyMember`) pertence a uma empresa e também tem um `User`.

**Por que separar `User` de `Student`/`Company`?** Porque o sistema tem três tipos de gente (admin, empresa, aluno). O **login é a parte comum** (fica em `User`: e-mail, senha, papel), e os **dados específicos** ficam em tabelas próprias (`Student`, `Company`). Isso evita repetição e mantém o login num lugar só. É um desenho limpo e reutilizável.

---

## 9. Passo a passo de uma ação real: o LOGIN

Vamos seguir o caminho de um clique, de cima a baixo. Isso mostra as camadas trabalhando juntas:

1. O programa abre em `Main.java`, que manda mostrar a `LoginFrame` (usando `SwingUtilities.invokeLater`, o jeito certo de iniciar telas Swing).
2. O usuário digita e-mail + senha e clica **"Entrar"** (ou aperta Enter — o botão é o "default button" da janela).
3. A `LoginFrame` (GUI) chama `authService.login(email, senha)`.
4. O `AuthService` (Service):
   - Confere se e-mail e senha **não estão vazios**. Se estiverem, lança erro "Informe o e-mail/senha".
   - Pede ao `UserDAO` para buscar o usuário pelo e-mail (já em minúsculas e sem espaços: `email.trim().toLowerCase()`).
   - Usa o `PasswordUtil.verify()` para comparar a senha digitada com o **hash** salvo no banco.
   - Se o usuário não existe **ou** a senha não bate → erro "E-mail ou senha inválidos." (Repare: a mensagem é a mesma para os dois casos — isso é de propósito, para não dizer a um invasor se o e-mail existe.)
   - **Verifica se o usuário é ADMIN.** Se for aluno ou empresa, **barra** com "Acesso restrito à equipe UniALFA."
5. Deu certo? A `LoginFrame` se fecha (`dispose()`) e abre a `DashboardFrame` (tela principal).
6. Deu errado? Aparece um popup vermelho de erro com a mensagem, e o usuário continua na tela de login.

🔑 **Detalhe de segurança:** a senha nunca é guardada como texto puro. É guardada como **hash bcrypt**. Mais sobre isso na seção 17.

---

## 10. Passo a passo: CADASTRAR um aluno (com transação)

Esse exemplo é ótimo porque mostra um detalhe técnico importante — a **transação**.

Quando você cadastra um aluno, o sistema precisa criar **duas coisas ao mesmo tempo**: o **login** (`User`) e o **aluno** (`Student`). O método `saveWithUser()` faz isso assim:

1. Gera dois IDs únicos (**UUID**) — um para o `User`, outro para o `Student`.
2. Cria um `User` com papel **STUDENT**. Curiosidade: a senha inicial do aluno é o **próprio CPF** dele.
3. **Desliga o auto-commit** (`setAutoCommit(false)`) → "vou fazer várias coisas, só confirma no final".
4. Salva o `User`.
5. Salva o `Student`.
6. Se **tudo** deu certo → `commit()` (confirma de vez no banco).
7. Se **qualquer coisa** falhou → `rollback()` (desfaz tudo, como se nada tivesse acontecido).
8. No final, religa o auto-commit (`setAutoCommit(true)`).

**Por que isso importa?** Imagine criar o login mas falhar ao criar o aluno. Você ficaria com um login "órfão", sem aluno por trás. A transação garante o princípio do **"tudo ou nada"**: ou as duas coisas entram juntas, ou nenhuma entra. O banco nunca fica pela metade.

**Analogia:** transferência bancária. Tirar dinheiro de uma conta e colocar na outra precisa acontecer junto. Se a segunda metade falha, desfaz a primeira — senão o dinheiro some no ar.

**E antes de tudo isso**, o `StudentService.criar()` já tinha validado nome, RA, CPF, e checado duplicidade de RA/CPF/e-mail. Ou seja: a transação é a última linha de defesa; as regras vêm antes.

---

## 11. Como uma tela de lista funciona por dentro (StudentListPanel)

Vamos abrir o capô de uma tela típica — a lista de alunos. Quase todas as listas do sistema seguem esse mesmo molde, então entender uma é entender quase todas.

A `StudentListPanel` é dividida em três faixas:
- **Topo:** o título "Gestão de Alunos" + botões "+ Novo Aluno", "Importar .txt" e "Atualizar".
- **Barra de busca:** um campo para buscar por **nome ou RA**, com botões "Buscar" e "Limpar".
- **Tabela:** as colunas Nome, RA, E-mail e Aptidão.
- **Rodapé:** botões "Editar" e "Marcar Inapto/Apto".

Pontos espertos de usabilidade:
- **Botões inteligentes:** "Editar" e o de aptidão começam **desabilitados**. Só ligam quando você seleciona uma linha. (Faz sentido: não dá para editar "nada".)
- **Texto dinâmico:** o botão de aptidão muda o texto conforme a linha selecionada — vira "Marcar Inapto" se o aluno está apto, ou "Marcar Apto" se está inapto.
- **Duplo clique:** clicar duas vezes numa linha abre o formulário de edição daquele aluno. Atalho prático.
- **Confirmação:** ao marcar/desmarcar aptidão, aparece um "Deseja realmente...?" antes de aplicar. Evita clique errado.
- **Coluna colorida:** a coluna "Aptidão" usa o `StatusCellRenderer` para mostrar "Apto" em verde e "Inapto" em vermelho.

**O "TableModel" (o cérebro da tabela):** o Swing separa a tabela visual (`JTable`) dos dados (`TableModel`). O `StudentTableModel` é uma classe interna que diz à tabela: "tenho 4 colunas (Nome, RA, E-mail, Aptidão), tenho X linhas, e o valor da célula [linha, coluna] é tal". Quando os dados mudam, ele avisa a tabela com `fireTableDataChanged()` e a tela se redesenha sozinha. É o padrão **MVC** (Model-View-Controller) em miniatura.

**O fluxo de uma busca:**
1. Usuário digita "João" e clica "Buscar".
2. O painel chama `service.buscar("João")`.
3. O Service repassa ao `StudentDAO.findByTerm("João")`, que roda um SQL com `WHERE nome LIKE '%João%' OR ra LIKE '%João%'`.
4. A lista volta e é jogada no TableModel, que redesenha a tabela.

---

## 12. As regras de negócio mais espertas (empresas, status em cascata)

O `CompanyService` tem a lógica mais interessante do projeto: gerenciar o **status de uma empresa** e, em cascata, **ativar ou desativar os logins** dos funcionários dela.

As ações possíveis:
- **Analisar** (`analisar`): só funciona se a empresa estiver **PENDING**. Move para **ANALYSING** e **desativa** os logins dos funcionários (a empresa ainda não foi aprovada, então ninguém dela deve conseguir entrar).
- **Aprovar** (`aprovar`): bloqueia se já estiver aprovada. Move para **APPROVED** e **reativa** todos os logins da empresa (agora pode trabalhar).
- **Bloquear** (`bloquear`): bloqueia se já estiver bloqueada. Move para **BLOCKED** e **desativa** todos os logins da empresa (punição: ninguém dela entra mais).

👉 A sacada é o **efeito cascata**: mudar o status da empresa **automaticamente** mexe no acesso de todos os funcionários dela (`userDAO.setActiveByCompany(...)`). Assim, o administrador faz **uma** ação ("bloquear empresa") e o sistema cuida de **todas** as consequências. É a regra de negócio protegendo a coerência dos dados.

**Por que isso é importante numa apresentação?** Porque mostra que o sistema não é só "salvar e listar" — ele tem **regras reais** que refletem como uma faculdade de verdade trataria uma empresa suspeita.

---

## 13. O Dashboard e seus contadores em tempo real

A tela de "Início" mostra quatro contadores que dão um resumo instantâneo da situação:
- **Empresas pendentes** (esperando aprovação)
- **Vagas ativas** (abertas para candidatura)
- **Candidaturas abertas** (pendentes ou em análise)
- **Alunos aptos** (liberados para estagiar)

O truque técnico é elegante: em vez de fazer 4 consultas separadas ao banco, o `DashboardDAO` faz **uma única consulta** com quatro subconsultas dentro:
```sql
SELECT
  (SELECT COUNT(*) FROM Company WHERE status = 'PENDING')        AS empresasPendentes,
  (SELECT COUNT(*) FROM Job WHERE status = 'ACTIVE' AND deletedAt IS NULL) AS vagasAtivas,
  (SELECT COUNT(*) FROM Application WHERE status IN ('PENDING','ANALYSING') AND deletedAt IS NULL) AS candidaturasAbertas,
  (SELECT COUNT(*) FROM Student WHERE isEligible = 1)            AS alunosAptos
```
Uma ida ao banco traz tudo. Mais rápido e mais limpo. O resultado é embrulhado num objeto `DashboardStats` e exibido na tela.

Se a consulta falhar por algum motivo, o método devolve `DashboardStats(0,0,0,0)` — ou seja, **zeros**, em vez de quebrar a tela. É uma decisão defensiva: a tela sempre abre, mesmo se o banco tropeçar.

---

## 14. A importação de alunos em lote

Em vez de cadastrar aluno por aluno, dá para importar vários de uma vez de um arquivo `.txt`. O `FileImportUtil` lê o arquivo no formato:

```
nome;ra;cpf;email
João Silva;12345;111.222.333-44;joao@aluno.unialfa.edu.br
Maria Souza;67890;222.333.444-55;maria@aluno.unialfa.edu.br
```

Para cada linha, ele:
- **Ignora** linhas vazias ou que começam com `#` (comentário).
- Separa os campos pelo `;`.
- Pula linhas com menos de 4 campos (registra no log "campos insuficientes").
- **Limpa o CPF**, deixando só os números.
- **Valida** RA, CPF e e-mail de cada um. Quem não passa é ignorado (com o motivo no log).

Depois o `StudentService.importar()`:
- Salva os alunos válidos um a um.
- **Pula** RAs e CPFs que já existem no banco (registra "RA X já cadastrado").
- Conta quantos entraram e monta um relatório de erros.
- Se **nenhum** aluno entrou (e havia alunos no arquivo), lança um erro com o resumo.
- No final, devolve a lista dos que foram realmente salvos.

**Por que é robusto?** Porque um aluno com erro **não trava** os outros. O sistema processa o que dá, ignora o que não dá, e te conta exatamente o que aconteceu. É o jeito profissional de fazer importação.

---

## 15. As validações de CPF e CNPJ

O sistema não só confere se o CPF tem 11 dígitos — ele **calcula os dígitos verificadores** de verdade, igual a Receita Federal faz.

**Para o CPF:**
- Tem que ter 11 dígitos.
- Não pode ser tudo igual (ex.: `111.111.111-11` é matematicamente válido, mas é bloqueado de propósito).
- Calcula o **1º dígito verificador** usando pesos de 10 a 2.
- Calcula o **2º dígito verificador** usando pesos de 11 a 2.
- Os dois dígitos calculados precisam bater com os informados.

**Para o CNPJ:** mesma ideia, com 14 dígitos e dois conjuntos de pesos próprios.

**Formatação automática (deixa bonito na tela):**
- CNPJ: `12345678000199` → `12.345.678/0001-99`
- Telefone celular (11 dígitos): `11912345678` → `(11) 91234-5678`
- Telefone fixo (10 dígitos): `1132165478` → `(11) 3216-5478`

**RA:** considerado válido se tiver de 5 a 20 dígitos numéricos.
**E-mail:** validado por uma expressão regular (`algo@algo.algo`).

Isso evita lixo no banco e mostra cuidado com a qualidade dos dados — um diferencial real do projeto.

---

## 16. As cores e o visual (identidade azul)

Todo o sistema segue uma identidade visual **azul**, limpa e profissional. As cores principais:
- **Azul primário `#1565C0`** — botões principais, topo das telas, título.
- **Azul-escuro `#1A237E`** — fundo do menu lateral.
- **Azul-escuro selecionado `#0D47A1`** — item de menu ativo e botão "Sair".
- **Azul claro `#64B5F6`** — a faixa que marca o item selecionado no menu.
- **Cinza `#EEEEEE` / texto `#333333`** — botões secundários.
- **Vermelho `#C62828`** — botões de perigo (ações destrutivas).

### Os status coloridos nas tabelas (`StatusCellRenderer`)
Para deixar as listas fáceis de ler num relance, a coluna de "situação" é colorida **só no texto** (fundo sempre branco), seguindo uma lógica de semáforo:
- 🟢 **Verde** (`#2E7D32`) — coisas positivas: APPROVED, ACTIVE, Ativo, Apto.
- 🔵 **Azul** (`#1565C0`) — em andamento: ANALYSING, Administrador.
- 🟡 **Âmbar/amarelo** (`#F57F17`) — aguardando: PENDING, PAUSED, Recrutador.
- 🔴 **Vermelho** (`#C62828`) — negativos: REJECTED, BLOCKED, CLOSED, Inativo, Inapto.
- ⚪ **Cinza** (`#757575`) — neutro: CANCELLED.

O bacana é que **um único renderer** cobre todas as telas (empresas, vagas, candidaturas, cursos, usuários). Sem código repetido: a tabela manda o texto, o renderer escolhe a cor. Se o texto não estiver no mapa de cores, usa um cinza-escuro padrão.

### O menu lateral e o "baralho de telas"
A `DashboardFrame` usa um **CardLayout**: é como um **baralho de cartas empilhadas**. Todos os painéis (Empresas, Alunos, Vagas...) estão carregados, mas só um aparece por vez. Clicou em "Alunos"? Ele traz a carta dos alunos para a frente. É instantâneo e não recria a tela toda.

O menu também tem **efeito hover** (o item sob o mouse muda de cor) e marca o item ativo com uma **faixa azul à esquerda** + fundo destacado.

---

## 17. Segurança: como as senhas são guardadas

A senha **nunca** é guardada como texto puro. Ela é guardada como **hash bcrypt** — uma "embaralhada" que **não dá para desfazer**.

**Como funciona o login então?**
1. Quando o aluno é criado, a senha (inicialmente o CPF) é transformada em hash e salva.
2. No login, o usuário digita a senha.
3. O `PasswordUtil.verify()` embaralha a senha digitada **do mesmo jeito** e compara os dois embaralhados.
4. Se baterem, a senha está certa.

**Por que isso é seguro?** Porque mesmo quem rouba o banco de dados **não vê as senhas reais** — só vê os hashes embaralhados. E o bcrypt é proposalmente **lento**, o que dificulta ataques de força bruta (tentar milhões de senhas).

**Detalhe honesto do código:** neste módulo Java, o `PasswordUtil` só tem o método `verify()` (conferir). A **geração** do hash das senhas de admin é feita por outra parte do sistema (a API/seed). Faz sentido: este back office é principalmente um leitor/conferidor de credenciais de ADMIN.

Outras camadas de segurança:
- **`PreparedStatement`** em todos os DAOs → proteção contra **SQL Injection**.
- **Login restrito a ADMIN** → alunos e empresas não entram nesse programa.
- **Senha do banco no `.env`** → fora do código-fonte.
- O `User` tem campos `totpSecret` e `totpEnabled` no banco — preparação para **autenticação em dois fatores (2FA)** no futuro, embora ainda não usada por este módulo.

---

## 18. Pontos honestos: o que está pronto e o que é "esqueleto"

Para você não ser pego de surpresa numa pergunta, aqui vai a visão honesta:

**Totalmente funcional:**
- Login com bcrypt e restrição a ADMIN.
- Cadastro, edição, busca, importação e aptidão de alunos (com transação).
- Gestão de status de empresas (analisar/aprovar/bloquear) com cascata nos logins.
- Dashboard com contadores em tempo real.
- Listagens com tabelas, busca e status coloridos.
- Validações de CPF/CNPJ/e-mail/telefone.
- Exportação de relatórios em `.txt` (empresas, alunos, vagas, candidaturas), com nome de arquivo que inclui data/hora (ex.: `relatorio_alunos_20260616_143015.txt`).

**Esqueleto / parcial (sinceridade vale ponto):**
- O `ApplicationService` está **vazio** — a lógica de candidaturas que existe está direto no DAO/painel; o Service ainda é um espaço reservado para futuras regras.
- Algumas telas de detalhe são principalmente de **consulta** (ver), não de edição completa.

Isso é absolutamente normal num projeto de hackathon/faculdade: prioriza-se o que demonstra valor. E ter um Service vazio reservado mostra que a **arquitetura já prevê** onde a lógica vai entrar.

---

## 19. Como o projeto está organizado nas pastas

```
portal-desktop-swing/
├── pom.xml                  → configuração do Maven (dependências, Java 17)
├── .env / .env.example      → dados de conexão com o banco
├── README.md                → instruções resumidas
├── EXPLICACAO.md            → este documento
├── db/
│   ├── schema.sql           → cria as 11 tabelas
│   └── seed.sql             → popula dados de teste
└── src/main/java/com/portal/
    ├── Main.java            → ponto de entrada (abre o login)
    ├── config/
    │   └── DatabaseConfig   → pool de conexões (Singleton)
    ├── model/               → entidades (Student, Company, Job...)
    │   └── enums/           → listas fixas (Role, JobStatus...)
    ├── dao/                 → acesso ao banco (BaseDAO + DAOs)
    ├── service/             → regras de negócio (Facade)
    ├── util/                → apoio (validação, botões, import, relatórios)
    └── gui/                 → telas Swing
        ├── login/           → LoginFrame
        ├── dashboard/       → DashboardFrame + Home
        ├── students/        → lista, formulário, importação
        ├── companies/       → lista e detalhe
        ├── jobs/            → lista e detalhe
        ├── applications/    → lista e detalhe
        ├── course/          → painel e formulário
        ├── users/           → lista e detalhe
        └── reports/         → painel de relatórios
```

A estrutura espelha exatamente as camadas: cada pasta é uma responsabilidade. É fácil achar qualquer coisa: "regra de aluno" → `service/StudentService`; "SQL de aluno" → `dao/StudentDAO`; "tela de aluno" → `gui/students/`.

---

## 20. Glossário rápido (para não travar na hora de explicar)

| Termo | Significado simples |
|---|---|
| **POO** | Programação Orientada a Objetos — organizar o código em "objetos" (aluno, empresa...) |
| **Swing** | Biblioteca do Java para fazer janelas/telas |
| **DAO** | Classe que fala com o banco de dados |
| **Service** | Classe com as regras do negócio |
| **Model/Entidade** | Classe que representa um dado (Aluno, Vaga) |
| **Enum** | Lista fixa de opções válidas |
| **Hash (bcrypt)** | Senha embaralhada de forma irreversível |
| **Pool de conexões** | Conjunto de conexões reaproveitadas com o banco |
| **Transação** | Conjunto de operações "tudo ou nada" |
| **Commit / Rollback** | Confirmar de vez / desfazer tudo |
| **UUID** | Identificador único e aleatório (ex.: `3f2a9c7e-...`) |
| **CardLayout** | "Baralho" de telas; mostra uma por vez |
| **TableModel** | O "cérebro" que alimenta uma tabela na tela |
| **Singleton** | Padrão que garante uma única instância |
| **Facade** | Porta de entrada simples que esconde a complexidade |
| **PreparedStatement** | Consulta SQL segura, com `?` no lugar dos valores |
| **SQL Injection** | Ataque que injeta comandos via campos de texto |
| **Soft delete** | "Apagar" marcando o registro, sem removê-lo de fato |
| **Foreign key** | Ligação entre tabelas com regras de integridade |

---

## 21. Perguntas e respostas para defender o projeto

Se te perguntarem, responda assim:

**"O que é o projeto?"**
→ Um back office desktop em Java Swing para a equipe da UniALFA administrar o Portal de Estágios — aprovar empresas, gerenciar alunos, ver vagas e candidaturas, e gerar relatórios.

**"Qual é a arquitetura?"**
→ Em camadas: GUI → Service → DAO → Banco. A GUI nunca fala direto com o banco; tudo passa pelo Service. Isso organiza o código e centraliza as regras.

**"Quais padrões de projeto usou?"**
→ Cinco: Singleton (pool de conexões), DAO, Template Method (no BaseDAO), Factory Method (botões) e Facade (services).

**"Como garante segurança?"**
→ Senha em hash bcrypt, login restrito a ADMIN, PreparedStatement contra SQL Injection, e credenciais do banco no `.env` (fora do código).

**"E a integridade dos dados?"**
→ Transações com commit/rollback (tudo ou nada) ao criar aluno + login juntos, e validação real de CPF/CNPJ com dígitos verificadores.

**"Tem alguma regra de negócio interessante?"**
→ Sim: ao mudar o status de uma empresa (aprovar/bloquear), o sistema ativa ou desativa em cascata os logins de todos os funcionários dela.

**"Como funciona o dashboard?"**
→ Uma única consulta SQL com quatro subconsultas traz todos os contadores de uma vez; se falhar, devolve zeros para a tela nunca quebrar.

**"Faz parte de algo maior?"**
→ Sim: é um de três módulos (API Node.js, site PHP e este back office Java) que compartilham o mesmo banco MySQL.

**"Por que Swing e não web?"**
→ Porque é um sistema interno, usado por poucos funcionários, que roda direto na máquina sem precisar de servidor web ou navegador. E o foco do trabalho era demonstrar **POO em Java**.

**"O que faltou ou ficou para depois?"**
→ A lógica de candidaturas ainda está mais no DAO do que no Service (o `ApplicationService` é um esqueleto reservado), e algumas telas são só de consulta. A arquitetura, porém, já prevê onde tudo isso entra.

---

## 22. Resumo final

Em uma linha:
> Um sistema **bem organizado em camadas**, com **padrões de projeto clássicos**, **segurança de senha (bcrypt)**, **validações sérias (CPF/CNPJ)**, **regras de negócio reais (status em cascata)** e uma **interface azul limpa** — feito para a equipe da UniALFA administrar estágios com tranquilidade.

O que faz esse projeto se destacar:
1. **Separação de responsabilidades** clara (GUI / Service / DAO / Model).
2. **Cinco padrões de projeto** aplicados de forma natural, não forçada.
3. **Cuidado com dados:** validação de verdade, transações, soft delete.
4. **Cuidado com segurança:** hash, acesso restrito, consultas protegidas.
5. **Boa experiência de uso:** busca, status coloridos, confirmações, importação em lote.
6. **Faz parte de um ecossistema** maior, conversando com Node e PHP pelo banco.

Boa leitura no ônibus! 🚌📖 Qualquer parte que você quiser que eu detalhe ainda mais, é só pedir.
