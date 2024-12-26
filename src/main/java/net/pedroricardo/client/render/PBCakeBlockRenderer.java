package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fml.ModList;
import net.pedroricardo.PBConfig;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PBCandleCakeBlock;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.client.PedrosBakeryClient;
import net.pedroricardo.client.registry.CakeFeatureRenderer;
import net.pedroricardo.client.registry.CakeFeatureRendererRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PBCakeBlockRenderer implements BlockEntityRenderer<PBCakeBlockEntity> {
    public static final PBCakeBlockEntity RENDER_CAKE = new PBCakeBlockEntity(BlockPos.ZERO, PBBlocks.CAKE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));

    @Override
    public void render(PBCakeBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        renderCake(entity, matrices, vertexConsumers, light, overlay);
    }

    public static void renderCake(PBCakeBlockEntity entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState state = entity.hasLevel() ? entity.getBlockState() : PBBlocks.CAKE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        if (state == null || entity.isRemoved()) return; // entity.isRemoved() here seems more like a hack, because it shouldn't even be here if it is removed. TODO: investigate why removed cakes are still rendered

        matrices.translate(0.5f, 0.5f, 0.5f);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            matrices.mulPose(Axis.YN.rotationDegrees(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        }
        matrices.translate(-0.5f, -0.5f, -0.5f);

        boolean irisFix = PedrosBakeryClient.isRenderingInWorld && (ModList.get().isLoaded("iris") || ModList.get().isLoaded("oculus"));
        float height = 0.0f;
        for (CakeBatter<FullBatterSizeContainer> layer : entity.getBatterList()) {
            matrices.pushPose();

            renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(RenderType.entityCutout(layer.getFlavor().getCakeTextureLocation())), light, getBakeTimeOverlay(layer.getBakeTime(), overlay), getBakeTimeColor(layer.getBakeTime(), 0xFFFFFFFF));
            if (layer.getTop().isPresent()) {
                renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(layer.getTop().get().getCakeTextureLocation())), light, overlay, 0xFFFFFFFF);
            }

            for (int i = 0; i < layer.getFeatures().size(); i++) {
                CakeFeature feature = layer.getFeatures().get(i);
                if (feature == null) continue;
                CakeFeatureRenderer renderer = CakeFeatureRendererRegistry.get(feature);
                if (renderer != null) {
                    if (irisFix && renderer.needsIrisFix()) {
                        matrices.pushPose();
                        matrices.translate(0.5f + layer.getSizeContainer().getBites() / 32.0f, layer.getSizeContainer().getHeight() / 32.0f, 0.5f);
                        float scaleMultiplier = 1.0f;
                        if (Minecraft.getInstance().cameraEntity != null && entity.hasLevel()) {
                            double d = Minecraft.getInstance().cameraEntity.getX() - (entity.getBlockPos().getX() + 0.5);
                            double e = Minecraft.getInstance().cameraEntity.getY() - (entity.getBlockPos().getY() + height + layer.getSizeContainer().getHeight() / 2.0f);
                            double f = Minecraft.getInstance().cameraEntity.getZ() - (entity.getBlockPos().getZ() + 0.5);
                            scaleMultiplier = Mth.sqrt((float) (d * d + e * e + f * f));
                        }
                        float scaleWidth = 1.0f / 1024.0f * (i + 1) * scaleMultiplier / (layer.getSizeContainer().getSize());
                        float scaleHeight = 1.0f / 1024.0f * (i + 1) * scaleMultiplier / (layer.getSizeContainer().getHeight() / 4.0f);
                        matrices.scale(1.0f + scaleWidth, 1.0f + scaleHeight, 1.0f + scaleWidth);
                        matrices.translate(-0.5f - layer.getSizeContainer().getBites() / 32.0f, -layer.getSizeContainer().getHeight() / 32.0f, -0.5f);
                    }
                    renderer.render(feature, entity, layer, matrices, vertexConsumers, light, overlay);
                    if (irisFix && renderer.needsIrisFix()) {
                        matrices.popPose();
                    }
                }
            }

            matrices.popPose();
            matrices.translate(0.0f, layer.getSizeContainer().getHeight() / 16.0f, 0.0f);
            height += layer.getSizeContainer().getHeight() / 16.0f;
        }
        if (entity.getBlockState().getBlock() instanceof PBCandleCakeBlock block) {
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(block.getCandle().defaultBlockState().setValue(BlockStateProperties.LIT, entity.getBlockState().getValue(BlockStateProperties.LIT)), matrices, vertexConsumers, light, overlay);
        }
    }

    public static void renderCakeBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, CakeBatter<FullBatterSizeContainer> batter, PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        renderCakeBatter(batterList, batter, matrices, vertexConsumer, light, overlay, color, PedrosBakery.CONFIG.cakeRenderQuality.get());
    }

    public static void renderCakeBatter(List<CakeBatter<FullBatterSizeContainer>> batterList, CakeBatter<FullBatterSizeContainer> batter, PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, PBConfig.CakeRenderQuality quality) {
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

        matrices.pushPose();
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
        matrices.popPose();
    }

    public static int getBakeTimeColor(int bakeTime, int color) {
        if (bakeTime <= PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
            return color;
        }

        int alpha = color >> 24 & 0xFF;
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;

        float factor = Math.max(-1.0f/(2.0f * PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) * bakeTime + 1.5f, 0.25f);

        red = (int) Mth.clamp(red * factor, 0, 255);
        green = (int) Mth.clamp(green * factor, 0, 255);
        blue = (int) Mth.clamp(blue * factor, 0, 255);

        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int getBakeTimeOverlay(int bakeTime, int overlay) {
        if (bakeTime > PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) {
            return overlay;
        }
        return OverlayTexture.pack(((float) PedrosBakery.CONFIG.ticksUntilCakeBaked.get() - bakeTime) / (2.0f * PedrosBakery.CONFIG.ticksUntilCakeBaked.get()), false);
    }

    @Override
    public boolean shouldRenderOffScreen(PBCakeBlockEntity blockEntity) {
        return true;
    }
}
