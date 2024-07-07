package net.pedroricardo.render;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.CakeStandBlockEntity;
import net.pedroricardo.model.PBModelLayers;
import org.joml.Quaternionf;

public class CakeStandBlockRenderer implements BlockEntityRenderer<CakeStandBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderer;
    private final ModelPart dome;
    private final ModelPart plate;

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData dome = modelPartData.addChild("dome", ModelPartBuilder.create().uv(0, 16).cuboid(-15.0F, -16.0F, 1.0F, 14.0F, 15.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));
        ModelPartData plate = modelPartData.addChild("plate", ModelPartBuilder.create().uv(-16, 0).cuboid(-16.0F, -1.0F, 0.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 53).cuboid(-13.0F, -1.0F, 3.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public CakeStandBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart modelPart = ctx.getLayerModelPart(PBModelLayers.CAKE_STAND);
        this.dome = modelPart.getChild("dome");
        this.plate = modelPart.getChild("plate");
        this.itemRenderer = ctx.getItemRenderer();
        this.blockRenderer = ctx.getRenderManager();
    }

    @Override
    public void render(CakeStandBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.dome.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cake_stand.png"))), light, overlay);
        this.plate.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cake_stand.png"))), light, overlay);
        matrices.pop();

        if (entity.getItem().getItem() instanceof BlockItem blockItem) {
            matrices.translate(0.5f, 0.0625f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(-0.5f, 0.0f, -0.5f);
            BlockState state = blockItem.getBlock().getDefaultState();
            if (state.hasBlockEntity()) {
                BlockEntity blockEntity = ((BlockEntityProvider) blockItem.getBlock()).createBlockEntity(entity.getPos(), state);
                if (blockEntity != null) {
                    blockEntity.readComponents(entity.getItem());
                    MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
                }
            } else {
                MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(state, matrices, vertexConsumers, light, overlay);
            }
        } else {
            matrices.translate(0.5f, 0.5f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            this.itemRenderer.renderItem(entity.getItem(), ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), (int) entity.getPos().asLong());
        }
    }
}
