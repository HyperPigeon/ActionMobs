package net.hyper_pigeon.action_mobs.mixin;

import net.hyper_pigeon.action_mobs.duck_type.ActionMobRenderHandler;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMobEntityRenderer.class)
public abstract class AgeableMobEntityRendererMixin <T extends MobEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> extends MobEntityRenderer<T, S, M> implements ActionMobRenderHandler {
    private boolean isActionMobStatue;

    public AgeableMobEntityRendererMixin(EntityRendererFactory.Context context, M entityModel, float f) {
        super(context, entityModel, f);
    }

    public void setActionMobStatue(boolean value){
        isActionMobStatue = value;
    }

    public boolean isActionMobStatue(){
        return isActionMobStatue;
    }


    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"), cancellable = true)
    private void doNotAssignNewModelIfActionMob(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        if(isActionMobStatue()) {
            super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
            ci.cancel();
        }
    }
}
