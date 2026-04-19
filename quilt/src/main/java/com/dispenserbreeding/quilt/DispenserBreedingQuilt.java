package com.dispenserbreeding.quilt;

import com.dispenserbreeding.DispenserBreedingCommon;
import com.dispenserbreeding.platform.Services;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.ModInitializer;
import org.quiltmc.loader.api.QuiltLoader;

public final class DispenserBreedingQuilt implements ModInitializer {
    @Override
    public void onInitialize(ModContainer mod) {
        Services.bind(QuiltLoader::getConfigDir);
        DispenserBreedingCommon.init();
    }
}
