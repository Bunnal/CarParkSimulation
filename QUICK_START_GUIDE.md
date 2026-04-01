# 🚀 QUICK START GUIDE - Car Park Management Simulation

## Prerequisites Check ✓

Before running, ensure you have:
- ☑ Java 17 or higher
- ☑ Maven 3.6 or higher

Check versions:
```bash
java -version    # Should show Java 17+
mvn -version     # Should show Maven 3.6+
```

## 🎯 Running the Application

### Option 1: Using Maven (Recommended)
```bash
# Navigate to project directory
cd CarParkSimulation

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

### Option 2: Using the Quick Start Script
```bash
# Make script executable (first time only)
chmod +x run.sh

# Run
./run.sh
```

### Option 3: Create and Run JAR
```bash
# Package application
mvn clean package

# Run the JAR
java -jar target/car-park-sim-1.0.0.jar
```

## 🎮 Using the Application

### Step 1: Configure Simulation Parameters

When the application opens, you'll see:

**Buffer Capacity** (5-50)
- The maximum number of cars that can be parked
- Smaller = more blocking, higher = more buffering
- Recommended: 10-15

**Number of Producers** (1-10)
- Car owners trying to park
- More producers = faster filling
- Recommended: 3

**Number of Consumers** (1-10)
- Security guards removing cars
- More consumers = faster emptying
- Recommended: 2-3

**Production Rate** (100-2000ms)
- Delay between car arrivals
- Lower = faster production
- Recommended: 500ms

**Consumption Rate** (100-2000ms)
- Delay between car removals
- Lower = faster consumption
- Recommended: 800ms

### Step 2: Start the Simulation

Click **▶ Start Simulation**

You will see:
- Progress bar showing occupancy
- Real-time statistics updating
- Thread status circles changing colors

### Step 3: Observe Thread Behavior

**Thread Status Colors:**
- 🟢 **Green**: Thread is actively working
  - Producer: Adding a car
  - Consumer: Removing a car

- 🟠 **Orange**: Thread is waiting/blocked
  - Producer: Car park is full, waiting for space
  - Consumer: Car park is empty, waiting for cars

- 🔴 **Red**: Thread has stopped

### Step 4: Adjust Rates Dynamically

While simulation is running:
- Move the **Production Rate** slider → affects all producers
- Move the **Consumption Rate** slider → affects all consumers
- Changes apply immediately!

### Step 5: Monitor Statistics

Watch the dashboard update in real-time:

**Occupancy %**: How full the car park is (0-100%)
**Available Slots**: Number of free parking spaces
**Throughput**: Cars processed per second
**Avg Wait Time**: Average time cars spend parked (in seconds)
**Total Processed**: Total number of cars that have exited

### Step 6: Stop or Reset

**⏸ Stop Simulation**: Stops all threads gracefully
**🔄 Reset**: Clears all statistics (stop first if running)

## 🧪 Recommended Test Scenarios

### Scenario A: Balanced System
```
Capacity: 15
Producers: 3
Consumers: 3
Production Rate: 500ms
Consumption Rate: 500ms
```
**Expected**: Smooth operation, ~50% occupancy, mostly green circles

### Scenario B: Producer Overload (Parking Lot Fills Up)
```
Capacity: 10
Producers: 5
Consumers: 2
Production Rate: 200ms (fast)
Consumption Rate: 1000ms (slow)
```
**Expected**: High occupancy (80-100%), many orange producer circles (blocked), few orange consumers

### Scenario C: Consumer Overload (Parking Lot Empties)
```
Capacity: 10
Producers: 2
Consumers: 5
Production Rate: 1000ms (slow)
Consumption Rate: 200ms (fast)
```
**Expected**: Low occupancy (0-20%), many orange consumer circles (blocked), few orange producers

### Scenario D: Tiny Buffer (High Contention)
```
Capacity: 5 (small!)
Producers: 4
Consumers: 4
Production Rate: 300ms
Consumption Rate: 300ms
```
**Expected**: Frequent blocking, circles constantly changing between green and orange

### Scenario E: Large Buffer (Low Contention)
```
Capacity: 50 (large!)
Producers: 3
Consumers: 3
Production Rate: 500ms
Consumption Rate: 500ms
```
**Expected**: Almost never full, rarely see orange circles, low occupancy

## 🎓 What You're Observing

### The Producer-Consumer Problem

This simulation demonstrates:

1. **Producers (Car Owners)**: Try to add items to a shared buffer
2. **Consumers (Security Guards)**: Try to remove items from the buffer
3. **Bounded Buffer (Car Park)**: Limited capacity requires synchronization

### Synchronization in Action

When you see **orange circles**:
- The thread is **blocked** on a condition variable
- It's **not spinning** or wasting CPU (no busy-waiting)
- It will be **signaled** when it can proceed

**Producers turn orange** when:
- Car park is FULL → waiting for space

**Consumers turn orange** when:
- Car park is EMPTY → waiting for cars

### Thread Safety

All operations are thread-safe using:
- **ReentrantLock**: Exclusive access to car park
- **Condition Variables**: Efficient waiting (notFull, notEmpty)
- **Try-Finally**: Guaranteed lock release

No race conditions possible!

## 📊 Understanding the Metrics

### Occupancy %
- Shows how full the car park is
- 0% = Empty
- 100% = Full
- Ideal: 40-60% (balanced system)

### Available Slots
- Current / Total capacity
- Example: "5/10" means 5 free, 5 occupied

### Throughput
- Cars exiting per second
- Higher = better performance
- Limited by consumption rate

### Average Wait Time
- How long cars stay parked (in seconds)
- Depends on both production and consumption rates
- Should stabilize over time

### Total Processed
- Count of cars that have exited
- Keeps increasing while running
- Reset button clears this

## 🐛 Troubleshooting

### Application Won't Start
```bash
# Check Java version
java -version

