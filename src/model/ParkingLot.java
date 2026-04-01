package model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Shared bounded buffer used by producers and consumers.
 *
 * Synchronization design:
 * - {@code availableSlots} counts how many free parking spaces remain.
 * - {@code occupiedSlots} counts how many cars can be consumed.
 * - {@code mutex} protects the queue and all derived statistics.
 *
 * Producers block on the empty-slot semaphore when the lot is full.
 * Consumers block on the occupied-slot semaphore when the lot is empty.
 * No busy waiting is used anywhere in this class.
 */
public class ParkingLot {

    private final Deque<Car> parkedCars;
    private final AdjustableSemaphore availableSlots;
    private final Semaphore occupiedSlots;
    private final ReentrantLock mutex;

    private volatile int capacity;
    private long totalParked;
    private long totalRetrieved;
    private long totalTurnaroundTimeMillis;
    private long simulationStartMillis;

    /**
     * Creates a new parking lot with the specified capacity.
     *
     * @param capacity maximum number of cars allowed in the lot
     */
    public ParkingLot(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1.");
        }

        this.capacity = capacity;
        this.parkedCars = new ArrayDeque<>();
        this.availableSlots = new AdjustableSemaphore(capacity, true);
        this.occupiedSlots = new Semaphore(0, true);
        this.mutex = new ReentrantLock(true);
        this.totalParked = 0L;
        this.totalRetrieved = 0L;
        this.totalTurnaroundTimeMillis = 0L;
        this.simulationStartMillis = System.currentTimeMillis();
    }

    /**
     * Parks a car in the shared buffer, blocking when the lot is full.
     *
     * @param car the car to park
     * @throws InterruptedException if the producer is interrupted while waiting
     */
    public void park(Car car) throws InterruptedException {
        availableSlots.acquire();

        boolean added = false;
        boolean locked = false;

        try {
            mutex.lockInterruptibly();
            locked = true;

            car.markParked();
            parkedCars.addLast(car);
            totalParked++;
            added = true;
        } finally {
            if (locked) {
                mutex.unlock();
            }

            if (added) {
                occupiedSlots.release();
            } else {
                availableSlots.release();
            }
        }
    }

    /**
     * Removes and returns a car from the shared buffer, blocking when empty.
     *
     * @return the car removed from the lot
     * @throws InterruptedException if the consumer is interrupted while waiting
     */
    public Car retrieve() throws InterruptedException {
        occupiedSlots.acquire();

        boolean removed = false;
        boolean locked = false;
        boolean shouldReleaseAvailableSlot = false;
        Car car = null;

        try {
            mutex.lockInterruptibly();
            locked = true;

            car = parkedCars.removeFirst();
            car.markDeparted();

            totalRetrieved++;
            totalTurnaroundTimeMillis += car.getTurnaroundTimeMillis();
            shouldReleaseAvailableSlot = parkedCars.size() < capacity;
            removed = true;

            return car;
        } finally {
            if (locked) {
                mutex.unlock();
            }

            if (!removed) {
                occupiedSlots.release();
            } else if (shouldReleaseAvailableSlot) {
                availableSlots.release();
            }
        }
    }

    /**
     * Dynamically updates the buffer capacity while the simulation is running.
     *
     * If the new capacity is lower than the current occupancy, the lot temporarily
     * stays over capacity until consumers remove enough cars. Producers remain blocked
     * because no new available-slot permits are released until the occupancy drops.
     *
     * @param newCapacity the new desired capacity
     */
    public void setCapacity(int newCapacity) {
        if (newCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1.");
        }

        mutex.lock();
        try {
            capacity = newCapacity;

            int desiredAvailablePermits = Math.max(0, newCapacity - parkedCars.size());
            int currentAvailablePermits = availableSlots.availablePermits();
            int delta = desiredAvailablePermits - currentAvailablePermits;

            if (delta > 0) {
                availableSlots.release(delta);
            } else if (delta < 0) {
                availableSlots.reducePermitsSafely(-delta);
            }
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the current number of parked cars.
     *
     * @return occupied slot count
     */
    public int getOccupiedCount() {
        mutex.lock();
        try {
            return parkedCars.size();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Compatibility alias for older UI code.
     *
     * @return occupied slot count
     */
    public int getOccupied() {
        return getOccupiedCount();
    }

    /**
     * Returns the number of free slots based on the configured capacity.
     *
     * @return free slot count
     */
    public int getAvailableCount() {
        mutex.lock();
        try {
            return Math.max(0, capacity - parkedCars.size());
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Compatibility alias for older UI code.
     *
     * @return free slot count
     */
    public int getAvailable() {
        return getAvailableCount();
    }

    /**
     * Returns the configured capacity of the lot.
     *
     * @return capacity value
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the occupancy percentage, which may exceed 100 when capacity is
     * lowered below the current occupancy while the simulation is running.
     *
     * @return occupancy percentage
     */
    public double getOccupancyPercent() {
        mutex.lock();
        try {
            if (capacity == 0) {
                return 0.0;
            }
            return (parkedCars.size() * 100.0) / capacity;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the average throughput in cars per second since the current run began.
     *
     * @return throughput in cars per second
     */
    public double getThroughputPerSecond() {
        mutex.lock();
        try {
            long elapsedMillis = Math.max(1L, System.currentTimeMillis() - simulationStartMillis);
            return totalRetrieved / (elapsedMillis / 1000.0);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the average turnaround time from creation to retrieval.
     *
     * @return average wait time in milliseconds
     */
    public long getAverageWaitTimeMillis() {
        mutex.lock();
        try {
            if (totalRetrieved == 0L) {
                return 0L;
            }
            return totalTurnaroundTimeMillis / totalRetrieved;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the total number of cars ever parked in the current run.
     *
     * @return total parked count
     */
    public long getTotalParked() {
        mutex.lock();
        try {
            return totalParked;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns the total number of cars ever retrieved in the current run.
     *
     * @return total retrieved count
     */
    public long getTotalRetrieved() {
        mutex.lock();
        try {
            return totalRetrieved;
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Returns a stable snapshot of currently parked cars for the JavaFX view.
     *
     * @return immutable snapshot list
     */
    public List<Car> getParkedCarsSnapshot() {
        mutex.lock();
        try {
            return new ArrayList<>(parkedCars);
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Indicates whether a producer is likely to park immediately.
     *
     * @return true when a free slot permit is currently available
     */
    public boolean hasAvailableSlot() {
        return availableSlots.availablePermits() > 0;
    }

    /**
     * Indicates whether a consumer is likely to retrieve immediately.
     *
     * @return true when at least one occupied-slot permit is currently available
     */
    public boolean hasParkedCars() {
        return occupiedSlots.availablePermits() > 0;
    }

    /**
     * Reinitializes throughput and wait-time statistics without altering cars.
     */
    public void resetMetrics() {
        mutex.lock();
        try {
            totalParked = 0L;
            totalRetrieved = 0L;
            totalTurnaroundTimeMillis = 0L;
            simulationStartMillis = System.currentTimeMillis();
        } finally {
            mutex.unlock();
        }
    }

    /**
     * Semaphore subclass exposing controlled permit reduction for live capacity changes.
     */
    private static final class AdjustableSemaphore extends Semaphore {
        private AdjustableSemaphore(int permits, boolean fair) {
            super(permits, fair);
        }

        private void reducePermitsSafely(int reduction) {
            super.reducePermits(reduction);
        }
    }
}
