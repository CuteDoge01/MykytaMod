package com.cutedoge.mykytamod.mixin;

import com.cutedoge.mykytamod.BlueFoxNPCEntity;
import com.cutedoge.mykytamod.IsMykytaState; // Import our new interface
import com.cutedoge.mykytamod.MykytaMod;
import net.minecraft.client.render.entity.FoxEntityRenderer;
import net.minecraft.client.render.entity.state.FoxEntityRenderState;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FoxEntityRenderer.class)
public abstract class FoxEntityRendererMixin {

    private static final Identifier MYKYTA_TEXTURE = Identifier.of(MykytaMod.MOD_ID, "textures/entity/foxmykyta.png");

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/passive/FoxEntity;Lnet/minecraft/client/render/entity/state/FoxEntityRenderState;F)V", at = @At("TAIL"))
    private void mykytamod_updateRenderState(FoxEntity foxEntity, FoxEntityRenderState foxEntityRenderState, float f, CallbackInfo ci) {
        // We now cast to our safe interface.
        IsMykytaState mixedState = (IsMykytaState) foxEntityRenderState;
        // And call the setter method from the interface.
        mixedState.mykytamod_setIsMykyta(foxEntity instanceof BlueFoxNPCEntity);
    }

    @Inject(method = "getTexture(Lnet/minecraft/client/render/entity/state/FoxEntityRenderState;)Lnet/minecraft/util/Identifier;", at = @At("HEAD"), cancellable = true)
    private void mykytamod_getTexture(FoxEntityRenderState foxEntityRenderState, CallbackInfoReturnable<Identifier> cir) {
        // We cast to our safe interface here as well.
        IsMykytaState mixedState = (IsMykytaState) foxEntityRenderState;
        // And call the getter method from the interface.
        if (mixedState.mykytamod_getIsMykyta()) {
            cir.setReturnValue(MYKYTA_TEXTURE);
        }
    }
}