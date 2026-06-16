# Portal de Estágios UniALFA — Back Office (Java Swing)

Aplicação **desktop** usada pela equipe da faculdade para administrar o Portal de
Estágios: aprovar/bloquear empresas, gerenciar alunos, consultar vagas e
candidaturas e gerar relatórios. Faz parte de um sistema maior (API Node.js +
front-end PHP); este módulo é a **camada institucional (POO Java)**.

## Tecnologias

- **Java 17** + **Swing** (interface gráfica)
- **Maven** (build e dependências)
- **MySQL 8** (banco de dados)
- **HikariCP** (pool de conexões), **dotenv-java** (.env), **bcrypt** (senhas)

## Como rodar

1. **Criar o banco** (MySQL 8): rode os scripts da pasta `db/`:
   ```bash
   mysql -u root -p portal < db/schema.sql   # cria as tabelas
   mysql -u root -p portal < db/seed.sql     # popula dados de teste (opcional)
   ```

2. **Configurar o `.env`** na raiz do projeto (use o `.env.example` como base):
   ```
   DB_URL=jdbc:mysql://localhost:3306/portal
   DB_USER=seu_usuario
   DB_PASSWORD=sua_senha
   ```

3. **Executar**:
   ```bash
   mvn compile
   mvn exec:java -Dexec.mainClass=com.portal.Main
   ```
   Ou simplesmente abrir o projeto no IntelliJ e rodar `com.portal.Main`.

### Usuários de teste (criados pelo `seed.sql`)

| E-mail | Senha | Perfil |
|---|---|---|
| `admin@unialfa.com` | `Perigoso@2019` | ADMIN |
| `joao.silva@aluno.unialfa.edu.br` (e demais `@aluno...`) | `AlunoRandom@10` | STUDENT |

## Estrutura do projeto

```
src/main/java/com/portal/
├── Main.java          # ponto de entrada (abre a tela de login)
├── config/            # DatabaseConfig (pool de conexões — Singleton)
├── model/             # entidades do domínio (Student, Company, Job, ...)
│   └── enums/         # status e papéis (Role, JobStatus, ...)
├── dao/               # acesso ao banco (1 DAO por entidade, todos estendem BaseDAO)
├── service/           # regras de negócio (Facade entre a GUI e os DAOs)
├── util/              # apoio (Session, validações, exportar relatório, botões)
└── gui/               # telas Swing (login, dashboard, listas e formulários)

db/                    # schema.sql (tabelas) e seed.sql (dados de teste)
```

## Padrões de projeto aplicados

| Padrão | Onde | Para quê |
|---|---|---|
| **Singleton** | `config/DatabaseConfig` | Um único pool de conexões na aplicação |
| **DAO** | `dao/BaseDAO` + DAOs | Isolar o acesso ao banco por entidade |
| **Template Method** | `dao/BaseDAO` | Reúso de `getConnection()`, `now()` e `mapAddress()` |
| **Factory Method** | `util/ButtonFactory` | Botões padronizados (primary/secondary/danger) |
| **Facade** | `service/*` | A GUI fala com os services, não direto com os DAOs |

## Funcionalidades

- **Empresas:** aprovar, bloquear e consultar cadastros
- **Alunos:** cadastrar, editar, consultar, importar de `.txt` e marcar aptidão
- **Vagas e candidaturas:** consultar e acompanhar status
- **Relatórios:** exportação em `.txt`
- **Dashboard:** contadores em tempo real (empresas pendentes, vagas ativas, etc.)
- **Login:** autenticação com senha em hash (bcrypt) e controle de sessão
