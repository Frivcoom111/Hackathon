# Passos para Implementar o Login

## Fluxo completo

```
LoginFrame  →  AuthService  →  UserDAO  →  MySQL (tabela User)
           ←  User (objeto) ou AuthException  ←
           →  Session.setCurrentUser(user)
           →  abre DashboardFrame
```

---

## O que falta criar

| Arquivo | Pacote | Status |
|---|---|---|
| `Role.java` (enum) | `model/enums` | ❌ criar |
| `User.java` | `model` | ❌ criar |
| `UserDAO.java` | `dao` | ❌ criar |
| `PasswordUtil.java` | `util` | ❌ criar |
| `AuthService.java` | `service` | ❌ criar |
| `Session.java` | `util` | ❌ criar |
| `LoginFrame.java` | `gui/login` | ⚠️ stub vazio |
| `Main.java` | `com.portal` | ❌ criar |
| dependência BCrypt | `pom.xml` | ❌ adicionar |

---

## Passo 1 — Adicionar BCrypt ao pom.xml

As senhas no banco foram criadas pelo Node.js com bcrypt. O Java precisa
verificar esse hash — não é possível usar SHA-256 puro.

Adicionar dentro de `<dependencies>`:

```xml
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bcrypt</artifactId>
    <version>0.10.2</version>
</dependency>
```

---

## Passo 2 — Role.java (enum)

Arquivo: `src/main/java/com/portal/model/enums/Role.java`

```java
package com.portal.model.enums;

public enum Role {
    ADMIN, COMPANY, STUDENT
}
```

---

## Passo 3 — User.java (model)

Arquivo: `src/main/java/com/portal/model/User.java`

Campos necessários para o login (não precisa mapear tudo agora):

```java
package com.portal.model;

import com.portal.model.enums.Role;

public class User {
    private String id;
    private String email;
    private String password;   // hash bcrypt
    private Role role;
    private boolean isActive;

    // getters e setters
}
```

---

## Passo 4 — UserDAO.java

Arquivo: `src/main/java/com/portal/dao/UserDAO.java`

Único método necessário para o login:

```java
// Busca usuário pelo email — retorna null se não encontrar
public User findByEmail(String email) {
    String sql = "SELECT id, email, password, role, isActive FROM User WHERE email = ?";
    // PreparedStatement + ResultSet → montar User
}
```

Usa `DatabaseConfig.getConnection()`. Lembrar de fechar Connection, Statement e
ResultSet no finally (ou try-with-resources).

---

## Passo 5 — PasswordUtil.java

Arquivo: `src/main/java/com/portal/util/PasswordUtil.java`

```java
package com.portal.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PasswordUtil {

    // Verifica senha digitada contra o hash bcrypt do banco
    public static boolean verify(String rawPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(rawPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
```

---

## Passo 6 — AuthService.java

Arquivo: `src/main/java/com/portal/service/AuthService.java`

Regras de negócio do login:
1. Buscar user pelo email
2. Verificar se existe
3. Verificar se a senha bate (bcrypt)
4. Verificar se `role == ADMIN` (só admin acessa o Back Office)
5. Verificar se `isActive == true`

```java
public User login(String email, String password) throws AuthException {
    User user = userDAO.findByEmail(email);

    if (user == null) throw new AuthException("Usuário não encontrado.");
    if (!PasswordUtil.verify(password, user.getPassword()))
        throw new AuthException("Senha incorreta.");
    if (user.getRole() != Role.ADMIN)
        throw new AuthException("Acesso restrito à equipe UniALFA.");
    if (!user.isActive())
        throw new AuthException("Conta desativada. Contate o administrador.");

    return user;
}
```

Criar também `AuthException.java` (extends Exception) no mesmo pacote.

---

## Passo 7 — Session.java

Arquivo: `src/main/java/com/portal/util/Session.java`

Singleton simples para guardar o usuário logado durante toda a execução:

```java
package com.portal.util;

import com.portal.model.User;

public class Session {
    private static User currentUser;

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }
    public static void clear() { currentUser = null; }
}
```

---

## Passo 8 — LoginFrame.java (GUI Swing)

Arquivo: `src/main/java/com/portal/gui/login/LoginFrame.java`

### Layout esperado

```
┌──────────────────────────────────────┐
│                                      │
│         🎓 Portal UniALFA            │
│       Back Office Institucional      │
│                                      │
│   Email:   [________________________]│
│   Senha:   [________________________]│
│                                      │
│            [ Entrar ]                │
│                                      │
│   ⚠ Mensagem de erro aqui           │
│                                      │
└──────────────────────────────────────┘
```

### Estrutura da classe

```java
public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Portal UniALFA — Login");
        setSize(420, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);   // centraliza na tela
        initComponents();
    }

    private void initComponents() { /* montar layout */ }

    private void onLoginClick() {
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            User user = authService.login(email, password);
            Session.setCurrentUser(user);
            dispose();                        // fecha o login
            new DashboardFrame().setVisible(true);  // abre o dashboard
        } catch (AuthException e) {
            errorLabel.setText(e.getMessage());
        }
    }
}
```

Dicas:
- Vincular o botão Entrar também ao `KeyEvent.VK_ENTER` nos campos
- `errorLabel` começa invisível / texto vazio e aparece só no erro
- Usar `GridBagLayout` ou `BoxLayout` para o formulário centralizado

---

## Passo 9 — Main.java

Arquivo: `src/main/java/com/portal/Main.java`

```java
package com.portal;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
```

`SwingUtilities.invokeLater` garante que a GUI rode na Event Dispatch Thread (EDT),
evitando problemas de concorrência no Swing.

---

## Checklist de implementação

- [ ] Passo 1 — BCrypt no pom.xml
- [ ] Passo 2 — `Role.java`
- [ ] Passo 3 — `User.java`
- [ ] Passo 4 — `UserDAO.java`
- [ ] Passo 5 — `PasswordUtil.java`
- [ ] Passo 6 — `AuthService.java` + `AuthException.java`
- [ ] Passo 7 — `Session.java`
- [ ] Passo 8 — `LoginFrame.java` (implementar corpo)
- [ ] Passo 9 — `Main.java`
- [ ] Testar: login com admin válido → abre dashboard
- [ ] Testar: senha errada → exibe mensagem
- [ ] Testar: usuário não-ADMIN → exibe mensagem de acesso restrito

---

## Ordem sugerida de código

```
pom.xml → Role → User → UserDAO → PasswordUtil → AuthService → Session → LoginFrame → Main
```

Cada passo depende do anterior — seguir nessa ordem evita erros de compilação.
