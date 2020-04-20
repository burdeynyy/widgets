package com.miro.exception;

/**
 * Exception for the case of not founding the requested resource.
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(final Object id, final Class<?> clazz) {
        super(String.format("Object of type %s with id = %s is not found", clazz.getSimpleName(), id));
    }
}
