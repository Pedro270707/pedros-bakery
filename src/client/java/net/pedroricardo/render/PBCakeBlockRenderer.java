package net.pedroricardo.render;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.pedroricardo.PBConfigModel;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.PedrosBakeryClient;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PBCandleCakeBlock;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.size.FullBatterSizeContainer;
import net.pedroricardo.registry.CakeFeatureRenderer;
import net.pedroricardo.registry.CakeFeatureRendererRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PBCakeBlockRenderer implements BlockEntityRenderer<PBCakeBlockEntity> {
    public static final PBCakeBlockEntity RENDER_CAKE = new PBCakeBlockEntity(BlockPos.ORIGIN, PBBlocks.CAKE.getDefaultState());

    @Override
    public void render(PBCakeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        renderCake(entity, matrices, vertexConsumers, light, overlay);
    }

    public static void renderCake(PBCakeBlockEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        if (state == null || entity.isRemoved()) return; // entity.isRemoved() here seems more like a hack, because it shouldn't even be here if it is removed. TODO: investigate why removed cakes are still rendered

        boolean irisFix = PedrosBakeryClient.isRenderingInWorld && FabricLoader.getInstance().isModLoaded("iris");
        float height = 0.0f;
        for (CakeBatter<FullBatterSizeContainer> layer : entity.getBatterList()) {
            matrices.push();
            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(state.get(Properties.HORIZONTAL_FACING).asRotation()));
            matrices.translate(-0.5f, -0.5f, -0.5f);

            renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(layer.getFlavor().getCakeTextureLocation())), light, getBakeTimeOverlay(layer.getBakeTime(), overlay), getBakeTimeColor(layer.getBakeTime(), 0xFFFFFFFF));
            if (layer.getTop().isPresent()) {
                renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(layer.getTop().get().getCakeTextureLocation())), light, overlay, 0xFFFFFFFF);
            }

            for (int i = 0; i < layer.getFeatures().size(); i++) {
                CakeFeature feature = layer.getFeatures().get(i);
                if (feature == null) continue;
                CakeFeatureRenderer renderer = CakeFeatureRendererRegistry.get(feature);
                if (renderer != null) {
                    if (irisFix && renderer.needsIrisFix()) {
                        matrices.push();
                        matrices.translate(0.5f + layer.getSizeContainer().getBites() / 32.0f, layer.getSizeContainer().getHeight() / 32.0f, 0.5f);
                        float scaleMultiplier = 1.0f;
                        if (MinecraftClient.getInstance().cameraEntity != null && entity.hasWorld()) {
                            double d = MinecraftClient.getInstance().cameraEntity.getX() - (entity.getPos().getX() + 0.5);
                            double e = MinecraftClient.getInstance().cameraEntity.getY() - (entity.getPos().getY() + height + layer.getSizeContainer().getHeight() / 2.0f);
                            double f = MinecraftClient.getInstance().cameraEntity.getZ() - (entity.getPos().getZ() + 0.5);
                            scaleMultiplier = MathHelper.sqrt((float) (d * d + e * e + f * f));
                        }
                        float scaleWidth = 1.0f / 1024.0f * (i + 1) * scaleMultiplier / (layer.getSizeContainer().getSize());
                        float scaleHeight = 1.0f / 1024.0f * (i + 1) * scaleMultiplier / (layer.getSizeContainer().getHeight() / 4.0f);
                        matrices.scale(1.0f + scaleWidth, 1.0f + scaleHeight, 1.0f + scaleWidth);
                        matrices.translate(-0.5f - layer.getSizeContainer().getBites() / 32.0f, -layer.getSizeContainer().getHeight() / 32.0f, -0.5f);
                    }
                    renderer.render(feature, entity, layer, matrices, vertexConsumers, light, overlay);
                    if (irisFix && renderer.needsIrisFix()) {
                        matrices.pop();
                    }
                }
            }

            matrices.pop();
            matrices.translate(0.0f, layer.getSizeContainer().getHeight() / 16.0f, 0.0f);
            height += layer.getSizeContainer().getHeight() / 16.0f;
        }
        if (entity.getCachedState().getBlock() instanceof PBCandleCakeBlock block) {
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(block.getCandle().getDefaultState().with(Properties.LIT, entity.getCachedState().get(Properties.LIT)), matrices, vertexConsumers, light, overlay);
        }
    }

    public static void renderCakeBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, CakeBatter<FullBatterSizeContainer> batter, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        renderCakeBatter(batterList, batter, matrices, vertexConsumer, light, overlay, color, PedrosBakery.CONFIG.cakeRenderQuality());
    }

    public static void renderCakeBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, CakeBatter<FullBatterSizeContainer> batter, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, PBConfigModel.CakeRenderQuality quality) {
        float size = batter.getSizeContainer().getSize();
        float height = batter.getSizeContainer().getHeight();
        float bites = batter.getSizeContainer().getBites();

        float length = size - bites;
        if (length == 0) return;
        @Nullable CakeBatter<FullBatterSizeContainer> layerOnTop = null;
        @Nullable CakeBatter<FullBatterSizeContainer> layerUnder = null;
        int index = -1;
        for (int i = 0; i < batterList.size(); i++) {
            if (batterList.get(i) == batter) {
                index = i;
            }
        }
        if (index != -1) {
            if (index < batterList.size() - 1) {
                layerOnTop = batterList.get(index + 1);
            }
            if (index > 0) {
                layerUnder = batterList.get(index - 1);
            }
        }

        matrices.push();
        float halfSizeDifference = Math.round((16.0f - size) / 2.0f);
        float halfHeightDifference = Math.round((16.0f - height) / 2.0f);
        PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference, 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, halfSizeDifference, 16.0f, light, overlay, color);
        PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f + halfSizeDifference + bites, 16.0f, light, overlay, color);

        if (quality.renderSideBorders()) {
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference, 32.0f, light, overlay, color); // Top
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference, 80.0f - height, light, overlay, color); // Bottom
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 48.0f + halfHeightDifference, light, overlay, color); // Left
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 48.0f + halfHeightDifference, light, overlay, color); // Right
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 32.0f, light, overlay, color); // Top Left
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 32.0f, light, overlay, color); // Top Right
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 64.0f + 16.0f - height, light, overlay, color); // Bottom Left
            PBRenderHelper.createFace(Direction.NORTH, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 64.0f + 16.0f - height, light, overlay, color); // Bottom Right

            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 16.0f + halfSizeDifference, 32.0f, light, overlay, color); // Top
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 16.0f + halfSizeDifference, 80.0f - height, light, overlay, color); // Bottom
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0.0f, 48.0f + halfHeightDifference, light, overlay, color); // Left
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 48.0f + halfHeightDifference, light, overlay, color); // Right
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0, 32.0f, light, overlay, color); // Top Left
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 32.0f, light, overlay, color); // Top Right
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 0.0f, 64.0f + 16.0f - height, light, overlay, color); // Bottom Left
            PBRenderHelper.createFace(Direction.EAST, matrices, vertexConsumer, -8.0f - size / 2.0f, -height, 8.0f + size / 2.0f, size, height, 48.0f - size, 64.0f + 16.0f - height, light, overlay, color); // Bottom Right

            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference + bites, 32.0f, light, overlay, color); // Top
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference + bites, 80.0f - height, light, overlay, color); // Bottom
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, bites, 48.0f + halfHeightDifference, light, overlay, color); // Left
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f - size + bites, 48.0f + halfHeightDifference, light, overlay, color); // Right
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, bites, 32.0f, light, overlay, color); // Top Left
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f - size + bites, 32.0f, light, overlay, color); // Top Right
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, bites, 64.0f + 16.0f - height, light, overlay, color); // Bottom Left
            PBRenderHelper.createFace(Direction.SOUTH, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -height, 8.0f + size / 2.0f, length, height, 48.0f - size + bites, 64.0f + 16.0f - height, light, overlay, color); // Bottom Right
        }

        if (bites == 0) {
            PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 32.0f + Math.round((16.0f - size) / 2.0f), 16.0f, light, overlay, color);
            if (quality.renderSideBorders()) {
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference, 32.0f, light, overlay, color); // Top
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 16.0f + halfSizeDifference, 80.0f - height, light, overlay, color); // Bottom
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 48.0f + halfHeightDifference, light, overlay, color); // Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 48.0f + halfHeightDifference, light, overlay, color); // Right
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 32.0f, light, overlay, color); // Top Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 32.0f, light, overlay, color); // Top Right
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 0.0f, 64.0f + 16.0f - height, light, overlay, color); // Bottom Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f, length, height, 48.0f - size, 64.0f + 16.0f - height, light, overlay, color); // Bottom Right
            }
        } else {
            PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f + Math.round((16.0f - size) / 2.0f), 0.0f, light, overlay, color);
            if (quality.renderSideBorders()) {
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 64.0f + halfSizeDifference, 32.0f, light, overlay, color); // Top
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 64.0f + halfSizeDifference, 80.0f - height, light, overlay, color); // Bottom
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f, 48.0f + halfHeightDifference, light, overlay, color); // Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 96.0f - size, 48.0f + halfHeightDifference, light, overlay, color); // Right
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f, 32.0f, light, overlay, color); // Top Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 96.0f - size, 32.0f, light, overlay, color); // Top Right
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 48.0f, 64.0f + 16.0f - height, light, overlay, color); // Bottom Left
                PBRenderHelper.createFace(Direction.WEST, matrices, vertexConsumer, 8.0f - size / 2.0f, -height, -8.0f + size / 2.0f - bites, size, height, 96.0f + 16.0f - size, 64.0f + 16.0f - height, light, overlay, color); // Bottom Right
            }
        }


        if (layerOnTop == null || layerOnTop.getSizeContainer().getSize() < batter.getSizeContainer().getSize() || (layerOnTop.getSizeContainer().getSize() / 2.0f) - layerOnTop.getSizeContainer().getBites() < (batter.getSizeContainer().getSize() / 2.0f) - batter.getSizeContainer().getBites()) {
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, (16.0f + Math.round(8.0f - size / 2.0f)) + bites, Math.round(8.0f - size / 2.0f), light, overlay, color);
            if (quality.renderTopBorder()) {
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, 16.0f + halfSizeDifference + bites, 80.0f, light, overlay, color); // Top
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, 16.0f + halfSizeDifference + bites, 128.0f - size, light, overlay, color); // Bottom
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, bites, 96.0f + halfSizeDifference, light, overlay, color); // Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, 48.0f - size + bites, 96.0f + halfSizeDifference, light, overlay, color); // Right
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, bites, 80.0f, light, overlay, color); // Top Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, 48.0f - size + bites, 80.0f, light, overlay, color); // Top Right
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, bites, 128.0f - size, light, overlay, color); // Bottom Left
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, 8.0f - size / 2.0f, height, length, size, 48.0f - size + bites, 128.0f - size, light, overlay, color); // Bottom Right
            }
        }

        if (layerUnder == null || layerUnder.getSizeContainer().getSize() < batter.getSizeContainer().getSize() || (layerUnder.getSizeContainer().getSize() / 2.0f) - layerUnder.getSizeContainer().getBites() < (batter.getSizeContainer().getSize() / 2.0f) - batter.getSizeContainer().getBites()) {
            PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 32.0f + (Math.round(8.0f - size / 2.0f)) + bites, Math.round(8.0f - size / 2.0f), light, overlay, color);
            if (quality.renderBottomBorder()) {
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 64.0f + halfSizeDifference + bites, 80.0f, light, overlay, color); // Top
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 64.0f + halfSizeDifference + bites, 128.0f - size, light, overlay, color); // Bottom
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 48.0f + bites, 96.0f + halfSizeDifference, light, overlay, color); // Left
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 96.0f - size + bites, 96.0f + halfSizeDifference, light, overlay, color); // Right
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 48.0f + bites, 80.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 96.0f - size + bites, 80.0f, light, overlay, color);
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 48.0f + bites, 128.0f - size, light, overlay, color);
                PBRenderHelper.createFace(Direction.DOWN, matrices, vertexConsumer, 8.0f - size / 2.0f + bites, -8.0f - size / 2.0f, 0.0f, length, size, 96.0f - size + bites, 128.0f - size, light, overlay, color);
            }
        }
        matrices.pop();
    }

    public static int getBakeTimeColor(int bakeTime, int color) {
        if (bakeTime <= PedrosBakery.CONFIG.ticksUntilBaked()) {
            return color;
        }

        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        float factor = Math.max(-1.0f/(2.0f * PedrosBakery.CONFIG.ticksUntilBaked()) * bakeTime + 1.5f, 0.25f);

        red = (int)MathHelper.clamp(red * factor, 0, 255);
        green = (int)MathHelper.clamp(green * factor, 0, 255);
        blue = (int)MathHelper.clamp(blue * factor, 0, 255);

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getBakeTimeOverlay(int bakeTime, int overlay) {
        if (bakeTime > PedrosBakery.CONFIG.ticksUntilBaked()) {
            return overlay;
        }
        return OverlayTexture.getUv(((float) PedrosBakery.CONFIG.ticksUntilBaked() - bakeTime) / (2.0f * PedrosBakery.CONFIG.ticksUntilBaked()), false);
    }

    @Override
    public boolean rendersOutsideBoundingBox(PBCakeBlockEntity blockEntity) {
        return true;
    }
}
