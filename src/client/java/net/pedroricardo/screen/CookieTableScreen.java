package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.network.SetCookieShapePayload;
import net.pedroricardo.network.SetCookiePixelC2SPayload;
import org.joml.Vector2i;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class CookieTableScreen extends HandledScreen<CookieTableScreenHandler> {
    public static final Identifier TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/container/cookie_table.png");
    private CookieTableCanvasWidget canvas;

    public CookieTableScreen(CookieTableScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 196;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int screenX = (this.width - this.backgroundWidth) / 2;
        int screenY = (this.height - this.backgroundHeight) / 2;

        this.canvas = new CookieTableCanvasWidget(screenX + 48, screenY + 18, 16, 16, 5, 5, this);
        this.addSelectableChild(this.canvas);
        this.addDrawableChild(this.canvas);
        EraseButtonWidget eraseButtonWidget = new EraseButtonWidget(screenX + 130, screenY + 89);
        this.addSelectableChild(eraseButtonWidget);
        this.addDrawableChild(eraseButtonWidget);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.getMatrices().push();
        context.getMatrices().translate(48.0f, 18.0f, 0.0f);
        context.getMatrices().scale(5.0f, 5.0f, 1.0f);
        context.drawItem(this.handler.output.getStack(0), 0, 0);
        context.getMatrices().pop();
        super.drawForeground(context, mouseX, mouseY);
    }

    public void setPixel(Vector2i pixel, boolean value) {
        ClientPlayNetworking.send(new SetCookiePixelC2SPayload(pixel, value));
    }

    public void emptyPixels() {
        ClientPlayNetworking.send(new SetCookieShapePayload(Set.of()));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return this.canvas.mouseDown(mouseX, mouseY) || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean canvasMouseReleased = this.canvas.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button) || canvasMouseReleased;
    }

    @Environment(value=EnvType.CLIENT)
    class EraseButtonWidget extends PressableWidget {
        public static final Identifier TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "container/cookie_table/erase_button");
        public static final Identifier TEXTURE_DISABLED = Identifier.of(PedrosBakery.MOD_ID, "container/cookie_table/erase_button_disabled");
        public static final Identifier TEXTURE_HIGHLIGHTED = Identifier.of(PedrosBakery.MOD_ID, "container/cookie_table/erase_button_highlighted");
        public static final Identifier TEXTURE_SELECTED = Identifier.of(PedrosBakery.MOD_ID, "container/cookie_table/erase_button_selected");

        public EraseButtonWidget(int x, int y) {
            super(x, y, 9, 9, Text.translatable("container.cookie_table.clear_canvas"));
        }

        @Override
        public void onPress() {
            CookieTableScreen.this.emptyPixels();
        }

        @Override
        protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            Identifier identifier = !this.active ? TEXTURE_DISABLED : this.isSelected() ? TEXTURE_HIGHLIGHTED : TEXTURE;
            context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
