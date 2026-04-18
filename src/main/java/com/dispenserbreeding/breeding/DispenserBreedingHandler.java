package com.dispenserbreeding.breeding;

import java.util.Comparator;
import java.util.List;

import com.dispenserbreeding.config.ConfigManager;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DispenserBreedingHandler {
	private DispenserBreedingHandler() {
	}

	public static boolean tryBreedFromDispenser(BlockSource source, ItemStack stack) {
		if (stack.isEmpty() || !ConfigManager.get().enableDispenserBreeding) {
			return false;
		}

		ServerLevel level = source.level();
		Direction facing = source.state().getValue(DispenserBlock.FACING);

		Animal target = findBreedableAnimal(level, source, facing, stack);
		if (target == null) {
			return false;
		}

		stack.shrink(1);
		target.setInLove(null);
		return true;
	}

	private static Animal findBreedableAnimal(
		ServerLevel level,
		BlockSource source,
		Direction facing,
		ItemStack breedingStack
	) {
		Vec3 dispenseCenter = source.center().add(
			facing.getStepX(),
			facing.getStepY(),
			facing.getStepZ()
		);

		AABB searchBox = new AABB(
			dispenseCenter.x - rangeSide(),
			dispenseCenter.y - rangeVertical(),
			dispenseCenter.z - rangeSide(),
			dispenseCenter.x + rangeSide(),
			dispenseCenter.y + rangeVertical(),
			dispenseCenter.z + rangeSide()
		).expandTowards(
			facing.getStepX() * (rangeForward() - 1.0D),
			facing.getStepY() * (rangeForward() - 1.0D),
			facing.getStepZ() * (rangeForward() - 1.0D)
		);

		List<Animal> candidates = level.getEntitiesOfClass(
			Animal.class,
			searchBox,
			animal -> isValidTarget(animal, breedingStack)
		);

		return candidates.stream()
			.min(Comparator.comparingDouble(animal -> animal.distanceToSqr(dispenseCenter)))
			.orElse(null);
	}

	private static boolean isValidTarget(Animal animal, ItemStack breedingStack) {
		return animal.isAlive()
			&& !animal.isBaby()
			&& !animal.isInLove()
			&& animal.canFallInLove()
			&& animal.isFood(breedingStack);
	}

	private static double rangeForward() {
		return ConfigManager.get().rangeForward;
	}

	private static double rangeSide() {
		return ConfigManager.get().rangeSide;
	}

	private static double rangeVertical() {
		return ConfigManager.get().rangeVertical;
	}
}