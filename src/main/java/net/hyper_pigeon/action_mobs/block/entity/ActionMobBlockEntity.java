package net.hyper_pigeon.action_mobs.block.entity;

import com.mojang.serialization.Codec;
import net.hyper_pigeon.action_mobs.ActionMobs;
import net.hyper_pigeon.action_mobs.packet.UpdateActionBlockMobIsBaby;
import net.hyper_pigeon.action_mobs.packet.UpdateActionBlockMobPart;
import net.hyper_pigeon.action_mobs.packet.UpdateActionMobAngle;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.statue_type.StatueType;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
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

    protected EntityEquipment entityEquipment = null;

    protected HashMap<String, Vector3f> partAngles = new HashMap<>();

    protected float pitch = 0f;
    protected float yaw = 0f;

    protected boolean isBaby = false;

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

            if(blockEntity.entityEquipment != null && !world.isClient) {
                ((LivingEntity)blockEntity.statueEntity).equipment.copyFrom(blockEntity.entityEquipment);
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

    public void setEntityEquipment(EntityEquipment entityEquipment) {
        this.entityEquipment = entityEquipment;
        this.markDirty();
    }

    public EntityEquipment getEntityEquipment(){
        return this.entityEquipment;
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

    public void setPitch(float pitchValue) {
        this.pitch = pitchValue;
        markDirty();
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setYaw(float yawValue) {
        this.yaw = yawValue;
        markDirty();
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setIsBaby(boolean isBaby) {
        this.isBaby = isBaby;
        markDirty();
    }

    public boolean isBaby(){
        return isBaby;
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
            view.put("pitch", Codec.FLOAT, pitch);
            view.put("yaw", Codec.FLOAT, yaw);

            for(String partName : partAngles.keySet()) {
                view.put(partName, Codecs.VECTOR_3F, partAngles.get(partName));
            }

            view.put("is_baby", Codec.BOOL, isBaby);

            if (entityData != null && !entityData.isEmpty()) {
                view.put("entity_data", NbtCompound.CODEC, entityData);
            } else {
                NbtWriteView entityWriteView = NbtWriteView.create(logging);
                statueEntity.writeData(entityWriteView);
                view.put("entity_data", NbtCompound.CODEC, entityWriteView.getNbt());
            }

            if (this.entityEquipment != null && !this.entityEquipment.isEmpty()) {
                view.put("equipment", EntityEquipment.CODEC, this.entityEquipment);
            }
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        setEntityType(view.read("type", EntityType.CODEC).orElse(null));
        if (entityType != null) {
            view.read("pitch", Codec.FLOAT).ifPresentOrElse(this::setPitch, () -> setPitch(0));
            view.read("yaw", Codec.FLOAT).ifPresentOrElse(this::setYaw, () -> setYaw(0));

            StatueType statueType = StatueTypeDataLoader.statueTypesByEntityType.get(entityType);
            List<String> partNames = statueType.getPoseablePartNames();
            setPartAngles(partNames, view);

            Optional<NbtCompound> entityNbtCompound = view.read("entity_data", NbtCompound.CODEC);
            entityNbtCompound.ifPresent(this::setEntityData);

            Optional<EntityEquipment> entityEquipmentOptional = view.read("equipment", EntityEquipment.CODEC);
            entityEquipmentOptional.ifPresent(this::setEntityEquipment);

            view.read("is_baby", Codec.BOOL).ifPresentOrElse(this::setIsBaby, () -> setIsBaby(false));
        }
    }

    public void setCustomDataForItemStack(ItemStack itemStack){
        NbtComponent.set(DataComponentTypes.CUSTOM_DATA, itemStack, nbtCompound -> {
            nbtCompound.putString("entity_type", Registries.ENTITY_TYPE.getEntry(entityType).getKey().get().getValue().toString());
            nbtCompound.putBoolean("is_baby", isBaby());

            if(entityData != null) {
                nbtCompound.put("entity_data",entityData);
            }
            if(entityEquipment != null) {
                nbtCompound.put("equipment",EntityEquipment.CODEC,getEntityEquipment());
            }

            nbtCompound.putFloat("pitch", getPitch());
            nbtCompound.putFloat("yaw", getYaw());

            for(String partName : partAngles.keySet()) {
                Vector3f vector3f = partAngles.get(partName);
                nbtCompound.put(partName, Codecs.VECTOR_3F, vector3f);
            }
        });
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

    public void updateAngle(UpdateActionMobAngle updateActionMobAngle) {
        boolean isPitch = updateActionMobAngle.isPitch();
        float newAngle = updateActionMobAngle.newAngle();
        if(isPitch) {
            setPitch(newAngle);
        }
        else {
            setYaw(newAngle);
        }
    }

    public void updateIsBaby(UpdateActionBlockMobIsBaby updateActionBlockMobIsBaby) {
        boolean isBaby = updateActionBlockMobIsBaby.isBaby();
        setIsBaby(isBaby);
    }
}
