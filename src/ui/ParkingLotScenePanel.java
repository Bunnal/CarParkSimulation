package ui;

import controller.SimStats;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;
import model.Car;

/**
 * Scalable Swing panel that renders the parking lot and animated road traffic.
 */
public class ParkingLotScenePanel extends JPanel {

    private static final int DESIGN_WIDTH = 800;
    private static final int DESIGN_HEIGHT = 800;

    private static final Color GRASS = new Color(122, 209, 51);
    private static final Color GRASS_ACCENT = new Color(106, 189, 42);
    private static final Color LOT_BORDER = new Color(243, 222, 191);
    private static final Color LOT_OUTLINE = new Color(76, 91, 114);
    private static final Color ASPHALT = new Color(76, 92, 118);
    private static final Color ROAD = new Color(64, 74, 92);
    private static final Color ROAD_EDGE = new Color(53, 61, 75);
    private static final Color LANE_MARK = new Color(246, 199, 59);
    private static final Color WHITE_MARK = new Color(245, 245, 240);
    private static final Color BOOTH = new Color(229, 230, 234);
    private static final Color BOOTH_DARK = new Color(188, 191, 198);
    private static final Color WINDOW = new Color(93, 210, 234);
    private static final Color SHADOW = new Color(27, 35, 48, 55);
    private static final Color NOTE = new Color(96, 112, 99);

    private static final int LOT_X = 98;
    private static final int LOT_Y = 100;
    private static final int LOT_WIDTH = 604;
    private static final int LOT_HEIGHT = 534;
    private static final int LEFT_SLOT_START_X = 108;
    private static final int LEFT_SLOT_END_X = 334;
    private static final int RIGHT_SLOT_START_X = 466;
    private static final int RIGHT_SLOT_END_X = 692;
    private static final int LEFT_SLOT_CENTER_X = 196;
    private static final int RIGHT_SLOT_CENTER_X = 604;
    private static final int SLOT_TOP = 108;
    private static final int SLOT_BOTTOM = 624;

    private final List<RoadCar> animatedCars;
    private final List<IncomingCarAnimation> incomingCars;
    private final List<OutgoingCarAnimation> outgoingCars;
    private final Map<Integer, Car> knownCars;
    private final Map<Integer, Integer> slotAssignments;
    private final Set<Integer> lastParkedIds;
    private final Timer animationTimer;
    private SimStats stats;

    /**
     * Creates the parking lot renderer.
     */
    public ParkingLotScenePanel() {
        this.animatedCars = new ArrayList<>();
        this.incomingCars = new ArrayList<>();
        this.outgoingCars = new ArrayList<>();
        this.knownCars = new HashMap<>();
        this.slotAssignments = new HashMap<>();
        this.lastParkedIds = new HashSet<>();
        this.stats = new SimStats(12);

        setOpaque(false);
        setPreferredSize(new Dimension(780, 660));
        setMinimumSize(new Dimension(540, 440));

        createAnimatedCars();
        animationTimer = new Timer(16, event -> {
            updateAnimatedCars();
            updateIncomingCars();
            updateOutgoingCars();
            repaint();
        });
        animationTimer.start();
    }

    /**
     * Updates the currently displayed simulation snapshot.
     *
     * @param stats latest simulation data
     */
    public void setStats(SimStats stats) {
        if (stats != null) {
            syncParkingScene(stats);
            this.stats = stats;
            repaint();
        }
    }

    /**
     * Stops the internal animation timer.
     */
    public void stopAnimation() {
        animationTimer.stop();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        double scale = Math.min(getWidth() / (double) DESIGN_WIDTH, getHeight() / (double) DESIGN_HEIGHT);
        double translateX = (getWidth() - (DESIGN_WIDTH * scale)) * 0.5;
        double translateY = (getHeight() - (DESIGN_HEIGHT * scale)) * 0.5;

        g2.translate(translateX, translateY);
        g2.scale(scale, scale);

        drawBackground(g2);
        drawParkingLot(g2);
        drawRoad(g2);
        drawAnimatedCars(g2);
        drawIncomingCars(g2);
        drawOutgoingCars(g2);
        drawOverlay(g2);

        g2.dispose();
    }

    private void createAnimatedCars() {
        animatedCars.add(new RoadCar(-90, 720, 84, 30, 1.8, VehicleKind.BLUE_COMPACT));
        animatedCars.add(new RoadCar(860, 770, 94, 34, -2.0, VehicleKind.ORANGE_SEDAN));
        animatedCars.add(new RoadCar(280, 720, 76, 28, 1.2, VehicleKind.YELLOW_TAXI));
    }

