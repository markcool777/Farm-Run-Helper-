package com.Farmrunhelper;

import javax.inject.Inject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.runelite.client.eventbus.Subscribe;

import static net.runelite.api.Varbits.GRAPES_4960;

@Slf4j
@PluginDescriptor(
        name = "Farm Run Helper",
        description = "A plugin to help manage farm runs in RuneScape",
        tags = {"farming", "agriculture", "helper"}
)
public class FarmRunHelperPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private FarmRunHelperOverlay overlay;

    @Inject
    private FarmRunHelperConfig config;

    @Inject
    private EventBus eventBus;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private FarmRunData farmRunData;

    private NavigationButton navButton;

    private boolean hasPlanted = false; // Track planting status

    @Getter
    private boolean overlayVisible = false;
    @Setter
    private String currentRunType;

    private int currentStepIndex = 0;
    private int previousHerbPlantingStatus = -1; // Track previous Varbit value
    private boolean isInitialized = false; // Track if the plugin is initialized
    private long lastPlantingTimestamp = 0; // Time of the last detected planting
    private static final int PLANTING_COOLDOWN_MS = 10000; // 10-second cooldown
    private boolean isFirstTickAfterLogin = true; // Track the first tick after login

    @Override
    protected void startUp() {
        eventBus.register(this);
        setOverlayVisible(false);

        // Reset state to handle reinitialization or login
        resetState();

        BufferedImage defaultIcon = getDefaultIcon();
        if (defaultIcon == null) {
            throw new IllegalArgumentException("Icon cannot be null");
        }

        FarmRunHelperPanel panel = new FarmRunHelperPanel(this);

        navButton = NavigationButton.builder()
                .tooltip("Farm Run Helper")
                .icon(defaultIcon)
                .priority(5)
                .panel(panel)
                .build();
        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown() {
        setOverlayVisible(false);
        eventBus.unregister(this);
        clientToolbar.removeNavigation(navButton);
        log.info("Farm Run Helper stopped!");
    }

    private BufferedImage getDefaultIcon() {
        try {
            var imageStream = getClass().getResourceAsStream("com/Farmrunhelper/icon.png");
            if (imageStream == null) {
                log.error("Image stream is null for default icon");
                return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB); // Basic transparent placeholder
            }
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            log.error("Default icon could not be loaded", e);
            return new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB); // Basic transparent placeholder
        }
    }

    public void setOverlayVisible(boolean visible) {
        if (visible && !overlayVisible) {
            overlayManager.add(overlay);
        } else if (!visible && overlayVisible) {
            overlayManager.remove(overlay);
        }
        overlayVisible = visible;
    }

    @Provides
    FarmRunHelperConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(FarmRunHelperConfig.class);
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        // Ensure the overlay is active and a run is selected
        if (!isOverlayVisible() || getCurrentRunType() == null) {
            return;
        }

        // Get the current value of the herb planting Varbit
        int currentHerbPlantingStatus = client.getVarbitValue(GRAPES_4960);

        // Handle the first tick after login or reinitialization
        if (isFirstTickAfterLogin) {
            log.debug("First tick after login/reinitialization detected. Varbit: {}", currentHerbPlantingStatus);
            previousHerbPlantingStatus = currentHerbPlantingStatus;
            isFirstTickAfterLogin = false;
            return;
        }

        // Check for legitimate planting action: Transition from 0 to 6, with cooldown check
        if (!hasPlanted && previousHerbPlantingStatus == 0 && currentHerbPlantingStatus == 6) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPlantingTimestamp > PLANTING_COOLDOWN_MS) {
                log.info("Herb successfully planted! Moving to the next step.");
                hasPlanted = true;
                lastPlantingTimestamp = currentTime;
                moveToNextStep();
            } else {
                log.debug("Ignored planting action due to cooldown. Elapsed time: {}ms", currentTime - lastPlantingTimestamp);
            }
        }

        // Reset planting status if the Varbit returns to 0
        if (currentHerbPlantingStatus == 0) {
            hasPlanted = false;
        }

        // Log current patch status
        logPatchStatus();

        // Update the previous value for the next tick
        previousHerbPlantingStatus = currentHerbPlantingStatus;
    }

    private void moveToNextStep() {
        // Ensure a valid run type is selected
        List<FarmRunData.FarmStep> steps = farmRunData.getStepsForRunType(getCurrentRunType());
        if (steps == null || steps.isEmpty()) {
            log.warn("No steps available for the current run type: {}", getCurrentRunType());
            return;
        }

        currentStepIndex++;
        if (currentStepIndex >= steps.size()) {
            log.info("Farm run complete!");
            currentStepIndex = 0; // Reset to the first step if the run is complete
        } else {
            FarmRunData.FarmStep currentStep = steps.get(currentStepIndex);
            log.info("Moved to step {}: {}", currentStepIndex, currentStep.getDescription());
        }
    }

    private void resetState() {
        isFirstTickAfterLogin = true;
        hasPlanted = false;
        lastPlantingTimestamp = 0;
        previousHerbPlantingStatus = -1; // Reset previous Varbit value
        currentStepIndex = 0; // Reset step index
        log.debug("State reset after login or initialization.");
    }

    private void logPatchStatus() {
        List<GameObject> patchObjects = getCurrentStepPatchObjects();
        if (patchObjects != null && !patchObjects.isEmpty()) {
            log.info("Current patch objects: {}", patchObjects.size());
            for (GameObject obj : patchObjects) {
                log.info("Patch Object: {}", obj);
            }
        } else {
            log.info("No patch objects found at the current location.");
        }
    }

    public List<GameObject> getCurrentStepPatchObjects() {
        FarmRunData.FarmStep currentStep = getCurrentStep();
        if (currentStep == null) {
            return new ArrayList<>();  // Return empty list if no step is available
        }

        // Get the object ID for the patch
        int patchObjectId = currentStep.getPatchObjectId();

        // Find objects using the object ID
        return findObjectsByObjectId(patchObjectId);
    }

    public FarmRunData.FarmStep getCurrentStep() {
        List<FarmRunData.FarmStep> steps = farmRunData.getStepsForRunType(getCurrentRunType());
        return steps.isEmpty() ? null : steps.get(currentStepIndex);
    }

    public List<GameObject> findObjectsByObjectId(int objectId) {
        List<GameObject> objects = new ArrayList<>();
        for (Tile[] tiles : client.getScene().getTiles()) {
            for (Tile tile : tiles) {
                if (tile != null) {
                    for (GameObject object : tile.getGameObjects()) {
                        if (object != null && object.getId() == objectId) {
                            objects.add(object);
                        }
                    }
                }
            }
        }
        return objects;
    }

    public String getCurrentRunType() {
        return currentRunType != null ? currentRunType : "Herb Run"; // Default run type
    }

    public List<GameObject> getCurrentStepPatchObjects() {
        FarmRunData.FarmStep currentStep = getCurrentStep();
        if (currentStep == null) {
            return new ArrayList<>();  // Return empty list if no step is available
        }

        // Get the object ID for the patch
        int patchObjectId = currentStep.getPatchObjectId();

        // Find objects using the object ID
        return findObjectsByObjectId(patchObjectId);
    }

}
