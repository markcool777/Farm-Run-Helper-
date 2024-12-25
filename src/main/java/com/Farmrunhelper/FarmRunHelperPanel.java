package com.Farmrunhelper;

import javax.inject.Inject;
import javax.swing.*;
import lombok.Getter;
import net.runelite.client.ui.PluginPanel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class FarmRunHelperPanel extends PluginPanel {

    private final FarmRunHelperPlugin plugin;

    private JPanel cardPanel; // Panel for CardLayout
    private JPanel mainMenuPanel;
    private final Map<String, JPanel> runPanels = new HashMap<>();
    private final Map<String, FarmRunPanelData> runPanelData = new HashMap<>();

    private JLabel overlayLabel; // Overlay component

    @Inject
    public FarmRunHelperPanel(FarmRunHelperPlugin plugin) {
        this.plugin = plugin;

        setLayout(new BorderLayout()); // Ensure PluginPanel uses BorderLayout

        // Initialize overlay
        initOverlay();

        // Initialize CardLayout for main content
        initCardLayout();

        // Initialize data and panels
        initRunPanelData();
        initPanels();
    }

    private void initOverlay() {
        // Create overlay label
        overlayLabel = new JLabel("Farm Run Helper Active", SwingConstants.CENTER);
        overlayLabel.setOpaque(true);
        overlayLabel.setBackground(new Color(0, 0, 0, 128)); // Semi-transparent black
        overlayLabel.setForeground(Color.WHITE);
        overlayLabel.setFont(new Font("Arial", Font.BOLD, 14));
        overlayLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Add overlay to the top
        add(overlayLabel, BorderLayout.NORTH);
    }

    private void initCardLayout() {
        // Create cardPanel with CardLayout and add to center
        cardPanel = new JPanel(new CardLayout());
        add(cardPanel, BorderLayout.CENTER); // Add cardPanel to the center of the BorderLayout
    }

    private void initRunPanelData() {
        runPanelData.put("Herb Run", new FarmRunPanelData(
                new String[]{"","Tithe Farm Patch", "Trollheim Patch", "Weiss Patch", "Catherby Patch", "Falador Patch",
                        "Ardougne Patch", "Port Phasmatys Patch", "Harmony Island Patch"},
                new String[]{"","Herb Seeds", "Spade", "Watering Can", "Teleport Runes"},
                new String[]{"","Minigame Teleport", "Stony Basalt", "Icy Basalt", "Camelot Teleport Runes", "Explorer's Ring"}
        ));

        runPanelData.put("Tree Run", new FarmRunPanelData(
                new String[]{"","Spirit Tree to Tree Gnome Stronghold", "Spirit Tree to Tree Gnome Village", "Elf crystal to Lletya",
                        "Fairy ring to Tai Bo Wannai", "Brimhaven Patch", "Catherby Patch", "Lumbridge Patch",
                        "Varrock Patch", "Falador Patch", "Taverley Patch", "Skills Necklace to Farming Guild"},
                new String[]{"","Saplings", "Spade", "Teleport Runes", "Coins for Charter", "Dramen Staff or Lunar Staff"},
                new String[]{"","Spirit Tree Teleport", "Teleport Crystal", "Dramen Staff", "Lunar Staff", "Charter Ship"}
        ));

        runPanelData.put("Hardwood Run", new FarmRunPanelData(
                new String[]{"","Fossil Island Patch", "Varlamore Patch"},
                new String[]{"","Hardwood Saplings", "Spade", "Digsite Pendant"},
                new String[]{"","Digsite Pendant", "Dramen Staff or Lunar Staff"}
        ));
    }

    private void initPanels() {
        // Set up the cardPanel with a CardLayout
        cardPanel.setLayout(new CardLayout());

        // Create the main menu panel
        mainMenuPanel = new JPanel();
        mainMenuPanel.setLayout(new BoxLayout(mainMenuPanel, BoxLayout.Y_AXIS));

        // Add buttons for each run type to the main menu
        createMainMenuButton("Herb Run");
        createMainMenuButton("Tree Run");
        createMainMenuButton("Hardwood Run");

        // Add the main menu panel to the cardPanel with the identifier "Main Menu"
        cardPanel.add(mainMenuPanel, "Main Menu");

        // Create and add individual run panels for each run type
        for (String runType : runPanelData.keySet()) {
            JPanel runPanel = createRunPanel(runType);
            runPanels.put(runType, runPanel);
            cardPanel.add(runPanel, runType); // Add each run panel with its respective identifier
        }

        // Show the main menu panel by default
        showRunPanel("Main Menu");
    }


    private void createMainMenuButton(String runType) {
        JButton button = new JButton(runType);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.addActionListener(e -> startRun(runType));
        mainMenuPanel.add(button);
    }

    private JPanel createRunPanel(String runType) {
        JPanel runPanel = new JPanel();
        runPanel.setLayout(new BoxLayout(runPanel, BoxLayout.Y_AXIS)); // BoxLayout allows vertical stacking

        // Back button
        JButton backButton = new JButton("X");
        backButton.addActionListener(e -> goBackToMainMenu());
        JPanel backButtonPanel = new JPanel();
        backButtonPanel.add(backButton);
        runPanel.add(backButtonPanel);

        // Display run data
        FarmRunPanelData data = runPanelData.get(runType);
        if (data != null) {
            addSectionToPanel(runPanel, "Patch Locations", data.getPatchLocations());
            addSectionToPanel(runPanel, "Requirements", data.getRequirements());
            addSectionToPanel(runPanel, "Teleport Items", data.getTeleportItems());
        }

        // Make sure the panel can grow dynamically
        runPanel.add(Box.createVerticalGlue()); // This ensures the panel stretches to fill vertical space

        return runPanel;
    }

    private void addSectionToPanel(JPanel panel, String title, String[] items) {
        // Create a JTextArea to display the items (instead of a JList)
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createTitledBorder(title));

        // Add the items to the text area
        for (String item : items) {
            textArea.append(item + "\n");
        }

        // Make the textArea grow vertically based on its content
        textArea.setPreferredSize(new Dimension(250, textArea.getPreferredSize().height));

        // Add the textArea to the panel
        panel.add(textArea);
    }



    private void startRun(String runType) {
        plugin.setCurrentRunType(runType); // Update the current run type in the plugin
        showRunPanel(runType);            // Switch to the corresponding run panel

        // Enable overlay and set its current run type
        plugin.setOverlayVisible(true);
        updateOverlay(runType);
    }

    private void updateOverlay(String runType) {
        overlayLabel.setText("Currently Viewing: " + runType);
    }

    private void showRunPanel(String runType) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, runType);
    }

    private void goBackToMainMenu() {
        LayoutManager layout = cardPanel.getLayout();
        if (layout instanceof CardLayout) {
            CardLayout cl = (CardLayout) layout;
            cl.show(cardPanel, "Main Menu");
        } else {
            throw new IllegalStateException("cardPanel is not using CardLayout!");
        }

        // Hide the overlay when returning to the main menu
        plugin.setOverlayVisible(false);
        updateOverlay("Main Menu");
    }
}
