package controller;

import java.util.ArrayList;
import java.util.List;
import model.Car;
import model.Consumer;
import model.ParkingLot;
import model.Producer;

/**
 * Coordinates the producer-consumer simulation lifecycle.
 * This class owns the shared buffer instance, worker instances, and runtime configuration.
 */
public class SimulationController {

    private static final int DEFAULT_CAPACITY = 12;
    private static final int DEFAULT_PRODUCER_COUNT = 3;
    private static final int DEFAULT_CONSUMER_COUNT = 2;
    private static final int DEFAULT_PRODUCTION_RATE_MS = 850;
    private static final int DEFAULT_CONSUMPTION_RATE_MS = 1300;
    private static final long JOIN_TIMEOUT_MS = 800L;

    private final List<Producer> producers;
    private final List<Consumer> consumers;
    private final List<Thread> workerThreads;

    private ParkingLot parkingLot;
    private boolean running;
    private int capacity;
    private int producerCount;
    private int consumerCount;
    private int productionRateMs;
    private int consumptionRateMs;

    /**
     * Creates a controller with sensible defaults and an empty parking lot for the initial view.
     */
    public SimulationController() {
        this.producers = new ArrayList<>();
        this.consumers = new ArrayList<>();
        this.workerThreads = new ArrayList<>();
        this.capacity = DEFAULT_CAPACITY;
        this.producerCount = DEFAULT_PRODUCER_COUNT;
        this.consumerCount = DEFAULT_CONSUMER_COUNT;
        this.productionRateMs = DEFAULT_PRODUCTION_RATE_MS;
        this.consumptionRateMs = DEFAULT_CONSUMPTION_RATE_MS;
        this.parkingLot = new ParkingLot(capacity);
        this.running = false;
    }

    /**
     * Starts a fresh simulation run using the current configuration.
     */
    public synchronized void start() {
        if (running) {
            return;
        }

        Car.resetCounter();
        parkingLot = new ParkingLot(capacity);
        producers.clear();
        consumers.clear();
        workerThreads.clear();

        for (int i = 1; i <= producerCount; i++) {
            Producer producer = new Producer(i, parkingLot, productionRateMs);
            producers.add(producer);
            workerThreads.add(createWorkerThread(producer, "CarOwner-" + i));
        }

        for (int i = 1; i <= consumerCount; i++) {
            Consumer consumer = new Consumer(i, parkingLot, consumptionRateMs);
            consumers.add(consumer);
            workerThreads.add(createWorkerThread(consumer, "SecurityGuard-" + i));
        }

        running = true;
        for (Thread workerThread : workerThreads) {
            workerThread.start();
        }
    }

    /**
     * Stops the current run and interrupts any waiting workers.
     */
    public synchronized void stop() {
        if (!running) {
            return;
        }

        running = false;

        for (Producer producer : producers) {
            producer.requestStop();
        }
        for (Consumer consumer : consumers) {
            consumer.requestStop();
        }
        for (Thread workerThread : workerThreads) {
            workerThread.interrupt();
        }
        for (Thread workerThread : workerThreads) {
            try {
                workerThread.join(JOIN_TIMEOUT_MS);
            } catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        producers.clear();
        consumers.clear();
        workerThreads.clear();
    }

    /**
     * Stops the simulation and rebuilds an empty parking lot with the current capacity.
     */
    public synchronized void reset() {
        stop();
        Car.resetCounter();
        parkingLot = new ParkingLot(capacity);
    }

    /**
     * Releases all resources during application shutdown.
     */
    public synchronized void shutdown() {
        stop();
    }

    /**
     * Returns whether the simulation is running.
     *
     * @return true when running
     */
    public synchronized boolean isRunning() {
        return running;
    }

    /**
     * Returns the configured capacity.
     *
     * @return configured capacity
     */
    public synchronized int getCapacity() {
        return capacity;
    }

    /**
     * Updates the shared-buffer capacity and applies it immediately when running.
     *
     * @param capacity new capacity
     */
    public synchronized void setCapacity(int capacity) {
        if (capacity < 1) {
            return;
        }

        this.capacity = capacity;
        parkingLot.setCapacity(capacity);
    }

    /**
     * Returns the configured producer count.
     *
     * @return producer count
     */
    public synchronized int getProducerCount() {
        return producerCount;
    }

    /**
     * Updates the producer count for the next run.
     *
     * @param producerCount new producer count
     */
    public synchronized void setProducerCount(int producerCount) {
        if (producerCount > 0) {
            this.producerCount = producerCount;
        }
    }

    /**
     * Returns the configured consumer count.
     *
     * @return consumer count
     */
    public synchronized int getConsumerCount() {
        return consumerCount;
    }

    /**
     * Updates the consumer count for the next run.
     *
     * @param consumerCount new consumer count
     */
    public synchronized void setConsumerCount(int consumerCount) {
        if (consumerCount > 0) {
            this.consumerCount = consumerCount;
        }
    }

    /**
     * Returns the configured production rate in milliseconds.
     *
     * @return production rate
     */
    public synchronized int getProductionRateMs() {
        return productionRateMs;
    }

    /**
     * Updates the production rate and pushes it to active producers.
     *
     * @param productionRateMs new production rate
     */
    public synchronized void setProductionRateMs(int productionRateMs) {
        if (productionRateMs < 1) {
            return;
        }

        this.productionRateMs = productionRateMs;
        for (Producer producer : producers) {
            producer.setRateMs(productionRateMs);
        }
    }

    /**
     * Returns the configured consumption rate in milliseconds.
     *
     * @return consumption rate
     */
    public synchronized int getConsumptionRateMs() {
        return consumptionRateMs;
    }

    /**
     * Updates the consumption rate and pushes it to active consumers.
     *
     * @param consumptionRateMs new consumption rate
     */
    public synchronized void setConsumptionRateMs(int consumptionRateMs) {
        if (consumptionRateMs < 1) {
            return;
        }

        this.consumptionRateMs = consumptionRateMs;
        for (Consumer consumer : consumers) {
            consumer.setRateMs(consumptionRateMs);
        }
    }

    /**
     * Builds a complete immutable statistics snapshot for the JavaFX dashboard.
     *
     * @return current simulation snapshot
     */
    public synchronized SimStats getStats() {
        return new SimStats(
            running,
            parkingLot.getOccupiedCount(),
            parkingLot.getCapacity(),
            parkingLot.getThroughputPerSecond(),
            parkingLot.getAverageWaitTimeMillis(),
            parkingLot.getTotalParked(),
            parkingLot.getTotalRetrieved(),
            parkingLot.getParkedCarsSnapshot(),
            buildProducerSnapshots(),
            buildConsumerSnapshots()
        );
    }

    private Thread createWorkerThread(Runnable worker, String threadName) {
        Thread thread = new Thread(worker, threadName);
        thread.setDaemon(true);
        return thread;
    }

    private List<WorkerSnapshot> buildProducerSnapshots() {
        List<WorkerSnapshot> snapshots = new ArrayList<>();
        for (Producer producer : producers) {
            snapshots.add(new WorkerSnapshot(
                producer.getId(),
                "Car Owner " + producer.getId(),
                producer.getState(),
                producer.getCarsProduced()
            ));
        }
        return snapshots;
    }

    private List<WorkerSnapshot> buildConsumerSnapshots() {
        List<WorkerSnapshot> snapshots = new ArrayList<>();
        for (Consumer consumer : consumers) {
            snapshots.add(new WorkerSnapshot(
                consumer.getId(),
                "Security Guard " + consumer.getId(),
                consumer.getState(),
                consumer.getCarsProcessed()
            ));
        }
        return snapshots;
    }
}
