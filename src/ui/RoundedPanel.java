package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Simple rounded Swing panel used to present cards.
 */
public class RoundedPanel extends JPanel {

    private final Color backgroundColor;
    private final int arc;

    /**
     * Creates a rounded panel with the supplied background color and corner radius.
     *
     * @param backgroundColor background fill color
     * @param arc corner radius in pixels
     */
    public RoundedPanel(Color backgroundColor, int arc) {
        this(null, backgroundColor, arc);
    }

    /**
     * Creates a rounded panel with a layout manager.
     *
     * @param layout layout manager to use
     * @param backgroundColor background fill color
     * @param arc corner radius in pixels
     */
    public RoundedPanel(LayoutManager layout, Color backgroundColor, int arc) {
        super(layout);
        this.backgroundColor = backgroundColor;
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(graphics);
    }
}
