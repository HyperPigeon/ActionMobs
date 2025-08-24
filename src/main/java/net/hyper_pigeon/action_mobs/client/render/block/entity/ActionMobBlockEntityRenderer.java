package net.hyper_pigeon.action_mobs.client.render.block.entity;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.action_mobs.block.AbstractActionMobBlock;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobModelPartRenderHandler;
import net.hyper_pigeon.action_mobs.packet.C2SUpdateActionBlockMobPart;
import net.hyper_pigeon.action_mobs.packet.UpdateActionBlockMobPart;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.List;
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
            LivingEntityRenderState entityRenderState = (LivingEntityRenderState)entityRenderer.getAndUpdateRenderState(renderEntity, 0);
            EntityModel<LivingEntityRenderState> model = (EntityModel<LivingEntityRenderState>) ((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel();
            Function<String, ModelPart> function = ((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel().getRootPart().createPartGetter();

            List<String> poseablePartNames = StatueTypeDataLoader.statueTypesByEntityType.get(renderEntity.getType()).getPoseablePartNames();

            if(blockEntity.getEdited().containsValue(false)) {
                model.setAngles(entityRenderState);
            }


            for(String partName : poseablePartNames) {
                ModelPart modelPart = function.apply(partName);
                if(blockEntity.isPartEdited(partName)) {
                    Vector3f vector3f = blockEntity.getPartAngle(partName);
                    vector3f = convertToRadiansVector(vector3f);
                    ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setFixedAngles(vector3f);
                }
                else {
                    Vector3f vanillaPose = new Vector3f(modelPart.pitch, modelPart.yaw, modelPart.roll);
                    ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setFixedAngles(vanillaPose);
                    blockEntity.setPartEdited(partName, true);
                    updateActionMobBlockPart(blockEntity, partName, convertToRoundedAnglesVector(vanillaPose));
                }
                ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setIsActionMobModelPart(true);
            }
            entityRenderState.baby = blockEntity.isBaby();
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

    public static Vector3f convertToRoundedAnglesVector(Vector3f vector3f){
        float anglesX = (float) (int) (vector3f.x() * (180F/Math.PI));
        float anglesY = (float) (int) (vector3f.y() * (180F/Math.PI));
        float anglesZ = (float) (int) (vector3f.z() * (180F/Math.PI));

        return new Vector3f(anglesX,anglesY,anglesZ);
    }

    protected void updateActionMobBlockPart(ActionMobBlockEntity blockEntity, String partName, Vector3f vector3f) {
        UpdateActionBlockMobPart updateActionBlockMobPart = new UpdateActionBlockMobPart(vector3f, partName);
        C2SUpdateActionBlockMobPart c2SUpdateActionBlockMobPart = new C2SUpdateActionBlockMobPart(blockEntity.getPos(), updateActionBlockMobPart);
        ClientPlayNetworking.send(c2SUpdateActionBlockMobPart);
    }
}
