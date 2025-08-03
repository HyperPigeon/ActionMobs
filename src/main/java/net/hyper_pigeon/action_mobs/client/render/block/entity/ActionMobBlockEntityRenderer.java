package net.hyper_pigeon.action_mobs.client.render.block.entity;

import net.hyper_pigeon.action_mobs.block.AbstractActionMobBlock;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public record ActionMobBlockEntityRenderer(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<ActionMobBlockEntity> {
    @Override
    public void render(ActionMobBlockEntity blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        if (blockEntity.getWorld() == null || blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir()) return;

        Direction blockDirection = (Direction)blockEntity.getCachedState().get(AbstractActionMobBlock.FACING);
        float degreeOffset = (blockDirection.equals(Direction.WEST) || blockDirection.equals(Direction.EAST)) ? -180F : 1800F;

        matrices.push();
        matrices.translate(0.5D, 0D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockDirection.getPositiveHorizontalDegrees() + degreeOffset));
        Entity renderEntity = blockEntity.getStatueEntity();
        if (renderEntity != null) {
            //noinspection unchecked
            EntityRenderer<Entity, EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) context.getEntityRenderDispatcher().getRenderer(renderEntity);
            EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(renderEntity, 0);
//            ((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel().getRootPart().
            entityRenderer.render(entityRenderState, matrices, vertexConsumers, light);
        }

        matrices.pop();

    }
}
