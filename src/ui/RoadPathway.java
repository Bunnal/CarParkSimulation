package ui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Visual representation of a road pathway with lane markings and signs.
 * Creates realistic road graphics for entry and exit paths.
 * 
 * @author Car Park Management System
 * @version 2.0
 */
public class RoadPathway extends Pane {
    
    /**
     * Road direction enum.
     */
    public enum Direction {
        HORIZONTAL, VERTICAL
    }
    
    /**
     * Road type enum.
     */
    public enum RoadType {
        ENTRY, EXIT
    }
    
    private final Direction direction;
    private final RoadType roadType;
    private final double roadWidth;
    private final double roadLength;
    
    /**
     * Creates a road pathway with specified dimensions and type.
     * 
     * @param roadLength Length of the road
     * @param roadWidth Width of the road
     * @param direction Direction of the road (horizontal/vertical)
     * @param roadType Type of road (entry/exit)
     */
    public RoadPathway(double roadLength, double roadWidth, Direction direction, RoadType roadType) {
        this.roadLength = roadLength;
        this.roadWidth = roadWidth;
        this.direction = direction;
        this.roadType = roadType;
        
        createRoad();
    }
    
    /**
     * Creates the road visual elements.
     */
    private void createRoad() {
        // Road base (asphalt)
        Rectangle roadBase = new Rectangle(roadLength, roadWidth);
        roadBase.setFill(Color.web("#505050"));
        roadBase.setStroke(Color.web("#303030"));
        roadBase.setStrokeWidth(2);
        
        // Road edges (white lines)
        Line topEdge = new Line(0, 0, roadLength, 0);
        topEdge.setStroke(Color.WHITE);
        topEdge.setStrokeWidth(3);
        
        Line bottomEdge = new Line(0, roadWidth, roadLength, roadWidth);
        bottomEdge.setStroke(Color.WHITE);
        bottomEdge.setStrokeWidth(3);
        
        getChildren().addAll(roadBase, topEdge, bottomEdge);
        
        // Add lane markings (dashed center line)
        addLaneMarkings();
        
        // Add directional arrows
        addDirectionalArrows();
        
        // Add road sign
        addRoadSign();
    }
    
    /**
     * Adds dashed lane markings in the center of the road.
     */
    private void addLaneMarkings() {
        double dashLength = 15;
        double gapLength = 10;
        double centerY = roadWidth / 2;
        
        for (double x = 0; x < roadLength; x += (dashLength + gapLength)) {
            Line dash = new Line(x, centerY, Math.min(x + dashLength, roadLength), centerY);
            dash.setStroke(Color.YELLOW);
            dash.setStrokeWidth(2);
            dash.getStrokeDashArray().addAll(dashLength, gapLength);
            getChildren().add(dash);
        }
    }
    
    /**
     * Adds directional arrows on the road.
     */
    private void addDirectionalArrows() {
        Color arrowColor = roadType == RoadType.ENTRY ? Color.LIGHTGREEN : Color.ORANGE;
        
        int numArrows = 3;
        double spacing = roadLength / (numArrows + 1);
        
        for (int i = 1; i <= numArrows; i++) {
            Group arrow = createArrow(arrowColor);
            arrow.setLayoutX(i * spacing - 15);
            arrow.setLayoutY(roadWidth / 2 - 8);
            getChildren().add(arrow);
        }
    }
    
    /**
     * Creates a single arrow shape.
     */
    private Group createArrow(Color color) {
        Group arrow = new Group();
        
        // Arrow shaft
        Rectangle shaft = new Rectangle(15, 4);
        shaft.setFill(color);
        shaft.setLayoutX(0);
        shaft.setLayoutY(6);
        
        // Arrow head
        Polygon head = new Polygon();
        head.getPoints().addAll(
            15.0, 0.0,   // Top point
            15.0, 16.0,  // Bottom point
            25.0, 8.0    // Right point
        );
        head.setFill(color);
        
        arrow.getChildren().addAll(shaft, head);
        return arrow;
    }
    
    /**
     * Adds a road sign at the start of the road.
     */
    private void addRoadSign() {
        double signX = 10;
        double signY = roadWidth + 5;
        
        // Sign post
        Rectangle post = new Rectangle(3, 15);
        post.setFill(Color.GRAY);
        post.setLayoutX(signX);
        post.setLayoutY(signY);
        
        // Sign board
        Rectangle board = new Rectangle(35, 18);
        board.setFill(roadType == RoadType.ENTRY ? Color.web("#4CAF50") : Color.web("#FF9800"));
        board.setStroke(Color.WHITE);
        board.setStrokeWidth(2);
        board.setArcWidth(4);
        board.setArcHeight(4);
        board.setLayoutX(signX - 16);
        board.setLayoutY(signY - 20);
        
        // Sign text
        Text signText = new Text(roadType == RoadType.ENTRY ? "ENTRY" : "EXIT");
        signText.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        signText.setFill(Color.WHITE);
        signText.setLayoutX(signX - 12);
        signText.setLayoutY(signY - 7);
        
        getChildren().addAll(post, board, signText);
    }
    
    /**
     * Creates an entry road pathway.
     * 
     * @param length Length of the road
     * @return RoadPathway instance
     */
    public static RoadPathway createEntryRoad(double length) {
        return new RoadPathway(length, 60, Direction.HORIZONTAL, RoadType.ENTRY);
    }
    
    /**
     * Creates an exit road pathway.
     * 
     * @param length Length of the road
     * @return RoadPathway instance
     */
    public static RoadPathway createExitRoad(double length) {
        return new RoadPathway(length, 60, Direction.HORIZONTAL, RoadType.EXIT);
    }
}
