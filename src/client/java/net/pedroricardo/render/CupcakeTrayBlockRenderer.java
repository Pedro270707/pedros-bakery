package net.pedroricardo.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.model.PBModelLayers;

import java.util.List;

public class CupcakeTrayBlockRenderer implements BlockEntityRenderer<CupcakeTrayBlockEntity> {
    public static final CupcakeTrayBlockEntity RENDER_CUPCAKE_TRAY = new CupcakeTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.CUPCAKE_TRAY.getDefaultState());
    private final ModelPart root;

    public CupcakeTrayBlockRenderer(BlockEntityRendererFactory.Context ctx) {
        ModelPart root = ctx.getLayerModelPart(PBModelLayers.CUPCAKE_TRAY);
        this.root = root.getChild(EntityModelPartNames.ROOT);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild(EntityModelPartNames.ROOT, ModelPartBuilder.create().uv(-16, 0).cuboid(-15.0F, -3.0F, -1.0F, 16.0F, 0.0F, 16.0F, Dilation.NONE)
                .uv(0, 16).cuboid(-6.0F, -3.0F, 2.0F, 4.0F, 4.0F, 4.0F, Dilation.NONE)
                .uv(0, 16).cuboid(-12.0F, -3.0F, 2.0F, 4.0F, 4.0F, 4.0F, Dilation.NONE)
                .uv(0, 16).cuboid(-12.0F, -3.0F, 8.0F, 4.0F, 4.0F, 4.0F, Dilation.NONE)
                .uv(0, 16).cuboid(-6.0F, -3.0F, 8.0F, 4.0F, 4.0F, 4.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void render(CupcakeTrayBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.root.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(Identifier.of(PedrosBakery.MOD_ID, "textures/entity/cupcake_tray.png"))), light, overlay);
        matrices.pop();

        CupcakeTrayBatter trayBatter = entity.getBatter();
        List<CakeBatter<FixedBatterSizeContainer>> batterList = trayBatter.stream();

        for (int i = 0; i < batterList.size(); i++) {
            if (batterList.get(i).isEmpty()) continue;
            float x = 3.0f + ((i >> 1 & 1) == 1 ? 6 : 0);
            float y = 3.0f + ((i & 1) == 1 ? 6 : 0);
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(batterList.get(i).getFlavor().getCupcakeTextureLocation())), x, y, 4.0f, 4.0f, 4.0f, 4.0f, 0.0f, 8.0f, 4.0f, 16.0f, 16.0f, light, PBCakeBlockRenderer.getBakeTimeOverlay(batterList.get(i).getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batterList.get(i).getBakeTime(), 0xFFFFFFFF));
        }
    }
}
