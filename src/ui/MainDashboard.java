package ui;

import controller.SimStats;
import controller.SimulationController;
import controller.WorkerSnapshot;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;
import model.WorkerState;

/**
 * Swing dashboard panel for the car park simulation.
 */
public class MainDashboard extends JPanel {

    private static final Color APP_BACKGROUND = new Color(236, 244, 232);
    private static final Color CARD_BACKGROUND = new Color(255, 255, 255);
    private static final Color CARD_BORDER = new Color(213, 224, 207);
    private static final Color TEXT_PRIMARY = new Color(36, 48, 65);
    private static final Color TEXT_MUTED = new Color(105, 118, 103);
    private static final Color GREEN = new Color(44, 157, 96);
    private static final Color AMBER = new Color(211, 168, 59);
    private static final Color RED = new Color(214, 91, 74);

    private final SimulationController controller;
    private final ParkingLotScenePanel parkingLotPanel;
    private final Timer refreshTimer;

    private final JLabel runStateLabel;
    private final JLabel occupancyValueLabel;
    private final JLabel availableValueLabel;
    private final JLabel throughputValueLabel;
    private final JLabel avgWaitValueLabel;
    private final JLabel producedValueLabel;
    private final JLabel consumedValueLabel;
    private final JLabel occupancyDetailLabel;
    private final JLabel capacityWarningLabel;
    private final JProgressBar occupancyBar;

    private final JSpinner capacitySpinner;
    private final JSpinner producerSpinner;
    private final JSpinner consumerSpinner;
    private final JSlider productionRateSlider;
    private final JSlider consumptionRateSlider;
    private final JLabel productionRateValueLabel;
    private final JLabel consumptionRateValueLabel;

    private final JButton startButton;
    private final JButton stopButton;
    private final JButton resetButton;

    private final JPanel producerStatusList;
    private final JPanel consumerStatusList;

    /**
     * Creates the Swing dashboard.
     *
     * @param controller simulation controller used to start, stop, and query state
     */
    public MainDashboard(SimulationController controller) {
        this.controller = controller;
        this.parkingLotPanel = new ParkingLotScenePanel();

        this.runStateLabel = createPillLabel("Stopped", RED);
        this.occupancyValueLabel = createMetricValueLabel("0%");
        this.availableValueLabel = createMetricValueLabel("0 slots");
        this.throughputValueLabel = createMetricValueLabel("0.00 cars/sec");
        this.avgWaitValueLabel = createMetricValueLabel("0.00 s");
        this.producedValueLabel = createMetricValueLabel("0");
        this.consumedValueLabel = createMetricValueLabel("0");
        this.occupancyDetailLabel = createMutedLabel("0 / 0 slots occupied");
        this.capacityWarningLabel = createMutedLabel("");
        this.occupancyBar = new JProgressBar(0, 100);

        this.capacitySpinner = createSpinner(controller.getCapacity(), 4, 30, 1);
        this.producerSpinner = createSpinner(controller.getProducerCount(), 1, 10, 1);
        this.consumerSpinner = createSpinner(controller.getConsumerCount(), 1, 10, 1);
        this.productionRateSlider = createSlider(controller.getProductionRateMs(), 250, 2500);
        this.consumptionRateSlider = createSlider(controller.getConsumptionRateMs(), 250, 3000);
        this.productionRateValueLabel = createValueBadge(controller.getProductionRateMs() + " ms");
        this.consumptionRateValueLabel = createValueBadge(controller.getConsumptionRateMs() + " ms");

        this.startButton = createActionButton("Start Simulation", GREEN);
        this.stopButton = createActionButton("Stop", RED);
        this.resetButton = createActionButton("Reset", new Color(234, 239, 228));
        this.resetButton.setForeground(TEXT_PRIMARY);

        this.producerStatusList = createWorkerListPanel();
        this.consumerStatusList = createWorkerListPanel();

        setLayout(new BorderLayout(18, 18));
        setBackground(APP_BACKGROUND);
        setBorder(new EmptyBorder(20, 20, 32, 20));

        add(createHeader(), BorderLayout.NORTH);
        add(createControlsScroller(), BorderLayout.WEST);
        add(createStatusScroller(), BorderLayout.EAST);
        add(createCenterPanel(), BorderLayout.CENTER);

        wireControls();

        refreshTimer = new Timer(150, event -> refreshUI());
        refreshTimer.start();
        refreshUI();
    }

    /**
     * Stops UI timers and simulation workers.
     */
    public void shutdown() {
        refreshTimer.stop();
        parkingLotPanel.stopAnimation();
        controller.shutdown();
    }

