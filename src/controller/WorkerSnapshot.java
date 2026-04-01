package controller;

import model.WorkerState;

/**
 * Immutable worker-state snapshot used by the UI dashboard.
 */
public class WorkerSnapshot {

    private final int id;
    private final String name;
    private final WorkerState state;
    private final long processedCount;

    /**
     * Creates a new worker snapshot.
     *
     * @param id worker identifier
     * @param name worker display name
     * @param state current worker state
     * @param processedCount total operations completed by the worker
     */
    public WorkerSnapshot(int id, String name, WorkerState state, long processedCount) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.processedCount = processedCount;
    }

    /**
     * Returns the worker identifier.
     *
     * @return worker id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the display name for the worker.
     *
     * @return display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the current worker state.
     *
     * @return worker state
     */
    public WorkerState getState() {
        return state;
    }

    /**
     * Returns the total number of completed operations for this worker.
     *
     * @return processed count
     */
    public long getProcessedCount() {
        return processedCount;
    }
}
