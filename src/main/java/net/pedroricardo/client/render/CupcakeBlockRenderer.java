package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeBlockEntity;
import net.pedroricardo.client.model.PBModelLayers;

public class CupcakeBlockRenderer implements BlockEntityRenderer<CupcakeBlockEntity> {
    public static final CupcakeBlockEntity RENDER_CUPCAKE = new CupcakeBlockEntity(BlockPos.ZERO, PBBlocks.CUPCAKE.get().defaultBlockState());
    private final ModelPart liner;
    private final ModelPart top;
    private static final ResourceLocation EMPTY_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/cupcake/empty.png");

    public CupcakeBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart root = ctx.bakeLayer(PBModelLayers.CUPCAKE);
        this.liner = root.getChild("liner");
        this.top = root.getChild("top");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition liner = modelPartData.addOrReplaceChild("liner", CubeListBuilder.create().texOffs(0, 8).addBox(-3.0F, -4.0F, -1.0F, 4.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(1.0F, 24.0F, -1.0F));
        PartDefinition top = modelPartData.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -1.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(1.0F, 21.0F, -1.0F));
        return LayerDefinition.create(modelData, 16, 16);
    }

    private void setAngles(PoseStack matrices, float rotationDegrees) {
        matrices.mulPose(Axis.YP.rotationDegrees(rotationDegrees));
    }

    @Override
    public void render(CupcakeBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.setAngles(matrices, RotationSegment.convertToDegrees(entity.getBlockState().getValue(BlockStateProperties.ROTATION_16)));
        if (entity.getBatter().isEmpty()) {
            this.liner.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(EMPTY_TEXTURE)), light, overlay);
        } else {
            ResourceLocation texture = entity.getBatter().getFlavor().getCupcakeTextureLocation();
            this.liner.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(texture)), light, overlay);
            int bakeTimeColor = PBCakeBlockRenderer.getBakeTimeColor(entity.getBatter().getBakeTime(), 0xFFFFFFFF);
            this.top.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(texture)), light, PBCakeBlockRenderer.getBakeTimeOverlay(entity.getBatter().getBakeTime(), overlay), ((bakeTimeColor >> 16) & 0xFF) / 255.0f, ((bakeTimeColor >> 8) & 0xFF) / 255.0f, (bakeTimeColor & 0xFF) / 255.0f, ((bakeTimeColor >> 24) & 0xFF) / 255.0f);
            entity.getBatter().getTop().ifPresent(top -> {
                ResourceLocation topTexture = top.getCupcakeTextureLocation();
                this.liner.render(matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(topTexture)), light, overlay);
                this.top.render(matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(topTexture)), light, overlay);
            });
        }
        matrices.popPose();
    }
}
