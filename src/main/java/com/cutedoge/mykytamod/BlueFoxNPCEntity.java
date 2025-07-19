package com.cutedoge.mykytamod;

import com.cutedoge.mykytamod.goal.StayWithinHomeRadiusGoal;
import com.cutedoge.mykytamod.goal.TeleportHomeIfTooFarGoal;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.player.PlayerEntity;


import java.util.Collection; // Import Collection

public class BlueFoxNPCEntity extends FoxEntity implements IHasHome {

    private static final Identifier GOT_BOAT_ADVANCEMENT_ID = Identifier.of(MykytaMod.MOD_ID, "got_boat");

    public BlueFoxNPCEntity(EntityType<? extends FoxEntity> entityType, World world) {
        super(entityType, world);
    }

    @Nullable
    private BlockPos homePosition = null;

// ... inside your BlueFoxNPCEntity class ...

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        // Your original code remains untouched below.
        if (!this.getWorld().isClient()) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
            ServerWorld serverWorld = (ServerWorld) this.getWorld();

            AdvancementEntry advancementEntry = serverWorld.getServer().getAdvancementLoader().get(GOT_BOAT_ADVANCEMENT_ID);

            if (advancementEntry != null) {
                AdvancementProgress progress = serverPlayer.getAdvancementTracker().getProgress(advancementEntry);

                if (progress.isDone()) {
                    Text message = Text.of("<Микита> Іп-яп! Веди себе добре та будь розумничком, пухнастику.");
                    player.sendMessage(message, false);
                } else {
                    Text message = Text.of("<Микита> Здається ти вирушаєш в неймовірну подорож! На, тримай ось це, сподіваюсь це тобі стане в нагоді.");
                    player.sendMessage(message, false);

                    ItemStack boatStack = new ItemStack(Items.OAK_BOAT);
                    player.giveItemStack(boatStack);

                    serverPlayer.getAdvancementTracker().grantCriterion(advancementEntry, "impossible");
                }
            } else {
                MykytaMod.LOGGER.warn("Could not find advancement: " + GOT_BOAT_ADVANCEMENT_ID);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.clear(goal -> true);
        this.targetSelector.clear(goal -> true);


        // Priority 0: Critical movement
        this.goalSelector.add(0, new TeleportHomeIfTooFarGoal(this, 64));
        this.goalSelector.add(2, new SwimGoal(this));

        // Priority 1: The "leash," always a high priority.
        this.goalSelector.add(1, new StayWithinHomeRadiusGoal(this, 1.2D, 24));

        // Priority 2: Wandering is now a HIGH priority. He wants to move!
        // We also reduced the 'chance' from 120 to 40. This means the goal will try to
        // start much more often (every 2 seconds on average, instead of 6).
        this.goalSelector.add(3, new WanderAroundGoal(this, 1.0D, 40, false));

        // Priority 3: Eating berries is still a nice-to-have action.
        this.goalSelector.add(5, this.new EatBerriesGoal(1.2D, 20, 1));

        // Priority 4: Looking at the player is a key part of "standing and looking."
        this.goalSelector.add(4, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));

        // Priority 5: Sitting is now a LOW priority behavior.
        // He will only consider sitting if he isn't already walking, eating, or looking at a player.
        this.goalSelector.add(5, this.new SitDownAndLookAroundGoal());

        // Priority 6: The most basic idle action.
        this.goalSelector.add(7, new LookAroundGoal(this));

    }

    @Nullable
    @Override
    public FoxEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }

    @Nullable
    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        EntityData data = super.initialize(world, difficulty, spawnReason, entityData);
        MykytaMod.LOGGER.info("[INIT] Initialize called. Current home is: " + this.getHomePosition());
        if (this.getHomePosition() == null) {
            this.setHomePosition(this.getBlockPos());
            MykytaMod.LOGGER.info("[INIT] Home was null, setting new home at: " + this.getHomePosition());
        }
        this.getNavigation().setCanSwim(true);
        return data;
    }

    @Nullable
    public BlockPos getHomePosition() {
        return this.homePosition;
    }

    @Override
    public void setHomePosition(BlockPos pos) {
        this.homePosition = pos;
    }

// In BlueFoxNPCEntity.java

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);

        BlockPos homePos = this.getHomePosition();
        if (homePos != null) {
            NbtCompound homePosCompound = new NbtCompound();
            homePosCompound.putInt("X", homePos.getX());
            homePosCompound.putInt("Y", homePos.getY());
            homePosCompound.putInt("Z", homePos.getZ());
            nbt.put("MykytaHomePos", homePosCompound);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("MykytaHomePos", 10)) {
            NbtCompound homePosCompound = nbt.getCompound("MykytaHomePos");
            int x = homePosCompound.getInt("X");
            int y = homePosCompound.getInt("Y");
            int z = homePosCompound.getInt("Z");
            this.setHomePosition(new BlockPos(x, y, z));
        }
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        // It is VERY important to call super.tick() first!
        // This lets the parent FoxEntity do all of its normal processing.
        super.tick();

        // --- VEHICLE PREVENTION ---
        // Check if Mykyta is currently riding in any vehicle (boat, minecart, etc.).
        if (this.hasVehicle()) {
            // If he is, force him to dismount immediately.
            this.stopRiding();
        }
    }


}