# Should be 17 or higher
# If not, install newer Java
```

### Maven Error
```bash
# Update Maven
brew upgrade maven  # macOS
# or download from maven.apache.org
```

### UI Freezes
- This shouldn't happen! Our design prevents UI blocking
- If it does, check Java version compatibility

### Threads Not Stopping
- Use Stop button first
- If that doesn't work, close window
- Application cleanup is automatic

### No Visual Updates
- Check that AnimationTimer is running
- Statistics should update ~10 times per second

## 💡 Tips for Best Experience

1. **Start with defaults** to get a feel for the system
2. **Try extreme scenarios** (1 producer, 10 consumers)
3. **Watch the color changes** to understand blocking
4. **Adjust rates in real-time** to see immediate effects
5. **Compare throughput** between different configurations
6. **Let it run** for 30+ seconds to see stable statistics

## 🏆 Learning Objectives

By using this application, you'll understand:

✅ How producer-consumer pattern works
✅ Why synchronization is necessary
✅ How condition variables prevent busy-waiting
✅ How threads coordinate in shared-memory systems
✅ The effect of buffer size on system performance
✅ Visual representation of thread states

## 📚 Additional Resources

- **README.md**: Complete documentation
- **TECHNICAL_DOCUMENTATION.txt**: Implementation details
- **PROJECT_SUMMARY.md**: Project overview
- **Source code comments**: Javadoc on all methods

## ❓ FAQ

**Q: Can I have 0 producers or consumers?**
A: No, minimum is 1 for each

**Q: Can I change capacity while running?**
A: No, you must stop and restart with new capacity

**Q: Can I change rates while running?**
A: Yes! Slide the rate sliders anytime

**Q: Why are some threads always orange?**
A: When production >> consumption (or vice versa), some threads stay blocked

**Q: Is this safe for concurrent access?**
A: Yes! All operations are properly synchronized with locks

**Q: What happens if I close the window?**
A: All threads stop gracefully, no resource leaks

## 🎉 Enjoy the Simulation!

Watch the Producer-Consumer problem come to life in real-time!

---

**Need Help?**
- Check the README.md for detailed documentation
- Review source code comments for implementation details
- Experiment with different configurations to learn patterns
