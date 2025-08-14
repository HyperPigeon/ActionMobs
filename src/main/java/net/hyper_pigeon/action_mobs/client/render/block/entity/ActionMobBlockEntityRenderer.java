package net.hyper_pigeon.action_mobs.client.render.block.entity;

import net.hyper_pigeon.action_mobs.block.AbstractActionMobBlock;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobModelPartRenderHandler;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobRenderHandler;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.client.model.ModelPart;
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
import org.joml.Vector3f;

import java.util.function.Function;

public record ActionMobBlockEntityRenderer(BlockEntityRendererFactory.Context context) implements BlockEntityRenderer<ActionMobBlockEntity> {
    @Override
    public void render(ActionMobBlockEntity blockEntity, float tickProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Vec3d cameraPos) {
        if (blockEntity.getWorld() == null || blockEntity.getWorld().getBlockState(blockEntity.getPos()).isAir()) return;

        Direction blockDirection = (Direction)blockEntity.getCachedState().get(AbstractActionMobBlock.FACING);
        float degreeOffset = (blockDirection.equals(Direction.WEST) || blockDirection.equals(Direction.EAST)) ? -180F : 1800F;

        matrices.push();
        matrices.translate(0.5D, 0D, 0.5D);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockDirection.getPositiveHorizontalDegrees() + degreeOffset));

        //rotate by yaw
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(blockEntity.getYaw()));

        //rotate by pitch
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(blockEntity.getPitch()));

        Entity renderEntity = blockEntity.getStatueEntity();
        if (renderEntity != null) {
            //noinspection unchecked
            EntityRenderer<Entity, EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) context.getEntityRenderDispatcher().getRenderer(renderEntity);
//            ((ActionMobRenderHandler)((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel()).setIsActionMobRender(true);
            EntityRenderState entityRenderState = entityRenderer.getAndUpdateRenderState(renderEntity, 0);
            Function<String, ModelPart> function = ((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel().getRootPart().createPartGetter();
            for(String partName : StatueTypeDataLoader.statueTypesByEntityType.get(renderEntity.getType()).getPoseablePartNames()) {
                ModelPart modelPart = function.apply(partName);
                Vector3f vector3f = blockEntity.getPartAngle(partName);
                vector3f = convertToRadiansVector(vector3f);
                ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setIsActionMobModelPart(true);
                ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setFixedAngles(vector3f);
            }
            entityRenderer.render(entityRenderState, matrices, vertexConsumers, light);
//            ((ActionMobRenderHandler)((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel()).setIsActionMobRender(false);
        }

        matrices.pop();

    }

    public static Vector3f convertToRadiansVector(Vector3f vector3f){
        float radiansX = (float) (vector3f.x() * (Math.PI/180F));
        float radiansY = (float) (vector3f.y() * (Math.PI/180F));
        float radiansZ = (float) (vector3f.z() * (Math.PI/180F));

        return new Vector3f(radiansX,radiansY,radiansZ);
    }
}
