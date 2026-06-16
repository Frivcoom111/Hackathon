package com.portal.service;

/**
 * AuthException: exceção específica para erros de AUTENTICAÇÃO (login).
 *
 * É lançada pelo AuthService quando o login falha — por e-mail/senha inválidos,
 * campos em branco ou acesso não autorizado. Separá-la de ServiceException deixa
 * claro, para quem trata o erro, que o problema foi no login.
 */
// O "extends Exception" é nativo do Java: faz desta classe uma exceção checada.
public class AuthException extends Exception {
    /**
     * @param mensagem texto explicando a falha de login (ex.: "E-mail ou senha inválidos.").
     */
    public AuthException(String mensagem) {
        super(mensagem); // Repassa a mensagem para a classe-mãe Exception.
    }
}
