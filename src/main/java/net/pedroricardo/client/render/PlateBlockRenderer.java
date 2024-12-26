package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.ItemStandBlockEntity;
import net.pedroricardo.client.model.PBModelLayers;

public class PlateBlockRenderer implements BlockEntityRenderer<ItemStandBlockEntity> {
    private final ItemRenderer itemRenderer;
    private final BlockRenderDispatcher blockRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderer;
    private final ModelPart plate;

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition plate = modelPartData.addOrReplaceChild("plate", CubeListBuilder.create().texOffs(-16, 0).addBox(-16.0F, -1.0F, 0.0F, 16.0F, 0.0F, 16.0F, CubeDeformation.NONE)
                .texOffs(0, 53).addBox(-13.0F, -1.0F, 3.0F, 10.0F, 1.0F, 10.0F, CubeDeformation.NONE), PartPose.offset(8.0F, 24.0F, -8.0F));
        return LayerDefinition.create(modelData, 64, 64);
    }

    public PlateBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        ModelPart modelPart = ctx.bakeLayer(PBModelLayers.PLATE);
        this.plate = modelPart.getChild("plate");
        this.itemRenderer = ctx.getItemRenderer();
        this.blockRenderer = ctx.getBlockRenderDispatcher();
        this.blockEntityRenderer = ctx.getBlockEntityRenderDispatcher();
    }

    @Override
    public void render(ItemStandBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        BlockState state = entity.getBlockState();
        matrices.translate(0.5f, 0.5f, 0.5f);
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            matrices.mulPose(Axis.YN.rotationDegrees(state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()));
        }
        matrices.translate(-0.5f, -0.5f, -0.5f);
        matrices.pushPose();
        matrices.scale(-1.0f, -1.0f, 1.0f);
        matrices.translate(-0.5f, -1.5f, 0.5f);
        this.plate.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/plate.png"))), light, overlay);
        matrices.popPose();

        if (entity.getStack().getItem() instanceof BlockItem blockItem) {
            matrices.translate(0.5f, 0.0625f, 0.5f);
            matrices.scale(0.75f, 0.75f, 0.75f);
            matrices.translate(-0.5f, 0.0f, -0.5f);
            BlockState itemState = blockItem.getBlock().defaultBlockState();
            if (itemState.hasBlockEntity()) {
                BlockEntity blockEntity = ((EntityBlock) blockItem.getBlock()).newBlockEntity(entity.getBlockPos(), itemState);
                if (blockEntity != null) {
                    blockEntity.load(entity.getStack().getOrCreateTagElement("BlockEntityTag"));
                    this.blockEntityRenderer.renderItem(blockEntity, matrices, vertexConsumers, light, overlay);
                }
            }
            if (itemState.getRenderShape() == RenderShape.MODEL) {
                this.blockRenderer.renderSingleBlock(itemState, matrices, vertexConsumers, light, overlay);
            }
        } else {
            matrices.translate(0.5f, 0.0859375f, 0.5f);
            matrices.mulPose(Axis.XP.rotationDegrees(90.0f));
            matrices.mulPose(Axis.ZP.rotationDegrees(180.0f));
            matrices.scale(0.5f, 0.5f, 0.5f);
            this.itemRenderer.renderStatic(entity.getStack(), ItemDisplayContext.FIXED, light, overlay, matrices, vertexConsumers, entity.getLevel(), (int) entity.getBlockPos().asLong());
        }
    }
}
