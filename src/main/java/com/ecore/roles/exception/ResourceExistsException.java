package com.ecore.roles.exception;

import static java.lang.String.format;

public class ResourceExistsException extends WithPayloadException {

    public <T> ResourceExistsException(Class<T> resource, Object payload) {
        super(format("%s already exists", resource.getSimpleName()), payload);
    }
}
