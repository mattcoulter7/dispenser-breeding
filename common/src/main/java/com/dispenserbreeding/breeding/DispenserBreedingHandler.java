package com.dispenserbreeding.breeding;

import java.util.Comparator;
import java.util.List;

import com.dispenserbreeding.DispenserBreeding;
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

		// Log #1: selected animal
		DispenserBreeding.LOGGER.info(
			"Selected animal id={} pos={} inLove={} age={} canFallInLove={}",
			target.getId(),
			target.blockPosition(),
			target.isInLove(),
			target.getAge(),
			target.canFallInLove()
		);

		stack.shrink(1);
		target.setInLove(null);

		// Log #3: after feeding
		DispenserBreeding.LOGGER.info(
			"After feed: id={} type={} loveTime={} isInLove={}",
			target.getId(),
			target.getType(),
			target.getInLoveTime(),
			target.isInLove()
		);

		// Log #4: nearby potential mates
		List<Animal> nearby = level.getEntitiesOfClass(
			Animal.class,
			target.getBoundingBox().inflate(3),
			a -> a != target
		);
		for (Animal other : nearby) {
			DispenserBreeding.LOGGER.info(
				"Nearby: id={} inLove={} canMate={}",
				other.getId(),
				other.isInLove(),
				target.canMate(other)
			);
		}

		// Log #7: pair check (smoking gun)
		DispenserBreeding.LOGGER.info(
			"PAIR CHECK: target={} hasMateNearby={}",
			target.getId(),
			level.getEntitiesOfClass(
				Animal.class,
				target.getBoundingBox().inflate(3),
				a -> a != target && a.isInLove() && target.canMate(a)
			).size()
		);

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

		// Log #2: all candidates
		DispenserBreeding.LOGGER.info("Candidates ({}):", candidates.size());
		for (Animal a : candidates) {
			DispenserBreeding.LOGGER.info(
				"  - id={} dist={} inLove={} age={} canFallInLove={}",
				a.getId(),
				a.distanceToSqr(dispenseCenter),
				a.isInLove(),
				a.getAge(),
				a.canFallInLove()
			);
		}

		return candidates.stream()
			.min(Comparator.comparingDouble(animal -> animal.distanceToSqr(dispenseCenter)))
			.orElse(null);
	}

	private static boolean isValidTarget(Animal animal, ItemStack breedingStack) {
		return isReadyToBreed(animal)
			&& animal.isFood(breedingStack);
	}

	private static boolean isReadyToBreed(Animal animal) {
		return animal.isAlive()
			&& animal.getAge() == 0
			&& !animal.isInLove()
			&& animal.canFallInLove();
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