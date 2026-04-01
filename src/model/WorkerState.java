package model;

/**
 * Represents the current lifecycle state of a producer or consumer thread.
 */
public enum WorkerState {
    ACTIVE,
    WAITING,
    STOPPED
}
