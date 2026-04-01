package model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents the shared buffer (car park) in the Producer-Consumer problem.
 * This class uses explicit locks and condition variables for thread synchronization.
 * Producers (Car Owners) add cars and Consumers (Security Guards) remove cars.
 * 
 * Thread-Safety: All public methods are synchronized using ReentrantLock.
 * 
 * @author Car Park Management System
 * @version 1.0
 */
public class CarPark {
    private final Queue<Car> parkedCars;
    private final int capacity;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;
    
    // Statistics
    private long totalCarsProcessed;
    private long totalWaitTime;
    private long simulationStartTime;
    
    /**
     * Creates a new CarPark with the specified capacity.
     * 
     * @param capacity The maximum number of cars that can be parked
     * @throws IllegalArgumentException if capacity is less than 1
     */
    public CarPark(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.capacity = capacity;
        this.parkedCars = new LinkedList<>();
        this.lock = new ReentrantLock(true); // Fair lock for FIFO ordering
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.totalCarsProcessed = 0;
        this.totalWaitTime = 0;
        this.simulationStartTime = System.currentTimeMillis();
    }
    
    /**
     * Adds a car to the car park. If the park is full, the thread will wait
     * until space becomes available.
     * 
     * @param car The car to add to the park
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void parkCar(Car car) throws InterruptedException {
        lock.lock();
        try {
            // Wait while the car park is full
            while (parkedCars.size() >= capacity) {
                notFull.await();
            }
            
            // Add the car to the park
            parkedCars.offer(car);
            
            // Signal that the park is not empty
            notEmpty.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Removes and returns a car from the car park. If the park is empty,
     * the thread will wait until a car becomes available.
     * 
     * @return The car that was removed from the park
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public Car removeCar() throws InterruptedException {
        lock.lock();
        try {
            // Wait while the car park is empty
            while (parkedCars.isEmpty()) {
                notEmpty.await();
            }
            
            // Remove a car from the park
            Car car = parkedCars.poll();
            car.setDepartureTime(java.time.LocalDateTime.now());
            
            // Update statistics
            totalCarsProcessed++;
            totalWaitTime += car.getParkingDuration();
            
            // Signal that the park is not full
            notFull.signal();
            
            return car;
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets the current number of cars in the park.
     * 
     * @return The number of parked cars
     */
    public int getCurrentOccupancy() {
        lock.lock();
        try {
            return parkedCars.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets the maximum capacity of the car park.
     * 
     * @return The capacity
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Gets the number of available parking slots.
     * 
     * @return The number of available slots
     */
    public int getAvailableSlots() {
        lock.lock();
        try {
            return capacity - parkedCars.size();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Calculates the current occupancy percentage.
     * 
     * @return The occupancy as a percentage (0-100)
     */
    public double getOccupancyPercentage() {
        lock.lock();
        try {
            return (parkedCars.size() * 100.0) / capacity;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Checks if the car park is full.
     * 
     * @return true if the park is at capacity, false otherwise
     */
    public boolean isFull() {
        lock.lock();
        try {
            return parkedCars.size() >= capacity;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Checks if the car park is empty.
     * 
     * @return true if the park has no cars, false otherwise
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return parkedCars.isEmpty();
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Gets the total number of cars processed since simulation start.
     * 
     * @return The total cars processed
     */
    public long getTotalCarsProcessed() {
        lock.lock();
        try {
            return totalCarsProcessed;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Calculates the throughput in cars per second.
     * 
     * @return The throughput rate
     */
    public double getThroughput() {
        lock.lock();
        try {
            long elapsedSeconds = (System.currentTimeMillis() - simulationStartTime) / 1000;
            if (elapsedSeconds == 0) {
                return 0.0;
            }
            return totalCarsProcessed / (double) elapsedSeconds;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Calculates the average wait time for cars in seconds.
     * 
     * @return The average wait time, or 0 if no cars have been processed
     */
    public double getAverageWaitTime() {
        lock.lock();
        try {
            if (totalCarsProcessed == 0) {
                return 0.0;
            }
            return totalWaitTime / (double) totalCarsProcessed;
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Resets all statistics to zero.
     */
    public void resetStatistics() {
        lock.lock();
        try {
            totalCarsProcessed = 0;
            totalWaitTime = 0;
            simulationStartTime = System.currentTimeMillis();
        } finally {
            lock.unlock();
        }
    }
}
