package de.sty.stopwatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the StopWatch class.
 */
public class StopWatchTest {

    @BeforeEach
    void setUp() {
        // Reset the StopWatch before each test
        StopWatch.reset();
    }

    @Test
    void testBasicTiming() throws InterruptedException {
        // Start timing an operation
        StopWatch.start("basicOperation");

        // Sleep for a known duration
        Thread.sleep(100);

        // Stop timing and get the elapsed time
        long elapsedTime = StopWatch.stop();

        // Verify that the elapsed time is at least the sleep duration
        assertTrue(elapsedTime >= 100, "Elapsed time should be at least 100ms");

        // Verify that the elapsed time is reasonable (not much more than the sleep duration)
        assertTrue(elapsedTime < 200, "Elapsed time should be less than 200ms");
    }

    @Test
    void testHierarchicalTiming() throws InterruptedException {
        // Start timing a parent operation
        StopWatch.start("parentOperation");
        Thread.sleep(50);

        // Start timing a child operation
        StopWatch.start("childOperation1");
        Thread.sleep(100);
        long childTime1 = StopWatch.stop(); // Stop child operation

        // Start another child operation
        StopWatch.start("childOperation2");
        Thread.sleep(150);
        long childTime2 = StopWatch.stop(); // Stop child operation

        // Sleep more in the parent operation
        Thread.sleep(50);

        // Stop the parent operation
        long parentTime = StopWatch.stop();

        // Verify child timings
        assertTrue(childTime1 >= 100, "Child 1 time should be at least 100ms");
        assertTrue(childTime2 >= 150, "Child 2 time should be at least 150ms");

        // Verify parent timing includes all operations
        assertTrue(parentTime >= 350, "Parent time should be at least 350ms");

        // Get statistics and verify
        Map<String, Long> stats = StopWatch.getStats();
        assertTrue(stats.containsKey("parentOperation"), "Stats should contain parent operation");
        assertTrue(stats.containsKey("childOperation1"), "Stats should contain child operation 1");
        assertTrue(stats.containsKey("childOperation2"), "Stats should contain child operation 2");
    }

    @Test
    void testGetElapsedTimeWithoutStopping() throws InterruptedException {
        StopWatch.start("ongoingOperation");

        // Sleep for a known duration
        Thread.sleep(100);

        // Get elapsed time without stopping
        long elapsedTime1 = StopWatch.getElapsedTime();
        assertTrue(elapsedTime1 >= 100, "Elapsed time should be at least 100ms");

        // Sleep more and check again
        Thread.sleep(100);
        long elapsedTime2 = StopWatch.getElapsedTime();
        assertTrue(elapsedTime2 >= 200, "Elapsed time should be at least 200ms");
        assertTrue(elapsedTime2 > elapsedTime1, "Second elapsed time should be greater than first");

        // Finally stop
        long finalTime = StopWatch.stop();
        assertTrue(finalTime >= 200, "Final time should be at least 200ms");
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threadCount = 50;
        try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {

            CountDownLatch latch = new CountDownLatch(threadCount);

            // Start multiple threads that use StopWatch concurrently
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        // Each thread starts its own timing operation
                        StopWatch.start("thread" + threadId);
                        Thread.sleep(100);

                        // Each thread starts a child operation
                        StopWatch.start("childInThread" + threadId);
                        Thread.sleep(50);
                        StopWatch.stop(); // Stop child

                        StopWatch.stop(); // Stop parent
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Wait for all threads to complete
            assertTrue(latch.await(5, TimeUnit.SECONDS), "All threads should complete within timeout");
        }

        // Verify that stats contains entries for all threads
        Map<String, Long> stats = StopWatch.getStats();
        for (int i = 0; i < threadCount; i++) {
            assertTrue(stats.containsKey("thread" + i), "Stats should contain thread " + i);
            assertTrue(stats.containsKey("childInThread" + i), "Stats should contain child in thread " + i);
        }
    }

    @Test
    void testReset() throws InterruptedException {
        // Start and stop an operation
        StopWatch.start("operationBeforeReset");
        Thread.sleep(50);
        StopWatch.stop();

        // Verify it's in the stats
        Map<String, Long> statsBefore = StopWatch.getStats();
        assertTrue(statsBefore.containsKey("operationBeforeReset"), "Stats should contain operation before reset");

        // Reset the StopWatch for the current thread
        StopWatch.reset();

        // Start a new operation
        StopWatch.start("operationAfterReset");
        Thread.sleep(50);
        StopWatch.stop();

        // The stats should still contain both operations
        Map<String, Long> statsAfter = StopWatch.getStats();
        assertTrue(statsAfter.containsKey("operationBeforeReset"), "Stats should still contain operation before reset");
        assertTrue(statsAfter.containsKey("operationAfterReset"), "Stats should contain operation after reset");
    }

