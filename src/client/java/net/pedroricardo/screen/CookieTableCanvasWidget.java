package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

@Environment(EnvType.CLIENT)
public class CookieTableCanvasWidget extends ClickableWidget {
    private final CookieTableScreen parent;
    private final int pixelWidth;
    private final int pixelHeight;
    private @Nullable Boolean currentPixel = null;

    public CookieTableCanvasWidget(int x, int y, int horizontalPixels, int verticalPixels, int pixelWidth, int pixelHeight, CookieTableScreen parent) {
        super(x, y, horizontalPixels * pixelWidth, verticalPixels * pixelHeight, ScreenTexts.EMPTY);
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.parent = parent;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) {
            Vector2i pixel = new Vector2i((mouseX - this.getX()) / this.pixelWidth, (mouseY - this.getY()) / this.pixelHeight);
            context.getMatrices().translate(0.0f, 0.0f, 1000.0f);
            context.drawBorder(this.getX() + pixel.x() * this.pixelWidth, this.getY() + pixel.y() * this.pixelHeight, this.pixelWidth, this.pixelHeight, 0xFFFFFFFF);
            context.getMatrices().translate(0.0f, 0.0f, -1000.0f);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.mouseDown(mouseX, mouseY);
    }

    public boolean mouseDown(double mouseX, double mouseY) {
        if (mouseX < this.getX() || mouseX >= this.getX() + this.getWidth() || mouseY < this.getY() || mouseY >= this.getY() + this.getHeight()) {
            return false;
        }
        Vector2i pixel = new Vector2i((int)((mouseX - this.getX()) / 5.0f), (int)((mouseY - this.getY()) / 5.0f));
        boolean containsPixel = this.parent.getScreenHandler().getCookieShape().contains(pixel);
        if (this.currentPixel == null) {
            this.currentPixel = !containsPixel;
        }
        if ((containsPixel && this.currentPixel) || (!containsPixel && !this.currentPixel)) {
            return false;
        }
        this.parent.setPixel(pixel, this.currentPixel);
        return true;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.currentPixel = null;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
