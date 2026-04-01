package model;

/**
 * Consumer thread representing a security guard processing exiting cars.
 */
public class Consumer implements Runnable {

    private final int id;
    private final ParkingLot parkingLot;

    private volatile boolean running;
    private volatile int rateMs;
    private volatile WorkerState state;

    private long carsProcessed;

    /**
     * Creates a new consumer.
     *
     * @param id unique consumer identifier
     * @param parkingLot shared parking-lot buffer
     * @param rateMs delay in milliseconds between retrieval attempts
     */
    public Consumer(int id, ParkingLot parkingLot, int rateMs) {
        this.id = id;
        this.parkingLot = parkingLot;
        this.rateMs = rateMs;
        this.running = true;
        this.state = WorkerState.WAITING;
        this.carsProcessed = 0L;
    }

    /**
     * Consumer loop:
     * 1. Block on the parking lot if no cars are available.
     * 2. Remove the next car from the shared buffer.
     * 3. Sleep for the configured processing interval.
     */
    @Override
    public void run() {
        Thread.currentThread().setName("SecurityGuard-" + id);

        try {
            while (running) {
                state = parkingLot.hasParkedCars() ? WorkerState.ACTIVE : WorkerState.WAITING;
                parkingLot.retrieve();

                carsProcessed++;
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
     * Requests a graceful shutdown for this consumer.
     */
    public void requestStop() {
        running = false;
    }

    /**
     * Returns the consumer identifier.
     *
     * @return consumer id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current processing interval in milliseconds.
     *
     * @return consumer rate
     */
    public int getRateMs() {
        return rateMs;
    }

    /**
     * Updates the consumer interval in real time.
     *
     * @param rateMs new delay in milliseconds
     */
    public void setRateMs(int rateMs) {
        if (rateMs > 0) {
            this.rateMs = rateMs;
        }
    }

    /**
     * Returns the worker state for UI rendering.
     *
     * @return current state
     */
    public WorkerState getState() {
        return state;
    }

    /**
     * Returns the number of cars processed by this consumer.
     *
     * @return processed count
     */
    public long getCarsProcessed() {
        return carsProcessed;
    }
}
