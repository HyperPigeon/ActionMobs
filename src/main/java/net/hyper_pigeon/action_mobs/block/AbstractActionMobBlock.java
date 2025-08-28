package net.hyper_pigeon.action_mobs.block;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.packet.S2CUpdateActionMobEquipment;
import net.hyper_pigeon.action_mobs.packet.UpdateActionMobEquipment;
import net.hyper_pigeon.action_mobs.statue_type.StatueType;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.block.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.Optional;

public abstract class AbstractActionMobBlock extends BlockWithEntity implements Waterloggable {

    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;
    protected AbstractActionMobBlock(Settings settings) {
        super(settings);
    }

    protected VoxelShape targetedOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.fullCube();
    }


    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context != ShapeContext.absent() && context instanceof EntityShapeContext esc && esc.getEntity() instanceof PlayerEntity player) {
            return targetedOutlineShape(state, world, pos, context);
        } else {
            return VoxelShapes.empty();
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (!world.isClient() && placer != null && world.getBlockEntity(pos) instanceof ActionMobBlockEntity be) {
            var data = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            if(data != null) {
                NbtCompound value = data.copyNbt();
                String type = value.getString("entity_type", "minecraft:zombie");
                String[] splitType = type.split(":");
                Identifier identifier = Identifier.of(splitType[0], splitType[1]);
                EntityType<?> entityType = Registries.ENTITY_TYPE.get(identifier);

                StatueType statueType = StatueTypeDataLoader.statueTypesByEntityType.get(entityType);

                Entity entity = entityType.create(world, SpawnReason.EVENT);
                be.setStatueEntity(entity);

                boolean canBeBaby = statueType.canBeBaby();
                be.setCanBeBaby(canBeBaby);

                boolean isBaby =  value.getBoolean("is_baby", false);
                be.setIsBaby(isBaby);

                Optional<NbtCompound> entityNbtCompound = value.get("entity_data", NbtCompound.CODEC);
                entityNbtCompound.ifPresent(be::setEntityData);

                Optional<EntityEquipment> optionalEntityEquipment =  value.get("equipment", EntityEquipment.CODEC);
                optionalEntityEquipment.ifPresent(entityEquipment -> {
                    be.setEntityEquipment(entityEquipment);
                    for(EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        ItemStack equipmentStack =  entityEquipment.get(equipmentSlot);
                        ((LivingEntity)entity).equipStack(equipmentSlot, equipmentStack);
                        UpdateActionMobEquipment updateActionMobEquipment = new UpdateActionMobEquipment(equipmentSlot, equipmentStack);
                        for (ServerPlayerEntity serverPlayer : PlayerLookup.world((ServerWorld) world)) {
                            ServerPlayNetworking.send(serverPlayer, new S2CUpdateActionMobEquipment(pos, updateActionMobEquipment));
                        }
                    }
                });

                float pitch = value.getFloat("pitch", 0);
                be.setPitch(pitch);

                float yaw = value.getFloat("yaw", 0);
                be.setYaw(yaw);

                List<String> partNames = statueType.getPoseablePartNames();

                be.initPartAngles(partNames);

                for (String partName : partNames) {
                    Optional<Vector3f> anglesOptional = value.get(partName, Codecs.VECTOR_3F);
                    anglesOptional.ifPresent(angles -> be.setPartAngle(partName, angles));
                    anglesOptional.ifPresent(angles -> be.setPartEdited(partName, true));
                }


            }
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }


}
