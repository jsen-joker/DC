package com.dryork.config.shiro.exception;

import org.apache.shiro.authc.AuthenticationException;

public class TokenException extends AuthenticationException {
    public TokenException() {
    }

    public TokenException(String message) {
        super(message);
    }
}
