package com.Farmrunhelper;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import com.Farmrunhelper.FarmRunData;  // Import the correct FarmRunData class

@Slf4j
public class FarmRunHelperOverlay extends Overlay {

    private final FarmRunHelperPlugin plugin;
    private final FarmRunData farmRunData;
    final Client client;

    private static final int PADDING = 5; // Padding between sections and lines
    private static final int MAX_WIDTH = 200; // Maximum width for the overlay box

    @Inject
    public FarmRunHelperOverlay(FarmRunHelperPlugin plugin, FarmRunData farmRunData, OverlayManager overlayManager, Client client) {
        this.plugin = plugin;
        this.farmRunData = farmRunData;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.isOverlayVisible() || farmRunData == null) {
            return null; // If the overlay is not visible, do not render
        }

        String currentRunType = plugin.getCurrentRunType();
        List<FarmRunData.FarmStep> steps = farmRunData.getStepsForRunType(currentRunType);
        highlightPatch(graphics);

        if (steps == null || steps.isEmpty()) {
            return new Dimension(0, 0); // No steps to display
        }

        // Get the current step from the plugin
        FarmRunData.FarmStep currentStep = plugin.getCurrentStep();
        if (currentStep == null) {
            return new Dimension(0, 0); // No current step available
        }

        String stepDescription = currentStep.getDescription();

        // Fonts for run type, step description, and requirements
        Font stepTextFont = new Font("Arial", Font.PLAIN, 12);
        graphics.setFont(stepTextFont);
        FontMetrics stepTextMetrics = graphics.getFontMetrics();

        Font runTypeFont = new Font("Arial", Font.BOLD, 16);
        graphics.setFont(runTypeFont);
        FontMetrics runTypeMetrics = graphics.getFontMetrics();

        // Wrap the step text and requirement text
        List<String> wrappedStepText = wrapText(List.of(stepDescription), graphics, MAX_WIDTH);
        List<String> wrappedRequirementsText = wrapText(currentStep.getRequiredItems() != null ? List.of(currentStep.getRequiredItems()) : new ArrayList<>(), graphics, MAX_WIDTH);

        int boxWidth = MAX_WIDTH + 2 * PADDING;
        // Call calculateBoxHeight with null checks for runTypeMetrics
        int boxHeight = calculateBoxHeight(wrappedStepText, stepTextMetrics, runTypeMetrics)
                + calculateBoxHeight(wrappedRequirementsText, stepTextMetrics, null);

        int fixedX = 0;
        int fixedY = 0;
        int yPosition = fixedY + PADDING + (runTypeMetrics != null ? runTypeMetrics.getHeight() : 0); // Check if runTypeMetrics is null

        // Draw background
        graphics.setColor(new Color(0, 0, 0, 150));
        graphics.fillRect(fixedX, fixedY, boxWidth, boxHeight);

        // Draw border
        graphics.setColor(Color.WHITE);
        graphics.drawRect(fixedX, fixedY, boxWidth, boxHeight);

        // Draw run type
        graphics.setFont(runTypeFont);
        graphics.drawString(currentRunType, fixedX + PADDING, yPosition);
        yPosition += (runTypeMetrics != null ? runTypeMetrics.getHeight() : 0) + PADDING;

        // Draw step description
        graphics.setFont(stepTextFont);
        for (String line : wrappedStepText) {
            graphics.drawString(line, fixedX + PADDING, yPosition);
            yPosition += stepTextMetrics.getHeight() + PADDING;
        }

        // Add padding between step description and requirements
        yPosition += PADDING * 3;

        // Draw requirements
        if (!wrappedRequirementsText.isEmpty()) {
            graphics.setFont(stepTextFont); // Use the same font for requirements
            for (String requirement : wrappedRequirementsText) {
                graphics.drawString(requirement, fixedX + PADDING, yPosition);
                yPosition += stepTextMetrics.getHeight() + PADDING;
            }
        }

        return new Dimension(boxWidth, boxHeight);
    }

    private void highlightPatch(Graphics2D graphics) {
        // Add logic to highlight the patch (for example, showing a border or change in background color).
        // You can use farmRunData to determine the correct location and highlight the farming patch.
    }

    private int calculateBoxHeight(List<String> wrappedText, FontMetrics stepTextMetrics, FontMetrics runTypeMetrics) {
        if (wrappedText == null || stepTextMetrics == null) {
            log.error("Invalid input to calculateBoxHeight: wrappedText={}, stepTextMetrics={}", wrappedText, stepTextMetrics);
            return 0;
        }

        int totalHeight = PADDING; // Top padding
        totalHeight += (runTypeMetrics != null ? runTypeMetrics.getHeight() + PADDING : 0); // Run type height + spacing
        totalHeight += wrappedText.size() * (stepTextMetrics.getHeight() + PADDING); // Text lines
        totalHeight += PADDING * 3; // Extra bottom padding for additional space
        return totalHeight;
    }

    private List<String> wrapText(List<String> overlayText, Graphics2D graphics, int maxWidth) {
        List<String> wrappedLines = new ArrayList<>();
        FontMetrics metrics = graphics.getFontMetrics();

        for (String text : overlayText) {
            StringBuilder currentLine = new StringBuilder();

            for (String word : text.split(" ")) {
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

                if (metrics.stringWidth(testLine) > maxWidth) {
                    wrappedLines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine.append(currentLine.length() == 0 ? word : " " + word);
                }
            }

            if (currentLine.length() > 0) {
                wrappedLines.add(currentLine.toString());
            }
        }

        return wrappedLines;
    }
}

