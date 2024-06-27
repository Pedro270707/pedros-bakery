package net.pedroricardo.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PBCakeBlock;
import net.pedroricardo.block.PBCandleCakeBlock;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeLayer;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.registry.CakeFeatureRenderer;
import net.pedroricardo.registry.CakeFeatureRendererRegistry;

public class PBCakeBlockRenderer implements BlockEntityRenderer<PBCakeBlockEntity> {
    public static final PBCakeBlockEntity RENDER_CAKE = new PBCakeBlockEntity(BlockPos.ORIGIN, PBBlocks.CAKE.getDefaultState());

    @Override
    public void render(PBCakeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderCake(entity, matrices, vertexConsumers, light, overlay);
    }

    public static void renderCake(PBCakeBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        if (state == null) return;
        for (CakeLayer layer : entity.getLayers()) {
            matrices.push();
            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(state.get(Properties.HORIZONTAL_FACING).asRotation()));
            matrices.translate(-0.5f, -0.5f, -0.5f);

            renderCakeLayer(layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(layer.getFlavor().getTextureLocation())), light, getBakeTimeOverlay(layer.getBakeTime(), overlay), getBakeTimeColor(layer.getBakeTime(), 0xFFFFFFFF));
            if (layer.getTop().isPresent()) {
                renderCakeLayer(layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(layer.getTop().get().getTextureLocation())), light, overlay, 0xFFFFFFFF);
            }
            for (CakeFeature feature : layer.getFeatures()) {
                if (feature == null) continue;
                CakeFeatureRenderer renderer = CakeFeatureRendererRegistry.get(feature);
                if (renderer != null) {
                    renderer.render(entity, layer, matrices, vertexConsumers, light, overlay);
                }
            }

            matrices.pop();
            matrices.translate(0.0f, layer.getHeight() / 16.0f, 0.0f);
        }
        if (entity.getCachedState().getBlock() instanceof PBCandleCakeBlock block) {
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getCandle().getDefaultState().with(Properties.LIT, entity.getCachedState().get(Properties.LIT)), matrices, vertexConsumers, light, overlay);
        }
    }

    public static void renderCakeLayer(CakeLayer layer, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        float size = layer.getSize();
        float height = layer.getHeight();
        float bites = layer.getBites();

        renderCakeLayer(size, height, bites, matrices, vertexConsumer, light, overlay, color);
    }

    private static void renderCakeLayer(float size, float height, float bites, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        float length = size - bites;
        if (length == 0) return;

        matrices.push();
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f + Math.round((16.0f - size) / 2.0f) + bites, 16.0f, light, overlay, color);

        if (MinecraftClient.isFancyGraphicsOrBetter()) {
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f - size + 16.0f, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 32.0f, 32.0f + 16.0f - height, light, overlay, color);
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f + 16.0f - size, 32.0f + 16.0f - height, light, overlay, color);

            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 32.0f - size, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 32.0f, 32.0f + 16.0f - height, light, overlay, color);
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f + 16.0f - size, 32.0f + 16.0f - height, light, overlay, color);

            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, bites, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 32.0f - size + bites, 32.0f, light, overlay, color);
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 32.0f + bites, 32.0f + 16.0f - height, light, overlay, color);
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f + 16.0f - size + bites, 32.0f + 16.0f - height, light, overlay, color);
        }

        if (bites == 0) {
            PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
            if (MinecraftClient.isFancyGraphicsOrBetter()) {
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 32.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 32.0f - size, 32.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 32.0f, 32.0f + 16.0f - height, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f + 16.0f - size, 32.0f + 16.0f - height, light, overlay, color);
            }
        } else {
            PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 0.0f, light, overlay, color);
            if (MinecraftClient.isFancyGraphicsOrBetter()) {
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 0.0f, 48.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 32.0f - size, 48.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 32.0f, 48.0f + 16.0f - height, light, overlay, color);
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f + 16.0f - size, 48.0f + 16.0f - height, light, overlay, color);
            }
        }

        PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, (16.0f + Math.round(8.0f - size / 2.0f)) + bites, Math.round(8.0f - size / 2.0f), light, overlay, color);
        PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 32.0f + (Math.round(8.0f - size / 2.0f)) + bites, Math.round(8.0f - size / 2.0f), light, overlay, color);
        matrices.pop();
    }

    public static int getBakeTimeColor(int bakeTime, int color) {
        if (bakeTime <= PBCakeBlock.TICKS_UNTIL_BAKED) {
            return color;
        }

        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        float factor = Math.max(-1.0f/(2.0f * PBCakeBlock.TICKS_UNTIL_BAKED) * bakeTime + 1.5f, 0.25f);

        red = (int)MathHelper.clamp(red * factor, 0, 255);
        green = (int)MathHelper.clamp(green * factor, 0, 255);
        blue = (int)MathHelper.clamp(blue * factor, 0, 255);

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getBakeTimeOverlay(int bakeTime, int overlay) {
        if (bakeTime > PBCakeBlock.TICKS_UNTIL_BAKED) {
            return overlay;
        }
        return OverlayTexture.getUv(((float) PBCakeBlock.TICKS_UNTIL_BAKED - bakeTime) / (float)(2.0f * PBCakeBlock.TICKS_UNTIL_BAKED), false);
    }

    @Override
    public boolean rendersOutsideBoundingBox(PBCakeBlockEntity blockEntity) {
        return true;
    }
}
