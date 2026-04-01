package controller;

import model.Car;
import model.CarPark;

/**
 * Producer thread that represents Car Owners arriving at the car park.
 * This class implements the Producer role in the Producer-Consumer pattern.
 * It generates Car objects at a configurable rate and adds them to the shared CarPark buffer.
 * 
 * Thread-Safety: This class is designed to run in its own thread and uses
 * the synchronized methods of CarPark for safe concurrent access.
 * 
 * @author Car Park Management System
 * @version 1.0
 */
public class CarOwner implements Runnable {
    private final CarPark carPark;
    private final int id;
    private volatile int productionRate; // milliseconds between car arrivals
    private volatile boolean running;
    private volatile boolean waiting;
    private long carsProduced;
    
    /**
     * Creates a new CarOwner (Producer) thread.
     * 
     * @param carPark The shared car park buffer
     * @param id The unique identifier for this producer
     * @param productionRate The delay in milliseconds between producing cars
     */
    public CarOwner(CarPark carPark, int id, int productionRate) {
        this.carPark = carPark;
        this.id = id;
        this.productionRate = productionRate;
        this.running = true;
        this.waiting = false;
        this.carsProduced = 0;
    }
    
    /**
     * The main execution loop for this producer thread.
     * Continuously generates cars and adds them to the car park.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("CarOwner-" + id);
        
        while (running) {
            try {
                // Create a new car
                Car car = new Car();
                
                // Set waiting flag before attempting to park (may block if full)
                waiting = carPark.isFull();
                
                // Try to park the car (blocks if car park is full)
                carPark.parkCar(car);
                
                // Successfully parked
                waiting = false;
                carsProduced++;
                
                // Sleep to simulate time between car arrivals
                Thread.sleep(productionRate);
                
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
     * Stops this producer thread gracefully.
     */
    public void stopProducing() {
        running = false;
    }
    
    /**
     * Checks if this producer is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Checks if this producer is waiting (blocked because car park is full).
     * 
     * @return true if waiting, false otherwise
     */
    public boolean isWaiting() {
        return waiting;
    }
    
    /**
     * Gets the unique ID of this producer.
     * 
     * @return The producer ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Gets the total number of cars produced by this producer.
     * 
     * @return The count of cars produced
     */
    public long getCarsProduced() {
        return carsProduced;
    }
    
    /**
     * Gets the current production rate.
     * 
     * @return The delay in milliseconds between producing cars
     */
    public int getProductionRate() {
        return productionRate;
    }
    
    /**
     * Sets the production rate dynamically.
     * 
     * @param productionRate The new delay in milliseconds between producing cars
     */
    public void setProductionRate(int productionRate) {
        if (productionRate > 0) {
            this.productionRate = productionRate;
        }
    }
}
