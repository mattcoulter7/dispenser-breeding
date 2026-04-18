package com.example.breeding;

import java.util.Comparator;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class DispenserBreedingHandler {
	private static final double RANGE_FORWARD = 1.75D;
	private static final double RANGE_SIDE = 0.9D;
	private static final double RANGE_VERTICAL = 1.0D;

	private DispenserBreedingHandler() {
	}

	public static boolean tryBreedFromDispenser(BlockPointer pointer, ItemStack stack) {
		if (stack.isEmpty()) {
			return false;
		}

		ServerWorld world = pointer.world();
		BlockState state = pointer.state();
		Direction facing = state.get(DispenserBlock.FACING);

		AnimalEntity target = findBreedableAnimal(world, pointer, facing, stack);
		if (target == null) {
			return false;
		}

		stack.decrement(1);
		target.lovePlayer(null);
		return true;
	}

	private static AnimalEntity findBreedableAnimal(
		ServerWorld world,
		BlockPointer pointer,
		Direction facing,
		ItemStack breedingStack
	) {
		Vec3d dispenseCenter = Vec3d.ofCenter(pointer.pos()).add(
			facing.getOffsetX(),
			facing.getOffsetY(),
			facing.getOffsetZ()
		);

		Box searchBox = new Box(
			dispenseCenter.x - RANGE_SIDE,
			dispenseCenter.y - RANGE_VERTICAL,
			dispenseCenter.z - RANGE_SIDE,
			dispenseCenter.x + RANGE_SIDE,
			dispenseCenter.y + RANGE_VERTICAL,
			dispenseCenter.z + RANGE_SIDE
		).stretch(
			facing.getOffsetX() * (RANGE_FORWARD - 1.0D),
			facing.getOffsetY() * (RANGE_FORWARD - 1.0D),
			facing.getOffsetZ() * (RANGE_FORWARD - 1.0D)
		);

		List<AnimalEntity> candidates = world.getEntitiesByClass(
			AnimalEntity.class,
			searchBox,
			animal -> isValidTarget(animal, breedingStack)
		);

		return candidates.stream()
			.min(Comparator.comparingDouble(animal -> animal.squaredDistanceTo(dispenseCenter)))
			.orElse(null);
	}

	private static boolean isValidTarget(AnimalEntity animal, ItemStack breedingStack) {
		return animal.isAlive()
			&& !animal.isBaby()
			&& !animal.isInLove()
			&& animal.canEat()
			&& animal.isBreedingItem(breedingStack);
	}
}