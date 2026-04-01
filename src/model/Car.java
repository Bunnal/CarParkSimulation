package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a car flowing through the producer-consumer simulation.
 * Each car is immutable apart from its lifecycle timestamps.
 */
public class Car {

    /**
     * Vehicle categories used by the parking-lot view.
     */
    public enum VehicleType {
        SEDAN,
        SUV,
        TAXI,
        VAN,
        EV,
        TRUCK
    }

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(1);
    private static final char[] LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final int id;
    private final String licensePlate;
    private final VehicleType vehicleType;
    private final LocalDateTime arrivalTime;
    private final long createdAtMillis;

    private volatile LocalDateTime departureTime;
    private volatile long parkedAtMillis;
    private volatile long departedAtMillis;

    /**
     * Creates a new car with a unique identifier and generated plate.
     */
    public Car() {
        this.id = ID_GENERATOR.getAndIncrement();
        this.licensePlate = generateLicensePlate();
        this.vehicleType = VehicleType.values()[(id - 1) % VehicleType.values().length];
        this.arrivalTime = LocalDateTime.now();
        this.createdAtMillis = System.currentTimeMillis();
        this.departureTime = null;
        this.parkedAtMillis = 0L;
        this.departedAtMillis = 0L;
    }

    /**
     * Marks the car as parked in the buffer.
     */
    public void markParked() {
        if (parkedAtMillis == 0L) {
            parkedAtMillis = System.currentTimeMillis();
        }
    }

    /**
     * Marks the car as departed and captures departure timestamps.
     */
    public void markDeparted() {
        departedAtMillis = System.currentTimeMillis();
        departureTime = LocalDateTime.now();
    }

    /**
     * Returns the unique numeric identifier for this car.
     *
     * @return car identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the generated license plate.
     *
     * @return license plate string
     */
    public String getLicensePlate() {
        return licensePlate;
    }

    /**
     * Returns a UI-friendly label for the car.
     *
     * @return display name in the form {@code Car <id>}
     */
    public String getDisplayName() {
        return "Car " + id;
    }

    /**
     * Returns the generated vehicle category.
     *
     * @return vehicle type
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Returns the creation timestamp for compatibility with the original model.
     *
     * @return arrival timestamp
     */
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Returns the departure timestamp when available.
     *
     * @return departure timestamp, or {@code null} if the car is still in the system
     */
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    /**
     * Sets the departure timestamp for compatibility with the original model.
     *
     * @param departureTime the departure time to record
     */
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
        this.departedAtMillis = System.currentTimeMillis();
    }

    /**
     * Returns the wall-clock time in milliseconds that the car has spent in the system.
     *
     * @return turnaround time in milliseconds
     */
    public long getTurnaroundTimeMillis() {
        if (departedAtMillis == 0L) {
            return 0L;
        }
        return Math.max(0L, departedAtMillis - createdAtMillis);
    }

    /**
     * Returns the time in milliseconds the car spent parked.
     *
     * @return residence time in milliseconds
     */
    public long getResidenceTimeMillis() {
        if (parkedAtMillis == 0L || departedAtMillis == 0L) {
            return 0L;
        }
        return Math.max(0L, departedAtMillis - parkedAtMillis);
    }

    /**
     * Returns the parking duration in seconds for compatibility with the earlier model.
     *
     * @return parking duration in seconds
     */
    public long getParkingDuration() {
        if (departureTime == null) {
            return 0L;
        }
        return Duration.between(arrivalTime, departureTime).getSeconds();
    }

    /**
     * Resets the shared ID counter.
     */
    public static void resetCounter() {
        ID_GENERATOR.set(1);
    }

    private String generateLicensePlate() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder(8);

        for (int i = 0; i < 3; i++) {
            builder.append(LETTERS[random.nextInt(LETTERS.length)]);
        }
        builder.append('-');
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Car{id=" + id + ", plate='" + licensePlate + "', type=" + vehicleType + "}";
    }
}
