package ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Visual representation of a car with detailed graphics.
 * Creates a car icon using JavaFX shapes to look more realistic.
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class CarIcon extends Group {
    private static final double CAR_WIDTH = 45;
    private static final double CAR_HEIGHT = 25;
    
    /**
     * Creates a detailed car icon with the specified color and label.
     * 
     * @param carColor The color of the car body
     * @param label The label to display on the car (e.g., license plate)
     */
    public CarIcon(Color carColor, String label) {
        // Car body (main rectangle)
        Rectangle body = new Rectangle(CAR_WIDTH, CAR_HEIGHT * 0.6);
        body.setFill(carColor);
        body.setStroke(Color.BLACK);
        body.setStrokeWidth(1.5);
        body.setArcWidth(8);
        body.setArcHeight(8);
        body.setLayoutX(0);
        body.setLayoutY(CAR_HEIGHT * 0.3);
        
        // Car roof/cabin
        Rectangle cabin = new Rectangle(CAR_WIDTH * 0.5, CAR_HEIGHT * 0.4);
        cabin.setFill(carColor.darker());
        cabin.setStroke(Color.BLACK);
        cabin.setStrokeWidth(1.5);
        cabin.setArcWidth(6);
        cabin.setArcHeight(6);
        cabin.setLayoutX(CAR_WIDTH * 0.25);
        cabin.setLayoutY(0);
        
        // Front window
        Rectangle frontWindow = new Rectangle(CAR_WIDTH * 0.15, CAR_HEIGHT * 0.3);
        frontWindow.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.6));
        frontWindow.setStroke(Color.DARKGRAY);
        frontWindow.setStrokeWidth(1);
        frontWindow.setLayoutX(CAR_WIDTH * 0.3);
        frontWindow.setLayoutY(CAR_HEIGHT * 0.05);
        
        // Back window
        Rectangle backWindow = new Rectangle(CAR_WIDTH * 0.15, CAR_HEIGHT * 0.3);
        backWindow.setFill(Color.LIGHTBLUE.deriveColor(0, 1, 1, 0.6));
        backWindow.setStroke(Color.DARKGRAY);
        backWindow.setStrokeWidth(1);
        backWindow.setLayoutX(CAR_WIDTH * 0.55);
        backWindow.setLayoutY(CAR_HEIGHT * 0.05);
        
        // Front wheel
        Circle frontWheel = new Circle(CAR_HEIGHT * 0.18);
        frontWheel.setFill(Color.BLACK);
        frontWheel.setStroke(Color.DARKGRAY);
        frontWheel.setStrokeWidth(1);
        frontWheel.setLayoutX(CAR_WIDTH * 0.25);
        frontWheel.setLayoutY(CAR_HEIGHT * 0.9);
        
        // Back wheel
        Circle backWheel = new Circle(CAR_HEIGHT * 0.18);
        backWheel.setFill(Color.BLACK);
        backWheel.setStroke(Color.DARKGRAY);
        backWheel.setStrokeWidth(1);
        backWheel.setLayoutX(CAR_WIDTH * 0.75);
        backWheel.setLayoutY(CAR_HEIGHT * 0.9);
        
        // Wheel hubs
        Circle frontHub = new Circle(CAR_HEIGHT * 0.08);
        frontHub.setFill(Color.GRAY);
        frontHub.setLayoutX(CAR_WIDTH * 0.25);
        frontHub.setLayoutY(CAR_HEIGHT * 0.9);
        
        Circle backHub = new Circle(CAR_HEIGHT * 0.08);
        backHub.setFill(Color.GRAY);
        backHub.setLayoutX(CAR_WIDTH * 0.75);
        backHub.setLayoutY(CAR_HEIGHT * 0.9);
        
        // Headlights
        Circle headlight = new Circle(CAR_HEIGHT * 0.08);
        headlight.setFill(Color.YELLOW);
        headlight.setStroke(Color.ORANGE);
        headlight.setStrokeWidth(1);
        headlight.setLayoutX(CAR_WIDTH * 0.95);
        headlight.setLayoutY(CAR_HEIGHT * 0.5);
        
        // Tail light
        Circle taillight = new Circle(CAR_HEIGHT * 0.08);
        taillight.setFill(Color.RED);
        taillight.setStroke(Color.DARKRED);
        taillight.setStrokeWidth(1);
        taillight.setLayoutX(CAR_WIDTH * 0.02);
        taillight.setLayoutY(CAR_HEIGHT * 0.5);
        
        // License plate
        Rectangle plate = new Rectangle(CAR_WIDTH * 0.35, CAR_HEIGHT * 0.2);
        plate.setFill(Color.WHITE);
        plate.setStroke(Color.BLACK);
        plate.setStrokeWidth(1);
        plate.setLayoutX(CAR_WIDTH * 0.32);
        plate.setLayoutY(CAR_HEIGHT * 0.65);
        
        // License plate text
        Text plateText = new Text(label);
        plateText.setFont(Font.font("Arial", FontWeight.BOLD, 6));
        plateText.setFill(Color.BLACK);
        plateText.setLayoutX(CAR_WIDTH * 0.35);
        plateText.setLayoutY(CAR_HEIGHT * 0.78);
        
        // Add all components to the group
        getChildren().addAll(
            body, cabin,
            frontWindow, backWindow,
            frontWheel, backWheel,
            frontHub, backHub,
            headlight, taillight,
            plate, plateText
        );
    }
    
    /**
     * Creates a simple car icon without label (for animations).
     * 
     * @param carColor The color of the car body
     */
    public CarIcon(Color carColor) {
        this(carColor, "");
    }
    
    /**
     * Creates a parked car icon for parking slots.
     * 
     * @param slotNumber The parking slot number
     * @return A StackPane containing the car icon and slot info
     */
    public static StackPane createParkedCar(int slotNumber) {
        StackPane carPane = new StackPane();
        carPane.setPrefSize(45, 30);
        
        // Create a small car icon
        CarIcon car = new CarIcon(Color.web("#1976D2"), String.format("%03d", slotNumber));
        car.setScaleX(0.8);
        car.setScaleY(0.8);
        
        carPane.getChildren().add(car);
        return carPane;
    }
}
