package com.portal.service;

import com.portal.dao.UserDAO;
import com.portal.model.User;
import com.portal.model.enums.Role;
import com.portal.util.PasswordUtil;

/**
 * AuthService: serviço responsável pela AUTENTICAÇÃO (login) dos usuários.
 *
 * A camada de serviço fica "entre" a tela (GUI) e o banco (DAO). Ela concentra as
 * REGRAS DE NEGÓCIO. Aqui, a regra é: validar os campos, conferir a senha e garantir
 * que apenas administradores entrem neste sistema.
 */
public class AuthService {

    // Este serviço usa o UserDAO para buscar o usuário no banco.
    private final UserDAO userDAO = new UserDAO();

    /**
     * Tenta autenticar um usuário com e-mail e senha.
     *
     * @return o User autenticado, se tudo estiver correto.
     * @throws AuthException se algum campo estiver vazio, as credenciais forem
     *                       inválidas, ou o usuário não for administrador.
     */
    public User login(String email, String senha) throws AuthException {
        // 1) Validações básicas: e-mail e senha não podem estar vazios.
        if (email == null || email.isBlank()) throw new AuthException("Informe o e-mail.");
        if (senha == null || senha.isBlank())  throw new AuthException("Informe a senha.");

        // 2) Busca o usuário pelo e-mail (normalizado: sem espaços e em minúsculas).
        User user = userDAO.findByEmail(email.trim().toLowerCase());

        // 3) Verifica se o usuário existe E se a senha digitada confere com o hash salvo.
        //    A mensagem é genérica de propósito (não diz se foi o e-mail ou a senha),
        //    o que é uma boa prática de segurança.
        if (user == null || !PasswordUtil.verify(senha, user.getPassword())) {
            throw new AuthException("E-mail ou senha inválidos.");
        }

        // 4) Regra de acesso: este painel é exclusivo da equipe (ADMIN).
        //    Mesmo com login correto, alunos e empresas não podem entrar aqui.
        if (user.getRole() != Role.ADMIN) {
            throw new AuthException("Acesso restrito à equipe UniALFA.");
        }

        return user; // Login bem-sucedido: devolve o usuário autenticado.
    }
}
