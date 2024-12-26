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
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.block.extras.beater.Liquid;
import net.pedroricardo.client.model.PBModelLayers;

public class BeaterBlockRenderer implements BlockEntityRenderer<BeaterBlockEntity> {
    private final ModelPart base;
    private final ModelPart top;
    private final ModelPart bowl;
    private final ModelPart whisk;
    private final ItemRenderer itemRenderer;

    public BeaterBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelPart = ctx.bakeLayer(PBModelLayers.BEATER);
        this.base = modelPart.getChild("base");
        this.bowl = modelPart.getChild("bowl");
        this.top = modelPart.getChild("top");
        this.whisk = this.top.getChild("whisk");
        this.itemRenderer = ctx.getItemRenderer();
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition base = modelPartData.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 1.0F, 10.0F, CubeDeformation.NONE)
                .texOffs(32, 18).addBox(-7.0F, -11.0F, -2.0F, 3.0F, 11.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition bowl = modelPartData.addOrReplaceChild("bowl", CubeListBuilder.create().texOffs(0, 22).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 6.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition top = modelPartData.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 11).addBox(-2.0F, -3.0F, -3.0F, 12.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offset(-6.0F, 13.0F, 0.0F));

        PartDefinition whisk = top.addOrReplaceChild("whisk", CubeListBuilder.create().texOffs(0, -2).addBox(0.0F, 0.0F, -1.0F, 0.0F, 4.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(4, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(-2, 4).addBox(-1.0F, 4.0F, -1.0F, 2.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(7.0F, 2.0F, 0.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    @Override
    public void render(BeaterBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!entity.hasLevel()) return;
        BlockState state = entity.getBlockState();
        matrices.pushPose();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            matrices.mulPose(Axis.YP.rotationDegrees(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        }
        float rotation = entity.getPoweredTicks(tickDelta) * 45.0f * 3.1415927f / 180.0f;
        this.top.xRot = 0.0f;
        this.base.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        this.whisk.yRot = rotation;
        this.top.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCull(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        this.bowl.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), light, overlay);
        matrices.popPose();
        if (entity.hasLiquid()) {
            if (entity.getLiquid().getType() == Liquid.Type.MILK) {
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderType.entityCutout(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/beater.png"))), 4, 4, 6, 8, 8, 0, 36, 8, 44, 64, 64, light, overlay, 0xFFFFFFFF);
            } else if (entity.getLiquid().getType() == Liquid.Type.FROSTING) {
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderType.entityCutout(((Liquid.Frosting) entity.getLiquid()).top().getCakeTextureLocation())), 4, 4, 6, 8, 8, 20, 4, light, overlay, 0xFFFFFFFF);
            } else if (entity.getLiquid().getType() == Liquid.Type.MIXTURE) {
                PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderType.entityCutout(((Liquid.Mixture) entity.getLiquid()).flavor().getCakeTextureLocation())), 4, 4, 6, 8, 8, 20, 4, light, OverlayTexture.pack(1.0f / 4.0f, false), 0xFFFFFFFF);
            }
        }
        if (entity.hasLevel() && entity.hasLiquid()) {
            matrices.translate(0.5f, 0.375f, 0.5f);
            matrices.scale(0.375f, 0.375f, 0.375f);
            matrices.mulPose(Axis.YN.rotation(rotation));
            matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
            for (ItemStack stack : entity.getItems()) {
                matrices.translate(0.0f, 0.0f, -0.0625f);
                this.itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, entity.getLevel(), (int) entity.getBlockPos().asLong());
            }
        }
    }
}
