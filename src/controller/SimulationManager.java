package controller;

import model.CarPark;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the entire car park simulation including all producer and consumer threads.
 * This class provides centralized control over the simulation lifecycle.
 * 
 * Responsibilities:
 * - Creating and managing producer (CarOwner) threads
 * - Creating and managing consumer (SecurityGuard) threads
 * - Starting and stopping the simulation
 * - Providing access to simulation statistics
 * 
 * @author Car Park Management System
 * @version 1.0
 */
public class SimulationManager {
    private final CarPark carPark;
    private final List<CarOwner> carOwners;
    private final List<SecurityGuard> securityGuards;
    private final List<Thread> producerThreads;
    private final List<Thread> consumerThreads;
    private volatile boolean simulationRunning;
    
    /**
     * Creates a new SimulationManager with the specified car park.
     * 
     * @param carPark The shared car park buffer to use for the simulation
     */
    public SimulationManager(CarPark carPark) {
        this.carPark = carPark;
        this.carOwners = new ArrayList<>();
        this.securityGuards = new ArrayList<>();
        this.producerThreads = new ArrayList<>();
        this.consumerThreads = new ArrayList<>();
        this.simulationRunning = false;
    }
    
    /**
     * Starts the simulation with the specified number of producers and consumers.
     * 
     * @param numProducers Number of CarOwner threads to create
     * @param numConsumers Number of SecurityGuard threads to create
     * @param productionRate Initial production rate in milliseconds
     * @param consumptionRate Initial consumption rate in milliseconds
     * @throws IllegalStateException if simulation is already running
     */
    public void startSimulation(int numProducers, int numConsumers, 
                                 int productionRate, int consumptionRate) {
        if (simulationRunning) {
            throw new IllegalStateException("Simulation is already running");
        }
        
        // Create and start producer threads
        for (int i = 0; i < numProducers; i++) {
            CarOwner owner = new CarOwner(carPark, i + 1, productionRate);
            carOwners.add(owner);
            Thread thread = new Thread(owner);
            producerThreads.add(thread);
            thread.start();
        }
        
        // Create and start consumer threads
        for (int i = 0; i < numConsumers; i++) {
            SecurityGuard guard = new SecurityGuard(carPark, i + 1, consumptionRate);
            securityGuards.add(guard);
            Thread thread = new Thread(guard);
            consumerThreads.add(thread);
            thread.start();
        }
        
        simulationRunning = true;
    }
    
    /**
     * Stops the simulation and waits for all threads to terminate.
     * This method blocks until all threads have finished.
     */
    public void stopSimulation() {
        if (!simulationRunning) {
            return;
        }
        
        // Signal all threads to stop
        for (CarOwner owner : carOwners) {
            owner.stopProducing();
        }
        for (SecurityGuard guard : securityGuards) {
            guard.stopConsuming();
        }
        
        // Interrupt all threads to wake them from blocking operations
        for (Thread thread : producerThreads) {
            thread.interrupt();
        }
        for (Thread thread : consumerThreads) {
            thread.interrupt();
        }
        
        // Wait for all threads to finish
        try {
            for (Thread thread : producerThreads) {
                thread.join(1000); // Wait up to 1 second
            }
            for (Thread thread : consumerThreads) {
                thread.join(1000); // Wait up to 1 second
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Clear all collections
        carOwners.clear();
        securityGuards.clear();
        producerThreads.clear();
        consumerThreads.clear();
        
        simulationRunning = false;
    }
    
    /**
     * Checks if the simulation is currently running.
     * 
     * @return true if running, false otherwise
     */
    public boolean isSimulationRunning() {
        return simulationRunning;
    }
    
    /**
     * Gets the list of all car owners (producers).
     * 
     * @return Unmodifiable list of car owners
     */
    public List<CarOwner> getCarOwners() {
        return new ArrayList<>(carOwners);
    }
    
    /**
     * Gets the list of all security guards (consumers).
     * 
     * @return Unmodifiable list of security guards
     */
    public List<SecurityGuard> getSecurityGuards() {
        return new ArrayList<>(securityGuards);
    }
    
    /**
     * Gets the car park being used in the simulation.
     * 
     * @return The car park instance
     */
    public CarPark getCarPark() {
        return carPark;
    }
    
    /**
     * Updates the production rate for all car owners.
     * 
     * @param productionRate The new production rate in milliseconds
     */
    public void setProductionRate(int productionRate) {
        for (CarOwner owner : carOwners) {
            owner.setProductionRate(productionRate);
        }
    }
    
    /**
     * Updates the consumption rate for all security guards.
     * 
     * @param consumptionRate The new consumption rate in milliseconds
     */
    public void setConsumptionRate(int consumptionRate) {
        for (SecurityGuard guard : securityGuards) {
            guard.setConsumptionRate(consumptionRate);
        }
    }
    
    /**
     * Gets the total number of cars produced by all car owners.
     * 
     * @return The total count of cars produced
     */
    public long getTotalCarsProduced() {
        return carOwners.stream()
                .mapToLong(CarOwner::getCarsProduced)
                .sum();
    }
    
    /**
     * Gets the total number of cars removed by all security guards.
     * 
     * @return The total count of cars removed
     */
    public long getTotalCarsRemoved() {
        return securityGuards.stream()
                .mapToLong(SecurityGuard::getCarsRemoved)
                .sum();
    }
}
