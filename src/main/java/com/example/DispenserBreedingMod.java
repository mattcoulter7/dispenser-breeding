package com.example;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DispenserBreedingMod implements ModInitializer {
	public static final String MOD_ID = "dispenserbreeding";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Dispenser Breeding mod initialised.");
	}
}