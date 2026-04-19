package com.dispenserbreeding;

import com.dispenserbreeding.config.ConfigManager;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DispenserBreeding {
	public static final String MOD_ID = "dispenserbreeding";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private DispenserBreeding() {
	}

	public static void init(Path configDirectory) {
		ConfigManager.load(configDirectory);
		LOGGER.info("Dispenser Breeding initialised.");
	}
}