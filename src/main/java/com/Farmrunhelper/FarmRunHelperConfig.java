package com.Farmrunhelper;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("farmrunhelper")
public interface FarmRunHelperConfig extends Config {

    @ConfigItem(
            keyName = "useCompost",
            name = "Use Compost",
            description = "Enable to track compost usage during farm runs"
    )
    default boolean useCompost() {
        return false; // Default: Compost tracking is off
    }

    public enum HighlightCondition {
        SEEDS_PLANTED,
        COMPOST_USED
    }

    @ConfigItem(
            keyName = "highlightUntil",
            name = "Highlight Until",
            description = "Choose whether to highlight the patch until seeds are planted or compost is used",
            position = 1
    )
    default HighlightCondition highlightUntil() {
        return HighlightCondition.SEEDS_PLANTED;
    }

}
