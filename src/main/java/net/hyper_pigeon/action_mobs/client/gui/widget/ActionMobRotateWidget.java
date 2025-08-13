package net.hyper_pigeon.action_mobs.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ActionMobRotateWidget extends ButtonWidget {
    private static final Identifier PAGE_FORWARD_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/page_forward_highlighted");
    private static final Identifier PAGE_FORWARD_TEXTURE = Identifier.ofVanilla("widget/page_forward");
    private static final Identifier PAGE_BACKWARD_HIGHLIGHTED_TEXTURE = Identifier.ofVanilla("widget/page_backward_highlighted");
    private static final Identifier PAGE_BACKWARD_TEXTURE = Identifier.ofVanilla("widget/page_backward");

    private boolean isLeftRotate;
    private PressAction onPress;



    public ActionMobRotateWidget(int x, int y, int width, int height, boolean isLeftRotate, PressAction onPress) {
        super(x,
                y,
                width,
                height,
                isLeftRotate ? Text.translatable("gui.action_mobs.rotate_left") : Text.translatable("gui.action_mobs.rotate_right"),
                onPress,
                DEFAULT_NARRATION_SUPPLIER);
        this.isLeftRotate = isLeftRotate;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        Identifier identifier;
        if (this.isLeftRotate) {
            identifier = this.isSelected() ? PAGE_BACKWARD_HIGHLIGHTED_TEXTURE : PAGE_BACKWARD_TEXTURE;
        } else {
            identifier = this.isSelected() ? PAGE_FORWARD_HIGHLIGHTED_TEXTURE : PAGE_FORWARD_TEXTURE;
        }

        context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, identifier, this.getX(), this.getY(), width, height);
    }
}
