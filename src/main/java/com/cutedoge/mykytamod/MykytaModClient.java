package com.cutedoge.mykytamod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FoxEntityRenderer; // Import the vanilla renderer

@Environment(EnvType.CLIENT)
public class MykytaModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // We register the VANILLA renderer for our blue fox NPC.
        // The mixin we are about to create will handle changing the texture.
        EntityRendererRegistry.register(MykytaMod.BLUE_FOX_NPC, FoxEntityRenderer::new);
    }
}

