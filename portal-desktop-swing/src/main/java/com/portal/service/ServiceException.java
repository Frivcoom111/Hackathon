// Pacote "service": guarda as classes de REGRA DE NEGÓCIO (lógica do sistema).
package com.portal.service;

/**
 * ServiceException: exceção (erro) usada pela camada de serviço.
 *
 * O QUE É UMA EXCEÇÃO? É a forma do Java sinalizar que "algo deu errado". Quando uma
 * regra de negócio não é cumprida (ex.: "nome obrigatório"), o serviço lança esta
 * exceção, e a tela que chamou pode capturá-la e mostrar a mensagem ao usuário.
 *
 * "extends Exception" significa que ela É uma exceção (do tipo "checada"): o compilador
 * obriga quem chama o método a tratá-la (try/catch) ou declará-la (throws).
 */
public class ServiceException extends Exception {
    /**
     * @param message a mensagem explicando o que deu errado (ex.: "CPF inválido.").
     *                "super(message)" repassa essa mensagem para a classe-mãe Exception.
     */
    public ServiceException(String message) {
        super(message);
    }
}
