package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public class CookieTablePixelWidget extends PressableWidget {
    private final Vector2i pixel;
    private final PressAction onPress;

    public CookieTablePixelWidget(int x, int y, int width, int height, Vector2i pixel, PressAction onPress) {
        super(x, y, width, height, ScreenTexts.EMPTY);
        this.pixel = pixel;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        this.onPress.onPress(this);
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isFocused() || this.isHovered()) {
            context.getMatrices().translate(0.0f, 0.0f, 1000.0f);
            context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFFFFFF);
            context.getMatrices().translate(0.0f, 0.0f, -1000.0f);
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected MutableText getNarrationMessage() {
        return Text.literal(this.getPixel().x() + ", " + this.getPixel().y());
    }

    public Vector2i getPixel() {
        return this.pixel;
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(CookieTablePixelWidget widget);
    }
}
