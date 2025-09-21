package com.scientific.dataintegration.exceptions;

/**
 * Exception thrown when there are issues with JSON file operations.
 * This includes file reading errors, parsing errors, and format validation issues.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class JsonFileException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new JsonFileException with the specified detail message.
     * 
     * @param message the detail message explaining the cause of the exception
     */
    public JsonFileException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new JsonFileException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause the underlying cause of the exception
     */
    public JsonFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new JsonFileException with the specified cause.
     * 
     * @param cause the underlying cause of the exception
     */
    public JsonFileException(Throwable cause) {
        super(cause);
    }
}