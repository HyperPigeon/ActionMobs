package net.hyper_pigeon.action_mobs.block.entity;

import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ActionMobBlockEntity extends BlockEntity {


    protected Entity statueEntity = null;
    protected EntityType<?> entityType = null;
    protected NbtCompound entityData = null;

    public ActionMobBlockEntity(BlockPos pos, BlockState state) {
        super(ActionMobsBlocks.ACTION_MOB_BLOCK_ENTITY, pos, state);
    }

    public static void tick(World world, BlockPos blockPos, BlockState state, ActionMobBlockEntity blockEntity) {
        if (blockEntity.statueEntity == null && blockEntity.entityType != null) {
            blockEntity.setStatueEntity(blockEntity.entityType.create(world, SpawnReason.EVENT));
            if (blockEntity.entityData != null) {
                ErrorReporter.Logging logging = new ErrorReporter.Logging(blockEntity.getReporterContext(), ActionMobs.LOGGER);
                ReadView nbtReadView = NbtReadView.create(logging, world.getRegistryManager(), blockEntity.entityData);
                blockEntity.statueEntity.readData(nbtReadView);
            }
        }
    }

    public void setEntityType(EntityType<?> entityType) {
        this.entityType = entityType;
        this.markDirty();
    }

    public void setEntityData(NbtCompound entityData) {
        this.entityData = entityData;
        this.markDirty();
    }

    public Entity getStatueEntity() {
        return this.statueEntity;
    }

    public void setStatueEntity(Entity statueEntity) {
        this.statueEntity = statueEntity;
        this.entityType = statueEntity == null ? null : statueEntity.getType();
        this.markDirty();
    }

    public void markDirty() {
        super.markDirty();
        if (world != null) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
            if (world instanceof ServerWorld sw) sw.getChunkManager().markForUpdate(pos);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        ErrorReporter.Logging logging = new ErrorReporter.Logging(this.getReporterContext(), ActionMobs.LOGGER);
        if (entityType != null) {
            view.put("type", EntityType.CODEC, entityType);
        }
        if (statueEntity != null) {
            if (entityData != null && !entityData.isEmpty()) {
                view.put("entity_data", NbtCompound.CODEC, entityData);
            } else {
                NbtWriteView entityWriteView = NbtWriteView.create(logging);
                statueEntity.writeData(entityWriteView);
                view.put("entity_data", NbtCompound.CODEC, entityWriteView.getNbt());
            }
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        setEntityType(view.read("type", EntityType.CODEC).orElse(null));
        if (entityType != null) {
            Optional<NbtCompound> entityNbtCompound = view.read("entity_data", NbtCompound.CODEC);
            entityNbtCompound.ifPresent(this::setEntityData);
        }
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