    private JPanel createHeader() {
        RoundedPanel header = new RoundedPanel(new BorderLayout(16, 0), CARD_BACKGROUND, 24);
        header.setBorder(createCardBorder(18, 22, 18, 22));

        JPanel textBox = new JPanel();
        textBox.setOpaque(false);
        textBox.setLayout(new BoxLayout(textBox, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Car Park Management Sim");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(
            "Swing dashboard for the producer-consumer simulation using semaphores and a fair mutex");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_MUTED);

        textBox.add(titleLabel);
        textBox.add(Box.createVerticalStrut(4));
        textBox.add(subtitleLabel);

        JPanel rightBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightBox.setOpaque(false);
        rightBox.add(runStateLabel);

        header.add(textBox, BorderLayout.CENTER);
        header.add(rightBox, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createControlsScroller() {
        RoundedPanel controlsCard = new RoundedPanel(CARD_BACKGROUND, 22);
        controlsCard.setLayout(new BoxLayout(controlsCard, BoxLayout.Y_AXIS));
        controlsCard.setBorder(createCardBorder(18, 18, 18, 18));
        controlsCard.setPreferredSize(new Dimension(310, 0));

        JLabel sectionTitle = createSectionTitle("Simulation Controls");
        JLabel sectionNote = createWrappedNote(
            "Capacity, production rate, and consumption rate apply immediately. Worker counts apply on the next start.");

        controlsCard.add(sectionTitle);
        controlsCard.add(Box.createVerticalStrut(8));
        controlsCard.add(sectionNote);
        controlsCard.add(Box.createVerticalStrut(14));
        controlsCard.add(createFieldRow("Buffer Capacity", capacitySpinner));
        controlsCard.add(Box.createVerticalStrut(10));
        controlsCard.add(createFieldRow("Car Owners", producerSpinner));
        controlsCard.add(Box.createVerticalStrut(10));
        controlsCard.add(createFieldRow("Security Guards", consumerSpinner));
        controlsCard.add(Box.createVerticalStrut(18));
        controlsCard.add(createSliderGroup("Production Rate", productionRateSlider, productionRateValueLabel));
        controlsCard.add(Box.createVerticalStrut(14));
        controlsCard.add(createSliderGroup("Consumption Rate", consumptionRateSlider, consumptionRateValueLabel));
        controlsCard.add(Box.createVerticalStrut(18));
        controlsCard.add(createActionRow());
        controlsCard.add(Box.createVerticalStrut(18));
        controlsCard.add(createSectionTitle("Legend"));
        controlsCard.add(Box.createVerticalStrut(10));
        controlsCard.add(createLegendItem("Active", GREEN));
        controlsCard.add(Box.createVerticalStrut(8));
        controlsCard.add(createLegendItem("Waiting / Blocked", AMBER));
        controlsCard.add(Box.createVerticalStrut(8));
        controlsCard.add(createLegendItem("Stopped", RED));
        controlsCard.add(Box.createVerticalGlue());

        JScrollPane scroller = new JScrollPane(controlsCard);
        scroller.setPreferredSize(new Dimension(320, 0));
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroller;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(0, 12));
        centerPanel.setOpaque(false);

        JPanel metricsGrid = new JPanel(new GridLayout(2, 3, 12, 12));
        metricsGrid.setOpaque(false);
        metricsGrid.add(createMetricCard("Occupancy", occupancyValueLabel));
        metricsGrid.add(createMetricCard("Available Slots", availableValueLabel));
        metricsGrid.add(createMetricCard("Throughput", throughputValueLabel));
        metricsGrid.add(createMetricCard("Average Wait", avgWaitValueLabel));
        metricsGrid.add(createMetricCard("Cars Produced", producedValueLabel));
        metricsGrid.add(createMetricCard("Cars Processed", consumedValueLabel));

        RoundedPanel parkingCard = new RoundedPanel(new BorderLayout(0, 10), CARD_BACKGROUND, 24);
        parkingCard.setBorder(createCardBorder(16, 16, 16, 16));

        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = createSectionTitle("Live Parking Lot");
        capacityWarningLabel.setForeground(new Color(182, 106, 42));

        occupancyBar.setValue(0);
        occupancyBar.setStringPainted(false);
        occupancyBar.setBorder(BorderFactory.createLineBorder(new Color(221, 231, 217)));
        occupancyBar.setBackground(new Color(221, 234, 214));
        occupancyBar.setForeground(GREEN);
        occupancyBar.setPreferredSize(new Dimension(220, 14));
        occupancyBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 14));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(occupancyBar);
        titlePanel.add(Box.createVerticalStrut(6));
        titlePanel.add(occupancyDetailLabel);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(capacityWarningLabel);

