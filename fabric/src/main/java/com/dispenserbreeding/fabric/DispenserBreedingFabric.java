package com.dispenserbreeding.fabric;

import com.dispenserbreeding.DispenserBreedingCommon;
import com.dispenserbreeding.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public final class DispenserBreedingFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Services.bind(() -> FabricLoader.getInstance().getConfigDir());
        DispenserBreedingCommon.init();
    }
}