    private void updateAnimatedCars() {
        for (RoadCar car : animatedCars) {
            car.x += car.speed;

            if (car.speed > 0 && car.x - (car.width * 0.5) > DESIGN_WIDTH + 70) {
                car.x = -100;
            } else if (car.speed < 0 && car.x + (car.width * 0.5) < -70) {
                car.x = DESIGN_WIDTH + 100;
            }
        }
    }

    private void syncParkingScene(SimStats snapshot) {
        List<Car> parkedCars = snapshot.getParkedCars();
        Map<Integer, Car> currentCars = new HashMap<>();
        Set<Integer> currentIds = new HashSet<>();
        for (Car car : parkedCars) {
            currentIds.add(car.getId());
            currentCars.put(car.getId(), car);
        }

        Set<Integer> removedIds = new HashSet<>(lastParkedIds);
        removedIds.removeAll(currentIds);

        for (Integer removedId : removedIds) {
            Integer slotIndex = slotAssignments.get(removedId);
            Car removedCar = knownCars.get(removedId);
            if (slotIndex != null && removedCar != null && !isOutgoing(removedId)) {
                removeIncomingAnimation(removedId);
                outgoingCars.add(new OutgoingCarAnimation(removedCar, slotIndex));
            }
        }

        slotAssignments.entrySet().removeIf(entry -> !currentIds.contains(entry.getKey()) && !isOutgoing(entry.getKey()));
        incomingCars.removeIf(animation -> !currentIds.contains(animation.car.getId()));
        knownCars.putAll(currentCars);

        Set<Integer> occupiedSlots = new HashSet<>(slotAssignments.values());
        for (Car car : parkedCars) {
            if (slotAssignments.containsKey(car.getId())) {
                continue;
            }

            int slotIndex = findNextOpenSlot(occupiedSlots);
            slotAssignments.put(car.getId(), slotIndex);
            occupiedSlots.add(slotIndex);

            if (!lastParkedIds.contains(car.getId()) && !isIncoming(car.getId()) && !isOutgoing(car.getId())) {
                incomingCars.add(new IncomingCarAnimation(car, slotIndex));
            }
        }

        lastParkedIds.clear();
        lastParkedIds.addAll(currentIds);
    }

    private int findNextOpenSlot(Set<Integer> occupiedSlots) {
        int slotIndex = 0;
        while (occupiedSlots.contains(slotIndex)) {
            slotIndex++;
        }
        return slotIndex;
    }

    private void updateIncomingCars() {
        Iterator<IncomingCarAnimation> iterator = incomingCars.iterator();
        while (iterator.hasNext()) {
            IncomingCarAnimation animation = iterator.next();
            animation.progress = Math.min(1.0, animation.progress + 0.018);

            if (animation.progress >= 1.0) {
                iterator.remove();
            }
        }
    }

    private void updateOutgoingCars() {
        Iterator<OutgoingCarAnimation> iterator = outgoingCars.iterator();
        while (iterator.hasNext()) {
            OutgoingCarAnimation animation = iterator.next();
            animation.progress = Math.min(1.0, animation.progress + 0.02);

            if (animation.progress >= 1.0) {
                slotAssignments.remove(animation.car.getId());
                knownCars.remove(animation.car.getId());
                iterator.remove();
            }
        }
    }

    private boolean isIncoming(int carId) {
        for (IncomingCarAnimation animation : incomingCars) {
            if (animation.car.getId() == carId) {
                return true;
            }
        }
        return false;
    }

    private boolean isOutgoing(int carId) {
        for (OutgoingCarAnimation animation : outgoingCars) {
            if (animation.car.getId() == carId) {
                return true;
            }
        }
        return false;
    }

    private void removeIncomingAnimation(int carId) {
        incomingCars.removeIf(animation -> animation.car.getId() == carId);
    }

    private void drawBackground(Graphics2D g2) {
        g2.setColor(GRASS);
        g2.fillRoundRect(0, 0, DESIGN_WIDTH, DESIGN_HEIGHT, 28, 28);

        g2.setColor(GRASS_ACCENT);
        g2.fillRoundRect(10, 10, DESIGN_WIDTH - 20, DESIGN_HEIGHT - 20, 24, 24);

        g2.setColor(new Color(238, 244, 234));
        g2.fillRoundRect(16, 16, DESIGN_WIDTH - 32, DESIGN_HEIGHT - 32, 22, 22);
    }

