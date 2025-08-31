package net.hyper_pigeon.action_mobs.mixin;

import net.minecraft.client.render.entity.AgeableMobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AgeableMobEntityRenderer.class)
public interface AgeableMobEntityRendererAccessor<T extends MobEntity,
        S extends LivingEntityRenderState,
        M extends EntityModel<? super S>> {
    @Accessor
    M getAdultModel();

    @Accessor
    M getBabyModel();

}
