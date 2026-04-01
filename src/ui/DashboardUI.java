package ui;

import controller.CarOwner;
import controller.SecurityGuard;
import controller.SimulationManager;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.CarPark;

/**
 * Enhanced JavaFX dashboard for the Car Park Management Simulation.
 * Provides real-time visualization with:
 * - Live statistics (occupancy, throughput, wait time)
 * - Visual car park with parking slots
 * - Animated car entry/exit
 * - Traffic light indicators
 * - Thread status indicators
 * - Dynamic controls for simulation parameters
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class DashboardUI {
    private final Stage primaryStage;
    private SimulationManager simulationManager;
    private CarPark carPark;
    private AnimationTimer updateTimer;
    
    // UI Controls
    private Label occupancyLabel;
    private Label availableSlotsLabel;
    private Label throughputLabel;
    private Label avgWaitTimeLabel;
    private Label totalProcessedLabel;
    private ProgressBar occupancyBar;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    
    // Configuration Controls
    private Spinner<Integer> capacitySpinner;
    private Spinner<Integer> producersSpinner;
    private Spinner<Integer> consumersSpinner;
    private Slider productionRateSlider;
    private Slider consumptionRateSlider;
    private Label productionRateLabel;
    private Label consumptionRateLabel;
    
    // Thread Status Containers
    private HBox producerStatusBox;
    private HBox consumerStatusBox;
    
    // Visual Components
    private CarParkVisualizer visualizer;
    private int lastOccupancy = 0;
    
    // Constants
    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 900;
    private static final int UPDATE_INTERVAL_MS = 100;
    
    /**
     * Creates a new DashboardUI.
     * 
     * @param primaryStage The primary stage for this JavaFX application
     */
    public DashboardUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeSimulation();
        setupUI();
        startUpdateTimer();
    }
    
    /**
     * Initializes the simulation with default parameters.
     */
    private void initializeSimulation() {
        carPark = new CarPark(10);
        simulationManager = new SimulationManager(carPark);
    }
    
    /**
     * Sets up the complete UI layout with visual simulation.
     */
    private void setupUI() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // Top: Title
        root.setTop(createTitleSection());
        
        // Center: Visual simulation and Statistics
        root.setCenter(createCenterSectionWithVisuals());
        
        // Bottom: Controls
        root.setBottom(createControlSection());
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Car Park Management Sim - Producer-Consumer Demo with Visual Simulation");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(e -> cleanup());
    }
    
    /**
     * Creates the title section.
     */
    private VBox createTitleSection() {
        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(0, 0, 15, 0));
        
        Label titleLabel = new Label("🚗 Car Park Management Simulation");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        Label subtitleLabel = new Label("Producer-Consumer Pattern with Multi-threading");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setStyle("-fx-text-fill: #666;");
        
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        return titleBox;
    }
    
    /**
     * Creates the center section with visual simulation and statistics.
     */
    private VBox createCenterSectionWithVisuals() {
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(10));
        
        // Visual Simulation Panel
        visualizer = new CarParkVisualizer(carPark);
        
        // Statistics and Thread Status in horizontal layout
        HBox statsAndThreads = new HBox(15);
        statsAndThreads.getChildren().addAll(
            createStatisticsPanel(),
            createThreadStatusPanel()
        );
        
        centerBox.getChildren().addAll(visualizer, statsAndThreads);
        return centerBox;
    }
    
    /**
     * Creates the center section with statistics and thread status.
     */
    private VBox createCenterSection() {
        VBox centerBox = new VBox(15);
        centerBox.setPadding(new Insets(10));
        
        // Statistics Panel
        centerBox.getChildren().add(createStatisticsPanel());
        
        // Thread Status Panel
        centerBox.getChildren().add(createThreadStatusPanel());
        
        return centerBox;
    }
    
    /**
     * Creates the statistics panel showing real-time metrics.
     */
    private VBox createStatisticsPanel() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        statsBox.setPadding(new Insets(15));
        
        Label statsTitle = new Label("📊 Real-time Statistics");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Occupancy Progress Bar
        VBox occupancyBox = new VBox(5);
        Label occupancyTitle = new Label("Occupancy:");
        occupancyTitle.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        occupancyLabel = new Label("0%");
        occupancyBar = new ProgressBar(0);
        occupancyBar.setPrefWidth(400);
        occupancyBar.setStyle("-fx-accent: #4CAF50;");
        occupancyBox.getChildren().addAll(occupancyTitle, occupancyBar, occupancyLabel);
        
        // Statistics Grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(30);
        statsGrid.setVgap(10);
        statsGrid.setPadding(new Insets(10, 0, 0, 0));
        
        availableSlotsLabel = createStatLabel("Available Slots:", "0/10");
        throughputLabel = createStatLabel("Throughput:", "0.00 cars/sec");
        avgWaitTimeLabel = createStatLabel("Avg Wait Time:", "0.00 sec");
        totalProcessedLabel = createStatLabel("Total Processed:", "0");
        
        statsGrid.add(availableSlotsLabel, 0, 0);
        statsGrid.add(throughputLabel, 1, 0);
        statsGrid.add(avgWaitTimeLabel, 0, 1);
        statsGrid.add(totalProcessedLabel, 1, 1);
        
        statsBox.getChildren().addAll(statsTitle, new Separator(), occupancyBox, statsGrid);
        return statsBox;
    }
    
    /**
     * Creates the thread status panel.
     */
    private VBox createThreadStatusPanel() {
        VBox statusBox = new VBox(10);
        statusBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        statusBox.setPadding(new Insets(15));
        
        Label statusTitle = new Label("🔄 Thread Status");
        statusTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        // Producers
        Label producersLabel = new Label("Producers (Car Owners):");
        producersLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        producerStatusBox = new HBox(10);
        producerStatusBox.setAlignment(Pos.CENTER_LEFT);
        
        // Consumers
        Label consumersLabel = new Label("Consumers (Security Guards):");
        consumersLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 13));
        consumerStatusBox = new HBox(10);
        consumerStatusBox.setAlignment(Pos.CENTER_LEFT);
        
        // Legend
        HBox legendBox = new HBox(20);
        legendBox.setAlignment(Pos.CENTER);
        legendBox.setPadding(new Insets(10, 0, 0, 0));
        legendBox.getChildren().addAll(
            createLegendItem("Active", Color.GREEN),
            createLegendItem("Waiting/Blocked", Color.ORANGE),
            createLegendItem("Stopped", Color.RED)
        );
        
        statusBox.getChildren().addAll(
            statusTitle, new Separator(),
            producersLabel, producerStatusBox,
            new Separator(),
            consumersLabel, consumerStatusBox,
            legendBox
        );
        
        return statusBox;
    }
    
    /**
     * Creates a legend item for thread status.
     */
    private HBox createLegendItem(String text, Color color) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER);
        Circle circle = new Circle(6, color);
        Label label = new Label(text);
        label.setFont(Font.font("Arial", 11));
        box.getChildren().addAll(circle, label);
        return box;
    }
    
    /**
     * Creates the control section with simulation controls.
     */
    private VBox createControlSection() {
        VBox controlBox = new VBox(15);
        controlBox.setPadding(new Insets(15, 0, 0, 0));
        
        // Configuration Panel
        controlBox.getChildren().add(createConfigurationPanel());
        
        // Action Buttons
        controlBox.getChildren().add(createActionButtons());
        
        return controlBox;
    }
    
    /**
     * Creates the configuration panel.
     */
    private VBox createConfigurationPanel() {
        VBox configBox = new VBox(10);
        configBox.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-border-color: #ddd; -fx-border-radius: 5;");
        configBox.setPadding(new Insets(15));
        
        Label configTitle = new Label("⚙️ Configuration");
        configTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        
        GridPane configGrid = new GridPane();
        configGrid.setHgap(20);
        configGrid.setVgap(10);
        configGrid.setPadding(new Insets(10, 0, 0, 0));
        
        // Capacity Spinner
        Label capacityLabel = new Label("Buffer Capacity:");
        capacitySpinner = new Spinner<>(5, 50, 10, 5);
        capacitySpinner.setEditable(true);
        capacitySpinner.setPrefWidth(100);
        
        // Producers Spinner
        Label producersLabel = new Label("Producers:");
        producersSpinner = new Spinner<>(1, 10, 3, 1);
        producersSpinner.setEditable(true);
        producersSpinner.setPrefWidth(100);
        
        // Consumers Spinner
        Label consumersLabel = new Label("Consumers:");
        consumersSpinner = new Spinner<>(1, 10, 2, 1);
        consumersSpinner.setEditable(true);
        consumersSpinner.setPrefWidth(100);
        
        configGrid.add(capacityLabel, 0, 0);
        configGrid.add(capacitySpinner, 1, 0);
        configGrid.add(producersLabel, 2, 0);
        configGrid.add(producersSpinner, 3, 0);
        configGrid.add(consumersLabel, 4, 0);
        configGrid.add(consumersSpinner, 5, 0);
        
        // Production Rate Slider
        VBox productionBox = new VBox(5);
        Label productionTitle = new Label("Production Rate (delay between arrivals):");
        HBox productionSliderBox = new HBox(10);
        productionSliderBox.setAlignment(Pos.CENTER_LEFT);
        productionRateSlider = new Slider(100, 2000, 500);
        productionRateSlider.setPrefWidth(300);
        productionRateSlider.setShowTickMarks(true);
        productionRateSlider.setShowTickLabels(true);
        productionRateSlider.setMajorTickUnit(500);
        productionRateSlider.valueProperty().addListener((obs, old, newVal) -> {
            productionRateLabel.setText(String.format("%d ms", newVal.intValue()));
            if (simulationManager.isSimulationRunning()) {
                simulationManager.setProductionRate(newVal.intValue());
            }
        });
        productionRateLabel = new Label("500 ms");
        productionRateLabel.setPrefWidth(80);
        productionSliderBox.getChildren().addAll(productionRateSlider, productionRateLabel);
        productionBox.getChildren().addAll(productionTitle, productionSliderBox);
        
        // Consumption Rate Slider
        VBox consumptionBox = new VBox(5);
        Label consumptionTitle = new Label("Consumption Rate (delay between exits):");
        HBox consumptionSliderBox = new HBox(10);
        consumptionSliderBox.setAlignment(Pos.CENTER_LEFT);
        consumptionRateSlider = new Slider(100, 2000, 800);
        consumptionRateSlider.setPrefWidth(300);
        consumptionRateSlider.setShowTickMarks(true);
        consumptionRateSlider.setShowTickLabels(true);
        consumptionRateSlider.setMajorTickUnit(500);
        consumptionRateSlider.valueProperty().addListener((obs, old, newVal) -> {
            consumptionRateLabel.setText(String.format("%d ms", newVal.intValue()));
            if (simulationManager.isSimulationRunning()) {
                simulationManager.setConsumptionRate(newVal.intValue());
            }
        });
        consumptionRateLabel = new Label("800 ms");
        consumptionRateLabel.setPrefWidth(80);
        consumptionSliderBox.getChildren().addAll(consumptionRateSlider, consumptionRateLabel);
        consumptionBox.getChildren().addAll(consumptionTitle, consumptionSliderBox);
        
        configBox.getChildren().addAll(configTitle, new Separator(), configGrid, productionBox, consumptionBox);
        return configBox;
    }
    
    /**
     * Creates action buttons.
     */
    private HBox createActionButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(10));
        
        startButton = new Button("▶ Start Simulation");
        startButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20;");
        startButton.setOnAction(e -> startSimulation());
        
        stopButton = new Button("⏸ Stop Simulation");
        stopButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20;");
        stopButton.setDisable(true);
        stopButton.setOnAction(e -> stopSimulation());
        
        resetButton = new Button("🔄 Reset");
        resetButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20;");
        resetButton.setOnAction(e -> resetSimulation());
        
        buttonBox.getChildren().addAll(startButton, stopButton, resetButton);
        return buttonBox;
    }
    
    /**
     * Creates a statistics label.
     */
    private Label createStatLabel(String title, String initialValue) {
        Label label = new Label(title + " " + initialValue);
        label.setFont(Font.font("Arial", 13));
        return label;
    }
    
    /**
     * Starts the simulation with visual updates.
     */
    private void startSimulation() {
        int capacity = capacitySpinner.getValue();
        int numProducers = producersSpinner.getValue();
        int numConsumers = consumersSpinner.getValue();
        int productionRate = (int) productionRateSlider.getValue();
        int consumptionRate = (int) consumptionRateSlider.getValue();
        
        // Create new car park with updated capacity
        carPark = new CarPark(capacity);
        simulationManager = new SimulationManager(carPark);
        
        // Recreate visualizer with new carpark
        visualizer.stop();
        BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
        VBox centerBox = (VBox) root.getCenter();
        centerBox.getChildren().remove(0);
        visualizer = new CarParkVisualizer(carPark);
        centerBox.getChildren().add(0, visualizer);
        lastOccupancy = 0;
        
        // Start simulation
        simulationManager.startSimulation(numProducers, numConsumers, productionRate, consumptionRate);
        
        // Update UI
        startButton.setDisable(true);
        stopButton.setDisable(false);
        capacitySpinner.setDisable(true);
        producersSpinner.setDisable(true);
        consumersSpinner.setDisable(true);
        
        // Update thread status indicators
        updateThreadStatusIndicators();
    }
    
    /**
     * Stops the simulation.
     */
    private void stopSimulation() {
        simulationManager.stopSimulation();
        
        // Update UI
        startButton.setDisable(false);
        stopButton.setDisable(true);
        capacitySpinner.setDisable(false);
        producersSpinner.setDisable(false);
        consumersSpinner.setDisable(false);
        
        // Clear thread status indicators
        producerStatusBox.getChildren().clear();
        consumerStatusBox.getChildren().clear();
    }
    
    /**
     * Resets the simulation.
     */
    private void resetSimulation() {
        if (simulationManager.isSimulationRunning()) {
            stopSimulation();
        }
        carPark.resetStatistics();
        updateStatistics();
    }
    
    /**
     * Starts the update timer for real-time UI updates.
     */
    private void startUpdateTimer() {
        updateTimer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= UPDATE_INTERVAL_MS * 1_000_000) {
                    updateUI();
                    lastUpdate = now;
                }
            }
        };
        updateTimer.start();
    }
    
    /**
     * Updates the UI with current statistics.
     */
    private void updateUI() {
        if (simulationManager.isSimulationRunning()) {
            updateStatistics();
            updateThreadStatusColors();
        }
    }
    
    /**
     * Updates statistics labels and visual components.
     */
    private void updateStatistics() {
        Platform.runLater(() -> {
            double occupancy = carPark.getOccupancyPercentage();
            int currentOccupancy = carPark.getCurrentOccupancy();
            int capacity = carPark.getCapacity();
            
            occupancyLabel.setText(String.format("%.1f%%", occupancy));
            occupancyBar.setProgress(occupancy / 100.0);
            
            availableSlotsLabel.setText(String.format("Available Slots: %d/%d", 
                carPark.getAvailableSlots(), capacity));
            
            throughputLabel.setText(String.format("Throughput: %.2f cars/sec", 
                carPark.getThroughput()));
            
            avgWaitTimeLabel.setText(String.format("Avg Wait Time: %.2f sec", 
                carPark.getAverageWaitTime()));
            
            totalProcessedLabel.setText(String.format("Total Processed: %d", 
                carPark.getTotalCarsProcessed()));
        });
    }
    
    /**
     * Updates thread status indicators.
     */
    private void updateThreadStatusIndicators() {
        Platform.runLater(() -> {
            producerStatusBox.getChildren().clear();
            for (CarOwner owner : simulationManager.getCarOwners()) {
                Circle indicator = new Circle(10);
                Tooltip.install(indicator, new Tooltip("Producer #" + owner.getId()));
                producerStatusBox.getChildren().add(indicator);
            }
            
            consumerStatusBox.getChildren().clear();
            for (SecurityGuard guard : simulationManager.getSecurityGuards()) {
                Circle indicator = new Circle(10);
                Tooltip.install(indicator, new Tooltip("Consumer #" + guard.getId()));
                consumerStatusBox.getChildren().add(indicator);
            }
        });
    }
    
    /**
     * Updates thread status colors based on current state.
     */
    private void updateThreadStatusColors() {
        Platform.runLater(() -> {
            int producerIndex = 0;
            for (CarOwner owner : simulationManager.getCarOwners()) {
                if (producerIndex < producerStatusBox.getChildren().size()) {
                    Circle circle = (Circle) producerStatusBox.getChildren().get(producerIndex);
                    if (!owner.isRunning()) {
                        circle.setFill(Color.RED);
                    } else if (owner.isWaiting()) {
                        circle.setFill(Color.ORANGE);
                    } else {
                        circle.setFill(Color.GREEN);
                    }
                }
                producerIndex++;
            }
            
            int consumerIndex = 0;
            for (SecurityGuard guard : simulationManager.getSecurityGuards()) {
                if (consumerIndex < consumerStatusBox.getChildren().size()) {
                    Circle circle = (Circle) consumerStatusBox.getChildren().get(consumerIndex);
                    if (!guard.isRunning()) {
                        circle.setFill(Color.RED);
                    } else if (guard.isWaiting()) {
                        circle.setFill(Color.ORANGE);
                    } else {
                        circle.setFill(Color.GREEN);
                    }
                }
                consumerIndex++;
            }
        });
    }
    
    /**
     * Shows the UI.
     */
    public void show() {
        primaryStage.show();
    }
    
    /**
     * Cleans up resources when the application closes.
     */
    private void cleanup() {
        if (updateTimer != null) {
            updateTimer.stop();
        }
        if (simulationManager.isSimulationRunning()) {
            simulationManager.stopSimulation();
        }
    }
}
