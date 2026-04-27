package com.dispenserbreeding.fabric.gametest;

import java.lang.reflect.Method;
import java.util.List;

import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

public final class DispenserBreedingGameTest implements CustomTestMethodInvoker {
	private static final int FLOOR_Y = 0;
	private static final int ENTITY_Y = 1;

	@GameTest(timeoutTicks = 300)
	public void oneCowEntersLoveModeButDoesNotBreed(GameTestHelper helper) {
		Cow cow = helper.spawn(EntityType.COW, 3, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 1);
		triggerDispenser(helper, 3, ENTITY_Y, 1);

		helper.succeedWhen(() -> {
			if (!cow.isInLove()) {
				throw new GameTestAssertException("Expected cow to enter love mode");
			}

			if (countBabyCowsNear(cow) > 0) {
				throw new GameTestAssertException("Expected no baby cow with one cow");
			}
		});
	}

	@GameTest(timeoutTicks = 600)
	public void twoCowsBreedAfterTwoDispenserFeeds(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 2);

		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		helper.succeedWhen(() -> {
			if (countBabyCowsNear(cowA) < 1) {
				throw new GameTestAssertException("Expected a baby cow after two dispenser feeds");
			}
		});
	}

	@GameTest(timeoutTicks = 500)
	public void separatedCowsEnterLoveModeButDoNotBreed(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 6, ENTITY_Y, 3);

		for (int y = 1; y <= 2; y++) {
			helper.setBlock(4, y, 2, Blocks.STONE);
			helper.setBlock(4, y, 3, Blocks.STONE);
			helper.setBlock(4, y, 4, Blocks.STONE);
		}

		placeDispenser(helper, 2, ENTITY_Y, 1, Direction.SOUTH, 1);
		placeDispenser(helper, 6, ENTITY_Y, 1, Direction.SOUTH, 1);

		triggerDispenser(helper, 2, ENTITY_Y, 1);
		triggerDispenser(helper, 6, ENTITY_Y, 1);

		helper.runAfterDelay(120, () -> {
			if (!cowA.isInLove() || !cowB.isInLove()) {
				throw new GameTestAssertException("Expected both cows to enter love mode");
			}

			if (countBabyCowsNear(cowA) > 0) {
				throw new GameTestAssertException("Expected no baby cow while cows are separated");
			}

			helper.succeed();
		});
	}

	private static void placeDispenser(
		GameTestHelper helper,
		int x,
		int y,
		int z,
		Direction facing,
		int wheatCount
	) {
		helper.setBlock(
			x,
			y,
			z,
			Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, facing)
		);

		ServerLevel level = helper.getLevel();
		BlockPos absolutePos = helper.absolutePos(new BlockPos(x, y, z));
		DispenserBlockEntity dispenser = (DispenserBlockEntity) level.getBlockEntity(absolutePos);

		if (dispenser == null) {
			throw new GameTestAssertException("Expected dispenser block entity");
		}

		dispenser.setItem(0, new ItemStack(Items.WHEAT, wheatCount));
	}

	private static void triggerDispenser(GameTestHelper helper, int x, int y, int z) {
		helper.setBlock(x, y, z - 1, Blocks.REDSTONE_BLOCK);
		helper.runAfterDelay(4, () -> helper.setBlock(x, y, z - 1, Blocks.AIR));
	}

	private static long countBabyCowsNear(Cow cow) {
		List<Cow> nearbyCows = cow.level().getEntitiesOfClass(
			Cow.class,
			cow.getBoundingBox().inflate(8.0D),
			Cow::isAlive
		);

		return nearbyCows.stream()
			.filter(Cow::isBaby)
			.count();
	}

	@Override
	public void invokeTestMethod(GameTestHelper helper, Method method) throws ReflectiveOperationException {
		for (int x = 0; x <= 8; x++) {
			for (int z = 0; z <= 6; z++) {
				helper.setBlock(x, FLOOR_Y, z, Blocks.GRASS_BLOCK);
				helper.setBlock(x, FLOOR_Y + 1, z, Blocks.AIR);
				helper.setBlock(x, FLOOR_Y + 2, z, Blocks.AIR);
				helper.setBlock(x, FLOOR_Y + 3, z, Blocks.AIR);
			}
		}

		method.invoke(this, helper);
	}
}