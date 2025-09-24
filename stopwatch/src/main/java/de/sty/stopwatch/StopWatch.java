package de.sty.stopwatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

/**
 * A thread-safe stopwatch for measuring execution times with support for hierarchical timing.
 * This class allows tracking time for different operations within a call hierarchy,
 * such as service calls, persistence operations, and business logic calculations.
 */
public class StopWatch {

    // Thread-local storage to maintain separate timing contexts for each thread
    private static final ThreadLocal<TimingContext> CURRENT_CONTEXT = ThreadLocal.withInitial(TimingContext::new);

    // Store all timing data by name for statistics
    private static final ConcurrentHashMap<String, AtomicLong> timingStats = new ConcurrentHashMap<>();

    // Logging control
    private static volatile boolean loggingEnabled = false;
    private static volatile BiConsumer<String, String> logger = (operation, message) -> System.out.println("[StopWatch] " + operation + ": " + message);

    /**
     * Enables logging of start and stop operations.
     */
    public static void enableLogging() {
        loggingEnabled = true;
    }

    /**
     * Disables logging of start and stop operations.
     */
    public static void disableLogging() {
        loggingEnabled = false;
    }

    /**
     * Sets a custom logger implementation.
     * 
     * @param customLogger A BiConsumer that takes operation name and message
     */
    public static void setLogger(BiConsumer<String, String> customLogger) {
        if (customLogger != null) {
            logger = customLogger;
        }
    }

    /**
     * Checks if logging is currently enabled.
     * 
     * @return true if logging is enabled, false otherwise
     */
    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Starts timing an operation with the given name.
     * If there's already an active timing operation, this becomes a child operation.
     *
     * @param name The name of the operation to time
     * @return The current StopWatch instance for method chaining
     */
    public static StopWatch start(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("StopWatch.start name must not be null or blank");
        }
        TimingContext context = CURRENT_CONTEXT.get();
        context.startTiming(name);

        // Log start if logging is enabled
        if (loggingEnabled) {
            logger.accept(name, "Started");
        }

        return getInstance();
    }

    /**
     * Stops timing the current operation and records its duration.
     * If this operation has child operations, they will be included in the total time.
     *
     * @return The elapsed time in milliseconds
     */
    public static long stop() {
        TimingContext context = CURRENT_CONTEXT.get();
        long elapsedTime = context.stopTiming();

        // Log stop if logging is enabled
        if (loggingEnabled && context.getLastStoppedNodeName() != null) {
            logger.accept(context.getLastStoppedNodeName(), "Finished - elapsed time: " + elapsedTime + "ms");
        }

        return elapsedTime;
    }

    /**
     * Gets the elapsed time for the current operation without stopping it.
     *
     * @return The current elapsed time in milliseconds
     */
    public static long getElapsedTime() {
        TimingContext context = CURRENT_CONTEXT.get();
        return context.getElapsedTime();
    }

    /**
     * Clears all timing data for the current thread.
     */
    public static void reset() {
        CURRENT_CONTEXT.remove();
    }

    /**
     * Gets statistics for all timed operations.
     *
     * @return A map of operation names to their total elapsed times
     */
    public static Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        timingStats.forEach((key, value) -> stats.put(key, value.get()));
        return stats;
    }

    // Private constructor to enforce static usage
    private StopWatch() {
    }

    // Singleton instance for method chaining
    @SuppressWarnings("InstantiationOfUtilityClass")
    private static final StopWatch INSTANCE = new StopWatch();

    private static StopWatch getInstance() {
        return INSTANCE;
    }

    /**
     * Internal class to maintain timing context for each thread.
     */
    private static class TimingContext {
        private TimingNode currentNode;
        private final TimingNode rootNode;
        private String lastStoppedNodeName;

        public TimingContext() {
            rootNode = new TimingNode("root", null);
            currentNode = rootNode;
        }

        public void startTiming(String name) {
            TimingNode newNode = new TimingNode(name, currentNode);
            currentNode.addChild(newNode);
            currentNode = newNode;
        }

        public long stopTiming() {
            if (currentNode == rootNode) {
                // Nothing to stop: clear lastStoppedNodeName to avoid misleading logs
                lastStoppedNodeName = null;
                return 0;
            }

            // Save the name before stopping
            lastStoppedNodeName = currentNode.getName();

            long elapsedTime = currentNode.stop();

            // Update statistics
            //noinspection unused
            timingStats.computeIfAbsent(currentNode.getName(), unused -> new AtomicLong(0))
                    .addAndGet(elapsedTime);

            // Move up in the hierarchy
            currentNode = currentNode.getParent();

            return elapsedTime;
        }

        public long getElapsedTime() {
            if (currentNode == rootNode) {
                return 0L;
            }
            return currentNode.getElapsedTime();
        }

        public String getCurrentNodeName() {
            return currentNode != rootNode ? currentNode.getName() : null;
        }

        public String getLastStoppedNodeName() {
            return lastStoppedNodeName;
        }
    }

    /**
     * Represents a node in the timing hierarchy tree.
     */
    private static class TimingNode {
        private final String name;
        private final TimingNode parent;
        private final Map<String, TimingNode> children = new HashMap<>();
        private final long startTime;
        private long endTime = -1;

        public TimingNode(String name, TimingNode parent) {
            this.name = name;
            this.parent = parent;
            this.startTime = System.currentTimeMillis();
        }

        public String getName() {
            return name;
        }

        public TimingNode getParent() {
            return parent;
        }

        public void addChild(TimingNode child) {
            children.put(child.getName(), child);
        }

        public long stop() {
            if (endTime == -1) {
                endTime = System.currentTimeMillis();
            }
            return getElapsedTime();
        }

        public long getElapsedTime() {
            if (endTime == -1) {
                return System.currentTimeMillis() - startTime;
            } else {
                return endTime - startTime;
            }
        }
    }
}
