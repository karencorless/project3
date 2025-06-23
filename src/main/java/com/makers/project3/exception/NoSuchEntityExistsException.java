package com.makers.project3.exception;

public class NoSuchEntityExistsException extends RuntimeException {
    private String message;

    public NoSuchEntityExistsException() {}

    public NoSuchEntityExistsException(String modelName) {
        super("No such " + modelName + " exists!");
        this.message = "No such " + modelName + " exists!";
    }
}
