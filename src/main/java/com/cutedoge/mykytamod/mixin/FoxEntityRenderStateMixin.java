package com.cutedoge.mykytamod.mixin;

import com.cutedoge.mykytamod.IsMykytaState;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

// This mixin now implements our interface.
@Mixin(FoxEntityRenderState.class)
public class FoxEntityRenderStateMixin implements IsMykytaState {
    @Unique
    private boolean isMykyta = false;

    // We implement the methods from the interface.
    @Override
    public boolean mykytamod_getIsMykyta() {
        return this.isMykyta;
    }

    @Override
    public void mykytamod_setIsMykyta(boolean isMykyta) {
        this.isMykyta = isMykyta;
    }
}