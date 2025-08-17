package net.hyper_pigeon.action_mobs.block;

import com.mojang.serialization.MapCodec;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.client.gui.screen.ingame.ActionMobEditScreen;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Consumer;

public class ActionMobBlock extends AbstractActionMobBlock{
    public static final MapCodec<ActionMobBlock> CODEC = createCodec(ActionMobBlock::new);
    public ActionMobBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ActionMobBlockEntity(pos,state);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type,ActionMobsBlocks.ACTION_MOB_BLOCK_ENTITY, ActionMobBlockEntity::tick);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient) {
            return onUseWithItemClient(stack, state, world, pos, player, hand, hit);
        }
        else {
            return onUseWithItemServer(stack, state, world, pos, player, hand, hit);
        }
    }

    protected ActionResult onUseWithItemClient(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getGameMode().equals(GameMode.ADVENTURE)
                && world.getBlockEntity(pos) instanceof ActionMobBlockEntity be
                && be.getStatueEntity() != null) {
            LivingEntity statueEntity = (LivingEntity) be.getStatueEntity();
            if(player.isSneaking()) {
                MinecraftClient.getInstance().setScreen(new ActionMobEditScreen(be));
                return ActionResult.CONSUME;
            }
            else if(!stack.isEmpty() && StatueTypeDataLoader.statueTypesByEntityType.get(statueEntity.getType()).canEquip()) {
                EquipmentSlot equipmentSlot = statueEntity.getPreferredEquipmentSlot(stack);
                if(statueEntity.canEquip(stack,equipmentSlot) && statueEntity.getEquippedStack(equipmentSlot).isEmpty()) {
                    ItemStack splitStack = stack.split(1);
                    statueEntity.equipStack(equipmentSlot, splitStack);
//                    player.setStackInHand(hand, ItemStack.EMPTY);
                    be.setEntityEquipment(statueEntity.equipment);
                    return ActionResult.CONSUME;
                }
            }
            else {
                EnumMap<EquipmentSlot, ItemStack> map = statueEntity.equipment.map;
                for(EquipmentSlot equipmentSlot : map.keySet()) {
                    if(!map.get(equipmentSlot).isEmpty()) {
                        ItemStack itemStack = statueEntity.getEquippedStack(equipmentSlot);
                        statueEntity.equipStack(equipmentSlot, ItemStack.EMPTY);
                        player.setStackInHand(hand, itemStack);
                        be.setEntityEquipment(statueEntity.equipment);
                        return ActionResult.CONSUME;
                    }
                }
            }

        }
        return ActionResult.FAIL;
    }

    protected ActionResult onUseWithItemServer(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getGameMode().equals(GameMode.ADVENTURE)
                && world.getBlockEntity(pos) instanceof ActionMobBlockEntity be
                && be.getStatueEntity() != null) {
            LivingEntity statueEntity = (LivingEntity) be.getStatueEntity();
            if(!player.isSneaking()) {
                if(!stack.isEmpty() && StatueTypeDataLoader.statueTypesByEntityType.get(statueEntity.getType()).canEquip()) {
                    EquipmentSlot equipmentSlot = statueEntity.getPreferredEquipmentSlot(stack);
                    if(statueEntity.canEquip(stack,equipmentSlot) && statueEntity.getEquippedStack(equipmentSlot).isEmpty()) {
                        ItemStack splitStack = stack.split(1);
                        statueEntity.equipStack(equipmentSlot, splitStack);
//                    player.setStackInHand(hand, ItemStack.EMPTY);
                        be.setEntityEquipment(statueEntity.equipment);
                        return ActionResult.CONSUME;
                    }
                }
                else {
                    EnumMap<EquipmentSlot, ItemStack> map = statueEntity.equipment.map;
                    for(EquipmentSlot equipmentSlot : map.keySet()) {
                        if(!map.get(equipmentSlot).isEmpty()) {
                            ItemStack itemStack = statueEntity.getEquippedStack(equipmentSlot);
                            statueEntity.equipStack(equipmentSlot, ItemStack.EMPTY);
                            player.setStackInHand(hand, itemStack);
                            be.setEntityEquipment(statueEntity.equipment);
                            return ActionResult.CONSUME;
                        }
                    }
                }
            }
        }
        return ActionResult.FAIL;
    }

//    @Override
//    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
//        if (world instanceof ServerWorld serverWorld
//                && !player.shouldSkipBlockDrops()
//                && serverWorld.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
//                && world.getBlockEntity(pos) instanceof ActionMobBlockEntity actionMobBlockEntity) {
//            ItemStack itemStack = new ItemStack(this);
//            actionMobBlockEntity.setCustomDataForItemStack(itemStack);
//            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
//            itemEntity.setToDefaultPickupDelay();
//            world.spawnEntity(itemEntity);
//        }
//
//        return super.onBreak(world, pos, state, player);
//    }

    @Override
    protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
        BlockEntity blockEntity = builder.getOptional(LootContextParameters.BLOCK_ENTITY);
        if(blockEntity instanceof ActionMobBlockEntity actionMobBlockEntity) {
            ItemStack itemStack = new ItemStack(this);
            actionMobBlockEntity.setCustomDataForItemStack(itemStack);
            return Collections.singletonList(itemStack);
        }
        return super.getDroppedStacks(state, builder);
    }

    protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
        ItemStack itemStack =  super.getPickStack(world, pos, state, true);
        if(world.getBlockEntity(pos) instanceof ActionMobBlockEntity actionMobBlockEntity) {
            actionMobBlockEntity.setCustomDataForItemStack(itemStack);
        }
        return itemStack;
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        var data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if(data != null) {
            NbtCompound value = data.copyNbt();
            String entityType = value.getString("entity_type", "minecraft:zombie");
            String[] splitType = entityType.split(":");
            String entityName = splitType[1].replace("_", " ");
            textConsumer.accept(Text.translatable(entityName.substring(0,1).toUpperCase() + entityName.substring(1)));
        }
        textConsumer.accept(Text.translatable("block.action_mobs.action_mob.tooltip.0").formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("block.action_mobs.action_mob.tooltip.1").formatted(Formatting.GRAY));
    }
}
