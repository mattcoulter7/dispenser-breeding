package com.dispenserbreeding.mixin;

import com.dispenserbreeding.breeding.GroundBreedingItemGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
	@Unique
	private boolean dispenserbreeding$groundGoalAdded = false;

	@Inject(method = "registerGoals", at = @At("TAIL"))
	private void dispenserbreeding$addGroundBreedingGoal(CallbackInfo ci) {
		if (dispenserbreeding$groundGoalAdded) {
			return;
		}

		Animal self = (Animal) (Object) this;
		GoalSelector goalSelector = ((MobAccessor) (Mob) self).dispenserbreeding$getGoalSelector();
		goalSelector.addGoal(5, new GroundBreedingItemGoal(self));
		dispenserbreeding$groundGoalAdded = true;
	}
}
