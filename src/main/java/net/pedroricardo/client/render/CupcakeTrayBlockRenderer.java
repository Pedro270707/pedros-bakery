package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.client.model.PBModelLayers;

import java.util.List;

public class CupcakeTrayBlockRenderer implements BlockEntityRenderer<CupcakeTrayBlockEntity> {
    public static final CupcakeTrayBlockEntity RENDER_CUPCAKE_TRAY = new CupcakeTrayBlockEntity(BlockPos.ZERO, PBBlocks.CUPCAKE_TRAY.get().defaultBlockState());
    private final ModelPart root;

    public CupcakeTrayBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart root = ctx.bakeLayer(PBModelLayers.CUPCAKE_TRAY);
        this.root = root.getChild(PartNames.ROOT);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition root = modelPartData.addOrReplaceChild(PartNames.ROOT, CubeListBuilder.create().texOffs(-16, 0).addBox(-15.0F, -3.0F, -1.0F, 16.0F, 0.0F, 16.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-6.0F, -3.0F, 2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-12.0F, -3.0F, 2.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-12.0F, -3.0F, 8.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-6.0F, -3.0F, 8.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(7.0F, 23.0F, -7.0F));
        return LayerDefinition.create(modelData, 32, 32);
    }

    @Override
    public void render(CupcakeTrayBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.root.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/cupcake_tray.png"))), light, overlay);
        matrices.popPose();

        CupcakeTrayBatter trayBatter = entity.getBatter();
        List<CakeBatter<FixedBatterSizeContainer>> batterList = trayBatter.stream();

        for (int i = 0; i < batterList.size(); i++) {
            if (batterList.get(i).isEmpty()) continue;
            float x = 3.0f + ((i >> 1 & 1) == 1 ? 6 : 0);
            float y = 3.0f + ((i & 1) == 1 ? 6 : 0);
            PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderType.entityCutout(batterList.get(i).getFlavor().getCupcakeTextureLocation())), x, y, 4.0f, 4.0f, 4.0f, 4.0f, 0.0f, 8.0f, 4.0f, 16.0f, 16.0f, light, PBCakeBlockRenderer.getBakeTimeOverlay(batterList.get(i).getBakeTime(), overlay), PBCakeBlockRenderer.getBakeTimeColor(batterList.get(i).getBakeTime(), 0xFFFFFFFF));
        }
    }
}
