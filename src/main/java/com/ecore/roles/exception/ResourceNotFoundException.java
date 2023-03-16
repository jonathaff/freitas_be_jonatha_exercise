package com.ecore.roles.exception;

import java.util.UUID;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {

    public <T> ResourceNotFoundException(Class<T> resource, UUID id) {
        super(format("%s %s not found", resource.getSimpleName(), id));
    }

    public <T> ResourceNotFoundException(Class<T> resource, UUID id, UUID id2) {
        super(format("%s Combined UUIDs (%s and %s) not found", resource.getSimpleName(), id, id2));
    }
}
