package net.pedroricardo.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.network.PBNetworkRegistry;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class CookieTableScreen extends HandledScreen<CookieTableScreenHandler> {
    public static final Identifier TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/gui/container/cookie_table.png");
    private Map<Vector2i, CookieTablePixelWidget> pixelWidgets = new HashMap<>();

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
        int pixelWidgetWidth = 5, pixelWidgetHeight = 5;
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                Vector2i pixel = new Vector2i(x, y);
                CookieTablePixelWidget widget = new CookieTablePixelWidget(screenX + 48 + x * pixelWidgetWidth, screenY + 18 + y * pixelWidgetHeight, pixelWidgetWidth, pixelWidgetHeight, pixel, (widget1) -> this.togglePixel(widget1.getPixel()));
                this.addSelectableChild(widget);
                this.addDrawableChild(widget);
                this.pixelWidgets.put(pixel, widget);
            }
        }
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

    public void togglePixel(Vector2i pixel) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.encodeAsJson(PBCodecs.VECTOR_2I, pixel);
        ClientPlayNetworking.send(PBNetworkRegistry.TOGGLE_COOKIE_SHAPE, buf);
    }
}
