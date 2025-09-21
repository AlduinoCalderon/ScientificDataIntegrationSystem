package com.scientific.dataintegration.exceptions;

/**
 * Exception thrown when there are issues with CSV file operations.
 * This includes file writing errors, formatting issues, and validation problems.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class CsvFileException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new CsvFileException with the specified detail message.
     * 
     * @param message the detail message explaining the cause of the exception
     */
    public CsvFileException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new CsvFileException with the specified detail message and cause.
     * 
     * @param message the detail message explaining the cause of the exception
     * @param cause the underlying cause of the exception
     */
    public CsvFileException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new CsvFileException with the specified cause.
     * 
     * @param cause the underlying cause of the exception
     */
    public CsvFileException(Throwable cause) {
        super(cause);
    }
}