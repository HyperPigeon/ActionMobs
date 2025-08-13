package net.hyper_pigeon.action_mobs.duck_type;

import org.joml.Vector3f;

public interface ActionMobModelPartRenderHandler {


    public void setIsActionMobModelPart(boolean value);

    public boolean getIsActionMobModelPart();

    public void setFixedAngles(Vector3f vector3f);

    public Vector3f getFixedAngles();
}
