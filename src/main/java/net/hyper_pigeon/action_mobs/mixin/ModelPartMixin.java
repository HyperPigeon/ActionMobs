package net.hyper_pigeon.action_mobs.mixin;

import net.hyper_pigeon.action_mobs.duck_type.ActionMobModelPartRenderHandler;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelPart.class)
public class ModelPartMixin implements ActionMobModelPartRenderHandler {

    @Shadow public float pitch;
    @Shadow public float yaw;
    @Shadow public float roll;
    private float fixedPitch;
    private float fixedYaw;
    private float fixedRoll;

    private boolean isActionMobModelPart;

    public final Vector3f zero = new Vector3f(0,0,0);


    @Override
    public void setIsActionMobModelPart(boolean value) {
        this.isActionMobModelPart = value;
    }

    @Override
    public boolean getIsActionMobModelPart() {
        return isActionMobModelPart;
    }

    @Override
    public void setFixedAngles(Vector3f vector3f) {
        this.fixedPitch = vector3f.x();
        this.fixedYaw = vector3f.y();
        this.fixedRoll = vector3f.z();
    }

    @Override
    public Vector3f getFixedAngles() {
        return new Vector3f(fixedPitch, fixedYaw, fixedRoll);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V", at = @At("HEAD"))
    public void reapplyFixedAngleBeforeRender(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci){
        if(getIsActionMobModelPart()) {
            this.pitch = fixedPitch;
            this.yaw = fixedYaw;
            this.roll = fixedRoll;
        }

    }
}
