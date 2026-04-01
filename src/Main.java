import controller.SimulationController;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ui.MainDashboard;

/**
 * Main Swing application entry point for the car park simulation.
 */
public final class Main {

    private Main() {
    }

    /**
     * Creates and shows the main Swing window.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            configureLookAndFeel();

            JFrame frame = new JFrame("Car Park Management Sim");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MainDashboard dashboard = new MainDashboard(new SimulationController());
            frame.setContentPane(dashboard);

            frame.setMinimumSize(new Dimension(1180, 820));
            frame.setSize(1420, 940);
            frame.setLocationRelativeTo(null);
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent event) {
                    dashboard.shutdown();
                }
            });

            frame.setVisible(true);
        });
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exception) {
            // Fall back to the default Swing look and feel when the system look and feel is unavailable.
        }

        Font baseFont = new Font("SansSerif", Font.PLAIN, 13);
        UIManager.put("Label.font", baseFont);
        UIManager.put("Button.font", baseFont);
        UIManager.put("Spinner.font", baseFont);
        UIManager.put("Slider.font", baseFont);
        UIManager.put("ProgressBar.font", baseFont);
    }
}
