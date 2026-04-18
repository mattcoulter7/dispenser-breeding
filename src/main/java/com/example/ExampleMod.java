package com.example;

import com.example.dispenser.WheatBreedingDispenserBehavior;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.Items;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExampleMod implements ModInitializer {
	public static final String MOD_ID = "examplemod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DispenserBlock.registerBehavior(Items.WHEAT, new WheatBreedingDispenserBehavior());
		LOGGER.info("Registered dispenser breeding behaviour for wheat.");
	}
}