package com.cutedoge.mykytamod.goal;

import com.cutedoge.mykytamod.IHasHome;
import com.cutedoge.mykytamod.MykytaMod;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class TeleportHomeIfTooFarGoal extends Goal {
    private final PathAwareEntity mob;
    private final int teleportRadius;

    public TeleportHomeIfTooFarGoal(PathAwareEntity mob, int teleportRadius) {
        this.mob = mob;
        // We use the square of the radius for more efficient distance checks.
        this.teleportRadius = teleportRadius * teleportRadius;
        // This goal causes movement, so we should set the control flag.
        this.setControls(EnumSet.of(Control.MOVE));
    }

    /**
     * This goal can only start if the mob is very far from home.
     */
    @Override
    public boolean canStart() {
        if (!(this.mob instanceof IHasHome homeEntity)) {
            return false;
        }

        BlockPos homePos = homeEntity.getHomePosition();
        if (homePos == null) {
            return false; // No home set.
        }

        // Only run this check if the home chunk is loaded and the entity isn't being ridden.
        if (this.mob.getWorld().isChunkLoaded(homePos) && !this.mob.hasVehicle()) {
            return this.mob.getBlockPos().getSquaredDistance(homePos) > this.teleportRadius;
        }

        return false;
    }

    /**
     * This goal is instantaneous, it should not continue.
     */
    @Override
    public boolean shouldContinue() {
        return false;
    }

    /**
     * When the goal starts, perform the teleport.
     */
    @Override
    public void start() {
        if (!(this.mob instanceof IHasHome homeEntity)) {
            return;
        }
        BlockPos homePos = homeEntity.getHomePosition();
        if (homePos != null) {
            MykytaMod.LOGGER.info("Mykyta is too far! Teleporting from " + this.mob.getBlockPos() + " back home to " + homePos);

            // Teleport the entity to the center of its home block.
            this.mob.teleport(homePos.getX() + 0.5, homePos.getY()+0.1, homePos.getZ() + 0.5, true);

            // Play a sound effect to make the teleport feel intentional.
            this.mob.playSound(SoundEvents.ENTITY_FOX_TELEPORT, 1.0F, 1.0F);
        }
    }
}