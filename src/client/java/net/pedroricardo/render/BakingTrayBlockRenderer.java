package net.pedroricardo.render;

import net.minecraft.client.render.RenderLayer;
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
import net.pedroricardo.block.helpers.CakeBatter;

public class BakingTrayBlockRenderer implements BlockEntityRenderer<BakingTrayBlockEntity> {
    public static final BakingTrayBlockEntity RENDER_TRAY = new BakingTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.BAKING_TRAY.getDefaultState());
    private static final Identifier BAKING_TRAY_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/baking_tray.png");

    public BakingTrayBlockRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(BakingTrayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        int size = entity.getSize();
        int height = entity.getHeight();
        int color = 0xFFFFFFFF;

        matrices.push();
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 16.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 16.0f - size + 16.0f, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 32.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 32.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 32.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(BAKING_TRAY_TEXTURE)), 8.0f - size / 2.0f, -8.0f - size / 2.0f, 0.0f, size, size, 32.0f + (Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, overlay, color);

        CakeBatter batter = entity.getCakeBatter();
        if (!batter.isEmpty()) {
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(batter.getFlavor().getTextureLocation())), 8.0f - size / 2.0f, 8.0f - size / 2.0f, entity.getCakeBatter().getHeight(), size, size, (16.0f + Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color));
        }
        matrices.pop();
    }
}
