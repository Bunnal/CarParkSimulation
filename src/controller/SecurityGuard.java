package controller;

import model.Car;
import model.CarPark;

/**
 * Consumer thread that represents Security Guards removing cars from the car park.
 * This class implements the Consumer role in the Producer-Consumer pattern.
 * It removes Car objects from the shared CarPark buffer at a configurable rate.
 * 
 * Thread-Safety: This class is designed to run in its own thread and uses
 * the synchronized methods of CarPark for safe concurrent access.
 * 
 * @author Car Park Management System
 * @version 1.0
 */
public class SecurityGuard implements Runnable {
    private final CarPark carPark;
    private final int id;
    private volatile int consumptionRate; // milliseconds between car removals
    private volatile boolean running;
    private volatile boolean waiting;
    private long carsRemoved;
    
    /**
     * Creates a new SecurityGuard (Consumer) thread.
     * 
     * @param carPark The shared car park buffer
     * @param id The unique identifier for this consumer
     * @param consumptionRate The delay in milliseconds between removing cars
     */
    public SecurityGuard(CarPark carPark, int id, int consumptionRate) {
        this.carPark = carPark;
        this.id = id;
        this.consumptionRate = consumptionRate;
        this.running = true;
        this.waiting = false;
        this.carsRemoved = 0;
    }
    
    /**
     * The main execution loop for this consumer thread.
     * Continuously removes cars from the car park.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("SecurityGuard-" + id);
        
        while (running) {
            try {
                // Set waiting flag before attempting to remove (may block if empty)
                waiting = carPark.isEmpty();
                
                // Try to remove a car (blocks if car park is empty)
                Car car = carPark.removeCar();
                
                // Successfully removed
                waiting = false;
                carsRemoved++;
                
                // Sleep to simulate time to process car exit
                Thread.sleep(consumptionRate);
                
            } catch (InterruptedException e) {
                // Thread was interrupted - check if we should continue
                if (!running) {
                    Thread.currentThread().interrupt(); // Restore interrupt status
                    break;
                }
            }
        }
    }
    
    /**
     * Stops this consumer thread gracefully.
     */
    public void stopConsuming() {
        running = false;
    }
    
    /**
     * Checks if this consumer is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Checks if this consumer is waiting (blocked because car park is empty).
     * 
     * @return true if waiting, false otherwise
     */
    public boolean isWaiting() {
        return waiting;
    }
    
    /**
     * Gets the unique ID of this consumer.
     * 
     * @return The consumer ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the total number of cars removed by this consumer.
     * 
     * @return The count of cars removed
     */
    public long getCarsRemoved() {
        return carsRemoved;
    }
    
    /**
     * Gets the current consumption rate.
     * 
     * @return The delay in milliseconds between removing cars
     */
    public int getConsumptionRate() {
        return consumptionRate;
    }
    
    /**
     * Sets the consumption rate dynamically.
     * 
     * @param consumptionRate The new delay in milliseconds between removing cars
     */
    public void setConsumptionRate(int consumptionRate) {
        if (consumptionRate > 0) {
            this.consumptionRate = consumptionRate;
        }
    }
}
