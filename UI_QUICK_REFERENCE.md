# Car Park Simulation - UI Quick Reference

## System Overview

The application now displays a **realistic car park management system** with a queue-based entry system.

### The Flow (NEW LAYOUT)

```
┌─────────────┐  ┌──────────┐  ┌──────────┐  ┌─────────────┐  ┌──────────┐
│   QUEUE     │  │  ENTRY   │  │ INSIDE   │  │  PARKING    │  │   EXIT   │
│   ROAD      │→ │   GATE   │→ │  ROAD    │→ │   LOT       │→ │   GATE   │
│ (Waiting)   │  │ (Control)│  │ (Drive)  │  │ (Storage)   │  │ (Control)│
└─────────────┘  └──────────┘  └──────────┘  └─────────────┘  └──────────┘
```

## What Each Component Does

| Component | Purpose | Visual |
|-----------|---------|--------|
| **Queue Road** | Cars wait here if lot is full | Road before entry gate |
| **Entry Gate** | Admits cars when space available | Traffic light control |
| **Inside Road** | Path for cars to find parking slots | Road with lanes & arrows |
| **Parking Lot** | Stores parked cars | Grid of slots (5/row) |
| **Exit Gate** | Releases cars when present | Traffic light control |

## Visual Indicators

### Parking Lot Slots
- 🟢 **GREEN** = Parking space is empty and available
- 🔴 **RED** = Parking space is occupied by a car

### Traffic Lights
- 🟢 **GREEN** = Gate is OPEN (cars can pass)
- ⚫ **GRAY** = Gate is CLOSED (cars cannot pass)

### Entry Gate Light
- 🟢 GREEN when: `parking_occupied < capacity` (space available)
- ⚫ GRAY when: `parking_occupied == capacity` (full, cars must wait on queue road)

### Exit Gate Light
- 🟢 GREEN when: `parking_occupied > 0` (cars present to exit)
- ⚫ GRAY when: `parking_occupied == 0` (empty, no cars)

## Real-Time Features

✅ **Automatic Queue Management**
- Queue Road shows where cars wait
- Entry gate blocks entry when full
- Automatic control based on parking status

✅ **Live Parking Status**
- Slots update color as cars enter/exit
- Shows exact number of parked cars
- Real-time visual feedback

✅ **Smooth Animation**
- Updates 60 times per second
- No lag or stuttering

## Using the Dashboard

1. **Start the simulation** - Click "Start Simulation" button
2. **Watch the queue** - See cars queue on the road when lot is full
3. **Monitor entry gate** - 🟢 opens when space available, 🔴 closes when full
4. **Check parking** - Slots change from green to red as cars park
5. **Observe exit** - Cars leave when exit gate is 🟢 green

## Understanding the Status

### Queue Road (Waiting Area)
- **When full**: Entry gate is 🔴 RED → Cars must wait on queue road
- **When has space**: Entry gate is 🟢 GREEN → Cars can enter from queue

### When Entry Gate is 🟢 GREEN
- Meaning: Parking lot has available slots
- Queue road cars can pass through
- Cars drive to find parking slots via inside road
- Look for green slots decreasing in parking lot

### When Entry Gate is ⚫ GRAY
- Meaning: Parking lot is FULL
- NO cars can enter from queue road
- Queue road will fill up with waiting cars
- All parking slots should be red (occupied)
- Security guards must remove cars to make space

### When Exit Gate is 🟢 GREEN
- Meaning: There are cars parked in the lot
- Security guards can remove cars
- Red slots should decrease as cars leave
- Entry gate may turn green (space available)

### When Exit Gate is ⚫ GRAY
- Meaning: Parking lot is EMPTY
- No cars to remove
- All slots should be green (available)

## Configuration

### Capacity
- Default: 10 parking slots
- Changeable before starting simulation
- Grid shows 5 slots per row (scales automatically)

### Producers (Car Owners)
- Number of threads generating cars
- Higher = more cars trying to enter
- Default: 3
- Cars queue on road road if entry gate is closed

### Consumers (Security Guards)
- Number of threads removing cars
- Higher = cars leaving faster
- Default: 2
- Opens exit gate when cars exist

### Production/Consumption Rate
- Slider controls how fast cars arrive/leave
- Affects system throughput
- Lower values = slower rate

## Key Difference from Old Version

**OLD**: Entry Gate → Parking Lot
- No queue system
- Cars couldn't wait visually

**NEW**: Queue Road → Entry Gate → Inside Road → Parking Lot → Exit Gate
- **Queue Road** shows where cars wait when lot is full
- **Entry Gate** controls admission with automatic traffic light
- **Inside Road** shows driving path within parking lot
- Realistic simulation of real parking behavior

## Troubleshooting

**Queue road is growing?**
- Parking lot is FULL (entry gate is 🔴 RED)
- Increase consumption rate to let cars exit
- Reduce production rate to slow arrivals

**Entry gate won't open?**
- Parking lot must be full (all red slots)
- Wait for security guards to remove cars
- Or increase consumer threads

**No cars in queue road?**
- Parking lot has space (entry gate is 🟢 GREEN)
- Increase production rate to generate more cars
- Or increase producer threads

## Key Takeaway

The UI now shows the **complete Producer-Consumer system**:

1. **Producers** (Car Owners) generate cars
2. **Queue Road** acts as waiting area when bounded buffer (parking lot) is full
3. **Entry Gate** controls admission based on buffer state
4. **Parking Lot** stores cars (bounded buffer)
5. **Exit Gate** controls departure
6. **Consumers** (Security Guards) remove cars

This is a **realistic simulation** of:
- Bounded buffer pattern (parking lot has capacity limit)
- Queue management (cars wait on queue road)
- Traffic control (automatic traffic lights)
- Real-world parking lot behavior

