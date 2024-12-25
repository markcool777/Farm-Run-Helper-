package com.Farmrunhelper;

public class FarmRunPanelData {

    // Data arrays to hold the details for farm run
    private final String[] patchLocations;
    private final String[] requirements;
    private final String[] teleportItems;

    // Constructor that initializes the fields with given data
    public FarmRunPanelData(String[] patchLocations, String[] requirements, String[] teleportItems) {
        this.patchLocations = patchLocations;
        this.requirements = requirements;
        this.teleportItems = teleportItems;
    }

    // Getters to access the data arrays
    public String[] getPatchLocations() {
        return patchLocations;
    }

    public String[] getRequirements() {
        return requirements;
    }

    public String[] getTeleportItems() {
        return teleportItems;
    }

    // Main method to demonstrate the structure and functionality
    public static void main(String[] args) {
        // Example: Herb Run data
        FarmRunPanelData herbRunData = new FarmRunPanelData(
                new String[]{
                        "-", "Tithe Farm Patch", "Trollheim Patch", "Weiss Patch", "Catherby Patch", "Falador Patch",
                        "Ardougne Patch", "Port Phasmatys Patch", "Harmony Island Patch"
                },
                new String[]{
                        "Herb Seeds", "Spade", "Watering Can"
                },
                new String[]{
                        "Minigame Teleport", "Stony Basalt", "Icy Basalt", "Camelot Teleport Runes", "Explorer's Ring"
                }
        );

        // Example: Tree Run data
        FarmRunPanelData treeRunData = new FarmRunPanelData(
                new String[]{
                        "Spirit Tree to Tree Gnome Stronghold", "Spirit Tree to Tree Gnome Village", "Elf crystal to Lletya",
                        "Fairy ring to Tai Bo Wannai", "Brimhaven Patch", "Catherby Patch", "Lumbridge Patch",
                        "Varrock Patch", "Falador Patch", "Taverley Patch", "Skills Necklace to Farming Guild"
                },
                new String[]{
                        "Saplings", "Spade", "Coins for Charter", "Dramen Staff or Lunar Staff"
                },
                new String[]{
                        "Spirit Tree Teleport", "Teleport Crystal", "Dramen Staff", "Lunar Staff", "Charter Ship"
                }
        );

        // Example: Hardwood Run data
        FarmRunPanelData hardwoodRunData = new FarmRunPanelData(
                new String[]{
                        "Fossil Island Patch", "Varlamore Patch"
                },
                new String[]{
                        "Hardwood Saplings", "Spade", "Digsite Pendant"
                },
                new String[]{
                        "Digsite Pendant", "Dramen Staff or Lunar Staff"
                }
        );

        // Print out the data for Herb Run
        System.out.println("Herb Run Patch Locations: ");
        for (String location : herbRunData.getPatchLocations()) {
            System.out.println(location);
        }

        System.out.println("\nHerb Run Requirements: ");
        for (String requirement : herbRunData.getRequirements()) {
            System.out.println(requirement);
        }

        System.out.println("\nHerb Run Teleport Items: ");
        for (String item : herbRunData.getTeleportItems()) {
            System.out.println(item);
        }
    }
}
