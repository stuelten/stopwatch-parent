package de.sty.stopwatch.examples;

import de.sty.stopwatch.StopWatch;

/**
 * Example usage of the StopWatch class in a Spring Boot REST service scenario.
 * This is a simplified example to demonstrate the hierarchical timing capabilities.
 */
public class StopWatchExample {

    /**
     * Simulates a REST endpoint handler method.
     * In a real Spring Boot application, this would be annotated with @GetMapping, etc.
     */
    @SuppressWarnings("UnusedReturnValue")
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
            return "Error processing request";
        } finally {
            // Stop timing the entire service call and get the total time
            long totalTime = StopWatch.stop();
            System.out.println("Total service call time: " + totalTime + "ms");
            
            // Print all collected statistics
            System.out.println("Timing statistics:");
            StopWatch.getStats().forEach((operation, time) -> 
                System.out.println("  " + operation + ": " + time + "ms"));
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
            System.out.println("Persistence operation time: " + persistenceTime + "ms");
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
            System.out.println("Business logic operation time: " + businessTime + "ms");
        }
    }
    
    /**
     * Main method to demonstrate the StopWatch usage.
     */
    public static void main(String[] args) {
        StopWatchExample example = new StopWatchExample();
        example.handleRestRequest("sample-request");
        
        // Reset the StopWatch for a new request
        StopWatch.reset();
        
        // Process another request
        example.handleRestRequest("another-request");
    }
}
