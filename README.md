# Car Park Management Sim

`Car Park Management Sim` is a Java desktop application that demonstrates the Producer-Consumer problem with a parking-lot theme. The current application uses Java Swing for the UI and a semaphore-based shared buffer for the simulation logic.

## Overview

The project simulates:

- Producers as `Car Owners` creating cars at a configurable rate
- Consumers as `Security Guards` removing cars from the lot at a configurable rate
- A shared bounded buffer represented by a parking lot with fixed capacity

The UI shows the simulation in real time through a top-down parking-lot scene, live dashboard metrics, worker status indicators, and animated vehicle movement.

## What Has Been Built So Far

- A Swing dashboard titled `Car Park Management Sim`
- A thread-safe `ParkingLot` buffer using:
  - `Semaphore` for available and occupied slots
  - a fair `ReentrantLock` as the mutex protecting shared state
- `Producer` and `Consumer` worker threads implemented as `Runnable`
- Real-time metrics:
  - occupancy percentage
  - available slots
  - throughput in cars per second
  - average wait time
  - total produced and processed cars
- Worker status indicators for active, waiting, and stopped states
- Runtime controls for:
  - lot capacity
  - producer count
  - consumer count
  - production rate
  - consumption rate
- A responsive custom-painted parking-lot scene in Swing
- A 2-lane road with looping animated traffic
- Parking-lot entry animation:
  - new cars drive in from the entrance and turn into a slot
- Parking-lot exit animation:
  - cars turn out of their slot and drive toward the exit gate
- Indexed car naming:
  - `Car 1`, `Car 2`, `Car 3`, ...
- On-car index labels:
  - `#1`, `#2`, `#3`, ...
- Layout tuning so the lot stays visually larger even when capacity reaches 30

## Active Architecture

### Model

- `src/model/Car.java`
  - car identity, timestamps, display naming, and vehicle type
- `src/model/ParkingLot.java`
  - bounded buffer and synchronization logic
- `src/model/Producer.java`
  - producer worker loop
- `src/model/Consumer.java`
  - consumer worker loop
- `src/model/WorkerState.java`
  - worker state enum used by the UI

### Controller

- `src/controller/SimulationController.java`
  - owns the simulation lifecycle and runtime configuration
- `src/controller/SimStats.java`
  - immutable UI snapshot of current simulation state
- `src/controller/WorkerSnapshot.java`
  - worker status payload for the dashboard

### UI

- `src/Main.java`
  - Swing application entry point
- `src/ui/MainDashboard.java`
  - main dashboard layout, controls, metrics, and worker panels
- `src/ui/ParkingLotScenePanel.java`
  - scalable custom-painted parking lot and road animation
- `src/ui/RoundedPanel.java`
  - reusable rounded container
- `src/ui/StatusDot.java`
  - worker state indicator dot

## Synchronization Design

The parking lot follows the classic Producer-Consumer pattern:

- Producers call `park(car)`
- Consumers call `retrieve()`
- `availableSlots` blocks producers when the lot is full
- `occupiedSlots` blocks consumers when the lot is empty
- `ReentrantLock` protects the critical section and all derived statistics
- `try/finally` is used for safe lock release
- no busy waiting is used

## UI Design Summary

The current UI is Swing-based and includes:

- a header with simulation status
- a left control panel
- a center metrics area and live parking-lot scene
- a right worker-status panel
- a scalable illustrated parking lot with:
  - tan lot border
  - asphalt center
  - slot dividers
  - booth
  - barrier gate
  - parking sign
  - animated road cars
  - incoming and outgoing parking actions

## Running the Project

### Requirements

- Java 17 or higher
- Maven 3.6 or higher

### Compile

```bash
mvn clean compile
```

### Run

```bash
java -cp target/classes Main
```

### Quick Start Script

```bash
./run.sh
```

## Default Behavior

When the app starts:

- the simulation is not running yet
- the dashboard is visible immediately
- the parking lot scene is already rendered
- the bottom road traffic is animated
- the user can adjust settings before starting the simulation

## Main User Controls

- `Buffer Capacity`
  - adjustable up to `30`
- `Car Owners`
  - number of producer threads
- `Security Guards`
  - number of consumer threads
- `Production Rate`
  - delay between car creation attempts
- `Consumption Rate`
  - delay between car retrieval attempts
- `Start Simulation`
- `Stop`
- `Reset`

## Current Visual Behavior

- New cars do not appear instantly.
  - They enter from the lot entrance and drive into their assigned slot.
- Exiting cars do not disappear instantly.
  - They leave their slot, align with the driveway, and drive out toward the gate.
- Each parked car has:
  - a visible index label on the car body
  - a small name tag above it
- The lot scales with the container and has been tuned to remain readable at higher capacities.

## Notes

- The active application is Swing, even though some older JavaFX-era files and dependencies are still present in the repository.
- The main runnable path is `Main -> MainDashboard -> ParkingLotScenePanel`.
- Some legacy or experimental classes remain in `src/` but are not part of the current main UI flow.

## Suggested Next Improvements

- compress the metrics area further to give the parking-lot scene even more height
- improve slot occupancy highlighting and lane guidance
- add event logging for arrivals and departures
- add screenshots or a short demo GIF to this README

## Project Goal

This project is meant to be both:

- an Operating Systems / concurrency demonstration
- a visually understandable parking-lot simulation

It combines synchronization concepts with a more realistic and engaging UI so thread behavior is easier to observe.
