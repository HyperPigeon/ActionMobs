package net.hyper_pigeon.action_mobs.block;

import com.mojang.serialization.MapCodec;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.client.gui.screen.ingame.ActionMobEditScreen;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
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
        if (!player.getGameMode().equals(GameMode.ADVENTURE)
                && world.isClient
                && world.getBlockEntity(pos) instanceof ActionMobBlockEntity be
                && be.getStatueEntity() != null) {
            LivingEntity statueEntity = (LivingEntity) be.getStatueEntity();
            if(player.isSneaking()) {
                MinecraftClient.getInstance().setScreen(new ActionMobEditScreen(be));
                return ActionResult.CONSUME;
            }
            else if(!stack.isEmpty()) {
                EquipmentSlot equipmentSlot = statueEntity.getPreferredEquipmentSlot(stack);
                if(statueEntity.canEquip(stack,equipmentSlot)) {
                    statueEntity.equipStack(equipmentSlot, stack);
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
                        return ActionResult.CONSUME;
                    }
                }
            }
        }
        return ActionResult.FAIL;
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
