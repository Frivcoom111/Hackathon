package com.portal.service;

//o "extends Exception" é nativo do java
public class AuthException extends Exception {
    public AuthException(String mensagem) {
        super(mensagem);
    }
}
