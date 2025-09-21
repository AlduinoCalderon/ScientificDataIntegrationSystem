package com.scientific.dataintegration.core;

import com.scientific.dataintegration.exceptions.JsonFileException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Utility class for reading and parsing JSON files.
 * Uses Jackson library for robust JSON processing with error handling.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class JsonFileReader {
    
    private static final Logger LOGGER = Logger.getLogger(JsonFileReader.class.getName());
    private final ObjectMapper objectMapper;
    
    /**
     * Constructs a new JsonFileReader with default configuration.
     */
    public JsonFileReader() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Reads a JSON file and parses it into a Map structure.
     * 
     * @param filePath path to the JSON file to read
     * @return Map representation of the JSON data
     * @throws JsonFileException if the file cannot be read or parsed
     */
    public Map<String, Object> readJsonAsMap(String filePath) throws JsonFileException {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new JsonFileException("File path cannot be null or empty");
        }
        
        File file = new File(filePath);
        
        // Validate file exists and is readable
        if (!file.exists()) {
            throw new JsonFileException("JSON file not found: " + filePath);
        }
        
        if (!file.isFile()) {
            throw new JsonFileException("Path is not a file: " + filePath);
        }
        
        if (!file.canRead()) {
            throw new JsonFileException("Cannot read file: " + filePath);
        }
        
        // Validate file extension
        if (!filePath.toLowerCase().endsWith(".json")) {
            LOGGER.warning("File does not have .json extension: " + filePath);
        }
        
        // Check if file is empty
        try {
            if (Files.size(Paths.get(filePath)) == 0) {
                throw new JsonFileException("JSON file is empty: " + filePath);
            }
        } catch (IOException e) {
            throw new JsonFileException("Error checking file size: " + filePath, e);
        }
        
        LOGGER.info("Reading JSON file: " + filePath);
        
        try {
            // Parse JSON into Map
            TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {};
            Map<String, Object> jsonData = objectMapper.readValue(file, typeRef);
            
            if (jsonData == null) {
                throw new JsonFileException("JSON file contains null data: " + filePath);
            }
            
            LOGGER.info("Successfully parsed JSON file with " + jsonData.size() + " top-level keys");
            return jsonData;
            
        } catch (IOException e) {
            String errorMessage = "Failed to parse JSON file: " + filePath;
            if (e.getMessage().contains("Unexpected character")) {
                errorMessage += ". The file may contain invalid JSON syntax.";
            } else if (e.getMessage().contains("Unexpected end-of-input")) {
                errorMessage += ". The JSON file appears to be incomplete.";
            }
            throw new JsonFileException(errorMessage, e);
        }
    }
    
    /**
     * Validates if a file contains valid JSON without fully parsing it.
     * 
     * @param filePath path to the JSON file to validate
     * @return true if the file contains valid JSON, false otherwise
     */
    public boolean isValidJson(String filePath) {
        try {
            readJsonAsMap(filePath);
            return true;
        } catch (JsonFileException e) {
            LOGGER.warning("JSON validation failed for " + filePath + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets basic information about a JSON file without fully parsing it.
     * 
     * @param filePath path to the JSON file
     * @return String containing file information
     * @throws JsonFileException if the file cannot be accessed
     */
    public String getFileInfo(String filePath) throws JsonFileException {
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new JsonFileException("File not found: " + filePath);
        }
        
        try {
            long fileSize = Files.size(Paths.get(filePath));
            String readableSize = formatFileSize(fileSize);
            
            return String.format("File: %s%nSize: %s (%d bytes)%nPath: %s", 
                    file.getName(), readableSize, fileSize, file.getAbsolutePath());
                    
        } catch (IOException e) {
            throw new JsonFileException("Error reading file information: " + filePath, e);
        }
    }
    
    /**
     * Formats file size in human-readable format.
     * 
     * @param bytes file size in bytes
     * @return formatted file size string
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
}