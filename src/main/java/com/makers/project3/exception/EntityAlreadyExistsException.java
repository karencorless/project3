package com.makers.project3.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    private String message;

    public EntityAlreadyExistsException () {}

    public EntityAlreadyExistsException(String entityName) {
        super(entityName + " already exists!");
        this.message = entityName + " already exists!";
    }
}
