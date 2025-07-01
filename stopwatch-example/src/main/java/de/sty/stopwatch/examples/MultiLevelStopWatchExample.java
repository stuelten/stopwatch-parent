package de.sty.stopwatch.examples;

import de.sty.stopwatch.StopWatch;

/**
 * Example demonstrating multiple levels of time keeping with StopWatch.
 * This example shows how to use StopWatch for deep nesting of operations
 * (4+ levels) and how to analyze the timing data.
 */
public class MultiLevelStopWatchExample {

    /**
     * Main method to run the example.
     */
    public static void main(String[] args) throws InterruptedException {
        MultiLevelStopWatchExample example = new MultiLevelStopWatchExample();
        example.executeComplexOperation();
        
        // Print all collected statistics
        System.out.println("\nFinal timing statistics:");
        StopWatch.getStats().forEach((operation, time) -> 
            System.out.println("  " + operation + ": " + time + "ms"));
    }
    
    /**
     * Executes a complex operation with multiple levels of nested operations.
     */
    public void executeComplexOperation() throws InterruptedException {
        // Level 1 - Top level operation
        StopWatch.start("level1-complexOperation");
        System.out.println("Starting level 1: complexOperation");
        
        try {
            // Some work at level 1
            Thread.sleep(50);
            
            // Level 2 - First major sub-operation
            executeDataProcessing();
            
            // Some more work at level 1
            Thread.sleep(30);
            
            // Level 2 - Second major sub-operation
            executeBusinessLogic();
            
            // Final work at level 1
            Thread.sleep(20);
            
        } finally {
            // Stop the top level operation
            long totalTime = StopWatch.stop();
            System.out.println("Completed level 1: complexOperation - " + totalTime + "ms");
        }
    }
    
    /**
     * Level 2 operation for data processing.
     */
    private void executeDataProcessing() throws InterruptedException {
        // Level 2 - Data processing operation
        StopWatch.start("level2-dataProcessing");
        System.out.println("  Starting level 2: dataProcessing");
        
        try {
            // Some work at level 2
            Thread.sleep(40);
            
            // Level 3 - Data loading sub-operation
            executeDataLoading();
            
            // More work at level 2
            Thread.sleep(30);
            
            // Level 3 - Data transformation sub-operation
            executeDataTransformation();
            
        } finally {
            // Stop the level 2 operation
            long time = StopWatch.stop();
            System.out.println("  Completed level 2: dataProcessing - " + time + "ms");
        }
    }
    
    /**
     * Level 3 operation for data loading.
     */
    private void executeDataLoading() throws InterruptedException {
        // Level 3 - Data loading operation
        StopWatch.start("level3-dataLoading");
        System.out.println("    Starting level 3: dataLoading");
        
        try {
            // Some work at level 3
            Thread.sleep(60);
            
            // Level 4 - Database query sub-operation
            executeDatabaseQuery();
            
            // More work at level 3
            Thread.sleep(25);
            
        } finally {
            // Stop the level 3 operation
            long time = StopWatch.stop();
            System.out.println("    Completed level 3: dataLoading - " + time + "ms");
        }
    }
    
    /**
     * Level 4 operation for database query.
     */
    private void executeDatabaseQuery() throws InterruptedException {
        // Level 4 - Database query operation
        StopWatch.start("level4-databaseQuery");
        System.out.println("      Starting level 4: databaseQuery");
        
        try {
            // Work at level 4
            Thread.sleep(100);
            
            // Level 5 - Connection pool operation
            executeConnectionPoolOperation();
            
        } finally {
            // Stop the level 4 operation
            long time = StopWatch.stop();
            System.out.println("      Completed level 4: databaseQuery - " + time + "ms");
        }
    }
    
    /**
     * Level 5 operation for connection pool.
     */
    private void executeConnectionPoolOperation() throws InterruptedException {
        // Level 5 - Connection pool operation
        StopWatch.start("level5-connectionPool");
        System.out.println("        Starting level 5: connectionPool");
        
        try {
            // Work at level 5
            Thread.sleep(50);
            
        } finally {
            // Stop the level 5 operation
            long time = StopWatch.stop();
            System.out.println("        Completed level 5: connectionPool - " + time + "ms");
        }
    }
    
    /**
     * Level 3 operation for data transformation.
     */
    private void executeDataTransformation() throws InterruptedException {
        // Level 3 - Data transformation operation
        StopWatch.start("level3-dataTransformation");
        System.out.println("    Starting level 3: dataTransformation");
        
        try {
            // Work at level 3
            Thread.sleep(70);
            
        } finally {
            // Stop the level 3 operation
            long time = StopWatch.stop();
            System.out.println("    Completed level 3: dataTransformation - " + time + "ms");
        }
    }
    
    /**
     * Level 2 operation for business logic.
     */
    private void executeBusinessLogic() throws InterruptedException {
        // Level 2 - Business logic operation
        StopWatch.start("level2-businessLogic");
        System.out.println("  Starting level 2: businessLogic");
        
        try {
            // Some work at level 2
            Thread.sleep(35);
            
            // Level 3 - Calculation sub-operation
            executeCalculation();
            
            // More work at level 2
            Thread.sleep(25);
            
        } finally {
            // Stop the level 2 operation
            long time = StopWatch.stop();
            System.out.println("  Completed level 2: businessLogic - " + time + "ms");
        }
    }
    
    /**
     * Level 3 operation for calculation.
     */
    private void executeCalculation() throws InterruptedException {
        // Level 3 - Calculation operation
        StopWatch.start("level3-calculation");
        System.out.println("    Starting level 3: calculation");
        
        try {
            // Work at level 3
            Thread.sleep(80);
            
        } finally {
            // Stop the level 3 operation
            long time = StopWatch.stop();
            System.out.println("    Completed level 3: calculation - " + time + "ms");
        }
    }
}
