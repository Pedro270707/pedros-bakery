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
import net.pedroricardo.block.ItemStandBlock;
import net.pedroricardo.block.entity.ItemStandBlockEntity;
import net.pedroricardo.block.entity.StackReadingBlockEntity;
import net.pedroricardo.model.PBModelLayers;

public class PlateBlockRenderer extends ItemStandBlockRenderer {
    private final ModelPart plate;

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData plate = modelPartData.addChild("plate", ModelPartBuilder.create().uv(-16, 0).cuboid(-16.0F, -1.0F, 0.0F, 16.0F, 0.0F, 16.0F, new Dilation(0.0F))
                .uv(0, 53).cuboid(-13.0F, -1.0F, 3.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    public PlateBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
        ModelPart modelPart = ctx.getLayerModelPart(PBModelLayers.PLATE);
        this.plate = modelPart.getChild("plate");
    }

    @Override
    public void render(ItemStandBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        super.render(entity, tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.plate.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/plate.png"))), light, overlay);
        matrices.pop();
    }
}
