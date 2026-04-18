package com.example.breeding;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class DispenserBreedingHandler {
	private static final double RANGE_FORWARD = 1.75D;
	private static final double RANGE_SIDE = 0.9D;
	private static final double RANGE_VERTICAL = 1.0D;

	private DispenserBreedingHandler() {
	}

	public static boolean tryBreedFromDispenser(BlockSource source, ItemStack stack) {
		if (stack.isEmpty()) {
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
			dispenseCenter.x - RANGE_SIDE,
			dispenseCenter.y - RANGE_VERTICAL,
			dispenseCenter.z - RANGE_SIDE,
			dispenseCenter.x + RANGE_SIDE,
			dispenseCenter.y + RANGE_VERTICAL,
			dispenseCenter.z + RANGE_SIDE
		).expandTowards(
			facing.getStepX() * (RANGE_FORWARD - 1.0D),
			facing.getStepY() * (RANGE_FORWARD - 1.0D),
			facing.getStepZ() * (RANGE_FORWARD - 1.0D)
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
}