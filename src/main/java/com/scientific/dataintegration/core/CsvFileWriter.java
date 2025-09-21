package com.scientific.dataintegration.core;

import com.scientific.dataintegration.exceptions.CsvFileException;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for writing data to CSV files.
 * Uses OpenCSV library for reliable CSV formatting and writing.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class CsvFileWriter {
    
    private static final Logger LOGGER = Logger.getLogger(CsvFileWriter.class.getName());
    
    /**
     * Writes data to a CSV file with headers.
     * 
     * @param filePath path to the output CSV file
     * @param headers array of column headers
     * @param data list of data rows, where each row is a string array
     * @throws CsvFileException if the file cannot be written
     */
    public void writeWithHeaders(String filePath, String[] headers, List<String[]> data) 
            throws CsvFileException {
        
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvFileException("File path cannot be null or empty");
        }
        
        if (headers == null || headers.length == 0) {
            throw new CsvFileException("Headers cannot be null or empty");
        }
        
        if (data == null) {
            throw new CsvFileException("Data cannot be null");
        }
        
        // Validate file path and create directories if needed
        try {
            Path outputPath = Paths.get(filePath);
            Path parentDir = outputPath.getParent();
            
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                LOGGER.info("Created directory: " + parentDir);
            }
            
        } catch (IOException e) {
            throw new CsvFileException("Failed to create output directory for: " + filePath, e);
        }
        
        LOGGER.info("Writing CSV file: " + filePath);
        LOGGER.info("Headers: " + headers.length + " columns");
        LOGGER.info("Data: " + data.size() + " rows");
        
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            // Write headers
            csvWriter.writeNext(headers);
            
            // Write data rows
            for (String[] row : data) {
                if (row.length != headers.length) {
                    LOGGER.warning("Row has " + row.length + " columns but expected " + headers.length);
                    // Pad or truncate row to match header count
                    String[] adjustedRow = new String[headers.length];
                    System.arraycopy(row, 0, adjustedRow, 0, Math.min(row.length, headers.length));
                    for (int i = row.length; i < headers.length; i++) {
                        adjustedRow[i] = "";
                    }
                    csvWriter.writeNext(adjustedRow);
                } else {
                    csvWriter.writeNext(row);
                }
            }
            
            LOGGER.info("Successfully wrote CSV file with " + (data.size() + 1) + " total rows (including header)");
            
        } catch (IOException e) {
            throw new CsvFileException("Failed to write CSV file: " + filePath, e);
        }
    }
    
    /**
     * Writes data to a CSV file without headers.
     * 
     * @param filePath path to the output CSV file
     * @param data list of data rows, where each row is a string array
     * @throws CsvFileException if the file cannot be written
     */
    public void writeWithoutHeaders(String filePath, List<String[]> data) throws CsvFileException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvFileException("File path cannot be null or empty");
        }
        
        if (data == null) {
            throw new CsvFileException("Data cannot be null");
        }
        
        // Validate file path and create directories if needed
        try {
            Path outputPath = Paths.get(filePath);
            Path parentDir = outputPath.getParent();
            
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
                LOGGER.info("Created directory: " + parentDir);
            }
            
        } catch (IOException e) {
            throw new CsvFileException("Failed to create output directory for: " + filePath, e);
        }
        
        LOGGER.info("Writing CSV file without headers: " + filePath);
        LOGGER.info("Data: " + data.size() + " rows");
        
        try (FileWriter fileWriter = new FileWriter(filePath);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            // Write data rows only
            for (String[] row : data) {
                csvWriter.writeNext(row);
            }
            
            LOGGER.info("Successfully wrote CSV file with " + data.size() + " rows");
            
        } catch (IOException e) {
            throw new CsvFileException("Failed to write CSV file: " + filePath, e);
        }
    }
    
    /**
     * Appends data to an existing CSV file.
     * 
     * @param filePath path to the CSV file
     * @param data list of data rows to append
     * @throws CsvFileException if the file cannot be written
     */
    public void appendData(String filePath, List<String[]> data) throws CsvFileException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new CsvFileException("File path cannot be null or empty");
        }
        
        if (data == null || data.isEmpty()) {
            LOGGER.info("No data to append");
            return;
        }
        
        LOGGER.info("Appending " + data.size() + " rows to CSV file: " + filePath);
        
        try (FileWriter fileWriter = new FileWriter(filePath, true);
             CSVWriter csvWriter = new CSVWriter(fileWriter)) {
            
            for (String[] row : data) {
                csvWriter.writeNext(row);
            }
            
            LOGGER.info("Successfully appended " + data.size() + " rows");
            
        } catch (IOException e) {
            throw new CsvFileException("Failed to append to CSV file: " + filePath, e);
        }
    }
    
    /**
     * Validates if a file path is suitable for CSV writing.
     * 
     * @param filePath path to validate
     * @return true if the path is valid for writing
     */
    public boolean isValidOutputPath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path path = Paths.get(filePath);
            Path parent = path.getParent();
            
            // Check if parent directory exists or can be created
            if (parent != null && !Files.exists(parent)) {
                return Files.isWritable(parent.getParent());
            }
            
            // Check if we can write to the directory
            return parent == null || Files.isWritable(parent);
            
        } catch (Exception e) {
            LOGGER.warning("Invalid output path: " + filePath + " - " + e.getMessage());
            return false;
        }
    }
}