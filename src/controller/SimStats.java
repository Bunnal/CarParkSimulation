package controller;

import java.util.Collections;
import java.util.List;
import model.Car;

/**
 * Immutable snapshot of the simulation state for UI rendering.
 */
public class SimStats {

    private final boolean running;
    private final int occupied;
    private final int capacity;
    private final double throughputPerSecond;
    private final long averageWaitTimeMillis;
    private final long totalProduced;
    private final long totalConsumed;
    private final List<Car> parkedCars;
    private final List<WorkerSnapshot> producerSnapshots;
    private final List<WorkerSnapshot> consumerSnapshots;

    /**
     * Creates a complete simulation snapshot.
     *
     * @param running whether the simulation is currently running
     * @param occupied occupied slots
     * @param capacity configured capacity
     * @param throughputPerSecond throughput in cars per second
     * @param averageWaitTimeMillis average wait time in milliseconds
     * @param totalProduced total produced cars
     * @param totalConsumed total consumed cars
     * @param parkedCars current parked cars
     * @param producerSnapshots producer worker snapshots
     * @param consumerSnapshots consumer worker snapshots
     */
    public SimStats(boolean running, int occupied, int capacity, double throughputPerSecond,
                    long averageWaitTimeMillis, long totalProduced, long totalConsumed,
                    List<Car> parkedCars, List<WorkerSnapshot> producerSnapshots,
                    List<WorkerSnapshot> consumerSnapshots) {
        this.running = running;
        this.occupied = occupied;
        this.capacity = capacity;
        this.throughputPerSecond = throughputPerSecond;
        this.averageWaitTimeMillis = averageWaitTimeMillis;
        this.totalProduced = totalProduced;
        this.totalConsumed = totalConsumed;
        this.parkedCars = parkedCars == null ? Collections.emptyList() : Collections.unmodifiableList(parkedCars);
        this.producerSnapshots = producerSnapshots == null
            ? Collections.emptyList() : Collections.unmodifiableList(producerSnapshots);
        this.consumerSnapshots = consumerSnapshots == null
            ? Collections.emptyList() : Collections.unmodifiableList(consumerSnapshots);
    }

    /**
     * Creates a zeroed-out snapshot for initialization.
     *
     * @param capacity configured capacity
     */
    public SimStats(int capacity) {
        this(false, 0, capacity, 0.0, 0L, 0L, 0L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Returns whether the simulation is running.
     *
     * @return true when running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the occupied slot count.
     *
     * @return occupied count
     */
    public int getOccupied() {
        return occupied;
    }

    /**
     * Returns the configured capacity.
     *
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the average throughput in cars per second.
     *
     * @return throughput
     */
    public double getThroughputPerSecond() {
        return throughputPerSecond;
    }

    /**
     * Returns the average wait time in milliseconds.
     *
     * @return wait time in milliseconds
     */
    public long getAverageWaitTimeMillis() {
        return averageWaitTimeMillis;
    }

    /**
     * Returns the total produced car count.
     *
     * @return produced count
     */
    public long getTotalProduced() {
        return totalProduced;
    }

    /**
     * Returns the total consumed car count.
     *
     * @return consumed count
     */
    public long getTotalConsumed() {
        return totalConsumed;
    }

    /**
     * Returns the parked-car snapshot.
     *
     * @return parked cars
     */
    public List<Car> getParkedCars() {
        return parkedCars;
    }

    /**
     * Returns producer worker snapshots.
     *
     * @return producer snapshots
     */
    public List<WorkerSnapshot> getProducerSnapshots() {
        return producerSnapshots;
    }

    /**
     * Returns consumer worker snapshots.
     *
     * @return consumer snapshots
     */
    public List<WorkerSnapshot> getConsumerSnapshots() {
        return consumerSnapshots;
    }

    /**
     * Returns the available slot count.
     *
     * @return available slots
     */
    public int getAvailableSlots() {
        return Math.max(0, capacity - occupied);
    }

    /**
     * Returns the occupancy percentage.
     *
     * @return occupancy percentage
     */
    public double getOccupancyPercent() {
        if (capacity == 0) {
            return 0.0;
        }
        return occupied * 100.0 / capacity;
    }

    /**
     * Returns whether capacity is temporarily below the current occupancy.
     *
     * @return true when occupied slots exceed configured capacity
     */
    public boolean isOverCapacity() {
        return occupied > capacity;
    }
}
