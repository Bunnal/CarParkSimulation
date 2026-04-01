package ui;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.CarPark;

/**
 * Complete car park visualization showing the entire system flow.
 * Displays: Queue Road → Entry Gate → Parking Lot → Exit Road
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class CarParkVisualizer extends VBox {
    
    private final CarPark carPark;
    private final EntryGate entryGate;
    private final ExitGate exitGate;
    private final RoadPathway queueRoad;
    private final RoadPathway parkingLotRoad;
    private final RoadPathway exitRoad;
    private final ParkingLotVisual parkingLot;
    private AnimationTimer updateTimer;
    
    /**
     * Creates a new car park visualizer.
     * 
     * @param carPark The CarPark model to visualize
     */
    public CarParkVisualizer(CarPark carPark) {
        this.carPark = carPark;
        
        // Create visual components
        this.entryGate = new EntryGate();
        this.exitGate = new ExitGate();
        this.queueRoad = RoadPathway.createEntryRoad(150);
        this.parkingLotRoad = RoadPathway.createEntryRoad(120);
        this.exitRoad = RoadPathway.createExitRoad(150);
        this.parkingLot = new ParkingLotVisual(carPark);
        
        setupLayout();
        startUpdateTimer();
    }
    
    /**
     * Sets up the layout with all components.
     */
    private void setupLayout() {
        this.setStyle("-fx-background-color: #f9f9f9;");
        this.setPadding(new Insets(15));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);
        
        // Title
        Text title = new Text("Car Park Management System");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setFill(Color.web("#333"));
        
        // Main flow layout (horizontal)
        HBox flowBox = createFlowLayout();
        
        this.getChildren().addAll(title, flowBox);
    }
    
    /**
     * Creates the main flow layout showing the complete system.
     */
    private HBox createFlowLayout() {
        HBox flowBox = new HBox(8);
        flowBox.setAlignment(Pos.CENTER);
        flowBox.setPadding(new Insets(15));
        flowBox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 2; -fx-border-radius: 5;");
        
        // 1. Waiting Queue Road (cars waiting to enter)
        VBox queueSection = createQueueSection();
        
        // 2. Entry Gate Section (control point)
        VBox entrySection = createGateSection(
            "ENTRY\nGATE",
            entryGate,
            "Controls\nEntry"
        );
        
        // 3. Parking Lot Road (inside parking, cars driving to slots)
        VBox lotRoadSection = createRoadSection(
            "INSIDE\nROAD",
            parkingLotRoad
        );
        
        // 4. Parking Lot Section (storage)
        VBox parkingSection = createParkingSection();
        
        // 5. Exit Road Section
        VBox exitRoadSection = createRoadSection(
            "EXIT\nROAD",
            exitRoad
        );
        
        // 6. Exit Gate Section
        VBox exitSection = createGateSection(
            "EXIT\nGATE",
            exitGate,
            "Controls\nExit"
        );
        
        flowBox.getChildren().addAll(
            queueSection,
            entrySection,
            lotRoadSection,
            parkingSection,
            exitRoadSection,
            exitSection
        );
        
        return flowBox;
    }
    
    /**
     * Creates the waiting queue section.
     */
    private VBox createQueueSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-color: #fff9f0;");
        section.setPrefWidth(130);
        
        Text label = new Text("QUEUE\nROAD");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setFill(Color.web("#333"));
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        // Queue visualization
        Pane queueVis = new Pane();
        queueVis.setPrefSize(100, 80);
        queueVis.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #999; -fx-border-width: 1;");
        
        // Draw the queue road
        queueRoad.setPrefSize(80, 40);
        queueVis.getChildren().add(queueRoad);
        queueRoad.setLayoutX(10);
        queueRoad.setLayoutY(20);
        
        Text desc = new Text("Waiting\nCars");
        desc.setFont(Font.font("Arial", 10));
        desc.setFill(Color.web("#666"));
        desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        section.getChildren().addAll(label, queueVis, desc);
        return section;
    }
    
    /**
     * Creates a gate section (entry or exit).
     */
    private VBox createGateSection(String label, Object gate, String desc) {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-color: #f0f8ff;");
        section.setPrefWidth(110);
        
        Text labelText = new Text(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        labelText.setFill(Color.web("#333"));
        labelText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        if (gate instanceof EntryGate) {
            section.getChildren().add((EntryGate) gate);
        } else if (gate instanceof ExitGate) {
            section.getChildren().add((ExitGate) gate);
        }
        
        Text descText = new Text(desc);
        descText.setFont(Font.font("Arial", 9));
        descText.setFill(Color.web("#666"));
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        section.getChildren().add(0, labelText);
        section.getChildren().add(descText);
        return section;
    }
    
    /**
     * Creates a road section.
     */
    private VBox createRoadSection(String label, RoadPathway road) {
        VBox section = new VBox(8);
        section.setAlignment(Pos.CENTER);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-color: #f9f9f9;");
        section.setPrefWidth(120);
        
        Text labelText = new Text(label);
        labelText.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        labelText.setFill(Color.web("#333"));
        labelText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        section.getChildren().add(labelText);
        section.getChildren().add(road);
        
        return section;
    }
    
    /**
     * Creates the parking lot section.
     */
    private VBox createParkingSection() {
        VBox section = new VBox(8);
        section.setAlignment(Pos.TOP_CENTER);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-color: #f1f8f4;");
        section.setPrefWidth(220);
        
        Text label = new Text("PARKING LOT\n(Storage)");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        label.setFill(Color.DARKGREEN);
        label.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        Text desc = new Text("🟢 Available | 🔴 Occupied");
        desc.setFont(Font.font("Arial", 9));
        desc.setFill(Color.web("#666"));
        desc.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        section.getChildren().addAll(label, parkingLot, desc);
        return section;
    }
    
    /**
     * Starts the update timer for real-time visualization.
     */
    private void startUpdateTimer() {
        updateTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateVisuals();
            }
        };
        updateTimer.start();
    }
    
    /**
     * Updates all visual components based on current simulation state.
     */
    private void updateVisuals() {
        // Update parking lot visual
        if (parkingLot != null) {
            parkingLot.updateParking();
        }
        
        // Update gates based on occupancy
        if (carPark.getCurrentOccupancy() < carPark.getCapacity()) {
            entryGate.open();
        } else {
            entryGate.close();
        }
        
        if (carPark.getCurrentOccupancy() > 0) {
            exitGate.open();
        } else {
            exitGate.close();
        }
    }
    
    /**
     * Stops the update timer.
     */
    public void stop() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
    }
}

