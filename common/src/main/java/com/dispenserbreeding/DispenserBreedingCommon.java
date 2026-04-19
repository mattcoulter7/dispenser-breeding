package com.dispenserbreeding;

import com.dispenserbreeding.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DispenserBreedingCommon {
    public static final String MOD_ID = "dispenserbreeding";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private DispenserBreedingCommon() {
    }

    public static void init() {
        ConfigManager.load();
        LOGGER.info("Dispenser Breeding initialised.");
    }
}
