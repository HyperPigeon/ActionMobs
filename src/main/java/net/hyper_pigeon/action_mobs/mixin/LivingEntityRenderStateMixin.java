package net.hyper_pigeon.action_mobs.mixin;


import net.hyper_pigeon.action_mobs.duck_type.ActionMobRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(LivingEntityRenderState.class)
public class LivingEntityRenderStateMixin implements ActionMobRenderState {

    private Map<String, Vector3f> partAngles;

    boolean isActionMob = false;
    @Override
    public void setActionMob(boolean value) {
        this.isActionMob = value;
    }

    @Override
    public boolean isActionMob() {
        return isActionMob;
    }

    @Override
    public void setPartAngles(Map<String, Vector3f> partAngles) {
        this.partAngles = partAngles;
    }

    @Override
    public Map<String, Vector3f> getPartAngles() {
        return partAngles;
    }

    @Override
    public Vector3f getPartAngle(String partName) {
        return partAngles.get(partName);
    }
}
