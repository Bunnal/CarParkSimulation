package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * Small colored circle component used for status indicators.
 */
public class StatusDot extends JComponent {

    private final Color color;
    private final int diameter;

    /**
     * Creates a status dot.
     *
     * @param color fill color
     * @param diameter diameter in pixels
     */
    public StatusDot(Color color, int diameter) {
        this.color = color;
        this.diameter = diameter;
        setOpaque(false);
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.fillOval(0, 0, diameter - 1, diameter - 1);
        g2.dispose();
    }
}
