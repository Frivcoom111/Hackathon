# Criar Usuário para Testar o Login Java

## Resposta curta: sim, você consegue criar direto no SQL

O Java **não depende da API do Carlos**. Ele conecta direto no MySQL via JDBC.
A API só existe para o Japa (PHP). Você pode inserir dados no banco manualmente
e o login Java vai funcionar imediatamente.

---

## Por que o password precisa de atenção especial

A senha não pode ser inserida em texto puro. O banco guarda um **hash bcrypt**,
e o Java verifica com `PasswordUtil.verify()`. Se você colocar "123456" direto
no campo password, o login nunca vai funcionar.

Você precisa gerar o hash antes de inserir.

---

## Como gerar o hash bcrypt

### Opção 1 — site online (mais rápido para testar)

Acesse: https://bcrypt-generator.com  
Digite sua senha (ex: `admin123`), clique em "Generate" — copie o resultado.  
Vai ser algo como: `$2a$12$Abc123...` (string longa começando com `$2`)

### Opção 2 — rodar um main Java rápido no próprio projeto

Crie temporariamente esse método em qualquer classe e rode:

```java
import at.favre.lib.crypto.bcrypt.BCrypt;

public class GerarHash {
    public static void main(String[] args) {
        String senha = "admin123";
        String hash = BCrypt.withDefaults().hashToString(12, senha.toCharArray());
        System.out.println(hash);
    }
}
```

Copie o hash impresso no console e use no INSERT abaixo.

---

## SQL para inserir o usuário de teste

Cole isso no MySQL Workbench (ou DBeaver, HeidiSQL, etc.):

```sql
-- Troque o hash pelo que você gerou acima
INSERT INTO users (id, email, password, role)
VALUES (
    UUID(),
    'admin@unialfa.com',
    '$2a$10$D7N/DPh0qb8hEHUB4hRdvuHgI2LJ36zjI3Ik9Y/7NYueyg9.3iqJy',
    'ADMIN'
);
```

Para STUDENT:
```sql
INSERT INTO User (id, email, password, role, createdAt, updatedAt)
VALUES (
    UUID(),
    'aluno@unialfa.com',
    '$2a$10$.p8Zex2j2KdCfnSsXxdM7ODi9AC4zRq2geefTXwqKhj8OC4ziQ4hu',
    'STUDENT',
    NOW(),
    NOW()
);
```

Para COMPANY:
```sql
INSERT INTO User (id, email, password, role, createdAt, updatedAt)
VALUES (
    UUID(),
    'empresa@teste.com',
    '$2a$10$EhWWmCwI7Eo7gAXfIF32pewzEba.63DxGp4e.ZU16xyo3HxQ7Ayym',
    'COMPANY',
    NOW(),
    NOW()
);
```
CRIADO
---

## Como o Java chega até esse usuário

```
LoginFrame
  └─ digita email + senha
       └─ AuthService.login(email, senha)
            └─ UserDAO.findByEmail(email)
                 └─ SELECT id, email, password, role FROM users WHERE email = ?
                      └─ MySQL (porta 3306, conexão direta via JDBC)
            └─ PasswordUtil.verify(senhaDigitada, hashDoBanco)
                 └─ BCrypt compara os dois → true ou false
            └─ se OK → Session.setCurrentUser(user)
                     → abre DashboardFrame
```

O `.env` do projeto tem `DB_URL`, `DB_USER` e `DB_PASSWORD`. Enquanto essas
variáveis apontarem pro mesmo banco que você inseriu o usuário, o login funciona.

---

## Checklist para testar hoje

- [ ] Gerar hash bcrypt da senha que vai usar
- [ ] Rodar o INSERT no banco (confirmar que a row foi criada)
- [ ] Rodar o projeto (`Main.java`)
- [ ] Digitar email + senha → deve abrir o DashboardFrame
- [ ] Testar senha errada → deve aparecer "E-mail ou senha inválidos."

---

## Estrutura das tabelas relevantes (resumo)

| Tabela | Campos principais |
|---|---|
| `users` | `id`, `email`, `password` (bcrypt), `role` (ADMIN/COMPANY/STUDENT) |
| `students` | `id`, `userId` (FK para users), `name`, `ra`, `cpf` |
| `companies` | `id`, `userId` (FK para users), `name`, `cnpj`, `status` |

O login só usa a tabela `users`. As outras só importam quando for montar
o perfil completo do usuário dentro do sistema.

senha que irei utilizar: Perigoso@2019
hash dela