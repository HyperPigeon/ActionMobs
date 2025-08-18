package net.hyper_pigeon.action_mobs.client.gui.screen.ingame;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hyper_pigeon.action_mobs.block.entity.ActionMobBlockEntity;
import net.hyper_pigeon.action_mobs.client.gui.widget.ActionMobRotateWidget;
import net.hyper_pigeon.action_mobs.duck_type.ActionMobModelPartRenderHandler;
import net.hyper_pigeon.action_mobs.packet.*;
import net.hyper_pigeon.action_mobs.statue_type.StatueTypeDataLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.function.Function;

import static net.hyper_pigeon.action_mobs.client.render.block.entity.ActionMobBlockEntityRenderer.convertToRadiansVector;

public class ActionMobEditScreen extends Screen {

    private final ActionMobBlockEntity actionMobBlockEntity;
    protected HashMap<String, ClickableWidget> partAngleButtons = new HashMap<>();
    private final float pitchYawRollChange = 15F;
    private float displayYaw = 0F;
    private int pageNumber = 0;

    public ActionMobEditScreen(ActionMobBlockEntity blockEntity) {
        super(Text.empty());
        this.actionMobBlockEntity = blockEntity;
    }

    @Override
    public void init() {
        super.init();
        if (this.client != null) {
            int textX = 72;
            int decreaseX = (int) ((155 + 80 + 30) * 0.9);
            int increaseX = (int) ((180 + 80 + 30) * 0.9);
            int y = 23;

            int guiScale = MinecraftClient.getInstance().options.getGuiScale().getValue();

            String[] partNames = actionMobBlockEntity.getPartAngles().keySet().toArray(new String[0]);

            int startingIndex = pageNumber * 6;
            int endIndex = Math.min(partNames.length, startingIndex + 6);

            for (int i = 0; i < partNames.length; i++) {
                String partName = partNames[i];

                String pitchName = partName + "_pitch";
                String yawName = partName + "_yaw";
                String rollName = partName + "_roll";

                // Pitch
                partAngleButtons.put(pitchName, new TextFieldWidget(this.client.textRenderer, textX + 45, y, 54, 18, Text.empty()));
                ((TextFieldWidget) (partAngleButtons.get(pitchName))).setText(String.valueOf(this.actionMobBlockEntity.getPartPitch(partName)));

                partAngleButtons.put(pitchName + "_decrease", ButtonWidget.builder(Text.literal("-"), action -> {
                    this.actionMobBlockEntity.setPartPitch(partName, this.actionMobBlockEntity.getPartPitch(partName) - pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(pitchName))).setText(String.valueOf(this.actionMobBlockEntity.getPartPitch(partName)));
                }).dimensions(decreaseX + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(pitchName + "_decrease"));

                partAngleButtons.put(pitchName + "_increase", ButtonWidget.builder(Text.literal("+"), action -> {
                    this.actionMobBlockEntity.setPartPitch(partName, this.actionMobBlockEntity.getPartPitch(partName) + pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(pitchName))).setText(String.valueOf(this.actionMobBlockEntity.getPartPitch(partName)));
                }).dimensions(increaseX + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(pitchName + "_increase"));

                // Yaw
                partAngleButtons.put(yawName, new TextFieldWidget(this.client.textRenderer, textX + 54 + 45, y, 54, 18, Text.empty()));
                ((TextFieldWidget) (partAngleButtons.get(yawName))).setText(String.valueOf(this.actionMobBlockEntity.getPartYaw(partName)));

                this.addDrawable(partAngleButtons.get(yawName));

                partAngleButtons.put(yawName + "_decrease", ButtonWidget.builder(Text.literal("-"), action -> {
                    this.actionMobBlockEntity.setPartYaw(partName, this.actionMobBlockEntity.getPartYaw(partName) - pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(yawName))).setText(String.valueOf(this.actionMobBlockEntity.getPartYaw(partName)));
                }).dimensions(decreaseX + 54 + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(yawName + "_decrease"));

                partAngleButtons.put(yawName + "_increase", ButtonWidget.builder(Text.literal("+"), action -> {
                    this.actionMobBlockEntity.setPartYaw(partName, this.actionMobBlockEntity.getPartYaw(partName) + pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(yawName))).setText(String.valueOf(this.actionMobBlockEntity.getPartYaw(partName)));
                }).dimensions(increaseX + 54 + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(yawName + "_increase"));

                // Roll
                partAngleButtons.put(rollName, new TextFieldWidget(this.client.textRenderer, textX + 108 + 45, y, 54, 18, Text.empty()));
                ((TextFieldWidget) (partAngleButtons.get(rollName))).setText(String.valueOf(this.actionMobBlockEntity.getPartRoll(partName)));

                this.addDrawable(partAngleButtons.get(rollName));

                partAngleButtons.put(rollName + "_decrease", ButtonWidget.builder(Text.literal("-"), action -> {
                    this.actionMobBlockEntity.setPartRoll(partName, this.actionMobBlockEntity.getPartRoll(partName) - pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(rollName))).setText(String.valueOf(this.actionMobBlockEntity.getPartRoll(partName)));
                }).dimensions(decreaseX + 108 + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(rollName + "_decrease"));

                partAngleButtons.put(rollName + "_increase", ButtonWidget.builder(Text.literal("+"), action -> {
                    this.actionMobBlockEntity.setPartRoll(partName, this.actionMobBlockEntity.getPartRoll(partName) + pitchYawRollChange);
                    updateActionMobBlockPart(partName, this.actionMobBlockEntity.getPartAngle(partName));
                    ((TextFieldWidget) (partAngleButtons.get(rollName))).setText(String.valueOf(this.actionMobBlockEntity.getPartRoll(partName)));
                }).dimensions(increaseX + 108 + 45, y, 18, 18).build());

                this.addDrawableChild(partAngleButtons.get(rollName + "_increase"));

                y += 27;

                this.addDrawable(partAngleButtons.get(pitchName));

            }

            if(StatueTypeDataLoader.statueTypesByEntityType.get(this.actionMobBlockEntity.getStatueEntity().getType()).canBeBaby()) {
                CheckboxWidget babyCheckboxWidget = CheckboxWidget.builder(Text.translatable("gui.action_mobs.baby"),
                                this.client.textRenderer)
                        .checked(this.actionMobBlockEntity.isBaby())
                        .callback(((checkbox, checked) -> {
                            this.updateActionMobBlockIsBaby(checked);
                        }))
                        .pos(textX+350+45, 10)
                        .build();

                this.addDrawableChild(babyCheckboxWidget);
            }

            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            int windowHeight =  minecraftClient.currentScreen.height;
            ActionMobRotateWidget leftRotateWidget = new ActionMobRotateWidget(
                    textX+340,
                    windowHeight - 15,
                    23/2,
                    13/2,
                    true,
                    widget -> {
                        displayYaw -= 0.174533F;
                    });

            ActionMobRotateWidget rightRotateWidget = new ActionMobRotateWidget(
                    textX+370 ,
                    windowHeight - 15,
                    23/2,
                    13/2,
                    false,
                    widget -> {
                        displayYaw += 0.174533F;
                    });

            this.addDrawableChild(leftRotateWidget);
            this.addDrawableChild(rightRotateWidget);

            TextFieldWidget pitchDisplay =  new TextFieldWidget(this.client.textRenderer, textX - 25, y, 54, 18, Text.empty());
            pitchDisplay.setText(String.valueOf(this.actionMobBlockEntity.getPitch()));

            ButtonWidget decreasePitch =  ButtonWidget.builder(Text.literal("-"), action -> {
                this.actionMobBlockEntity.setPitch(this.actionMobBlockEntity.getPitch() - pitchYawRollChange);
                pitchDisplay.setText(String.valueOf(this.actionMobBlockEntity.getPitch()));
                updateActionMobBlock(this.actionMobBlockEntity.getPitch(), true);
            }).dimensions(textX + 30, y, 18, 18).build();

            ButtonWidget increasePitch =  ButtonWidget.builder(Text.literal("+"), action -> {
                this.actionMobBlockEntity.setPitch(this.actionMobBlockEntity.getPitch() + pitchYawRollChange);
                pitchDisplay.setText(String.valueOf(this.actionMobBlockEntity.getPitch()));
                updateActionMobBlock(this.actionMobBlockEntity.getPitch(), true);
            }).dimensions(textX + 50, y, 18, 18).build();



            TextFieldWidget yawDisplay =  new TextFieldWidget(this.client.textRenderer, textX - 25, y + 27, 54, 18, Text.empty());
            yawDisplay.setText(String.valueOf(this.actionMobBlockEntity.getYaw()));

            ButtonWidget decreaseYaw =  ButtonWidget.builder(Text.literal("-"), action -> {
                this.actionMobBlockEntity.setYaw(this.actionMobBlockEntity.getYaw() - pitchYawRollChange);
                yawDisplay.setText(String.valueOf(this.actionMobBlockEntity.getYaw()));
                updateActionMobBlock(this.actionMobBlockEntity.getYaw(), false);
            }).dimensions(textX + 30, y + 27, 18, 18).build();

            ButtonWidget increaseYaw =  ButtonWidget.builder(Text.literal("+"), action -> {
                this.actionMobBlockEntity.setYaw(this.actionMobBlockEntity.getYaw() + pitchYawRollChange);
                yawDisplay.setText(String.valueOf(this.actionMobBlockEntity.getYaw()));
                updateActionMobBlock(this.actionMobBlockEntity.getYaw(), false);
            }).dimensions(textX + 50, y + 27, 18, 18).build();

            this.addDrawableChild(pitchDisplay);
            this.addDrawableChild(decreasePitch);
            this.addDrawableChild(increasePitch);

            this.addDrawableChild(yawDisplay);
            this.addDrawableChild(decreaseYaw);
            this.addDrawableChild(increaseYaw);
        }


    }


    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        this.renderInGameBackground(context);
        drawEntity(
                context,
                416,
                context.getScaledWindowHeight() - (context.getScaledWindowHeight() / 3),
                447,
                context.getScaledWindowHeight() - 10,
                25,
                0.25F
        );
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    protected void updateActionMobBlockPart(String partName, Vector3f vector3f) {
        UpdateActionBlockMobPart updateActionBlockMobPart = new UpdateActionBlockMobPart(vector3f, partName);
        C2SUpdateActionBlockMobPart c2SUpdateActionBlockMobPart = new C2SUpdateActionBlockMobPart(actionMobBlockEntity.getPos(), updateActionBlockMobPart);
        ClientPlayNetworking.send(c2SUpdateActionBlockMobPart);
    }

