package net.pedroricardo.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.RotationPropertyHelper;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeBlockEntity;
import net.pedroricardo.model.PBModelLayers;

public class CupcakeBlockRenderer implements BlockEntityRenderer<CupcakeBlockEntity> {
    public static final CupcakeBlockEntity RENDER_CUPCAKE = new CupcakeBlockEntity(BlockPos.ORIGIN, PBBlocks.CUPCAKE.getDefaultState());
    private final ModelPart liner;
    private final ModelPart top;
    private static final Identifier EMPTY_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cupcake/empty.png");

    public CupcakeBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart root = ctx.getLayerModelPart(PBModelLayers.CUPCAKE);
        this.liner = root.getChild("liner");
        this.top = root.getChild("top");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData liner = modelPartData.addChild("liner", ModelPartBuilder.create().uv(0, 8).cuboid(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, Dilation.NONE), ModelTransform.pivot(1.0F, 24.0F, -1.0F));
        ModelPartData top = modelPartData.addChild("top", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -3.0F, -1.0F, 4.0F, 3.0F, 4.0F, new Dilation(0.5F)), ModelTransform.pivot(1.0F, 21.0F, -1.0F));
        return TexturedModelData.of(modelData, 16, 16);
    }

    private void setAngles(MatrixStack matrices, float rotationDegrees) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationDegrees));
    }

    @Override
    public void render(CupcakeBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.setAngles(matrices, RotationPropertyHelper.toDegrees(entity.getCachedState().get(Properties.ROTATION)));
        if (entity.getBatter().isEmpty()) {
            this.liner.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(EMPTY_TEXTURE)), light, overlay);
        } else {
            Identifier texture = entity.getBatter().getFlavor().getCupcakeTextureLocation();
            this.liner.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture)), light, overlay);
            this.top.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture)), light, PBCakeBlockRenderer.getBakeTimeOverlay(entity.getBatter().getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(entity.getBatter().getBakeTime(), 0xFFFFFFFF));
            entity.getBatter().getTop().ifPresent(top -> {
                Identifier topTexture = top.getCupcakeTextureLocation();
                this.liner.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(topTexture)), light, overlay);
                this.top.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(topTexture)), light, overlay);
            });
        }
        matrices.pop();
    }
}
