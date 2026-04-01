package ui;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.CarPark;

/**
 * Visual representation of the parking lot with individual parking slots.
 * Displays occupied and available slots in a grid layout.
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class ParkingLotVisual extends GridPane {
    
    private static final double SLOT_WIDTH = 50;
    private static final double SLOT_HEIGHT = 60;
    private static final int SLOTS_PER_ROW = 5;
    
    private final CarPark carPark;
    private final StackPane[] slotPanes;
    
    /**
     * Creates a new parking lot visualizer.
     * 
     * @param carPark The CarPark model to visualize
     */
    public ParkingLotVisual(CarPark carPark) {
        this.carPark = carPark;
        this.slotPanes = new StackPane[carPark.getCapacity()];
        
        setupGridPane();
        createParkingSlots();
    }
    
    /**
     * Sets up the GridPane properties.
     */
    private void setupGridPane() {
        this.setHgap(8);
        this.setVgap(8);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #999; -fx-border-width: 2; -fx-border-radius: 5;");
    }
    
    /**
     * Creates individual parking slot visualizations.
     */
    private void createParkingSlots() {
        int capacity = carPark.getCapacity();
        
        for (int i = 0; i < capacity; i++) {
            StackPane slotPane = createSlot(i);
            slotPanes[i] = slotPane;
            
            int row = i / SLOTS_PER_ROW;
            int col = i % SLOTS_PER_ROW;
            this.add(slotPane, col, row);
        }
    }
    
    /**
     * Creates a single parking slot.
     * 
     * @param slotNumber The slot number
     * @return A StackPane representing the slot
     */
    private StackPane createSlot(int slotNumber) {
        StackPane slotPane = new StackPane();
        slotPane.setPrefSize(SLOT_WIDTH, SLOT_HEIGHT);
        
        // Slot background (available by default)
        Rectangle slotBackground = new Rectangle(SLOT_WIDTH, SLOT_HEIGHT);
        slotBackground.setFill(Color.web("#2ecc71"));
        slotBackground.setStroke(Color.DARKGREEN);
        slotBackground.setStrokeWidth(2);
        slotBackground.setArcWidth(4);
        slotBackground.setArcHeight(4);
        
        // Slot number text
        Text slotText = new Text(String.format("%d", slotNumber + 1));
        slotText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        slotText.setFill(Color.WHITE);
        
        slotPane.getChildren().addAll(slotBackground, slotText);
        slotPane.setUserData(slotBackground);
        
        return slotPane;
    }
    
    /**
     * Updates the visual representation based on current parking status.
     */
    public void updateParking() {
        int occupancy = carPark.getCurrentOccupancy();
        
        for (int i = 0; i < carPark.getCapacity(); i++) {
            Rectangle background = (Rectangle) slotPanes[i].getUserData();
            
            if (i < occupancy) {
                // Occupied slot
                background.setFill(Color.web("#e74c3c"));
                background.setStroke(Color.DARKRED);
            } else {
                // Available slot
                background.setFill(Color.web("#2ecc71"));
                background.setStroke(Color.DARKGREEN);
            }
        }
    }
}
