package ui;

import controller.SimStats;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import model.Car;

/**
 * Custom JavaFX parking-lot view rendered on a canvas.
 */
public class ParkingLotCanvasView extends StackPane {

    private static final double CANVAS_WIDTH = 760;
    private static final double CANVAS_HEIGHT = 620;

    private final Canvas canvas;
    private SimStats currentStats;

    /**
     * Creates the custom parking-lot renderer.
     */
    public ParkingLotCanvasView() {
        this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        this.currentStats = new SimStats(12);

        getStyleClass().add("parking-wrap");
        setMinSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        setPrefSize(CANVAS_WIDTH, CANVAS_HEIGHT);
        getChildren().add(canvas);

        draw();
    }

    /**
     * Renders the latest simulation snapshot.
     *
     * @param stats current simulation snapshot
     */
    public void render(SimStats stats) {
        this.currentStats = stats == null ? currentStats : stats;
        draw();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        drawBackground(gc);
        drawRoad(gc);
        drawParkingLot(gc);
        drawDriveway(gc);
        drawSlots(gc);
        drawParkedCars(gc);
        drawOverlayText(gc);
    }

    private void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.web("#7ac24a"));
        gc.fillRoundRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, 28, 28);

        gc.setFill(Color.web("#70b742"));
        gc.fillRoundRect(20, 20, CANVAS_WIDTH - 40, CANVAS_HEIGHT - 40, 24, 24);

        gc.setFill(Color.web("#eef4ea"));
        gc.fillRoundRect(32, 32, CANVAS_WIDTH - 64, CANVAS_HEIGHT - 64, 22, 22);
    }

    private void drawRoad(GraphicsContext gc) {
        double roadTop = 510;
        gc.setFill(Color.web("#4b5667"));
        gc.fillRect(0, roadTop, CANVAS_WIDTH, CANVAS_HEIGHT - roadTop);

        gc.setFill(Color.web("#404958"));
        gc.fillRect(0, roadTop + 8, CANVAS_WIDTH, CANVAS_HEIGHT - roadTop - 8);

        gc.setFill(Color.web("#f1c44c"));
        for (double x = 24; x < CANVAS_WIDTH; x += 44) {
            gc.fillRoundRect(x, roadTop + 50, 22, 6, 6, 6);
        }

        gc.setFill(Color.web("#f6f7f8"));
        gc.fillRect(0, roadTop + 10, CANVAS_WIDTH, 4);
        gc.fillRect(0, CANVAS_HEIGHT - 8, CANVAS_WIDTH, 4);

        drawRoadCar(gc, 130, roadTop + 26, 86, 28, Color.web("#2474c7"), Color.web("#7fd4f7"));
        drawRoadCar(gc, 620, roadTop + 78, 96, 30, Color.web("#db6b3d"), Color.web("#ffd84e"));
    }

    private void drawParkingLot(GraphicsContext gc) {
        gc.setFill(Color.web("#f0ddbd"));
        gc.fillRoundRect(68, 30, 624, 500, 26, 26);

        gc.setStroke(Color.web("#49566a"));
        gc.setLineWidth(8);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeRoundRect(92, 56, 576, 434, 18, 18);

        gc.setFill(Color.web("#4f5d73"));
        gc.fillRect(118, 84, 524, 348);

        gc.setStroke(Color.web("#d9dce1"));
        gc.setLineWidth(3);
        gc.strokeRect(118, 84, 524, 348);

        drawBolts(gc);
    }

    private void drawBolts(GraphicsContext gc) {
        gc.setFill(Color.web("#49566a"));
        double[][] boltCenters = {
            {92, 56}, {214, 56}, {338, 56}, {462, 56}, {668, 56},
            {92, 168}, {668, 168}, {92, 288}, {668, 288}, {92, 490}, {668, 490}
        };

        for (double[] boltCenter : boltCenters) {
            gc.fillOval(boltCenter[0] - 12, boltCenter[1] - 12, 24, 24);
        }
    }

    private void drawDriveway(GraphicsContext gc) {
        double drivewayX = 308;
        double drivewayY = 432;
        double drivewayWidth = 144;
        double drivewayHeight = 98;

        gc.setFill(Color.web("#4f5d73"));
        gc.fillRect(drivewayX, drivewayY, drivewayWidth, drivewayHeight);

        gc.setStroke(Color.web("#49566a"));
        gc.setLineWidth(7);
        gc.strokeRect(drivewayX, drivewayY, drivewayWidth, drivewayHeight);

        gc.setFill(Color.web("#3f4855"));
        gc.fillRect(drivewayX, drivewayY - 4, drivewayWidth, 18);

        gc.setFill(Color.web("#f1c44c"));
        for (double x = drivewayX + 10; x < drivewayX + drivewayWidth - 8; x += 18) {
            gc.fillPolygon(new double[]{x, x + 8, x + 16, x + 8},
                new double[]{drivewayY + 18, drivewayY + 6, drivewayY + 6, drivewayY + 18}, 4);
        }

        gc.setFill(Color.web("#f9f7f1"));
        gc.fillOval(drivewayX + 52, drivewayY + 34, 40, 40);
        gc.setStroke(Color.web("#49566a"));
        gc.setLineWidth(3);
        gc.strokeOval(drivewayX + 52, drivewayY + 34, 40, 40);
        gc.setFill(Color.web("#49566a"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        gc.fillText("P", drivewayX + 72, drivewayY + 64);

        gc.setFill(Color.web("#f5d854"));
        gc.fillRoundRect(drivewayX - 30, drivewayY + 52, 16, 34, 6, 6);
        gc.setFill(Color.web("#48556a"));
        gc.fillRoundRect(drivewayX - 24, drivewayY + 60, 8, 26, 4, 4);

        gc.setFill(Color.web("#f5d854"));
        gc.fillRoundRect(drivewayX - 12, drivewayY + 66, 186, 10, 10, 10);
        gc.setFill(Color.web("#df6454"));
        for (double x = drivewayX - 4; x < drivewayX + 160; x += 26) {
            gc.fillPolygon(new double[]{x, x + 10, x + 18, x + 8},
                new double[]{drivewayY + 66, drivewayY + 66, drivewayY + 76, drivewayY + 76}, 4);
        }
    }

    private void drawSlots(GraphicsContext gc) {
        int displaySlots = Math.max(8, Math.max(currentStats.getCapacity(), currentStats.getOccupied()));
        int rows = Math.max(4, (displaySlots + 1) / 2);

        double slotTop = 102;
        double slotBottom = 420;
        double rowPitch = (slotBottom - slotTop) / rows;

        gc.setStroke(Color.web("#f4f6f8"));
        gc.setLineWidth(7);
        gc.setLineCap(StrokeLineCap.BUTT);

        for (int row = 0; row <= rows; row++) {
            double y = slotTop + (row * rowPitch);
            gc.strokeLine(128, y, 290, y);
            gc.strokeLine(470, y, 632, y);
        }

        gc.setFill(Color.web("#e7ebee"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        for (int row = 0; row < rows; row++) {
            double y = slotTop + (row * rowPitch) + (rowPitch / 2);
            gc.fillText(String.valueOf((row * 2) + 1), 105, y);

            if ((row * 2) + 2 <= displaySlots) {
                gc.fillText(String.valueOf((row * 2) + 2), 655, y);
            }
        }
    }

    private void drawParkedCars(GraphicsContext gc) {
        java.util.List<Car> parkedCars = currentStats.getParkedCars();
        if (parkedCars.isEmpty()) {
            return;
        }

        int displaySlots = Math.max(currentStats.getCapacity(), currentStats.getOccupied());
        int rows = Math.max(4, (displaySlots + 1) / 2);
        double slotTop = 102;
        double slotBottom = 420;
        double rowPitch = (slotBottom - slotTop) / rows;
        double carWidth = 102;
        double carHeight = Math.max(18, Math.min(34, rowPitch * 0.62));

        for (int index = 0; index < parkedCars.size(); index++) {
            Car car = parkedCars.get(index);
            boolean leftColumn = index % 2 == 0;
            int row = index / 2;

            double centerX = leftColumn ? 208 : 552;
            double centerY = slotTop + (row * rowPitch) + (rowPitch / 2);
            double angle = (car.getId() % 3 == 0) ? -4 : (car.getId() % 3 == 1 ? 0 : 4);

            drawVehicle(gc, car, centerX, centerY, carWidth, carHeight, angle);
        }
    }

    private void drawOverlayText(GraphicsContext gc) {
        gc.setFill(Color.web("#243041"));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Top-Down Parking Lot View", 86, 36);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
        gc.setFill(Color.web("#607063"));
        gc.fillText(
            "Semaphores coordinate full/empty slots while the fair mutex protects the shared buffer.",
            86, 58);
    }

    private void drawRoadCar(GraphicsContext gc, double centerX, double centerY, double width,
                             double height, Color body, Color accent) {
        gc.save();
        gc.translate(centerX, centerY);

        gc.setFill(Color.color(0, 0, 0, 0.12));
        gc.fillRoundRect((-width / 2) + 4, (-height / 2) + 3, width, height, height, height);

        gc.setFill(body);
        gc.fillRoundRect(-width / 2, -height / 2, width, height, height, height);
        gc.setStroke(Color.web("#344150"));
        gc.setLineWidth(2);
        gc.strokeRoundRect(-width / 2, -height / 2, width, height, height, height);

        gc.setFill(accent);
        gc.fillRoundRect(-width * 0.16, -height * 0.28, width * 0.32, height * 0.56, 10, 10);

        gc.restore();
    }

    private void drawVehicle(GraphicsContext gc, Car car, double centerX, double centerY,
                             double width, double height, double angleDegrees) {
        gc.save();
        gc.translate(centerX, centerY);
        gc.rotate(angleDegrees);

        VehiclePalette palette = paletteFor(car);
        if (car.getVehicleType() == Car.VehicleType.TRUCK) {
            drawTruck(gc, car, width, height, palette);
        } else {
            drawCar(gc, car, width, height, palette);
        }

        gc.restore();
    }

    private void drawCar(GraphicsContext gc, Car car, double width, double height, VehiclePalette palette) {
        gc.setFill(Color.color(0, 0, 0, 0.16));
        gc.fillRoundRect((-width / 2) + 4, (-height / 2) + 3, width, height, height, height);

        gc.setFill(palette.body);
        gc.fillRoundRect(-width / 2, -height / 2, width, height, height, height);
        gc.setStroke(palette.outline);
        gc.setLineWidth(2);
        gc.strokeRoundRect(-width / 2, -height / 2, width, height, height, height);

        gc.setFill(Color.web("#fbf8f2"));
        gc.fillRoundRect(-width * 0.46, -height * 0.24, 12, height * 0.48, 6, 6);
        gc.fillRoundRect(width * 0.34, -height * 0.24, 12, height * 0.48, 6, 6);

        gc.setFill(Color.web("#262d37"));
        gc.fillRoundRect(-width * 0.28, -height * 0.58, width * 0.14, height * 0.18, 5, 5);
        gc.fillRoundRect(width * 0.1, -height * 0.58, width * 0.14, height * 0.18, 5, 5);
        gc.fillRoundRect(-width * 0.28, height * 0.40, width * 0.14, height * 0.18, 5, 5);
        gc.fillRoundRect(width * 0.1, height * 0.40, width * 0.14, height * 0.18, 5, 5);

        gc.setFill(palette.roof);
        gc.fillRoundRect(-width * 0.16, -height * 0.28, width * 0.32, height * 0.56, 10, 10);
        gc.setStroke(palette.outline);
        gc.strokeRoundRect(-width * 0.16, -height * 0.28, width * 0.32, height * 0.56, 10, 10);

        gc.setFill(Color.web("#76d4f2"));
        gc.fillRoundRect(-width * 0.14, -height * 0.22, width * 0.28, height * 0.16, 8, 8);
        gc.fillRoundRect(-width * 0.14, height * 0.05, width * 0.28, height * 0.16, 8, 8);

        gc.setFill(palette.accent);
        switch (car.getVehicleType()) {
            case TAXI -> {
                for (int i = 0; i < 5; i++) {
                    if (i % 2 == 0) {
                        gc.fillRect(-width * 0.38 + (i * 8), -height * 0.34, 8, 6);
                    }
                }
                gc.fillRoundRect(-10, -height * 0.45, 20, 8, 4, 4);
            }
            case EV -> gc.fillRoundRect(-6, -height * 0.36, 12, height * 0.72, 6, 6);
            case SUV -> {
                gc.fillRoundRect(-width * 0.08, -height * 0.16, width * 0.16, height * 0.32, 6, 6);
                gc.strokeLine(-width * 0.18, -height * 0.36, -width * 0.18, height * 0.36);
                gc.strokeLine(width * 0.18, -height * 0.36, width * 0.18, height * 0.36);
            }
            case VAN -> gc.fillRoundRect(-5, -height * 0.30, 10, height * 0.60, 6, 6);
            default -> gc.fillRoundRect(-4, -height * 0.34, 8, height * 0.68, 6, 6);
        }

        gc.setFill(Color.web("#243041"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, Math.max(8, height * 0.36)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(car.getLicensePlate().substring(0, 3), 0, 1);
    }

    private void drawTruck(GraphicsContext gc, Car car, double width, double height, VehiclePalette palette) {
        gc.setFill(Color.color(0, 0, 0, 0.16));
        gc.fillRoundRect((-width / 2) + 5, (-height / 2) + 4, width, height, height * 0.6, height * 0.6);

        double trailerWidth = width * 0.64;
        gc.setFill(palette.body);
        gc.fillRoundRect(-width / 2, -height / 2, trailerWidth, height, 12, 12);
        gc.fillRoundRect(width * 0.18, -height * 0.42, width * 0.32, height * 0.84, 12, 12);

        gc.setStroke(palette.outline);
        gc.setLineWidth(2);
        gc.strokeRoundRect(-width / 2, -height / 2, trailerWidth, height, 12, 12);
        gc.strokeRoundRect(width * 0.18, -height * 0.42, width * 0.32, height * 0.84, 12, 12);

        gc.setFill(Color.web("#f6f3ec"));
        gc.fillRoundRect(-width * 0.38, -height * 0.18, width * 0.38, height * 0.36, 10, 10);

        gc.setFill(Color.web("#76d4f2"));
        gc.fillRoundRect(width * 0.24, -height * 0.20, width * 0.18, height * 0.40, 8, 8);

        gc.setFill(Color.web("#262d37"));
        gc.fillRoundRect(-width * 0.32, -height * 0.58, width * 0.10, height * 0.18, 5, 5);
        gc.fillRoundRect(-width * 0.10, -height * 0.58, width * 0.10, height * 0.18, 5, 5);
        gc.fillRoundRect(width * 0.22, -height * 0.58, width * 0.10, height * 0.18, 5, 5);
        gc.fillRoundRect(-width * 0.32, height * 0.40, width * 0.10, height * 0.18, 5, 5);
        gc.fillRoundRect(-width * 0.10, height * 0.40, width * 0.10, height * 0.18, 5, 5);
        gc.fillRoundRect(width * 0.22, height * 0.40, width * 0.10, height * 0.18, 5, 5);

        gc.setFill(palette.accent);
        gc.fillRoundRect(-width * 0.16, -height * 0.20, width * 0.16, height * 0.40, 8, 8);

        gc.setFill(Color.web("#243041"));
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, Math.max(8, height * 0.32)));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(car.getLicensePlate().substring(0, 3), -width * 0.18, 0);
    }

    private VehiclePalette paletteFor(Car car) {
        return switch (car.getVehicleType()) {
            case SEDAN -> new VehiclePalette(Color.web("#2a7ad1"), Color.web("#65c6ef"),
                Color.web("#f7d45d"), Color.web("#1d4f84"));
            case SUV -> new VehiclePalette(Color.web("#e7d9bc"), Color.web("#f4eddc"),
                Color.web("#c7b189"), Color.web("#aa9b7d"));
            case TAXI -> new VehiclePalette(Color.web("#f0bc1d"), Color.web("#ffd85e"),
                Color.web("#1f242c"), Color.web("#b68c19"));
            case VAN -> new VehiclePalette(Color.web("#f2f1ee"), Color.web("#f9f8f4"),
                Color.web("#78bcf2"), Color.web("#b8b5ae"));
            case EV -> new VehiclePalette(Color.web("#79c92e"), Color.web("#a2e04d"),
                Color.web("#f1d15c"), Color.web("#5d8f2c"));
            case TRUCK -> new VehiclePalette(Color.web("#db5144"), Color.web("#ed8179"),
                Color.web("#f4b24c"), Color.web("#a53e36"));
        };
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
}