    @Test
    void testLogging() throws InterruptedException {
        // Create a list to capture log messages
        List<String> logMessages = new ArrayList<>();

        // Set a custom logger that adds messages to the list
        StopWatch.setLogger((operation, message) ->
                logMessages.add(operation + ": " + message));

        // Initially logging is disabled, so no messages should be captured
        StopWatch.start("operationWithLoggingDisabled");
        Thread.sleep(50);
        StopWatch.stop();

        // Verify no messages were captured
        assertTrue(logMessages.isEmpty(), "No log messages should be captured when logging is disabled");

        // Enable logging
        StopWatch.enableLogging();
        assertTrue(StopWatch.isLoggingEnabled(), "Logging should be enabled");

        // Perform some timing operations
        StopWatch.start("parentOperation");
        Thread.sleep(50);

        StopWatch.start("childOperation");
        Thread.sleep(100);
        StopWatch.stop(); // Stop child

        StopWatch.stop(); // Stop parent

        // Verify that the expected log messages were captured
        assertEquals(4, logMessages.size(), "Four log messages should be captured");
        assertTrue(logMessages.get(0).contains("parentOperation: Started"),
                "First message should be about starting parentOperation");
        assertTrue(logMessages.get(1).contains("childOperation: Started"),
                "Second message should be about starting childOperation");
        assertTrue(logMessages.get(2).contains("childOperation: Finished"),
                "Third message should be about finishing childOperation");
        assertTrue(logMessages.get(3).contains("parentOperation: Finished"),
                "Fourth message should be about finishing parentOperation");

        // Clear the log messages
        logMessages.clear();

        // Disable logging
        StopWatch.disableLogging();
        assertFalse(StopWatch.isLoggingEnabled(), "Logging should be disabled");

        // Perform another timing operation
        StopWatch.start("operationAfterDisablingLogging");
        Thread.sleep(50);
        StopWatch.stop();

        // Verify no messages were captured after disabling logging
        assertTrue(logMessages.isEmpty(), "No log messages should be captured after disabling logging");
    }

    @Test
    void testMultipleLevelsOfTimeKeeping() throws InterruptedException {
        // Level 1 - Top level operation
        StopWatch.start("level1");
        Thread.sleep(50);

        // Level 2 - First child operation
        StopWatch.start("level2A");
        Thread.sleep(60);

        // Level 3 - Grandchild operation
        StopWatch.start("level3A");
        Thread.sleep(70);

        // Level 4 - Great-grandchild operation
        StopWatch.start("level4");
        Thread.sleep(40);
        long level4Time = StopWatch.stop();

        // Continue at level 3
        Thread.sleep(30);
        long level3ATime = StopWatch.stop();

        // Level 3 - Another grandchild operation
        StopWatch.start("level3B");
        Thread.sleep(50);
        long level3BTime = StopWatch.stop();

        // Continue at level 2
        Thread.sleep(20);
        long level2ATime = StopWatch.stop();

        // Level 2 - Second child operation
        StopWatch.start("level2B");
        Thread.sleep(55);
        long level2BTime = StopWatch.stop();

        // Continue at level 1
        Thread.sleep(25);
        long level1Time = StopWatch.stop();

        // Verify timing relationships
        assertTrue(level4Time >= 40, "Level 4 time should be at least 40ms");
        assertTrue(level3ATime >= 70 + 40 + 30, "Level 3A time should include level 4 time");
        assertTrue(level3BTime >= 50, "Level 3B time should be at least 50ms");
        assertTrue(level2ATime >= 60 + level3ATime + level3BTime + 20, "Level 2A time should include all level 3 times");
        assertTrue(level2BTime >= 55, "Level 2B time should be at least 55ms");
        assertTrue(level1Time >= 50 + level2ATime + level2BTime + 25, "Level 1 time should include all level 2 times");

        // Verify all operations are in the stats
        Map<String, Long> stats = StopWatch.getStats();
        assertTrue(stats.containsKey("level1"), "Stats should contain level 1");
        assertTrue(stats.containsKey("level2A"), "Stats should contain level 2A");
        assertTrue(stats.containsKey("level2B"), "Stats should contain level 2B");
        assertTrue(stats.containsKey("level3A"), "Stats should contain level 3A");
        assertTrue(stats.containsKey("level3B"), "Stats should contain level 3B");
        assertTrue(stats.containsKey("level4"), "Stats should contain level 4");

        // Verify the hierarchical relationship in timing values
        assertTrue(stats.get("level1") >= stats.get("level2A") + stats.get("level2B"),
                "Level 1 time should be greater than or equal to the sum of level 2 times");
        assertTrue(stats.get("level2A") >= stats.get("level3A") + stats.get("level3B"),
                "Level 2A time should be greater than or equal to the sum of level 3 times");
        assertTrue(stats.get("level3A") >= stats.get("level4"),
                "Level 3A time should be greater than or equal to level 4 time");
    }
}
