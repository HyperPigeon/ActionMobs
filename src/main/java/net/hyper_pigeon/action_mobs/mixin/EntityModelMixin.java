package net.hyper_pigeon.action_mobs.mixin;

import net.hyper_pigeon.action_mobs.client.render.block.entity.ActionMobBlockEntityRenderer;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobModel;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobRenderState;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;
import java.util.function.Function;

@Mixin(EntityModel.class)
public class EntityModelMixin<S extends EntityRenderState> extends Model implements ActionMobModel {


    public EntityModelMixin(ModelPart root, Function<Identifier, RenderLayer> layerFactory) {
        super(root, layerFactory);
    }

    @Override
    public void setAngles(ActionMobRenderState actionMobRenderState) {
        this.resetTransforms();
        Map<String, Vector3f> partAngles = actionMobRenderState.getPartAngles();
        if(partAngles != null) {
            Function<String, ModelPart> partGetter = getRootPart().createPartGetter();
            for(String partName : partAngles.keySet()) {
                ModelPart modelPart = partGetter.apply(partName);
                if(modelPart != null) {
                    Vector3f angles = partAngles.get(partName);
                    Vector3f convertedAngles = ActionMobBlockEntityRenderer.convertToRadiansVector(angles);
                    modelPart.setAngles(convertedAngles.x(), convertedAngles.y(), convertedAngles.z());
                }
            }
        }
    }
}
