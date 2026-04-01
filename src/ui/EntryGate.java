package ui;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Visual representation of the entry gate to the parking lot.
 * Displays a traffic light indicator and gate barrier.
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class EntryGate extends Group {
    
    private Circle trafficLight;
    private boolean isOpen;
    
    /**
     * Creates a new entry gate with traffic light and barrier.
     */
    public EntryGate() {
        isOpen = false;
        createGate();
    }
    
    /**
     * Creates the gate visual elements.
     */
    private void createGate() {
        // Gate post (left)
        Rectangle leftPost = new Rectangle(15, 100);
        leftPost.setFill(Color.DARKGRAY);
        leftPost.setLayoutX(0);
        leftPost.setLayoutY(0);
        
        // Gate post (right)
        Rectangle rightPost = new Rectangle(15, 100);
        rightPost.setFill(Color.DARKGRAY);
        rightPost.setLayoutX(85);
        rightPost.setLayoutY(0);
        
        // Gate barrier (horizontal bar)
        Rectangle barrier = new Rectangle(70, 12);
        barrier.setFill(Color.web("#FF6B6B"));
        barrier.setStroke(Color.DARKRED);
        barrier.setStrokeWidth(2);
        barrier.setLayoutX(15);
        barrier.setLayoutY(44);
        
        // Traffic light housing
        Rectangle lightHousing = new Rectangle(40, 80);
        lightHousing.setFill(Color.BLACK);
        lightHousing.setStroke(Color.GRAY);
        lightHousing.setStrokeWidth(2);
        lightHousing.setLayoutX(30);
        lightHousing.setLayoutY(110);
        
        // Red light (stop)
        Circle redLight = new Circle(8);
        redLight.setFill(Color.LIGHTCORAL);
        redLight.setStroke(Color.DARKRED);
        redLight.setStrokeWidth(1);
        redLight.setLayoutX(50);
        redLight.setLayoutY(125);
        
        // Green light (go)
        trafficLight = new Circle(8);
        trafficLight.setFill(Color.LIGHTGRAY);
        trafficLight.setStroke(Color.DARKGRAY);
        trafficLight.setStrokeWidth(1);
        trafficLight.setLayoutX(50);
        trafficLight.setLayoutY(160);
        
        // Gate label
        Text gateLabel = new Text("ENTRY");
        gateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gateLabel.setFill(Color.DARKBLUE);
        gateLabel.setLayoutX(5);
        gateLabel.setLayoutY(200);
        
        getChildren().addAll(
            leftPost, rightPost, barrier,
            lightHousing, redLight, trafficLight,
            gateLabel
        );
    }
    
    /**
     * Opens the gate (changes traffic light to green).
     */
    public void open() {
        isOpen = true;
        trafficLight.setFill(Color.LIGHTGREEN);
    }
    
    /**
     * Closes the gate (changes traffic light to red).
     */
    public void close() {
        isOpen = false;
        trafficLight.setFill(Color.LIGHTGRAY);
    }
    
    /**
     * Gets the current gate status.
     * 
     * @return true if gate is open, false otherwise
     */
    public boolean isOpen() {
        return isOpen;
    }
    
    /**
     * Sets the gate status and updates the visual.
     * 
     * @param open true to open the gate, false to close it
     */
    public void setOpen(boolean open) {
        if (open) {
            open();
        } else {
            close();
        }
    }
}