    protected void updateActionMobBlock(float newAngle, boolean isPitch) {
        UpdateActionMobAngle updateActionMobAngle = new UpdateActionMobAngle(newAngle, isPitch);
        C2SUpdateActionBlockMobAngle c2SUpdateActionBlockMobAngle = new C2SUpdateActionBlockMobAngle(actionMobBlockEntity.getPos(), updateActionMobAngle);
        ClientPlayNetworking.send(c2SUpdateActionBlockMobAngle);
    }

    protected void updateActionMobBlockIsBaby(boolean isBaby) {
        UpdateActionBlockMobIsBaby updateActionBlockMobIsBaby = new UpdateActionBlockMobIsBaby(isBaby);
        C2SUpdateActionBlockMobIsBaby c2SUpdateActionBlockMobIsBaby = new C2SUpdateActionBlockMobIsBaby(actionMobBlockEntity.getPos(), updateActionBlockMobIsBaby);
        ClientPlayNetworking.send(c2SUpdateActionBlockMobIsBaby);
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int textX = 72;
        int y = 23;
        if (this.client != null) {
            super.render(context, mouseX, mouseY, delta);

            context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.action_mobs.pitch"), textX + 45, 9, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.action_mobs.yaw"), textX + 54 + 45, 9, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.action_mobs.roll"), textX + 108 + 45, 9, 0xFFFFFFFF);

            for (String partName : actionMobBlockEntity.getPartAngles().keySet()) {
                String splitPartName = partName.replace("_", " ");
                context.drawTextWithShadow(client.textRenderer, Text.literal(splitPartName.substring(0,1).toUpperCase() + splitPartName.substring(1)), 5, y + 5, 0xFFFFFFFF);
                y += 27;
            }

            context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.action_mobs.pitch"), 5, y + 5, 0xFFFFFFFF);
            context.drawTextWithShadow(client.textRenderer, Text.translatable("gui.action_mobs.yaw"), 5, y + 32, 0xFFFFFFFF);
        }
    }

    public void drawEntity(DrawContext context, int x1, int y1, int x2, int y2, int size, float scale) {
        LivingEntity entity = (LivingEntity) this.actionMobBlockEntity.getStatueEntity();

        context.enableScissor(x1, y1, x2, y2);
        Quaternionf quaternionf = (new Quaternionf()).rotateZ(3.1415927F).rotateY(3.1415927F).rotateY(displayYaw);
        float o = entity.getScale();
        Vector3f vector3f = new Vector3f(0.0F, entity.getHeight() / 2.0F + scale * o, 0.0F);
        float p = (float)size / o;
        drawEntity(context, x1, y1, x2, y2, p, vector3f, quaternionf, null);
        context.disableScissor();
    }

    public void drawEntity(DrawContext drawer, int x1, int y1, int x2, int y2, float scale, Vector3f translation, Quaternionf rotation, @Nullable Quaternionf overrideCameraAngle) {
        Entity entity = this.actionMobBlockEntity.getStatueEntity();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        EntityRenderer<Entity, EntityRenderState> entityRenderer = (EntityRenderer<Entity, EntityRenderState>) entityRenderDispatcher.getRenderer(entity);
        LivingEntityRenderState entityRenderState = (LivingEntityRenderState) entityRenderer.getAndUpdateRenderState(entity, 0F);

        Function<String, ModelPart> function = ((LivingEntityRenderer<?, ?, ?>)entityRenderer).getModel().getRootPart().createPartGetter();
        for(String partName : StatueTypeDataLoader.statueTypesByEntityType.get(entity.getType()).getPoseablePartNames()) {
            ModelPart modelPart = function.apply(partName);
            Vector3f vector3f = this.actionMobBlockEntity.getPartAngle(partName);
            vector3f = convertToRadiansVector(vector3f);
            ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setIsActionMobModelPart(true);
            ((ActionMobModelPartRenderHandler)(Object)(modelPart)).setFixedAngles(vector3f);
        }
        entityRenderState.baby = this.actionMobBlockEntity.isBaby();
        entityRenderState.hitbox = null;
        drawer.addEntity(entityRenderState, scale, translation, rotation, overrideCameraAngle, x1, y1, x2, y2);
    }


}
