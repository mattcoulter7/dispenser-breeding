package com.dispenserbreeding.fabric;

import com.dispenserbreeding.DispenserBreeding;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class DispenserBreedingFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		DispenserBreeding.init(FabricLoader.getInstance().getConfigDir());
	}
}