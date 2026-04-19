package com.dispenserbreeding.breeding;

import com.dispenserbreeding.config.ConfigManager;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class GroundBreedingItemGoal extends Goal {
    private final Animal animal;
    private ItemEntity targetItem;
    private int nextScanTick;

    public GroundBreedingItemGoal(Animal animal) {
        this.animal = animal;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!isFeatureEnabled() || !canAnimalBreedFromGround()) {
            return false;
        }

        if (nextScanTick > 0) {
            nextScanTick--;
            return false;
        }

        nextScanTick = scanIntervalTicks();
        targetItem = findNearestValidItem();

        return targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        return isFeatureEnabled()
            && canAnimalBreedFromGround()
            && isValidTargetItem(targetItem);
    }

    @Override
    public void start() {
        moveTowardsTarget();
    }

    @Override
    public void stop() {
        targetItem = null;
        animal.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if (targetItem == null) {
            return;
        }

        if (!isValidTargetItem(targetItem)) {
            stop();
            return;
        }

        animal.getLookControl().setLookAt(targetItem, 30.0F, animal.getMaxHeadXRot());

        if (animal.distanceToSqr(targetItem) <= consumeRadiusSquared()) {
            consumeTargetItem();
            stop();
            return;
        }

        if (animal.getNavigation().isDone()) {
            moveTowardsTarget();
        }
    }

    private void moveTowardsTarget() {
        if (targetItem == null) {
            return;
        }

        animal.getNavigation().moveTo(
            targetItem.getX(),
            targetItem.getY(),
            targetItem.getZ(),
            moveSpeed()
        );
    }

    private void consumeTargetItem() {
        if (targetItem == null) {
            return;
        }

        ItemStack stack = targetItem.getItem();
        if (stack.isEmpty() || !animal.isFood(stack) || !animal.canFallInLove() || animal.isInLove()) {
            return;
        }

        stack.shrink(1);

        if (stack.isEmpty()) {
            targetItem.discard();
        } else {
            targetItem.setItem(stack);
        }

        animal.setInLove(null);
    }

    private ItemEntity findNearestValidItem() {
        double radius = searchRadius();

        AABB searchBox = animal.getBoundingBox().inflate(radius, radius * 0.5D, radius);

        List<ItemEntity> candidates = animal.level().getEntitiesOfClass(
            ItemEntity.class,
            searchBox,
            this::isValidTargetItem
        );

        Vec3 animalPos = animal.position();

        return candidates.stream()
            .min(Comparator.comparingDouble(item -> item.position().distanceToSqr(animalPos)))
            .orElse(null);
    }

    private boolean isValidTargetItem(ItemEntity itemEntity) {
        if (itemEntity == null || !itemEntity.isAlive()) {
            return false;
        }

        ItemStack stack = itemEntity.getItem();
        return !stack.isEmpty() && animal.isFood(stack);
    }

    private boolean canAnimalBreedFromGround() {
        return animal.isAlive()
            && !animal.isBaby()
            && !animal.isInLove()
            && animal.canFallInLove();
    }

    private boolean isFeatureEnabled() {
        return ConfigManager.get().enableGroundPickupBreeding;
    }

    private double searchRadius() {
        return ConfigManager.get().groundPickupSearchRadius;
    }

    private double moveSpeed() {
        return ConfigManager.get().groundPickupMoveSpeed;
    }

    private double consumeRadiusSquared() {
        double radius = ConfigManager.get().groundPickupConsumeRadius;
        return radius * radius;
    }

    private int scanIntervalTicks() {
        return Math.max(1, ConfigManager.get().groundPickupScanIntervalTicks);
    }
}
