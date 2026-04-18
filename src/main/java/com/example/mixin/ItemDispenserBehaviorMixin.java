package com.example.mixin;

import com.example.breeding.DispenserBreedingHandler;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPointer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemDispenserBehavior.class)
public abstract class ItemDispenserBehaviorMixin {
	@Inject(method = "dispenseSilently", at = @At("HEAD"), cancellable = true)
	private void dispenserbreeding$tryBreedAnimal(
		BlockPointer pointer,
		ItemStack stack,
		CallbackInfoReturnable<ItemStack> cir
	) {
		if (DispenserBreedingHandler.tryBreedFromDispenser(pointer, stack)) {
			cir.setReturnValue(stack);
		}
	}
}