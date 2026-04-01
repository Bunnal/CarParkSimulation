package model;

/**
 * Producer thread representing a car owner arriving at the parking lot.
 */
public class Producer implements Runnable {

    private final int id;
    private final ParkingLot parkingLot;

    private volatile boolean running;
    private volatile int rateMs;
    private volatile WorkerState state;

    private long carsProduced;

    /**
     * Creates a producer with the provided runtime configuration.
     *
     * @param id unique producer identifier
     * @param parkingLot shared parking-lot buffer
     * @param rateMs delay in milliseconds between new cars
     */
    public Producer(int id, ParkingLot parkingLot, int rateMs) {
        this.id = id;
        this.parkingLot = parkingLot;
        this.rateMs = rateMs;
        this.running = true;
        this.state = WorkerState.WAITING;
        this.carsProduced = 0L;
    }

    /**
     * Producer loop:
     * 1. Create a new car.
     * 2. Block on the parking lot if no slot is available.
     * 3. Sleep for the configured production interval.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("CarOwner-" + id);

        try {
            while (running) {
                Car car = new Car();

                state = parkingLot.hasAvailableSlot() ? WorkerState.ACTIVE : WorkerState.WAITING;
                parkingLot.park(car);

                carsProduced++;
                state = WorkerState.ACTIVE;

                Thread.sleep(rateMs);
            }
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        } finally {
            state = WorkerState.STOPPED;
        }
    }

    /**
     * Requests a graceful shutdown for this producer.
     */
    public void requestStop() {
        running = false;
    }

    /**
     * Returns the producer identifier.
     *
     * @return producer id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current production rate in milliseconds.
     *
     * @return production rate
     */
    public int getRateMs() {
        return rateMs;
    }

    /**
     * Updates the production rate while the simulation is running.
     *
     * @param rateMs new production rate in milliseconds
     */
    public void setRateMs(int rateMs) {
        if (rateMs > 0) {
            this.rateMs = rateMs;
        }
    }

    /**
     * Returns the current worker state for dashboard rendering.
     *
     * @return current state
     */
    public WorkerState getState() {
        return state;
    }

    /**
     * Returns the total cars produced by this thread.
     *
     * @return produced count
     */
    public long getCarsProduced() {
        return carsProduced;
    }
}
