package com.dispenserbreeding.fabric.gametest;

import java.lang.reflect.Method;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;

public final class DispenserBreedingGameTest implements CustomTestMethodInvoker {
	private static final int FLOOR_Y = 0;
	private static final int ENTITY_Y = 1;

	@GameTest(maxTicks = 300)
	public void oneCowEntersLoveModeButDoesNotBreed(GameTestHelper helper) {
		Cow cow = helper.spawn(EntityType.COW, 3, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 1);
		triggerDispenser(helper, 3, ENTITY_Y, 1);

		helper.succeedWhen(() -> {
			helper.assertTrue(cow.isInLove(), "Expected cow to enter love mode");
			helper.assertFalse(countBabyCowsNear(cow) > 0, "Expected no baby cow with one cow");
		});
	}

	@GameTest(maxTicks = 600)
	public void twoCowsBreedAfterTwoDispenserFeeds(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 2);

		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		helper.succeedWhen(() -> {
			helper.assertTrue(countBabyCowsNear(cowA) >= 1, "Expected a baby cow after two dispenser feeds");
		});
	}

	@GameTest(maxTicks = 500)
	public void separatedCowsEnterLoveModeButDoNotBreed(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 6, ENTITY_Y, 2);

		for (int y = 1; y <= 2; y++) {
			helper.setBlock(4, y, 2, Blocks.STONE);
			helper.setBlock(4, y, 3, Blocks.STONE);
			helper.setBlock(4, y, 4, Blocks.STONE);
		}

		placeDispenser(helper, 2, ENTITY_Y, 1, Direction.SOUTH, 1);
		placeDispenser(helper, 6, ENTITY_Y, 1, Direction.SOUTH, 1);

		triggerDispenser(helper, 2, ENTITY_Y, 1);
		triggerDispenser(helper, 6, ENTITY_Y, 1);

		helper.runAfterDelay(40, () -> {
			helper.assertTrue(cowA.isInLove() && cowB.isInLove(), "Expected both cows to enter love mode");
			helper.assertFalse(countBabyCowsNear(cowA) > 0, "Expected no baby cow while cows are separated");

			helper.succeed();
		});
	}

	@GameTest(maxTicks = 200)
	public void cowsBreedWithoutPathingIfClose(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 3, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 3, ENTITY_Y, 4);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 2);

		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		helper.succeedWhen(() -> {
			helper.assertTrue(countBabyCowsNear(cowA) >= 1, "Expected instant breeding when cows are adjacent");
		});
	}

	@GameTest(maxTicks = 400)
	public void cowsDoNotReenterLoveDuringCooldown(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 4);

		// First successful breed
		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		helper.runAfterDelay(100, () -> {
			// Try to breed again while cooldown is active
			triggerDispenser(helper, 3, ENTITY_Y, 1);
			helper.runAfterDelay(4, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

			helper.runAfterDelay(20, () -> {
				helper.assertFalse(
					cowA.isInLove() || cowB.isInLove(),
					"Cows should NOT re-enter love mode during cooldown"
				);

				helper.succeed();
			});
		});
	}

	@GameTest(maxTicks = 7500)
	public void cowsCanBreedAgainAfterCooldown(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 4);

		// First breeding
		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		long[] cowCountAfterFirst = {0};

		// Allow enough time for the baby to spawn and be counted
		helper.runAfterDelay(200, () -> {
			cowCountAfterFirst[0] = countCowsNear(cowA);

			// Vanilla breeding cooldown is ~6000 ticks
			helper.runAfterDelay(6000, () -> {
				triggerDispenser(helper, 3, ENTITY_Y, 1);
				helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

				helper.succeedWhen(() -> helper.assertTrue(
					countCowsNear(cowA) > cowCountAfterFirst[0],
					"Cows should breed again after vanilla cooldown"
				));
			});
		});
	}

	@GameTest(maxTicks = 600)
	public void dispenserSpamDoesNotBreakBreeding(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 10);

		for (int i = 0; i < 10; i++) {
			int delay = i * 5;
			helper.runAfterDelay(delay, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));
		}

		helper.succeedWhen(() -> helper.assertTrue(
			countBabyCowsNear(cowA) >= 1,
			"Dispenser spam should not prevent breeding"
		));
	}

	@GameTest(maxTicks = 600)
	public void dispenserFeedsTwoDistinctCows(GameTestHelper helper) {
		Cow cowA = helper.spawn(EntityType.COW, 2, ENTITY_Y, 3);
		Cow cowB = helper.spawn(EntityType.COW, 4, ENTITY_Y, 3);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 2);

		triggerDispenser(helper, 3, ENTITY_Y, 1);

		// After the first feed exactly one cow should be in love
		helper.runAfterDelay(10, () -> {
			helper.assertTrue(
				cowA.isInLove() ^ cowB.isInLove(),
				"Expected exactly one cow to be in love after first feed"
			);

			triggerDispenser(helper, 3, ENTITY_Y, 1);
		});

		// After the second feed both should be in love, or a baby already spawned
		helper.runAfterDelay(40, () -> {
			boolean bothFedOrBabyExists =
				(cowA.isInLove() && cowB.isInLove()) || countBabyCowsNear(cowA) >= 1;

			helper.assertTrue(
				bothFedOrBabyExists,
				"Expected both cows to be fed or a baby to exist after two feeds"
			);

			helper.succeed();
		});
	}

	@GameTest(maxTicks = 600)
	public void cowsBreedInNarrowOneByThreeCorridor(GameTestHelper helper) {
		// Walls around a 1x3 lane at x=3, z=2..4
		for (int z = 2; z <= 4; z++) {
			helper.setBlock(2, ENTITY_Y, z, Blocks.STONE);
			helper.setBlock(4, ENTITY_Y, z, Blocks.STONE);
		}

		Cow cowA = helper.spawn(EntityType.COW, 3, ENTITY_Y, 2);
		Cow cowB = helper.spawn(EntityType.COW, 3, ENTITY_Y, 4);

		placeDispenser(helper, 3, ENTITY_Y, 1, Direction.SOUTH, 2);

		triggerDispenser(helper, 3, ENTITY_Y, 1);
		helper.runAfterDelay(20, () -> triggerDispenser(helper, 3, ENTITY_Y, 1));

		helper.succeedWhen(() -> {
			helper.assertTrue(
				countBabyCowsNear(cowA) >= 1,
				"Cows should breed in a narrow 1x3 corridor"
			);
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
			throw helper.assertionException("Expected dispenser block entity");
		}

		dispenser.setItem(0, new ItemStack(Items.WHEAT, wheatCount));
	}

	private static void triggerDispenser(GameTestHelper helper, int x, int y, int z) {
		helper.setBlock(x, y, z - 1, Blocks.REDSTONE_BLOCK);
		helper.runAfterDelay(4, () -> helper.setBlock(x, y, z - 1, Blocks.AIR));
	}

	private static long countBabyCowsNear(Cow cow) {
		return cow.level().getEntitiesOfClass(
			Cow.class,
			cow.getBoundingBox().inflate(8.0D),
			Cow::isAlive
		).stream()
			.filter(Cow::isBaby)
			.count();
	}

	private static long countCowsNear(Cow cow) {
		return cow.level().getEntitiesOfClass(
			Cow.class,
			cow.getBoundingBox().inflate(8.0D),
			Cow::isAlive
		).size();
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