package com.Farmrunhelper;

import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FarmRunData {

    private final List<FarmRunStep> treeRunSteps = new ArrayList<>();
    private final List<FarmRunStep> herbRunSteps = new ArrayList<>();
    private final List<FarmRunStep> hardwoodRunSteps = new ArrayList<>();

    public FarmRunData() {
        defineTreeRun();
        defineHerbRun();
        defineHardwoodRun();
    }

    private void defineTreeRun() {
        treeRunSteps.add(new FarmRunStep(
                "Spirit Tree to Tree Gnome Stronghold, plant fruit tree and normal tree",
                new WorldPoint(2488, 3470, 0),
                "Spade", "Saplings", "Spirit Tree Teleport"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Spirit Tree to Tree Gnome Village, follow Elkoy to patch, plant fruit tree",
                new WorldPoint(2530, 3176, 0),
                "Spade", "Saplings", "Spirit Tree Teleport"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Elf crystal to Lletya, plant fruit tree",
                new WorldPoint(2326, 3165, 0),
                "Spade", "Saplings", "Teleport Crystal"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Fairy ring to Tai Bo Wannai, run north to calquat patch, plant calquat",
                new WorldPoint(2788, 3084, 0),
                "Spade", "Calquat Sapling", "Dramen Staff or Lunar Staff"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Run north to Brimhaven, plant fruit tree",
                new WorldPoint(2764, 3212, 0),
                "Spade", "Saplings"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Charter ship to Catherby, plant fruit tree",
                new WorldPoint(2813, 3447, 0),
                "Spade", "Saplings", "Coins for charter"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Teleport to Lumbridge, west of Lumbridge Castle",
                new WorldPoint(3222, 3426, 0),
                "Spade", "Saplings", "Lumbridge teleport"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Teleport to Varrock, Varrock Castle courtyard",
                new WorldPoint(3329, 3230, 0),
                "Spade", "Saplings", "Varrock teleport"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Teleport to Falador, Falador Park",
                new WorldPoint(3035, 3312, 0),
                "Spade", "Saplings", "Falador teleport"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Run north and use agility shortcut in the wall if available to Taverley patch",
                new WorldPoint(2923, 3447, 0),
                "Spade", "Saplings"
        ));

        treeRunSteps.add(new FarmRunStep(
                "Skills necklace to farming guild, plant fruit tree lvl 65 and at lvl 85 farming normal tree and fight Hespori",
                new WorldPoint(1248, 3718, 0),
                "Spade", "Saplings", "Skills Necklace"
        ));
    }

    private void defineHerbRun() {
        herbRunSteps.add(new FarmRunStep(
                "Minigame teleport to Tithe Farm, use Kourend patch",
                new WorldPoint(1464, 3686, 0),
                "Spade", "Herb Seeds", "Teleport", "Minigame Teleport"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Stony basalt to Trollheim patch. With 73 agility and Fremennik hard diary use shortcut.",
                new WorldPoint(2842, 3694, 0),
                "Spade", "Herb Seeds", "Stony Basalt"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Icy basalt to Weiss patch",
                new WorldPoint(2846, 3940, 0),
                "Spade", "Herb Seeds", "Icy Basalt"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Camelot teleport to Catherby patch",
                new WorldPoint(2818, 3435, 0),
                "Spade", "Herb Seeds", "Camelot Teleport"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Explorer's ring teleport to Falador patch",
                new WorldPoint(3058, 3312, 0),
                "Spade", "Herb Seeds", "Explorer's Ring"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Ardougne Cape teleport to Ardougne patch",
                new WorldPoint(2658, 3378, 0),
                "Spade", "Herb Seeds", "Ardougne Cape"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Ectophial teleport to Port Phasmatys patch",
                new WorldPoint(3604, 3529, 0),
                "Spade", "Herb Seeds", "Ectophial"
        ));

        herbRunSteps.add(new FarmRunStep(
                "Harmony Island teleport to Harmony Island",
                new WorldPoint(3798, 2875, 0),
                "Spade", "Herb Seeds", "Harmony Island Teleport"
        ));
    }

    private void defineHardwoodRun() {
        hardwoodRunSteps.add(new FarmRunStep(
                "Digsite pendant to Fossil Island patch",
                new WorldPoint(3724, 3791, 0),
                "Spade", "Hardwood Saplings", "Digsite Pendant"
        ));

        hardwoodRunSteps.add(new FarmRunStep(
                "Fairy ring AJR to Varlamore patch",
                new WorldPoint(3707, 3832, 0),
                "Spade", "Hardwood Saplings", "Dramen Staff or Lunar Staff"
        ));
    }

    public List<FarmRunStep> getStepsForRunType(String runType) {
        switch (runType) {
            case "Tree Run":
                return treeRunSteps;
            case "Herb Run":
                return herbRunSteps;
            case "Hardwood Run":
                return hardwoodRunSteps;
            default:
                return new ArrayList<>(); // Return an empty list if no run type matches
        }
    }

    @Getter
    public static class FarmRunStep {
        private final String description;
        private final WorldPoint location;
        private final String[] requiredItems;
        private final String teleportMethod;

        // Constructor with teleport method
        public FarmRunStep(String description, WorldPoint location, String[] requiredItems, String teleportMethod) {
            this.description = description;
            this.location = location;
            this.requiredItems = requiredItems;
            this.teleportMethod = teleportMethod;
        }

        // Constructor without teleport method for steps without teleportation
        public FarmRunStep(String description, WorldPoint location, String... requiredItems) {
            this(description, location, requiredItems, null);
        }
    }
}
