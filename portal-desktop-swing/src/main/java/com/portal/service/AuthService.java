package com.portal.service;

import com.portal.dao.UserDAO;
import com.portal.model.User;
import com.portal.model.enums.Role;
import com.portal.util.PasswordUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String email, String password) throws AuthException {
        if (email == null || email.isBlank())
            throw new AuthException("Informe o e-mail.");
        if (password == null || password.isBlank())
            throw new AuthException("Informe a senha.");

        User user = userDAO.findByEmail(email);

        if (user == null)
            throw new AuthException("E-mail ou senha incorretos.");
        if (!PasswordUtil.verify(password, user.getPassword()))
            throw new AuthException("E-mail ou senha incorretos.");
        if (user.getRole() != Role.ADMIN)
            throw new AuthException("Acesso restrito à equipe UniALFA.");
        if (!user.isActive())
            throw new AuthException("Conta desativada. Contate o administrador.");

        return user;
    }
}
