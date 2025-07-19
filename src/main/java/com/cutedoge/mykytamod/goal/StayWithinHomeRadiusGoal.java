package com.cutedoge.mykytamod.goal; // Or wherever you want to place it

import com.cutedoge.mykytamod.IHasHome;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class StayWithinHomeRadiusGoal extends Goal {
    private final PathAwareEntity mob;
    private final double speed;
    private final int radius;

    public StayWithinHomeRadiusGoal(PathAwareEntity mob, double speed, int radius) {
        this.mob = mob;
        this.speed = speed;
        // We store the squared radius for more efficient distance checking.
        this.radius = radius * radius;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /**
     * This goal can only start if the mob is too far from its home.
     * It no longer tries to find a path here, making it more reliable.
     */
    @Override
    public boolean canStart() {
        if (!(this.mob instanceof IHasHome homeEntity)) {
            return false;
        }
        BlockPos homePos = homeEntity.getHomePosition();
        if (homePos == null) {
            return false;
        }
        // Return true only if the mob's squared distance from home is greater than the radius.
        return this.mob.getBlockPos().getSquaredDistance(homePos) > this.radius;
    }

    /**
     * This goal continues as long as the mob's navigation system is still active.
     */
    @Override
    public boolean shouldContinue() {
        return !this.mob.getNavigation().isIdle();
    }

    /**
     * When the goal starts, we directly command the mob to move to its home position.
     * This is deterministic and removes the previous unreliability.
     */
    @Override
    public void start() {
        if (!(this.mob instanceof IHasHome homeEntity)) {
            return;
        }
        BlockPos homePos = homeEntity.getHomePosition();
        if (homePos != null) {
            // Tell the navigation system to generate a path to the exact home coordinates.
            this.mob.getNavigation().startMovingTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5, this.speed);
        }
    }
}