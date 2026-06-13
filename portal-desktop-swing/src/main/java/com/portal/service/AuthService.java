package com.portal.service;

import com.portal.dao.UserDAO;
import com.portal.model.User;
import com.portal.model.enums.Role;
import com.portal.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String senha) throws AuthException {
        if (email == null || email.isBlank()) throw new AuthException("Informe o e-mail.");
        if (senha == null || senha.isBlank())  throw new AuthException("Informe a senha.");

        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null || !PasswordUtil.verify(senha, user.getPassword())) {
            throw new AuthException("E-mail ou senha inválidos.");
        }
        if (user.getRole() != Role.ADMIN) {
            throw new AuthException("Acesso restrito à equipe UniALFA.");
        }
        return user;
    }
}
