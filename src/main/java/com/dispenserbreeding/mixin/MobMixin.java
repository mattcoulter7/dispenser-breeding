package com.dispenserbreeding.mixin;

import com.dispenserbreeding.breeding.GroundBreedingItemGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {
	@Shadow
	protected GoalSelector goalSelector;

	@Unique
	private boolean dispenserbreeding$groundGoalChecked = false;

	@Inject(method = "tick", at = @At("TAIL"))
	private void dispenserbreeding$ensureGroundBreedingGoal(CallbackInfo ci) {
		if (dispenserbreeding$groundGoalChecked) {
			return;
		}

		dispenserbreeding$groundGoalChecked = true;

		if (!(((Object) this) instanceof Animal animal)) {
			return;
		}

		goalSelector.addGoal(5, new GroundBreedingItemGoal(animal));
	}
}