    private void drawParkingLot(Graphics2D g2) {
        Shape outerLot = new RoundRectangle2D.Double(55, 45, 690, 655, 24, 24);
        g2.setColor(LOT_BORDER);
        g2.fill(outerLot);

        g2.setColor(LOT_OUTLINE);
        g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(new RoundRectangle2D.Double(78, 78, 644, 589, 18, 18));

        drawBolts(g2);

        g2.setColor(ASPHALT);
        g2.fillRect(LOT_X, LOT_Y, LOT_WIDTH, LOT_HEIGHT);

        g2.setColor(new Color(225, 227, 231));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(LOT_X, LOT_Y, LOT_WIDTH, LOT_HEIGHT);

        drawParkingDividers(g2);
        drawDriveway(g2);
        drawBooth(g2);
        drawParkedCars(g2);
    }

    private void drawBolts(Graphics2D g2) {
        g2.setColor(LOT_OUTLINE);
        int diameter = 24;
        int[][] boltCenters = {
            {78, 78}, {224, 78}, {382, 78}, {540, 78}, {706, 78},
            {78, 225}, {706, 225}, {78, 388}, {706, 388}, {78, 553}, {706, 553},
            {78, 667}, {706, 667}
        };

        for (int[] center : boltCenters) {
            g2.fill(new Ellipse2D.Double(center[0] - diameter / 2.0, center[1] - diameter / 2.0, diameter, diameter));
        }
    }

    private void drawParkingDividers(Graphics2D g2) {
        g2.setColor(WHITE_MARK);
        g2.setStroke(new BasicStroke(7f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));

        int displaySlots = calculateDisplaySlots();
        int rows = Math.max(4, (displaySlots + 1) / 2);
        double slotTop = SLOT_TOP;
        double slotBottom = SLOT_BOTTOM;
        double rowPitch = (slotBottom - slotTop) / rows;

        for (int row = 0; row <= rows; row++) {
            int y = (int) Math.round(slotTop + (row * rowPitch));
            g2.drawLine(LEFT_SLOT_START_X, y, LEFT_SLOT_END_X, y);
            g2.drawLine(RIGHT_SLOT_START_X, y, RIGHT_SLOT_END_X, y);
        }

        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2.setColor(new Color(230, 234, 237));
        for (int row = 0; row < rows; row++) {
            int y = (int) Math.round(slotTop + (row * rowPitch) + (rowPitch * 0.5));
            g2.drawString(String.valueOf((row * 2) + 1), 82, y + 4);
            if ((row * 2) + 2 <= displaySlots) {
                g2.drawString(String.valueOf((row * 2) + 2), 706, y + 4);
            }
        }
    }

    private void drawDriveway(Graphics2D g2) {
        g2.setColor(ASPHALT);
        g2.fillRect(300, 625, 200, 75);

        g2.setColor(LOT_OUTLINE);
        g2.setStroke(new BasicStroke(7f));
        g2.drawRect(300, 625, 200, 75);

        g2.setColor(ROAD_EDGE);
        g2.fillRect(300, 622, 200, 18);

        g2.setColor(LANE_MARK);
        for (int x = 308; x < 486; x += 20) {
            Path2D stripe = new Path2D.Double();
            stripe.moveTo(x, 638);
            stripe.lineTo(x + 8, 626);
            stripe.lineTo(x + 15, 626);
            stripe.lineTo(x + 7, 638);
            stripe.closePath();
            g2.fill(stripe);
        }

        g2.setColor(new Color(250, 248, 244));
        g2.fill(new Ellipse2D.Double(353, 645, 40, 40));
        g2.setColor(LOT_OUTLINE);
        g2.setStroke(new BasicStroke(3f));
        g2.draw(new Ellipse2D.Double(353, 645, 40, 40));
        g2.setFont(new Font("SansSerif", Font.BOLD, 30));
        g2.drawString("P", 365, 676);

        g2.setColor(new Color(243, 208, 74));
        g2.fillRoundRect(270, 665, 16, 36, 6, 6);
        g2.setColor(LOT_OUTLINE);
        g2.fillRoundRect(276, 673, 8, 28, 4, 4);

        g2.setColor(new Color(251, 216, 81));
        g2.fillRoundRect(285, 678, 190, 10, 10, 10);
        g2.setColor(new Color(221, 86, 61));
        for (int x = 292; x < 462; x += 28) {
            Path2D stripe = new Path2D.Double();
            stripe.moveTo(x, 678);
            stripe.lineTo(x + 10, 678);
            stripe.lineTo(x + 18, 688);
            stripe.lineTo(x + 8, 688);
            stripe.closePath();
            g2.fill(stripe);
        }
    }

