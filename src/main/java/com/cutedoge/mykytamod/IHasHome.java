package com.cutedoge.mykytamod; // Or your preferred package

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * An interface for any entity that should have a persistent "home" position.
 */
public interface IHasHome {
    @Nullable
    BlockPos getHomePosition();

    void setHomePosition(BlockPos pos);
}