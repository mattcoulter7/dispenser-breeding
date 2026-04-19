package com.dispenserbreeding.forge;

import com.dispenserbreeding.DispenserBreedingCommon;
import com.dispenserbreeding.platform.Services;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod(DispenserBreedingCommon.MOD_ID)
public final class DispenserBreedingForge {
    public DispenserBreedingForge() {
        Services.bind(() -> FMLPaths.CONFIGDIR.get());
        DispenserBreedingCommon.init();
    }
}
