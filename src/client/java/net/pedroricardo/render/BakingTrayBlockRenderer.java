package net.pedroricardo.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;

public class BakingTrayBlockRenderer implements BlockEntityRenderer<BakingTrayBlockEntity> {
    public static final BakingTrayBlockEntity RENDER_TRAY = new BakingTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.BAKING_TRAY.getDefaultState());
    public static final BakingTrayBlockEntity RENDER_EXPANDABLE_TRAY = new BakingTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.EXPANDABLE_BAKING_TRAY.getDefaultState());

    public static final Identifier BAKING_TRAY_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/baking_tray/baking_tray.png");
    public static final Identifier EXPANDABLE_BAKING_TRAY_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/baking_tray/expandable_baking_tray.png");

    public BakingTrayBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BakingTrayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int size = entity.getSize();
        int height = entity.getHeight();
        int color = 0xFFFFFFFF;
        Identifier texture = entity.getCachedState().isOf(PBBlocks.EXPANDABLE_BAKING_TRAY) ? EXPANDABLE_BAKING_TRAY_TEXTURE : BAKING_TRAY_TEXTURE;

        matrices.push();
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 16.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 32.0f - size + 16.0f, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -8.0f - size / 2.0f, 0.0f, size, size, 32.0f + (Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, overlay, color);

        CakeBatter<HeightOnlyBatterSizeContainer> batter = entity.getCakeBatter();
        if (!batter.isEmpty()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(batter.getFlavor().getCakeTextureLocation()));
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, (16.0f + Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color));
            if (PedrosBakery.CONFIG.cakeRenderQuality().renderTopBorder()) {
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 16.0f + Math.round(8.0f - size / 2.0f), 80.0f, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Top
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 16.0f + Math.round(8.0f - size / 2.0f), 128.0f - size, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Bottom
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 0.0f, 96.0f + Math.round(8.0f - size / 2.0f), light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 48.0f - size, 96.0f + Math.round(8.0f - size / 2.0f), light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Right
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 0.0f, 80.0f, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Top Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 48.0f - size, 80.0f, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Top Right
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 0.0f, 128.0f - size, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Bottom Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, 48.0f - size, 128.0f - size, light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color)); // Bottom Right
            }
        }
        matrices.pop();
    }
}
