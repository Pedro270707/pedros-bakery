package net.pedroricardo.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.network.PBNetworkRegistry;
import net.pedroricardo.network.SetCookiePixelPacket;
import net.pedroricardo.network.SetCookieShapePacket;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class CookieTableScreen extends AbstractContainerScreen<CookieTableScreenHandler> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/gui/container/cookie_table.png");
    private CookieTableCanvasWidget canvas;

    public CookieTableScreen(CookieTableScreenHandler handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
        this.imageHeight = 196;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int screenX = (this.width - this.imageWidth) / 2;
        int screenY = (this.height - this.imageHeight) / 2;

        this.canvas = new CookieTableCanvasWidget(screenX + 48, screenY + 18, 16, 16, 5, 5, this);
        this.addWidget(this.canvas);
        this.addRenderableWidget(this.canvas);
        EraseButtonWidget eraseButtonWidget = new EraseButtonWidget(screenX + 130, screenY + 89);
        this.addWidget(eraseButtonWidget);
        this.addRenderableWidget(eraseButtonWidget);
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
        context.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
        context.pose().pushPose();
        context.pose().translate(48.0f, 18.0f, 0.0f);
        context.pose().scale(5.0f, 5.0f, 1.0f);
        context.renderItem(this.getMenu().output.getItem(0), 0, 0);
        context.pose().popPose();
        super.renderLabels(context, mouseX, mouseY);
    }

    public void setPixel(Vector2i pixel, boolean value) {
        PBNetworkRegistry.INSTANCE.sendToServer(new SetCookiePixelPacket(pixel, value));
    }

    public void emptyPixels() {
        this.setCookieShape(Set.of());
    }

    public void setCookieShape(Set<Vector2i> shape) {
        PBNetworkRegistry.INSTANCE.sendToServer(new SetCookieShapePacket(shape));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean canvasMouseDown = false;
        if (!PBHelpers.contains(this.getMenu().getCarried(), PBComponentTypes.COOKIE_SHAPE.get())) {
            canvasMouseDown = this.canvas.mouseDown(mouseX, mouseY, button);
        }
        return canvasMouseDown || super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean canvasMouseReleased = this.canvas.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button) || canvasMouseReleased;
    }

    @OnlyIn(Dist.CLIENT)
    class EraseButtonWidget extends AbstractButton {
        public static final ResourceLocation TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button.png");
        public static final ResourceLocation TEXTURE_DISABLED = new ResourceLocation(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_disabled.png");
        public static final ResourceLocation TEXTURE_HIGHLIGHTED = new ResourceLocation(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_highlighted.png");
        public static final ResourceLocation TEXTURE_SELECTED = new ResourceLocation(PedrosBakery.MOD_ID, "textures/gui/sprites/container/cookie_table/erase_button_selected.png");

        public EraseButtonWidget(int x, int y) {
            super(x, y, 9, 9, Component.translatable("container.cookie_table.clear_canvas"));
        }

        @Override
        public void onPress() {
            CookieTableScreen.this.emptyPixels();
        }

        @Override
        public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
            ResourceLocation identifier = !this.active ? TEXTURE_DISABLED : this.isHoveredOrFocused() ? TEXTURE_HIGHLIGHTED : TEXTURE;
            context.blit(identifier, this.getX(), this.getY(), 0, 0, 0, this.width, this.height, this.width, this.height);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput builder) {
        }
    }
}
