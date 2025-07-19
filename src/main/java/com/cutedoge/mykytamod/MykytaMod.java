package com.cutedoge.mykytamod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text; // <-- NEW IMPORT
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.minecraft.entity.passive.FoxEntity.createFoxAttributes;

public class MykytaMod implements ModInitializer {
    public static final String MOD_ID = "mykytamod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static EntityType<BlueFoxNPCEntity> BLUE_FOX_NPC;
    public static Item BLUE_FOX_NPC_SPAWN_EGG;

    @Override
    public void onInitialize() {
        // This is the correct, final implementation.

        LOGGER.info("Initializing MykytaMod!");

        RegistryKey<EntityType<?>> entityKey = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(MOD_ID, "blue_fox_npc"));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, "blue_fox_npc_spawn_egg"));

        BLUE_FOX_NPC = Registry.register(
                Registries.ENTITY_TYPE,
                entityKey,
                EntityType.Builder.create(BlueFoxNPCEntity::new, SpawnGroup.CREATURE)
                        .dimensions(0.6f, 0.7f)
                        .build(entityKey)
        );

        FabricDefaultAttributeRegistry.register(BLUE_FOX_NPC, createFoxAttributes());

        Item.Settings spawnEggSettings = new Item.Settings().registryKey(itemKey);

        BLUE_FOX_NPC_SPAWN_EGG = Registry.register(
                Registries.ITEM,
                itemKey,
                new SpawnEggItem(BLUE_FOX_NPC, spawnEggSettings)
        );

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(content -> {
            content.add(BLUE_FOX_NPC_SPAWN_EGG);
        });

    }
}
