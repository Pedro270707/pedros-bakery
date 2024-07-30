package net.pedroricardo.render;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.ItemStandBlockEntity;
import net.pedroricardo.model.PBModelLayers;

public class CakeStandBlockRenderer implements BlockEntityRenderer<ItemStandBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderManager blockRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderer;
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
        this.blockEntityRenderer = ctx.getRenderDispatcher();
    }

    @Override
    public void render(ItemStandBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        BlockState state = entity.getCachedState();
        matrices.translate(0.5f, 0.5f, 0.5f);
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(state.get(Properties.HORIZONTAL_FACING).asRotation()));
        }
        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.dome.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cake_stand.png"))), light, overlay);
        this.plate.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cake_stand.png"))), light, overlay);
        matrices.pop();

        if (entity.getStack().getItem() instanceof BlockItem blockItem) {
            matrices.translate(0.5f, 0.0625f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(-0.5f, 0.0f, -0.5f);
            BlockState itemState = blockItem.getBlock().getDefaultState();
            if (itemState.hasBlockEntity()) {
                BlockEntity blockEntity = ((BlockEntityProvider) blockItem.getBlock()).createBlockEntity(entity.getPos(), itemState);
                if (blockEntity != null) {
                    blockEntity.readComponents(entity.getStack());
                    this.blockEntityRenderer.renderEntity(blockEntity, matrices, vertexConsumers, light, overlay);
                }
            }
            if (itemState.getRenderType() == BlockRenderType.MODEL) {
                this.blockRenderer.renderBlockAsEntity(itemState, matrices, vertexConsumers, light, overlay);
            }
        } else {
            matrices.translate(0.5f, 0.0859375f, 0.5f);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            matrices.scale(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderItem(entity.getStack(), ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), (int) entity.getPos().asLong());
        }
    }
}
