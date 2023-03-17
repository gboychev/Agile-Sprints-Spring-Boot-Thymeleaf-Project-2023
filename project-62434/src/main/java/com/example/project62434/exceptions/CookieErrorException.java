package com.example.project62434.exceptions;

public class CookieErrorException extends RuntimeException {
    public CookieErrorException() {
    }

    public CookieErrorException(String message) {
        super(message);
    }

    public CookieErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CookieErrorException(Throwable cause) {
        super(cause);
    }

    public CookieErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
