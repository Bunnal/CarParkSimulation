# Gamma Prompt for Slide Generation

Use this prompt in Gamma to generate a presentation for my university Operating Systems project.

Create a clean, modern academic presentation titled `Car Park Management Sim: Producer-Consumer Visualization`.

Context:
- This is a Java desktop application built with Java Swing.
- The project demonstrates the Producer-Consumer problem using a parking-lot theme.
- Producers are `Car Owners`.
- Consumers are `Security Guards`.
- The shared bounded buffer is a `ParkingLot`.
- Synchronization uses:
  - `Semaphore` for available slots and occupied slots
  - a fair `ReentrantLock` as the mutex protecting shared state
- The goal is to explain Operating Systems synchronization concepts in a visually understandable way.

Important framing:
- Keep the explanation academically correct to the Producer-Consumer problem.
- Do not describe the system as a real-world smart parking product.
- Emphasize that the parking-lot graphics are a visualization of a bounded buffer.
- Mention that the UI was intentionally improved to better reflect full, empty, waiting, and active states without changing the core OS logic.

Audience:
- Lecturer and classmates in an Operating Systems or concurrency course.
- They care about synchronization correctness, thread coordination, no busy waiting, and clarity of demonstration.

Tone:
- Professional
- Clear
- Confident
- Academic but easy to understand

Design style:
- Minimal
- Modern
- White or light background
- Green, blue, amber, and red as accent colors
- Use diagrams, simple icons, and process flow visuals
- Avoid too much text on each slide

Create 11 slides with the following structure:

1. Title slide
- Project title
- Subtitle: `A Java Swing visualization of the Producer-Consumer problem`
- Presenter name placeholder
- Course placeholder

2. Problem overview
- Explain the Producer-Consumer problem
- Define producer, consumer, bounded buffer
- Mention synchronization challenges: race conditions, overfill, underflow, thread coordination

3. Project idea
- Explain why a parking lot is used as the visualization theme
- Map concepts clearly:
  - Car Owner -> Producer
  - Security Guard -> Consumer
  - ParkingLot -> Shared bounded buffer
  - Parking capacity -> Buffer size

4. System architecture
- Show a simple layered structure:
  - Model
  - Controller
  - UI
- Mention the main active path:
  - `Main`
  - `MainDashboard`
  - `ParkingLotScenePanel`

5. Synchronization design
- Explain semaphores and mutex usage
- Include:
  - `availableSlots` blocks producers when full
  - `occupiedSlots` blocks consumers when empty
  - fair `ReentrantLock` protects the critical section
  - no busy waiting

6. Workflow of the simulation
- Step-by-step flow:
  - Producer creates a car
  - Producer parks the car if space is available
  - Consumer retrieves a car if one exists
  - UI updates in real time

7. UI and visualization features
- Show that the Swing dashboard includes:
  - live metrics
  - worker status
  - scalable parking-lot view
  - animated entry and exit
  - state-aware gate visuals
  - full/empty/waiting indicators

8. Realism vs academic correctness
- Explain that the project keeps the OS assignment strict
- The UI is improved visually, but the core behavior remains a true Producer-Consumer buffer
- Mention that visuals were adjusted to better show waiting producers and waiting consumers

9. Demo scenarios
- Balanced system
- Producer overload
- Consumer overload
- Explain what should be visible in each case

10. Key learning outcomes
- Thread synchronization
- Semaphores and locking
- Fairness and mutual exclusion
- Avoiding busy waiting
- Translating OS theory into a visual simulation

11. Conclusion
- Summarize the project value
- Emphasize that it combines concurrency correctness with understandable visualization
- End with `Questions?`

Also include:
- Simple diagrams where useful
- Speaker-note friendly structure
- A few code-themed visuals, but do not overload slides with source code
- Keep bullet points concise
