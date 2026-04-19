package com.dispenserbreeding.mixin;

import com.dispenserbreeding.hooks.CommonHooks;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DefaultDispenseItemBehavior.class)
public abstract class ItemDispenserBehaviorMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void dispenserbreeding$tryBreedAnimal(
        BlockSource source,
        ItemStack stack,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        if (CommonHooks.tryBreedFromDispenser(source, stack)) {
            cir.setReturnValue(stack);
        }
    }
}
