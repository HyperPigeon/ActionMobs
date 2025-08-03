package net.hyper_pigeon.action_mobs.mixin;

import net.hyper_pigeon.action_mobs.client.render.item.ItemHandRenderer;
import net.hyper_pigeon.action_mobs.register.ActionMobsBlocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(value = "INVOKE",
                    target = "net/minecraft/client/render/item/ItemRenderer.renderItem (Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V",
                    ordinal = 0),
            cancellable = true)
    public void renderActionMobItem(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo callbackInfo){
        if(stack.getItem().equals(ActionMobsBlocks.ACTION_MOB_BLOCK.asItem())){
            ItemHandRenderer renderer = ItemHandRenderer.getRenderer(stack);
            renderer.render(matrices, vertexConsumer, light, stack);
            callbackInfo.cancel();
        }
    }
}
