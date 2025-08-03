package net.hyper_pigeon.action_mobs.block;

import com.mojang.serialization.MapCodec;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
}
