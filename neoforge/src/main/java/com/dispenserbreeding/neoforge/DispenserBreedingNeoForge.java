package com.dispenserbreeding.neoforge;

import com.dispenserbreeding.DispenserBreedingCommon;
import com.dispenserbreeding.platform.Services;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;

@Mod(DispenserBreedingCommon.MOD_ID)
public final class DispenserBreedingNeoForge {
    public DispenserBreedingNeoForge() {
        Services.bind(() -> FMLPaths.CONFIGDIR.get());
        DispenserBreedingCommon.init();
    }
}
