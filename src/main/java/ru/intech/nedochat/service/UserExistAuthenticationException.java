package ru.intech.nedochat.service;


import org.springframework.security.core.AuthenticationException;

public class UserExistAuthenticationException extends AuthenticationException {

    public UserExistAuthenticationException(final String message) {
        super(message);
    }

}