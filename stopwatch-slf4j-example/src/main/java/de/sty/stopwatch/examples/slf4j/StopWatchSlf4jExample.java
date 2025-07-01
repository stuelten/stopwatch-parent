package de.sty.stopwatch.examples.slf4j;

import de.sty.stopwatch.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example usage of the StopWatch class with SLF4J logging integration.
 * This demonstrates how to use StopWatch with SLF4J for logging timing information.
 */
public class StopWatchSlf4jExample {
    
    private static final Logger logger = LoggerFactory.getLogger(StopWatchSlf4jExample.class);
    
    /**
     * Initializes the StopWatch with SLF4J logger.
     */
    public StopWatchSlf4jExample() {
        // Configure StopWatch to use SLF4J for logging
        StopWatch.setLogger((operation, message) -> 
            logger.info("[StopWatch] {} - {}", operation, message));
        
        // Enable StopWatch logging
        StopWatch.enableLogging();
    }
    
    /**
     * Simulates a REST endpoint handler method.
     */
    public String handleRestRequest(String requestParam) {
        // Start timing the entire service call
        StopWatch.start("restServiceCall");
        
        try {
            // Simulate some initial processing
            Thread.sleep(50);
            
            // Call persistence layer and measure its execution time
            String persistenceResult = performPersistenceOperation(requestParam);
            
            // Call business logic layer and measure its execution time
            String businessResult = performBusinessLogic(persistenceResult);
            
            // Simulate some final processing
            Thread.sleep(50);
            
            return "Processed: " + businessResult;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Error processing request", e);
            return "Error processing request";
        } finally {
            // Stop timing the entire service call and get the total time
            long totalTime = StopWatch.stop();
            logger.info("Total service call time: {}ms", totalTime);
            
            // Log all collected statistics
            logger.info("Timing statistics:");
            StopWatch.getStats().forEach((operation, time) -> 
                logger.info("  {}: {}ms", operation, time));
        }
    }
    
    /**
     * Simulates a persistence layer operation (e.g., database access).
     */
    private String performPersistenceOperation(String input) throws InterruptedException {
        // Start timing the persistence operation
        StopWatch.start("persistenceOperation");
        
        try {
            // Simulate database access
            Thread.sleep(200);
            
            // Simulate a more specific database operation
            StopWatch.start("databaseQuery");
            Thread.sleep(150);
            StopWatch.stop(); // Stop the specific database operation timing
            
            return "Data for: " + input;
        } finally {
            // Stop timing the persistence operation and get its time
            long persistenceTime = StopWatch.stop();
            logger.info("Persistence operation time: {}ms", persistenceTime);
        }
    }
    
    /**
     * Simulates business logic processing.
     */
    private String performBusinessLogic(String input) throws InterruptedException {
        // Start timing the business logic operation
        StopWatch.start("businessLogicOperation");
        
        try {
            // Simulate some business logic processing
            Thread.sleep(100);
            
            // Simulate a more specific calculation
            StopWatch.start("calculation");
            Thread.sleep(120);
            StopWatch.stop(); // Stop the specific calculation timing
            
            // Simulate another specific operation
            StopWatch.start("dataTransformation");
            Thread.sleep(80);
            StopWatch.stop(); // Stop the specific data transformation timing
            
            return "Processed " + input;
        } finally {
            // Stop timing the business logic operation and get its time
            long businessTime = StopWatch.stop();
            logger.info("Business logic operation time: {}ms", businessTime);
        }
    }
    
    /**
     * Main method to demonstrate the StopWatch usage with SLF4J.
     */
    public static void main(String[] args) {
        // Set the SLF4J Simple logger to show all logs
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        
        StopWatchSlf4jExample example = new StopWatchSlf4jExample();
        example.handleRestRequest("sample-request");
        
        // Reset the StopWatch for a new request
        StopWatch.reset();
        
        // Process another request
        example.handleRestRequest("another-request");
    }
}
