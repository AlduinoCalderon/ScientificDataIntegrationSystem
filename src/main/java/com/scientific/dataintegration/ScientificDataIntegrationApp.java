package com.scientific.dataintegration;

import com.scientific.dataintegration.core.DataConverter;
import com.scientific.dataintegration.exceptions.CsvFileException;
import com.scientific.dataintegration.exceptions.JsonFileException;

import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Main application class for Scientific Data Integration System.
 * This application automates the conversion of JSON scientific data to CSV format.
 * 
 * @author AlduinoCalderon
 * @version 1.0
 */
public class ScientificDataIntegrationApp {
    
    private static final Logger LOGGER = Logger.getLogger(ScientificDataIntegrationApp.class.getName());
    
    /**
     * Main entry point for the Scientific Data Integration System.
     * 
     * @param args command line arguments: [input_json_file] [output_csv_file]
     */
    public static void main(String[] args) {
        LOGGER.info("Starting Scientific Data Integration System...");
        
        if (args.length == 2) {
            // Command line mode
            processFiles(args[0], args[1]);
        } else {
            // Interactive mode
            runInteractiveMode();
        }
    }
    
    /**
     * Processes files in command line mode.
     * 
     * @param inputJsonFile path to input JSON file
     * @param outputCsvFile path to output CSV file
     */
    private static void processFiles(String inputJsonFile, String outputCsvFile) {
        try {
            DataConverter converter = new DataConverter();
            converter.convertJsonToCsv(inputJsonFile, outputCsvFile);
            LOGGER.info("Conversion completed successfully!");
            System.out.println("✅ Conversion completed: " + inputJsonFile + " → " + outputCsvFile);
        } catch (JsonFileException | CsvFileException e) {
            LOGGER.severe("Conversion failed: " + e.getMessage());
            System.err.println("❌ Error: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Runs the application in interactive mode.
     */
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Scientific Data Integration System ===");
        System.out.println("JSON to CSV Converter for Scientific Data");
        System.out.println();
        
        while (true) {
            try {
                System.out.print("Enter JSON file path (or 'quit' to exit): ");
                String jsonFile = scanner.nextLine().trim();
                
                if ("quit".equalsIgnoreCase(jsonFile)) {
                    break;
                }
                
                System.out.print("Enter CSV output file path: ");
                String csvFile = scanner.nextLine().trim();
                
                DataConverter converter = new DataConverter();
                converter.convertJsonToCsv(jsonFile, csvFile);
                
                System.out.println("✅ Conversion completed successfully!");
                System.out.println();
                
            } catch (JsonFileException | CsvFileException e) {
                System.err.println("❌ Error: " + e.getMessage());
                System.out.println();
            } catch (Exception e) {
                System.err.println("❌ Unexpected error: " + e.getMessage());
                System.out.println();
            }
        }
        
        scanner.close();
        System.out.println("👋 Thank you for using Scientific Data Integration System!");
    }
}