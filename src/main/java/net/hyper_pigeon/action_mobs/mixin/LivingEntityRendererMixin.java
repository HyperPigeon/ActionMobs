package net.hyper_pigeon.action_mobs.mixin;


import net.hyper_pigeon.action_mobs.duck_type.ActionMobModel;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobRenderState;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Redirect(
            method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE", target = "net/minecraft/client/render/entity/model/EntityModel.setAngles (Lnet/minecraft/client/render/entity/state/EntityRenderState;)V")
    )
    private void redirectSetAngles(EntityModel<EntityRenderState> instance, EntityRenderState state) {
        if(((ActionMobRenderState)(state)).isActionMob()) {
            ActionMobModel actionMobModel = (ActionMobModel) instance;
            ActionMobRenderState actionMobRenderState = (ActionMobRenderState) state;
            actionMobModel.setAngles(actionMobRenderState);
        }
        else {
            instance.setAngles(state);
        }
    }


}