        parkingCard.add(titlePanel, BorderLayout.NORTH);
        parkingCard.add(parkingLotPanel, BorderLayout.CENTER);

        centerPanel.add(metricsGrid, BorderLayout.NORTH);
        centerPanel.add(parkingCard, BorderLayout.CENTER);
        return centerPanel;
    }

    private JScrollPane createStatusScroller() {
        RoundedPanel statusCard = new RoundedPanel(CARD_BACKGROUND, 22);
        statusCard.setLayout(new BoxLayout(statusCard, BoxLayout.Y_AXIS));
        statusCard.setBorder(createCardBorder(18, 18, 18, 18));
        statusCard.setPreferredSize(new Dimension(320, 0));

        statusCard.add(createSectionTitle("Worker Status"));
        statusCard.add(Box.createVerticalStrut(12));
        statusCard.add(createSubsectionLabel("Car Owners"));
        statusCard.add(Box.createVerticalStrut(8));
        statusCard.add(producerStatusList);
        statusCard.add(Box.createVerticalStrut(16));
        statusCard.add(createSubsectionLabel("Security Guards"));
        statusCard.add(Box.createVerticalStrut(8));
        statusCard.add(consumerStatusList);
        statusCard.add(Box.createVerticalGlue());

        JScrollPane scroller = new JScrollPane(statusCard);
        scroller.setPreferredSize(new Dimension(330, 0));
        scroller.setBorder(BorderFactory.createEmptyBorder());
        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroller;
    }

    private JPanel createMetricCard(String title, JLabel valueLabel) {
        RoundedPanel card = new RoundedPanel(CARD_BACKGROUND, 20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(createCardBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleLabel.setForeground(TEXT_MUTED);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(valueLabel);
        return card;
    }

    private JPanel createFieldRow(String labelText, JSpinner spinner) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        JLabel label = createFieldLabel(labelText);
        row.add(label, BorderLayout.WEST);
        row.add(spinner, BorderLayout.EAST);
        return row;
    }

    private JPanel createSliderGroup(String labelText, JSlider slider, JLabel valueLabel) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));

        JPanel topRow = new JPanel(new BorderLayout(12, 0));
        topRow.setOpaque(false);
        topRow.add(createFieldLabel(labelText), BorderLayout.WEST);
        topRow.add(valueLabel, BorderLayout.EAST);

        group.add(topRow);
        group.add(Box.createVerticalStrut(8));
        group.add(slider);
        return group;
    }

    private JPanel createActionRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 10, 0));
        row.setOpaque(false);
        row.add(startButton);
        row.add(stopButton);
        row.add(resetButton);
        return row;
    }

    private JPanel createLegendItem(String labelText, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        item.add(new StatusDot(color, 10));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(TEXT_MUTED);
        item.add(label);
        return item;
    }

    private void wireControls() {
        ChangeListener capacityListener = event ->
            controller.setCapacity((Integer) capacitySpinner.getValue());
        ChangeListener producerListener = event ->
            controller.setProducerCount((Integer) producerSpinner.getValue());
        ChangeListener consumerListener = event ->
            controller.setConsumerCount((Integer) consumerSpinner.getValue());
        ChangeListener productionRateListener = event -> {
            int rate = productionRateSlider.getValue();
            productionRateValueLabel.setText(rate + " ms");
            controller.setProductionRateMs(rate);
        };
        ChangeListener consumptionRateListener = event -> {
            int rate = consumptionRateSlider.getValue();
            consumptionRateValueLabel.setText(rate + " ms");
            controller.setConsumptionRateMs(rate);
        };

        capacitySpinner.addChangeListener(capacityListener);
        producerSpinner.addChangeListener(producerListener);
        consumerSpinner.addChangeListener(consumerListener);
        productionRateSlider.addChangeListener(productionRateListener);
        consumptionRateSlider.addChangeListener(consumptionRateListener);

        startButton.addActionListener(event -> {
            controller.start();
            refreshUI();
        });
        stopButton.addActionListener(event -> {
            controller.stop();
            refreshUI();
        });
        resetButton.addActionListener(event -> {
            controller.reset();
            refreshUI();
        });
    }

    private void refreshUI() {
        SimStats stats = controller.getStats();
        parkingLotPanel.setStats(stats);

        occupancyValueLabel.setText(String.format("%.0f%%", stats.getOccupancyPercent()));
        availableValueLabel.setText(stats.getAvailableSlots() + " slots");
        throughputValueLabel.setText(String.format("%.2f cars/sec", stats.getThroughputPerSecond()));
        avgWaitValueLabel.setText(String.format("%.2f s", stats.getAverageWaitTimeMillis() / 1000.0));
        producedValueLabel.setText(String.valueOf(stats.getTotalProduced()));
        consumedValueLabel.setText(String.valueOf(stats.getTotalConsumed()));
        occupancyDetailLabel.setText(
            String.format("%d / %d slots occupied", stats.getOccupied(), stats.getCapacity()));
        occupancyBar.setValue(Math.min(100, (int) Math.round(stats.getOccupancyPercent())));

        if (stats.isOverCapacity()) {
            capacityWarningLabel.setText(
                "Capacity is below the current occupancy, so producers remain blocked until guards free space.");
            capacityWarningLabel.setVisible(true);
        } else {
            capacityWarningLabel.setText(" ");
            capacityWarningLabel.setVisible(true);
        }

        updateRunState(stats.isRunning());
        updateWorkerList(producerStatusList, stats.getProducerSnapshots(), "No producers active.");
        updateWorkerList(consumerStatusList, stats.getConsumerSnapshots(), "No consumers active.");

        startButton.setEnabled(!stats.isRunning());
        stopButton.setEnabled(stats.isRunning());
        producerSpinner.setEnabled(!stats.isRunning());
        consumerSpinner.setEnabled(!stats.isRunning());
    }

    private void updateRunState(boolean running) {
        runStateLabel.setText(running ? "Running" : "Stopped");
        runStateLabel.setForeground(running ? GREEN : RED);
        runStateLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(running ? new Color(198, 225, 205) : new Color(239, 211, 206), 1),
            new EmptyBorder(8, 14, 8, 14)
        ));
        runStateLabel.setBackground(running ? new Color(223, 242, 228) : new Color(247, 230, 226));
        runStateLabel.setOpaque(true);
    }

    private void updateWorkerList(JPanel listPanel, java.util.List<WorkerSnapshot> workers, String emptyText) {
        listPanel.removeAll();

        if (workers.isEmpty()) {
            JLabel emptyLabel = createMutedLabel(emptyText);
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            listPanel.add(emptyLabel);
        } else {
            for (WorkerSnapshot worker : workers) {
                listPanel.add(createWorkerRow(worker));
                listPanel.add(Box.createVerticalStrut(8));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createWorkerRow(WorkerSnapshot worker) {
        RoundedPanel row = new RoundedPanel(CARD_BACKGROUND, 16);
        row.setLayout(new BorderLayout(10, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            new EmptyBorder(10, 12, 10, 12)
        ));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        StatusDot dot = new StatusDot(colorForState(worker.getState()), 12);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(worker.getName());
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(TEXT_PRIMARY);

        JLabel detailLabel = new JLabel(workerStateText(worker) + " • " + worker.getProcessedCount() + " cars");
        detailLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        detailLabel.setForeground(TEXT_MUTED);

        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(detailLabel);

        row.add(dot, BorderLayout.WEST);
        row.add(textPanel, BorderLayout.CENTER);
        return row;
    }

    private JPanel createWorkerListPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private JLabel createSectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createSubsectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private JLabel createMetricValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 24));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JLabel createMutedLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    private JLabel createWrappedNote(String text) {
        JLabel label = new JLabel("<html><div style='width:240px;'>" + text + "</div></html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(TEXT_MUTED);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel createValueBadge(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(TEXT_PRIMARY);
        label.setOpaque(true);
        label.setBackground(new Color(234, 239, 228));
        label.setBorder(new EmptyBorder(5, 8, 5, 8));
        return label;
    }

    private JLabel createPillLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(color);
        label.setOpaque(true);
        label.setBackground(new Color(247, 230, 226));
        label.setBorder(new EmptyBorder(8, 14, 8, 14));
        return label;
    }

    private JSpinner createSpinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new javax.swing.SpinnerNumberModel(value, min, max, step));
        spinner.setPreferredSize(new Dimension(92, 30));
        return spinner;
    }

    private JSlider createSlider(int value, int min, int max) {
        JSlider slider = new JSlider(min, max, value);
        slider.setOpaque(false);
        slider.setMajorTickSpacing(500);
        slider.setMinorTickSpacing(100);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        return slider;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 12, 10, 12));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    private javax.swing.border.Border createCardBorder(int top, int left, int bottom, int right) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(CARD_BORDER, 1),
            new EmptyBorder(top, left, bottom, right)
        );
    }

    private String workerStateText(WorkerSnapshot worker) {
        return switch (worker.getState()) {
            case ACTIVE -> "Active";
            case WAITING -> "Waiting on synchronization";
            case STOPPED -> "Stopped";
        };
    }

    private Color colorForState(WorkerState state) {
        return switch (state) {
            case ACTIVE -> GREEN;
            case WAITING -> AMBER;
            case STOPPED -> RED;
        };
    }
}
