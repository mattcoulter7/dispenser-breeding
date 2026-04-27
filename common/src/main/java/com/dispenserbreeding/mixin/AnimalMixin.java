package com.dispenserbreeding.mixin;

import com.dispenserbreeding.DispenserBreeding;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {

	// Log #5 — breeding actually happening
	@Inject(method = "spawnChildFromBreeding", at = @At("HEAD"))
	private void dispenserbreeding$logBreedSuccess(ServerLevel level, Animal partner, CallbackInfo ci) {
		Animal self = (Animal) (Object) this;
		DispenserBreeding.LOGGER.info(
			"BREED SUCCESS: parent1={} parent2={}",
			self.getId(),
			partner.getId()
		);
	}

	// Log #6 — love expiration tick
	@Inject(method = "tick", at = @At("TAIL"))
	private void dispenserbreeding$logLoveTick(CallbackInfo ci) {
		Animal self = (Animal) (Object) this;
		if (self.isInLove() && self.getInLoveTime() % 20 == 0) {
			DispenserBreeding.LOGGER.info(
				"Tick: id={} loveTime={}",
				self.getId(),
				self.getInLoveTime()
			);
		}
	}
}
