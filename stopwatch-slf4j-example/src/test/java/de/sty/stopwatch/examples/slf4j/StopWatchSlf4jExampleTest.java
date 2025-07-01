package de.sty.stopwatch.examples.slf4j;

import de.sty.stopwatch.StopWatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StopWatchSlf4jExample to verify SLF4J integration.
 */
public class StopWatchSlf4jExampleTest {

    private static final Logger logger = LoggerFactory.getLogger(StopWatchSlf4jExampleTest.class);
    private StopWatchSlf4jExample example;

    @BeforeEach
    public void setUp() {
        // Set the SLF4J Simple logger to show all logs
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "INFO");
        
        // Create a new instance for each test
        example = new StopWatchSlf4jExample();
    }

    @AfterEach
    public void tearDown() {
        // Reset StopWatch after each test
        StopWatch.reset();
        StopWatch.disableLogging();
    }

    @Test
    public void testHandleRestRequest() {
        // Process a request
        String result = example.handleRestRequest("test-request");
        
        // Verify the result
        assertNotNull(result);
        assertTrue(result.contains("Processed"));
        
        // Verify that statistics were collected
        assertFalse(StopWatch.getStats().isEmpty());
        
        // Log the statistics for verification
        logger.info("[DEBUG_LOG] StopWatch statistics:");
        StopWatch.getStats().forEach((operation, time) -> 
            logger.info("[DEBUG_LOG] {} - {}ms", operation, time));
    }

    @Test
    public void testSlf4jIntegration() throws InterruptedException {
        // Configure StopWatch to use SLF4J
        StopWatch.setLogger((operation, message) -> 
            logger.info("[DEBUG_LOG] [StopWatch-Test] {} - {}", operation, message));
        StopWatch.enableLogging();
        
        // Perform some timed operations
        StopWatch.start("testOperation");
        Thread.sleep(50);
        StopWatch.start("nestedOperation");
        Thread.sleep(25);
        StopWatch.stop(); // Stop nested operation
        StopWatch.stop(); // Stop test operation
        
        // Verify that statistics were collected
        assertTrue(StopWatch.getStats().containsKey("testOperation"));
        assertTrue(StopWatch.getStats().containsKey("nestedOperation"));
    }
}
