package com.ecore.roles.exception;

public class WithPayloadException extends RuntimeException {
    private final Object payload;

    public WithPayloadException(String message, Object payload) {
        super(message);
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }
}
