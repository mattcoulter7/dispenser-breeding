package com.example.dispenser;

import java.util.Comparator;
import java.util.List;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public final class WheatBreedingDispenserBehavior extends ItemDispenserBehavior {
	// Slightly larger than one block so we catch cows standing close to the face.
	private static final double RANGE_FORWARD = 1.75D;
	private static final double RANGE_SIDE = 0.9D;
	private static final double RANGE_VERTICAL = 1.0D;

	@Override
	protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
		if (stack.isEmpty()) {
			return stack;
		}

		ServerWorld world = pointer.world();
		Direction facing = pointer.state().get(DispenserBlock.FACING);

		CowEntity target = findBreedableCow(world, pointer, facing, stack);

		if (target == null) {
			// No valid cow found, so preserve vanilla behaviour and spit the wheat out.
			return super.dispenseSilently(pointer, stack);
		}

		// Consume one wheat and trigger vanilla love mode.
		stack.decrement(1);
		target.lovePlayer(null);

		return stack;
	}

	private CowEntity findBreedableCow(
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

		List<CowEntity> candidates = world.getEntitiesByClass(
			CowEntity.class,
			searchBox,
			cow -> isValidTarget(cow, breedingStack)
		);

		return candidates.stream()
			.min(Comparator.comparingDouble(cow -> cow.squaredDistanceTo(dispenseCenter)))
			.orElse(null);
	}

	private boolean isValidTarget(CowEntity cow, ItemStack breedingStack) {
		return cow.isAlive()
			&& !cow.isBaby()
			&& !cow.isInLove()
			&& cow.canEat()
			&& cow.isBreedingItem(breedingStack);
	}
}
