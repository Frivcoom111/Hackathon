# Configuração Inicial — Portal Desktop Swing

## O que já está pronto

| Item | Status |
|---|---|
| Projeto Maven criado (Java 17) | ✅ |
| Dependência MySQL Connector | ✅ |
| Dependência HikariCP (pool) | ✅ |
| Dependência dotenv-java | ✅ |
| `DatabaseConfig.java` (pool de conexão) | ✅ |
| `SimpleTestConnection.java` (teste de conexão) | ✅ |
| `.env` com credenciais do banco (Railway) | ✅ |
| `.gitignore` ignorando `.env` e `target/` | ✅ |
| Estrutura de pastas (`gui`, `dao`, `model`, `misc`) | ✅ |
| Banco MySQL hospedado no Railway | ✅ |
| `LoginFrame.java` (stub vazio) | ⚠️ existe, mas vazio |

---

## O que falta antes do primeiro commit

### 1. Adicionar BCrypt no pom.xml

As senhas no banco foram criadas pelo Node.js com bcrypt. Sem essa
dependência não é possível verificar a senha no login.

FEITO
```xml 
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bcrypt</artifactId>
    <version>0.10.2</version>
</dependency>
```

FEITO
### 2. Testar a conexão com o banco 

Antes de commitar, rodar `SimpleTestConnection.java` e confirmar que
a saída é `✅ Conexão OK`. Se falhar, verificar o `.env`.


### 3. Criar o arquivo `Main.java`

Ponto de entrada da aplicação. Só abre o `LoginFrame` por enquanto:

```java
package com.portal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
```

---

## Primeiro commit (Git)

O projeto ainda **não tem git inicializado**. Antes de começar o desenvolvimento
a sério, rodar:

```bash
git init
git add .
git commit -m "chore: configuração inicial do projeto Maven + conexão com banco"
```

> O `.env` já está no `.gitignore`, então as credenciais NÃO vão para o repositório.

Depois criar o repositório no GitHub e conectar:

```bash
git remote add origin https://github.com/<org>/<repo>.git
git push -u origin main
```

---

## Sequência de desenvolvimento após o commit inicial

```
1. [FEITO]  Configuração Maven + banco
2. [FEITO]  Estrutura de pacotes
3. [ ]      Adicionar BCrypt → pom.xml
4. [ ]      Testar conexão → SimpleTestConnection
5. [ ]      Main.java
6. [ ]      git init + primeiro commit + push
7. [ ]      Iniciar login (ver PASSOS_LOGIN.md)
```

---

## Referências

- Detalhes dos passos do login → `PASSOS_LOGIN.md`
- Estrutura completa de pacotes e metas → `ESTUTURA.md`
- Requisitos do hackathon e banco de dados → `ESTRUTURA_PASTA.md`