    private void drawBooth(Graphics2D g2) {
        g2.setColor(new Color(214, 216, 220));
        g2.fillRoundRect(515, 470, 132, 118, 12, 12);
        g2.setColor(BOOTH_DARK);
        g2.fillRoundRect(530, 484, 102, 34, 8, 8);
        g2.setColor(BOOTH);
        g2.fillRoundRect(527, 520, 106, 54, 8, 8);

        g2.setColor(new Color(245, 246, 248));
        g2.fillRoundRect(540, 532, 38, 26, 4, 4);
        g2.setColor(new Color(204, 67, 54));
        g2.fillRect(550, 536, 20, 18);
        g2.setColor(LOT_OUTLINE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(587, 535, 28, 28, 3, 3);
        g2.drawLine(601, 549, 622, 549);
        g2.drawLine(622, 549, 622, 565);

        g2.setColor(WHITE_MARK);
        for (int x = 536; x <= 614; x += 26) {
            g2.fillRoundRect(x, 476, 8, 18, 4, 4);
        }

        g2.setColor(LOT_OUTLINE);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(515, 470, 132, 118, 12, 12);
    }

    private void drawRoad(Graphics2D g2) {
        g2.setColor(ROAD_EDGE);
        g2.fillRect(0, 688, DESIGN_WIDTH, 112);
        g2.setColor(ROAD);
        g2.fillRect(0, 696, DESIGN_WIDTH, 104);

        g2.setColor(LANE_MARK);
        for (int x = 18; x < DESIGN_WIDTH; x += 46) {
            g2.fillRoundRect(x, 744, 22, 7, 4, 4);
        }
    }

    private void drawAnimatedCars(Graphics2D g2) {
        for (RoadCar car : animatedCars) {
            double angle = car.speed >= 0 ? 0 : 180;
            drawVehicle(g2, car.x, car.y, car.width, car.height, angle, car.kind, null);
        }
    }

    private void drawIncomingCars(Graphics2D g2) {
        int displaySlots = calculateDisplaySlots();
        for (IncomingCarAnimation animation : incomingCars) {
            SlotPlacement slot = slotPlacementFor(animation.slotIndex, displaySlots);
            MovingPlacement movingPlacement = incomingPlacementFor(slot, animation.progress);

            drawVehicle(g2, movingPlacement.centerX, movingPlacement.centerY, slot.width, slot.height,
                movingPlacement.angleDegrees, vehicleKindForCar(animation.car), animation.car);
            drawCarNameTag(g2, animation.car, movingPlacement.centerX, movingPlacement.centerY - 32);
        }
    }

    private void drawOutgoingCars(Graphics2D g2) {
        int displaySlots = calculateDisplaySlots();
        for (OutgoingCarAnimation animation : outgoingCars) {
            SlotPlacement slot = slotPlacementFor(animation.slotIndex, displaySlots);
            MovingPlacement movingPlacement = outgoingPlacementFor(slot, animation.progress);

            drawVehicle(g2, movingPlacement.centerX, movingPlacement.centerY, slot.width, slot.height,
                movingPlacement.angleDegrees, vehicleKindForCar(animation.car), animation.car);
            drawCarNameTag(g2, animation.car, movingPlacement.centerX, movingPlacement.centerY - 32);
        }
    }

    private void drawParkedCars(Graphics2D g2) {
        List<Car> parkedCars = stats.getParkedCars();
        if (parkedCars.isEmpty()) {
            return;
        }

        int displaySlots = calculateDisplaySlots();
        for (Car car : parkedCars) {
            if (isIncoming(car.getId())) {
                continue;
            }

            Integer slotIndex = slotAssignments.get(car.getId());
            if (slotIndex == null) {
                continue;
            }

            SlotPlacement slot = slotPlacementFor(slotIndex, displaySlots);
            drawVehicle(g2, slot.centerX, slot.centerY, slot.width, slot.height,
                slot.angleDegrees, vehicleKindForCar(car), car);
            drawCarNameTag(g2, car, slot.centerX, slot.centerY - 32);
        }
    }

    private int calculateDisplaySlots() {
        int highestAssignedSlot = -1;
        for (Integer slotIndex : slotAssignments.values()) {
            highestAssignedSlot = Math.max(highestAssignedSlot, slotIndex);
        }

        return Math.max(8, Math.max(Math.max(stats.getCapacity(), stats.getOccupied()), highestAssignedSlot + 1));
    }

    private SlotPlacement slotPlacementFor(int slotIndex, int displaySlots) {
        int rows = Math.max(4, (displaySlots + 1) / 2);
        double slotTop = SLOT_TOP;
        double slotBottom = SLOT_BOTTOM;
        double rowPitch = (slotBottom - slotTop) / rows;
        boolean leftColumn = slotIndex % 2 == 0;
        int row = slotIndex / 2;

        double centerX = leftColumn ? LEFT_SLOT_CENTER_X : RIGHT_SLOT_CENTER_X;
        double centerY = slotTop + (row * rowPitch) + (rowPitch * 0.5);
        double carWidth = 118;
        double carHeight = Math.max(26, Math.min(56, rowPitch * 0.84));
        double angleDegrees = leftColumn ? 180 : 0;

        return new SlotPlacement(centerX, centerY, carWidth, carHeight, angleDegrees);
    }

    private MovingPlacement incomingPlacementFor(SlotPlacement slot, double progress) {
        double easedProgress = smoothStep(progress);
        double entryX = 400;
        double entryY = 782;

        if (easedProgress < 0.62) {
            double laneProgress = easedProgress / 0.62;
            double y = interpolate(entryY, slot.centerY, smoothStep(laneProgress));
            return new MovingPlacement(entryX, y, -90);
        }

        double turnProgress = (easedProgress - 0.62) / 0.38;
        double easedTurn = smoothStep(turnProgress);
        double x = interpolate(entryX, slot.centerX, easedTurn);
        double angle = interpolate(-90, slot.angleDegrees, easedTurn);
        return new MovingPlacement(x, slot.centerY, angle);
    }

    private MovingPlacement outgoingPlacementFor(SlotPlacement slot, double progress) {
        double easedProgress = smoothStep(progress);
        double laneX = 400;
        double exitY = 782;

        if (easedProgress < 0.42) {
            double turnProgress = easedProgress / 0.42;
            double easedTurn = smoothStep(turnProgress);
            double x = interpolate(slot.centerX, laneX, easedTurn);
            double angle = interpolate(slot.angleDegrees, 90, easedTurn);
            return new MovingPlacement(x, slot.centerY, angle);
        }

        double driveProgress = (easedProgress - 0.42) / 0.58;
        double y = interpolate(slot.centerY, exitY, smoothStep(driveProgress));
        return new MovingPlacement(laneX, y, 90);
    }

    private void drawCarNameTag(Graphics2D g2, Car car, double centerX, double centerY) {
        if (car == null) {
            return;
        }

        String label = car.getDisplayName();
        Graphics2D tag = (Graphics2D) g2.create();
        tag.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics metrics = tag.getFontMetrics();
        int textWidth = metrics.stringWidth(label);
        int tagWidth = textWidth + 16;
        int tagHeight = 18;
        int x = (int) Math.round(centerX - (tagWidth * 0.5));
        int y = (int) Math.round(centerY - tagHeight);

        tag.setColor(new Color(29, 40, 54, 210));
        tag.fillRoundRect(x + 2, y + 2, tagWidth, tagHeight, 10, 10);
        tag.setColor(new Color(250, 248, 244));
        tag.fillRoundRect(x, y, tagWidth, tagHeight, 10, 10);
        tag.setColor(new Color(47, 61, 80));
        tag.drawRoundRect(x, y, tagWidth, tagHeight, 10, 10);
        tag.drawString(label, x + 8, y + 13);
        tag.dispose();
    }

    private double interpolate(double start, double end, double progress) {
        return start + ((end - start) * progress);
    }

    private double smoothStep(double value) {
        double clamped = Math.max(0.0, Math.min(1.0, value));
        return clamped * clamped * (3.0 - (2.0 * clamped));
    }

    private void drawOverlay(Graphics2D g2) {
        g2.setColor(new Color(36, 48, 65));
        g2.setFont(new Font("SansSerif", Font.BOLD, 18));
        g2.drawString("Top-Down Parking Lot View", 84, 38);

        g2.setColor(NOTE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("The layout scales with the window, while Swing timers keep the road animation smooth.", 84, 58);
    }

    private VehicleKind vehicleKindForCar(Car car) {
        return switch (car.getVehicleType()) {
            case SEDAN -> VehicleKind.BLUE_COMPACT;
            case SUV -> VehicleKind.BEIGE_SUV;
            case TAXI -> VehicleKind.YELLOW_TAXI;
            case VAN -> VehicleKind.WHITE_VAN;
            case EV -> VehicleKind.GREEN_CAR;
            case TRUCK -> VehicleKind.RED_TRUCK;
        };
    }

    private void drawVehicle(Graphics2D g2, double centerX, double centerY, double width, double height,
                             double angleDegrees, VehicleKind kind, Car car) {
        Graphics2D vehicle = (Graphics2D) g2.create();
        vehicle.translate(centerX, centerY);
        vehicle.rotate(Math.toRadians(angleDegrees));

        if (kind == VehicleKind.GREEN_TRUCK || kind == VehicleKind.RED_TRUCK) {
            drawTruck(vehicle, width, height, kind, car);
        } else {
            drawCar(vehicle, width, height, kind, car);
        }

        vehicle.dispose();
    }

    private void drawCar(Graphics2D g2, double width, double height, VehicleKind kind, Car car) {
        VehiclePalette palette = paletteFor(kind);

        Shape shadow = new RoundRectangle2D.Double(-width * 0.5 + 6, -height * 0.5 + 6,
            width, height, height * 0.5, height * 0.5);
        g2.setColor(SHADOW);
        g2.fill(shadow);

        Shape body = new RoundRectangle2D.Double(-width * 0.5, -height * 0.5,
            width, height, height * 0.5, height * 0.5);
        g2.setColor(palette.body);
        g2.fill(body);
        g2.setColor(palette.outline);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(body);

        g2.setColor(new Color(251, 248, 242));
        g2.fillRoundRect((int) (-width * 0.46), (int) (-height * 0.27), 14, (int) (height * 0.54), 6, 6);
        g2.fillRoundRect((int) (width * 0.32), (int) (-height * 0.27), 14, (int) (height * 0.54), 6, 6);

        g2.setColor(new Color(43, 48, 57));
        int wheelWidth = (int) Math.max(12, width * 0.11);
        int wheelHeight = (int) Math.max(8, height * 0.2);
        g2.fillRoundRect((int) (-width * 0.27), (int) (-height * 0.58), wheelWidth, wheelHeight, 6, 6);
        g2.fillRoundRect((int) (width * 0.12), (int) (-height * 0.58), wheelWidth, wheelHeight, 6, 6);
        g2.fillRoundRect((int) (-width * 0.27), (int) (height * 0.38), wheelWidth, wheelHeight, 6, 6);
        g2.fillRoundRect((int) (width * 0.12), (int) (height * 0.38), wheelWidth, wheelHeight, 6, 6);

        Shape cabin = new RoundRectangle2D.Double(-width * 0.18, -height * 0.3,
            width * 0.36, height * 0.6, height * 0.22, height * 0.22);
        g2.setColor(palette.roof);
        g2.fill(cabin);
        g2.setColor(palette.outline);
        g2.draw(cabin);

        g2.setColor(WINDOW);
        g2.fillRoundRect((int) (-width * 0.15), (int) (-height * 0.22),
            (int) (width * 0.3), (int) (height * 0.18), 8, 8);
        g2.fillRoundRect((int) (-width * 0.15), (int) (height * 0.04),
            (int) (width * 0.3), (int) (height * 0.18), 8, 8);

        switch (kind) {
            case AMBULANCE -> drawAmbulanceDetails(g2, width, height);
            case YELLOW_TAXI -> drawTaxiDetails(g2, width, height);
            case GREEN_CAR -> drawGreenCarDetails(g2, width, height);
            case RED_CAR, ORANGE_SEDAN -> drawSportStripe(g2, width, height, palette.accent);
            case BEIGE_SUV -> drawSuvDetails(g2, width, height);
            case WHITE_SEDAN, WHITE_VAN, BLUE_COMPACT -> drawCenterAccent(g2, width, height, palette.accent);
            default -> {
            }
        }

        if (car != null) {
            g2.setColor(new Color(36, 48, 65));
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, (int) (height * 0.28))));
            String indexLabel = "#" + car.getId();
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(indexLabel, -metrics.stringWidth(indexLabel) / 2, 4);
        }
    }

    private void drawTruck(Graphics2D g2, double width, double height, VehicleKind kind, Car car) {
        VehiclePalette palette = paletteFor(kind);

        g2.setColor(SHADOW);
        g2.fill(new RoundRectangle2D.Double(-width * 0.5 + 7, -height * 0.5 + 6,
            width, height, height * 0.3, height * 0.3));

        double trailerWidth = width * 0.68;
        Shape trailer = new RoundRectangle2D.Double(-width * 0.5, -height * 0.5,
            trailerWidth, height, height * 0.25, height * 0.25);
        Shape cab = new RoundRectangle2D.Double(width * 0.18, -height * 0.42,
            width * 0.32, height * 0.84, height * 0.25, height * 0.25);

        g2.setColor(palette.body);
        g2.fill(trailer);
        g2.fill(cab);
        g2.setColor(palette.outline);
        g2.setStroke(new BasicStroke(2f));
        g2.draw(trailer);
        g2.draw(cab);

        g2.setColor(WINDOW);
        g2.fillRoundRect((int) (width * 0.24), (int) (-height * 0.2),
            (int) (width * 0.18), (int) (height * 0.4), 8, 8);
        g2.setColor(new Color(243, 241, 233));
        g2.fillRoundRect((int) (-width * 0.39), (int) (-height * 0.18),
            (int) (width * 0.4), (int) (height * 0.36), 10, 10);

        g2.setColor(new Color(43, 48, 57));
        int wheelWidth = (int) Math.max(14, width * 0.08);
        int wheelHeight = (int) Math.max(9, height * 0.18);
        g2.fillRoundRect((int) (-width * 0.32), (int) (-height * 0.58), wheelWidth, wheelHeight, 5, 5);
        g2.fillRoundRect((int) (-width * 0.1), (int) (-height * 0.58), wheelWidth, wheelHeight, 5, 5);
        g2.fillRoundRect((int) (width * 0.23), (int) (-height * 0.58), wheelWidth, wheelHeight, 5, 5);
        g2.fillRoundRect((int) (-width * 0.32), (int) (height * 0.4), wheelWidth, wheelHeight, 5, 5);
        g2.fillRoundRect((int) (-width * 0.1), (int) (height * 0.4), wheelWidth, wheelHeight, 5, 5);
        g2.fillRoundRect((int) (width * 0.23), (int) (height * 0.4), wheelWidth, wheelHeight, 5, 5);

        if (kind == VehicleKind.GREEN_TRUCK) {
            g2.setColor(new Color(250, 210, 72));
            g2.fillRoundRect((int) (-width * 0.17), (int) (-height * 0.21),
                (int) (width * 0.2), (int) (height * 0.42), 6, 6);
        } else {
            g2.setColor(new Color(239, 132, 129));
            g2.fillRoundRect((int) (-width * 0.16), (int) (-height * 0.22),
                (int) (width * 0.16), (int) (height * 0.44), 10, 10);
            g2.fillRoundRect((int) (width * 0.05), (int) (-height * 0.22),
                (int) (width * 0.16), (int) (height * 0.44), 10, 10);
        }

        if (car != null) {
            g2.setColor(new Color(36, 48, 65));
            g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(8, (int) (height * 0.24))));
            String indexLabel = "#" + car.getId();
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(indexLabel, -metrics.stringWidth(indexLabel) / 2, 4);
        }
    }

    private void drawAmbulanceDetails(Graphics2D g2, double width, double height) {
        g2.setColor(new Color(227, 75, 63));
        g2.fillRect((int) (-width * 0.42), (int) (-height * 0.08), (int) (width * 0.84), (int) (height * 0.16));
        g2.fillRect(-8, (int) (-height * 0.26), 16, (int) (height * 0.52));
        g2.fillRect((int) (-width * 0.15), -8, (int) (width * 0.3), 16);
        g2.setColor(new Color(78, 149, 247));
        g2.fillRoundRect((int) (-width * 0.05), (int) (-height * 0.44), 12, 8, 4, 4);
        g2.fillRoundRect((int) (width * 0.02), (int) (-height * 0.44), 12, 8, 4, 4);
    }

    private void drawTaxiDetails(Graphics2D g2, double width, double height) {
        g2.setColor(new Color(32, 38, 49));
        int x = (int) (-width * 0.4);
        int y = (int) (-height * 0.36);
        int cellWidth = 10;
        for (int i = 0; i < 6; i++) {
            if (i % 2 == 0) {
                g2.fillRect(x + (i * cellWidth), y, cellWidth, 8);
            }
        }
        g2.fillRoundRect(-10, (int) (-height * 0.44), 20, 8, 4, 4);
    }

    private void drawGreenCarDetails(Graphics2D g2, double width, double height) {
        g2.setColor(new Color(245, 208, 73));
        g2.fillRoundRect((int) (-width * 0.18), (int) (-height * 0.15),
            (int) (width * 0.36), (int) (height * 0.3), 6, 6);
        g2.setColor(new Color(94, 176, 38));
        for (int x = -36; x <= 30; x += 12) {
            g2.drawLine(x, (int) (-height * 0.38), x + 8, (int) (-height * 0.32));
        }
    }

    private void drawSportStripe(Graphics2D g2, double width, double height, Color stripeColor) {
        g2.setColor(stripeColor);
        g2.fillRoundRect(-8, (int) (-height * 0.38), 16, (int) (height * 0.76), 8, 8);
    }

    private void drawSuvDetails(Graphics2D g2, double width, double height) {
        g2.setColor(new Color(204, 182, 136));
        g2.fillRoundRect((int) (-width * 0.12), (int) (-height * 0.16),
            (int) (width * 0.24), (int) (height * 0.32), 7, 7);
        g2.setColor(LOT_OUTLINE);
        g2.drawLine((int) (-width * 0.18), (int) (-height * 0.4), (int) (-width * 0.18), (int) (height * 0.4));
        g2.drawLine((int) (width * 0.18), (int) (-height * 0.4), (int) (width * 0.18), (int) (height * 0.4));
    }

    private void drawCenterAccent(Graphics2D g2, double width, double height, Color accent) {
        g2.setColor(accent);
        g2.fillRoundRect(-7, (int) (-height * 0.34), 14, (int) (height * 0.68), 7, 7);
    }

    private VehiclePalette paletteFor(VehicleKind kind) {
        return switch (kind) {
            case AMBULANCE -> new VehiclePalette(new Color(245, 245, 239), new Color(237, 241, 242),
                new Color(52, 202, 236), new Color(194, 69, 57));
            case GREEN_CAR -> new VehiclePalette(new Color(129, 205, 46), new Color(166, 223, 71),
                new Color(247, 211, 74), new Color(90, 126, 44));
            case RED_CAR -> new VehiclePalette(new Color(223, 69, 49), new Color(239, 91, 72),
                new Color(76, 204, 234), new Color(162, 46, 35));
            case BEIGE_SUV -> new VehiclePalette(new Color(244, 233, 209), new Color(250, 242, 225),
                new Color(203, 180, 136), new Color(176, 153, 118));
            case YELLOW_TAXI -> new VehiclePalette(new Color(251, 197, 34), new Color(255, 214, 71),
                new Color(49, 55, 67), new Color(190, 146, 20));
            case GREEN_TRUCK -> new VehiclePalette(new Color(111, 212, 43), new Color(85, 163, 40),
                new Color(252, 212, 70), new Color(77, 125, 47));
            case RED_TRUCK -> new VehiclePalette(new Color(226, 60, 50), new Color(234, 89, 74),
                new Color(239, 140, 136), new Color(170, 42, 34));
            case WHITE_SEDAN -> new VehiclePalette(new Color(244, 243, 240), new Color(251, 250, 247),
                new Color(84, 183, 249), new Color(183, 182, 179));
            case WHITE_VAN -> new VehiclePalette(new Color(236, 236, 233), new Color(249, 249, 247),
                new Color(87, 177, 240), new Color(179, 179, 176));
            case BLUE_COMPACT -> new VehiclePalette(new Color(44, 170, 224), new Color(80, 202, 239),
                new Color(252, 226, 88), new Color(35, 105, 148));
            case ORANGE_SEDAN -> new VehiclePalette(new Color(231, 124, 53), new Color(244, 155, 84),
                new Color(248, 223, 85), new Color(176, 88, 37));
        };
    }

    private static final class IncomingCarAnimation {
        private final Car car;
        private final int slotIndex;
        private double progress;

        private IncomingCarAnimation(Car car, int slotIndex) {
            this.car = car;
            this.slotIndex = slotIndex;
            this.progress = 0.0;
        }
    }

    private static final class OutgoingCarAnimation {
        private final Car car;
        private final int slotIndex;
        private double progress;

        private OutgoingCarAnimation(Car car, int slotIndex) {
            this.car = car;
            this.slotIndex = slotIndex;
            this.progress = 0.0;
        }
    }

    private static final class SlotPlacement {
        private final double centerX;
        private final double centerY;
        private final double width;
        private final double height;
        private final double angleDegrees;

        private SlotPlacement(double centerX, double centerY, double width, double height, double angleDegrees) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.width = width;
            this.height = height;
            this.angleDegrees = angleDegrees;
        }
    }

    private static final class MovingPlacement {
        private final double centerX;
        private final double centerY;
        private final double angleDegrees;

        private MovingPlacement(double centerX, double centerY, double angleDegrees) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.angleDegrees = angleDegrees;
        }
    }

    private enum VehicleKind {
        AMBULANCE,
        GREEN_CAR,
        RED_CAR,
        BEIGE_SUV,
        YELLOW_TAXI,
        GREEN_TRUCK,
        RED_TRUCK,
        WHITE_SEDAN,
        WHITE_VAN,
        BLUE_COMPACT,
        ORANGE_SEDAN
    }

    private static final class VehiclePalette {
        private final Color body;
        private final Color roof;
        private final Color accent;
        private final Color outline;

        private VehiclePalette(Color body, Color roof, Color accent, Color outline) {
            this.body = body;
            this.roof = roof;
            this.accent = accent;
            this.outline = outline;
        }
    }

    private static final class RoadCar {
        private double x;
        private final double y;
        private final double width;
        private final double height;
        private final double speed;
        private final VehicleKind kind;

        private RoadCar(double x, double y, double width, double height, double speed, VehicleKind kind) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.speed = speed;
            this.kind = kind;
        }
    }
}
