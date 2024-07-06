package net.pedroricardo.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.BeaterBlock;
import net.pedroricardo.block.BeaterLiquids;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.model.PBModelLayers;

public class BeaterBlockRenderer implements BlockEntityRenderer<BeaterBlockEntity> {
    private final ModelPart base;
    private final ModelPart top;
    private final ModelPart bowl;
    private final ModelPart whisk;
    private final ItemRenderer itemRenderer;

    public BeaterBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart modelPart = ctx.getLayerModelPart(PBModelLayers.BEATER);
        this.base = modelPart.getChild("base");
        this.bowl = modelPart.getChild("bowl");
        this.top = modelPart.getChild("top");
        this.whisk = this.top.getChild("whisk");
        this.itemRenderer = ctx.getItemRenderer();
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create().uv(0, 0).cuboid(-5.0F, -1.0F, -5.0F, 10.0F, 1.0F, 10.0F, new Dilation(0.0F))
                .uv(32, 18).cuboid(-7.0F, -11.0F, -2.0F, 3.0F, 11.0F, 4.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData bowl = modelPartData.addChild("bowl", ModelPartBuilder.create().uv(0, 22).cuboid(-4.0F, -7.0F, -4.0F, 8.0F, 6.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData top = modelPartData.addChild("top", ModelPartBuilder.create().uv(0, 11).cuboid(-2.0F, -3.0F, -3.0F, 12.0F, 5.0F, 6.0F, new Dilation(0.0F)), ModelTransform.pivot(-6.0F, 13.0F, 0.0F));

        ModelPartData whisk = top.addChild("whisk", ModelPartBuilder.create().uv(0, -2).cuboid(0.0F, 0.0F, -1.0F, 0.0F, 4.0F, 2.0F, new Dilation(0.0F))
                .uv(4, 0).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, new Dilation(0.0F))
                .uv(-2, 4).cuboid(-1.0F, 4.0F, -1.0F, 2.0F, 0.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, 2.0F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(BeaterBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!entity.hasWorld()) return;
        BlockState state = entity.getCachedState();
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.get(Properties.HORIZONTAL_FACING).asRotation()));
        }
        float rotation = entity.getPoweredTicks(tickDelta) * 45.0f * 3.1415927f / 180.0f;
        this.top.pitch = 0.0f;
        this.base.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        this.whisk.yaw = rotation;
        this.top.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        this.bowl.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        matrices.pop();
        if (state.get(BeaterBlock.LIQUID) == BeaterLiquids.MILK) {
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), 4, 4, 6, 8, 8, 0, 36, 8, 44, 64, 64, light, overlay, 0xFFFFFFFF);
        } else if (state.get(BeaterBlock.LIQUID) == BeaterLiquids.FROSTING && entity.getTop() != null) {
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getTop().getTextureLocation())), 4, 4, 6, 8, 8, 20, 4, light, overlay, 0xFFFFFFFF);
        } else if (state.get(BeaterBlock.LIQUID) == BeaterLiquids.MIXTURE && entity.getFlavor() != null) {
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(entity.getFlavor().getCakeTextureLocation())), 4, 4, 6, 8, 8, 20, 4, light, OverlayTexture.getUv(1.0f/4.0f, false), 0xFFFFFFFF);
        }
        if (entity.hasWorld() && state.get(BeaterBlock.LIQUID) != BeaterLiquids.EMPTY) {
            matrices.translate(0.5f, 0.375f, 0.5f);
            matrices.scale(0.375f, 0.375f, 0.375f);
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotation(rotation));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0f));
            this.itemRenderer.renderItem(entity.getItem(), ModelTransformationMode.FIXED, light, overlay, matrices, vertexConsumers, entity.getWorld(), (int)entity.getPos().asLong());
        }
    }
}
