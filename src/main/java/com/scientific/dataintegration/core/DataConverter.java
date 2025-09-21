package com.scientific.dataintegration.core;

import com.scientific.dataintegration.exceptions.CsvFileException;
import com.scientific.dataintegration.exceptions.JsonFileException;

import java.util.*;
import java.util.logging.Logger;

/**
 * Specialized data converter for scientific data processing.
 * Handles conversion from JSON to CSV with specific scientific data considerations.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class DataConverter {
    
    private static final Logger LOGGER = Logger.getLogger(DataConverter.class.getName());
    
    private final JsonFileReader jsonReader;
    private final CsvFileWriter csvWriter;
    
    /**
     * Constructs a new DataConverter with default configuration.
     */
    public DataConverter() {
        this.jsonReader = new JsonFileReader();
        this.csvWriter = new CsvFileWriter();
    }
    
    /**
     * Converts a JSON file containing scientific data to CSV format.
     * 
     * @param jsonFilePath path to the input JSON file
     * @param csvFilePath path to the output CSV file
     * @throws JsonFileException if the JSON file cannot be read or parsed
     * @throws CsvFileException if the CSV file cannot be written
     */
    public void convertJsonToCsv(String jsonFilePath, String csvFilePath) 
            throws JsonFileException, CsvFileException {
        
        LOGGER.info("Starting conversion: " + jsonFilePath + " → " + csvFilePath);
        
        // Read JSON data
        Map<String, Object> jsonData = jsonReader.readJsonAsMap(jsonFilePath);
        
        // Convert to CSV format
        if (isArrayData(jsonData)) {
            convertArrayData(jsonData, csvFilePath);
        } else {
            convertObjectData(jsonData, csvFilePath);
        }
        
        LOGGER.info("Conversion completed successfully");
    }
    
    /**
     * Checks if the JSON data represents an array of objects.
     * 
     * @param jsonData the parsed JSON data
     * @return true if the data is an array, false otherwise
     */
    private boolean isArrayData(Map<String, Object> jsonData) {
        return jsonData.values().stream()
                .anyMatch(value -> value instanceof List);
    }
    
    /**
     * Converts JSON array data to CSV format.
     * 
     * @param jsonData the parsed JSON data
     * @param csvFilePath path to the output CSV file
     * @throws CsvFileException if the CSV file cannot be written
     */
    @SuppressWarnings("unchecked")
    private void convertArrayData(Map<String, Object> jsonData, String csvFilePath) 
            throws CsvFileException {
        
        // Find the main data array
        List<Map<String, Object>> dataList = null;
        String dataKey = null;
        
        for (Map.Entry<String, Object> entry : jsonData.entrySet()) {
            if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                if (!list.isEmpty() && list.get(0) instanceof Map) {
                    dataList = (List<Map<String, Object>>) list;
                    dataKey = entry.getKey();
                    break;
                }
            }
        }
        
        if (dataList == null || dataList.isEmpty()) {
            throw new CsvFileException("No valid array data found in JSON file");
        }
        
        LOGGER.info("Found data array '" + dataKey + "' with " + dataList.size() + " records");
        
        // Extract headers from the first object
        Set<String> headerSet = new LinkedHashSet<>();
        for (Map<String, Object> record : dataList) {
            headerSet.addAll(record.keySet());
        }
        
        String[] headers = headerSet.toArray(new String[0]);
        
        // Convert data to string arrays
        List<String[]> csvData = new ArrayList<>();
        for (Map<String, Object> record : dataList) {
            String[] row = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                Object value = record.get(headers[i]);
                row[i] = formatValue(value);
            }
            csvData.add(row);
        }
        
        // Write to CSV
        csvWriter.writeWithHeaders(csvFilePath, headers, csvData);
        LOGGER.info("Written " + csvData.size() + " records to CSV file");
    }
    
    /**
     * Converts JSON object data to CSV format.
     * 
     * @param jsonData the parsed JSON data
     * @param csvFilePath path to the output CSV file
     * @throws CsvFileException if the CSV file cannot be written
     */
    private void convertObjectData(Map<String, Object> jsonData, String csvFilePath) 
            throws CsvFileException {
        
        // Create headers and single data row from the object
        List<String> headers = new ArrayList<>();
        List<String> values = new ArrayList<>();
        
        flattenObject("", jsonData, headers, values);
        
        String[] headerArray = headers.toArray(new String[0]);
        String[] valuesArray = values.toArray(new String[0]);
        List<String[]> csvData = new ArrayList<>();
        csvData.add(valuesArray);
        
        csvWriter.writeWithHeaders(csvFilePath, headerArray, csvData);
        LOGGER.info("Written object data with " + headers.size() + " fields to CSV file");
    }
    
    /**
     * Recursively flattens nested objects into flat key-value pairs.
     * 
     * @param prefix current key prefix
     * @param obj object to flatten
     * @param headers list to store flattened headers
     * @param values list to store flattened values
     */
    @SuppressWarnings("unchecked")
    private void flattenObject(String prefix, Object obj, List<String> headers, List<String> values) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenObject(key, entry.getValue(), headers, values);
            }
        } else {
            headers.add(prefix);
            values.add(formatValue(obj));
        }
    }
    
    /**
     * Formats a value for CSV output, handling special scientific data types.
     * 
     * @param value the value to format
     * @return formatted string representation
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.stream()
                    .map(this::formatValue)
                    .reduce((a, b) -> a + "; " + b)
                    .orElse("");
        }
        
        if (value instanceof Double || value instanceof Float) {
            // Handle scientific notation for large/small numbers
            double doubleValue = ((Number) value).doubleValue();
            if (Math.abs(doubleValue) >= 1e6 || (Math.abs(doubleValue) <= 1e-3 && doubleValue != 0)) {
                return String.format("%.6E", doubleValue);
            }
        }
        
        return value.toString();
    }
}