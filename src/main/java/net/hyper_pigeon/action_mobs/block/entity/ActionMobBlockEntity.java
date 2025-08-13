package net.hyper_pigeon.action_mobs.block.entity;

import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.packet.UpdateActionBlockMobPart;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.statue_type.StatueType;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.*;
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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ActionMobBlockEntity extends BlockEntity {


    protected Entity statueEntity = null;
    protected EntityType<?> entityType = null;
    protected NbtCompound entityData = null;

    protected HashMap<String, Vector3f> partAngles = new HashMap<>();

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

    public HashMap<String, Vector3f> getPartAngles() {
        return partAngles;
    }

    public void setPartAngles(List<String> partNames, ReadView readView) {
        for (String partName : partNames) {
            Optional<Vector3f> anglesOptional = readView.read(partName, Codecs.VECTOR_3F);
            anglesOptional.ifPresent(angles -> partAngles.put(partName, angles));
        }
        markDirty();
    }

    public void initPartAngles(List<String> partNames){
        for(String partName : partNames) {
            Vector3f zeroVector = new Vector3f(0,0,0);
            this.partAngles.put(partName, zeroVector);
        }
        markDirty();
    }

    public Vector3f getPartAngle(String partName) {
        return this.partAngles.get(partName);
    }

    public void setPartAngle(String partName, Vector3f vector3f) {
        this.partAngles.put(partName, vector3f);
        markDirty();
    }

    public float getPartPitch(String partName) {
        return this.partAngles.get(partName).x();
    }

    public void setPartPitch(String partName, float value) {
        Vector3f currentVector = this.partAngles.get(partName);
        Vector3f newVector = new Vector3f(value, currentVector.y, currentVector.z);
        this.partAngles.put(partName, newVector);
        markDirty();
    }

    public float getPartYaw(String partName) {
        return this.partAngles.get(partName).y();
    }

    public void setPartYaw(String partName, float value) {
        Vector3f currentVector = this.partAngles.get(partName);
        Vector3f newVector = new Vector3f(currentVector.x, value, currentVector.z);
        this.partAngles.put(partName, newVector);
        markDirty();
    }

    public float getPartRoll(String partName) {
        return this.partAngles.get(partName).z();
    }

    public void setPartRoll(String partName, float value) {
        Vector3f currentVector = this.partAngles.get(partName);
        Vector3f newVector = new Vector3f(currentVector.x, currentVector.y,value);
        this.partAngles.put(partName, newVector);
        markDirty();
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
            for(String partName : partAngles.keySet()) {
                view.put(partName, Codecs.VECTOR_3F, partAngles.get(partName));
            }

            if (entityData != null && !entityData.isEmpty()) {
                view.put("entity_data", NbtCompound.CODEC, entityData);
            } else {
                NbtWriteView entityWriteView = NbtWriteView.create(logging);
                statueEntity.writeData(entityWriteView);
                if (!((LivingEntity)statueEntity).equipment.isEmpty()) {
                    view.put("equipment", EntityEquipment.CODEC, ((LivingEntity)statueEntity).equipment);
                }

                view.put("entity_data", NbtCompound.CODEC, entityWriteView.getNbt());
            }
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        setEntityType(view.read("type", EntityType.CODEC).orElse(null));
        if (entityType != null) {
            StatueType statueType = StatueTypeDataLoader.statueTypesByEntityType.get(entityType);
            List<String> partNames = statueType.getPoseablePartNames();
            setPartAngles(partNames, view);
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

    public void updatePart(UpdateActionBlockMobPart updateActionBlockMobPart) {
        Vector3f vector3f = updateActionBlockMobPart.getNewAngles();
        String partName = updateActionBlockMobPart.partName();
        setPartAngle(partName, vector3f);
    }
}
