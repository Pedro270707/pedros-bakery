package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.screen.ScreenTexts;
import net.pedroricardo.item.PBComponentTypes;
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
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) {
            context.getMatrices().push();
            context.getMatrices().translate(0.0f, 0.0f, 151.0f);
            if (this.parent.getScreenHandler().getCursorStack().contains(PBComponentTypes.COOKIE_SHAPE)) {
                context.drawBorder(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFFFFFF);
            } else {
                Vector2i pixel = new Vector2i((mouseX - this.getX()) / this.pixelWidth, (mouseY - this.getY()) / this.pixelHeight);
                context.drawBorder(this.getX() + pixel.x() * this.pixelWidth, this.getY() + pixel.y() * this.pixelHeight, this.pixelWidth, this.pixelHeight, 0xFFFFFFFF);
            }
            context.getMatrices().pop();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.clicked(mouseX, mouseY)) {
            return false;
        }
        if (this.parent.getScreenHandler().getCursorStack().contains(PBComponentTypes.COOKIE_SHAPE)) {
            if (this.isValidClickButton(button)) {
                this.parent.setCookieShape(this.parent.getScreenHandler().getCursorStack().get(PBComponentTypes.COOKIE_SHAPE));
                return true;
            } else {
                return false;
            }
        }
        return this.mouseDown(mouseX, mouseY, button);
    }

    public boolean mouseDown(double mouseX, double mouseY, int button) {
        if (mouseX < this.getX() || mouseX >= this.getX() + this.getWidth() || mouseY < this.getY() || mouseY >= this.getY() + this.getHeight()) {
            return false;
        }
        Vector2i pixel = new Vector2i((int)((mouseX - this.getX()) / 5.0f), (int)((mouseY - this.getY()) / 5.0f));
        if (button == 0) {
            boolean containsPixel = this.parent.getScreenHandler().getCookieShape().contains(pixel);
            if (this.currentPixel == null) {
                this.currentPixel = !containsPixel;
            }
            if ((containsPixel && this.currentPixel) || (!containsPixel && !this.currentPixel)) {
                return false;
            }
            this.parent.setPixel(pixel, this.currentPixel);
        } else if (button == 1) {
            this.parent.setPixel(pixel, false);
            return true;
        }
        return false;
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.currentPixel = null;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
