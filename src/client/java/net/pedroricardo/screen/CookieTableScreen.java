package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.network.PBNetworkRegistry;
import org.joml.Vector2i;

import java.util.*;

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
        this.renderBackground(context);
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
        PacketByteBuf buf = PacketByteBufs.create();
        buf.encodeAsJson(PBCodecs.VECTOR_2I, pixel);
        buf.writeBoolean(value);
        ClientPlayNetworking.send(PBNetworkRegistry.SET_COOKIE_PIXEL, buf);
        this.getScreenHandler().setPixel(pixel, value);
    }

    public void emptyPixels() {
        this.setCookieShape(Set.of());
    }

    public void setCookieShape(Set<Vector2i> shape) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.encodeAsJson(PBCodecs.VECTOR_2I.listOf().<Set<Vector2i>>xmap(HashSet::new, ArrayList::new), shape);
        ClientPlayNetworking.send(PBNetworkRegistry.SET_COOKIE_SHAPE, buf);
        this.getScreenHandler().setCookieShape(shape);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean canvasMouseDown = false;
        if (!PBHelpers.contains(this.getScreenHandler().getCursorStack(), PBComponentTypes.COOKIE_SHAPE)) {
            canvasMouseDown = this.canvas.mouseDown(mouseX, mouseY, button);
        }
        return canvasMouseDown || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean canvasMouseReleased = this.canvas.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button) || canvasMouseReleased;
    }

    @Environment(value=EnvType.CLIENT)
    class EraseButtonWidget extends PressableWidget {
        public static final Identifier TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button.png");
        public static final Identifier TEXTURE_DISABLED = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_disabled.png");
        public static final Identifier TEXTURE_HIGHLIGHTED = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_highlighted.png");
        public static final Identifier TEXTURE_SELECTED = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_selected.png");

        public EraseButtonWidget(int x, int y) {
            super(x, y, 9, 9, Text.translatable("container.cookie_table.clear_canvas"));
        }

        @Override
        public void onPress() {
            CookieTableScreen.this.emptyPixels();
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            Identifier identifier = !this.active ? TEXTURE_DISABLED : this.isSelected() ? TEXTURE_HIGHLIGHTED : TEXTURE;
            context.drawTexture(identifier, this.getX(), this.getY(), 0, 0, 0, this.width, this.height, this.width, this.height);
        }

        @Override
        protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        }
    }
}
