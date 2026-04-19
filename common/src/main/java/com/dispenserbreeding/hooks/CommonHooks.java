package com.dispenserbreeding.hooks;

import com.dispenserbreeding.breeding.DispenserBreedingHandler;
import com.dispenserbreeding.breeding.GroundBreedingItemGoal;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;

public final class CommonHooks {
    private CommonHooks() {
    }

    public static boolean tryBreedFromDispenser(BlockSource source, ItemStack stack) {
        return DispenserBreedingHandler.tryBreedFromDispenser(source, stack);
    }

    public static void attachGroundGoalIfNeeded(Mob mob, GoalSelector goalSelector, boolean alreadyChecked) {
        if (alreadyChecked) {
            return;
        }

        if (mob instanceof Animal animal) {
            goalSelector.addGoal(5, new GroundBreedingItemGoal(animal));
        }
    }
}
