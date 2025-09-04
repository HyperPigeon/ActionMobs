package net.hyper_pigeon.action_mobs.duck_type;

import org.joml.Vector3f;

import java.util.Map;

public interface ActionMobRenderState {
    void setActionMob(boolean value);
    boolean isActionMob();

    void setPartAngles(Map<String, Vector3f> partAngles);
    Map<String, Vector3f> getPartAngles();
    Vector3f getPartAngle(String partName);
}
