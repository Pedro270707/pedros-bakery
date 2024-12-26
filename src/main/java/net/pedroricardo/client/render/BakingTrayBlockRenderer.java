package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;

public class BakingTrayBlockRenderer implements BlockEntityRenderer<BakingTrayBlockEntity> {
    public static final BakingTrayBlockEntity RENDER_TRAY = new BakingTrayBlockEntity(BlockPos.ZERO, PBBlocks.BAKING_TRAY.get().defaultBlockState());
    public static final BakingTrayBlockEntity RENDER_EXPANDABLE_TRAY = new BakingTrayBlockEntity(BlockPos.ZERO, PBBlocks.EXPANDABLE_BAKING_TRAY.get().defaultBlockState());

    public static final ResourceLocation BAKING_TRAY_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/baking_tray/baking_tray.png");
    public static final ResourceLocation EXPANDABLE_BAKING_TRAY_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/baking_tray/expandable_baking_tray.png");

    public BakingTrayBlockRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(BakingTrayBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        int size = entity.getSize();
        int height = entity.getHeight();
        int color = 0xFFFFFFFF;
        ResourceLocation texture = entity.getBlockState().is(PBBlocks.EXPANDABLE_BAKING_TRAY.get()) ? EXPANDABLE_BAKING_TRAY_TEXTURE : BAKING_TRAY_TEXTURE;

        matrices.pushPose();
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 16.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 32.0f - size + 16.0f, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 0.0f, 32.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color);

        PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), 8.0f - size / 2.0f, -8.0f - size / 2.0f, 0.0f, size, size, 32.0f + (Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, overlay, color);

        CakeBatter<HeightOnlyBatterSizeContainer> batter = entity.getCakeBatter();
        if (!batter.isEmpty()) {
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(batter.getFlavor().getCakeTextureLocation()));
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f, 8.0f - size / 2.0f, batter.getSizeContainer().getHeight(), size, size, (16.0f + Math.round(8.0f - size / 2.0f)), Math.round(8.0f - size / 2.0f), light, PBCakeBlockRenderer.getBakeTimeOverlay(batter.getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batter.getBakeTime(), color));
            if (PedrosBakery.CONFIG.cakeRenderQuality.get().renderTopBorder()) {
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
        matrices.popPose();
    }
}
