package net.pedroricardo.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PieBlockEntity;
import net.pedroricardo.client.PBClientHelpers;
import net.pedroricardo.client.model.PBModelLayers;
import net.pedroricardo.item.PBItems;

public class PieBlockRenderer implements BlockEntityRenderer<PieBlockEntity> {
	public static final ResourceLocation UNRAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/unraised.png");
	public static final ResourceLocation SLIGHTLY_RAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/slightly_raised.png");
	public static final ResourceLocation FULLY_RAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/fully_raised.png");
	public static final ResourceLocation PIE_FLAVOR_UNRAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_unraised.png");
	public static final ResourceLocation PIE_FLAVOR_SLIGHTLY_RAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_slightly_raised.png");
	public static final ResourceLocation PIE_FLAVOR_FULLY_RAISED_TEXTURE = new ResourceLocation(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_fully_raised.png");
	public static final PieBlockEntity RENDER_PIE = new PieBlockEntity(BlockPos.ZERO, PBBlocks.PIE.get().defaultBlockState());

	private final ModelPart[] unraisedBottom;
	private final ModelPart[] unraisedFilling;
	private final ModelPart[] unraisedTop;
	private final ModelPart unraisedPan;
	private final ModelPart[] slightlyRaisedBottom;
	private final ModelPart[] slightlyRaisedFilling;
	private final ModelPart[] slightlyRaisedTop;
	private final ModelPart slightlyRaisedPan;
	private final ModelPart[] fullyRaisedBottom;
	private final ModelPart[] fullyRaisedFilling;
	private final ModelPart[] fullyRaisedTop;
	private final ModelPart fullyRaisedPan;

	public PieBlockRenderer(BlockEntityRendererProvider.Context ctx) {
		ModelPart unraisedModelPart = ctx.bakeLayer(PBModelLayers.UNRAISED_PIE);
		this.unraisedPan = unraisedModelPart.getChild("pan");
		this.unraisedBottom = new ModelPart[4];
		this.unraisedFilling = new ModelPart[4];
		this.unraisedTop = new ModelPart[4];
		for (int i = 0; i < 4; i++) {
			this.unraisedBottom[i] = unraisedModelPart.getChild("bottom_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.unraisedFilling[i] = unraisedModelPart.getChild("filling_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.unraisedTop[i] = unraisedModelPart.getChild("top_slice_" + i);
		}

		ModelPart slightlyRaisedModelPart = ctx.bakeLayer(PBModelLayers.SLIGHTLY_RAISED_PIE);
		this.slightlyRaisedPan = slightlyRaisedModelPart.getChild("pan");
		this.slightlyRaisedBottom = new ModelPart[4];
		this.slightlyRaisedFilling = new ModelPart[4];
		this.slightlyRaisedTop = new ModelPart[4];
		for (int i = 0; i < 4; i++) {
			this.slightlyRaisedBottom[i] = slightlyRaisedModelPart.getChild("bottom_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.slightlyRaisedFilling[i] = slightlyRaisedModelPart.getChild("filling_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.slightlyRaisedTop[i] = slightlyRaisedModelPart.getChild("top_slice_" + i);
		}

		ModelPart fullyRaisedModelPart = ctx.bakeLayer(PBModelLayers.FULLY_RAISED_PIE);
		this.fullyRaisedPan = fullyRaisedModelPart.getChild("pan");
		this.fullyRaisedBottom = new ModelPart[4];
		this.fullyRaisedFilling = new ModelPart[4];
		this.fullyRaisedTop = new ModelPart[4];
		for (int i = 0; i < 4; i++) {
			this.fullyRaisedBottom[i] = fullyRaisedModelPart.getChild("bottom_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.fullyRaisedFilling[i] = fullyRaisedModelPart.getChild("filling_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.fullyRaisedTop[i] = fullyRaisedModelPart.getChild("top_slice_" + i);
		}
	}

	public static LayerDefinition getUnraisedTexturedMeshDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bottom_slice_0 = modelPartData.addOrReplaceChild("bottom_slice_0", CubeListBuilder.create().texOffs(28, 0).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, 1.0F));
		PartDefinition bottom_slice_1 = modelPartData.addOrReplaceChild("bottom_slice_1", CubeListBuilder.create().texOffs(28, 8).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_2 = modelPartData.addOrReplaceChild("bottom_slice_2", CubeListBuilder.create().texOffs(28, 16).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_3 = modelPartData.addOrReplaceChild("bottom_slice_3", CubeListBuilder.create().texOffs(28, 24).addBox(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 24.0F, 1.0F));
		PartDefinition filling_slice_0 = modelPartData.addOrReplaceChild("filling_slice_0", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition filling_slice_1 = modelPartData.addOrReplaceChild("filling_slice_1", CubeListBuilder.create().texOffs(0, 10).addBox(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_2 = modelPartData.addOrReplaceChild("filling_slice_2", CubeListBuilder.create().texOffs(0, 20).addBox(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_3 = modelPartData.addOrReplaceChild("filling_slice_3", CubeListBuilder.create().texOffs(0, 30).addBox(1.0F, -2.0F, 0.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 22.0F, 0.0F));
		PartDefinition top_slice_0 = modelPartData.addOrReplaceChild("top_slice_0", CubeListBuilder.create().texOffs(56, 0).addBox(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition top_slice_1 = modelPartData.addOrReplaceChild("top_slice_1", CubeListBuilder.create().texOffs(56, 10).addBox(-6.0F, -3.0F, 0.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -7.0F));
		PartDefinition top_slice_2 = modelPartData.addOrReplaceChild("top_slice_2", CubeListBuilder.create().texOffs(56, 20).addBox(-7.0F, -3.0F, 0.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(7.0F, 23.0F, -7.0F));
		PartDefinition top_slice_3 = modelPartData.addOrReplaceChild("top_slice_3", CubeListBuilder.create().texOffs(56, 30).addBox(0.0F, -2.0F, 0.0F, 7.0F, 3.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 22.0F, 0.0F));
		PartDefinition pan = modelPartData.addOrReplaceChild("pan", CubeListBuilder.create().texOffs(0, 110).addBox(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	public static LayerDefinition getSlightlyRaisedTexturedMeshDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bottom_slice_0 = modelPartData.addOrReplaceChild("bottom_slice_0", CubeListBuilder.create().texOffs(28, 0).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, 1.0F));
		PartDefinition bottom_slice_1 = modelPartData.addOrReplaceChild("bottom_slice_1", CubeListBuilder.create().texOffs(28, 8).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_2 = modelPartData.addOrReplaceChild("bottom_slice_2", CubeListBuilder.create().texOffs(28, 16).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_3 = modelPartData.addOrReplaceChild("bottom_slice_3", CubeListBuilder.create().texOffs(28, 24).addBox(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 24.0F, 1.0F));
		PartDefinition filling_slice_0 = modelPartData.addOrReplaceChild("filling_slice_0", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition filling_slice_1 = modelPartData.addOrReplaceChild("filling_slice_1", CubeListBuilder.create().texOffs(0, 11).addBox(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_2 = modelPartData.addOrReplaceChild("filling_slice_2", CubeListBuilder.create().texOffs(0, 22).addBox(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_3 = modelPartData.addOrReplaceChild("filling_slice_3", CubeListBuilder.create().texOffs(0, 33).addBox(1.0F, -3.0F, 0.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 22.0F, 0.0F));
		PartDefinition top_slice_0 = modelPartData.addOrReplaceChild("top_slice_0", CubeListBuilder.create().texOffs(56, 0).addBox(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 48).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition top_slice_1 = modelPartData.addOrReplaceChild("top_slice_1", CubeListBuilder.create().texOffs(56, 11).addBox(-6.0F, -4.0F, 0.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 57).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -7.0F));
		PartDefinition top_slice_2 = modelPartData.addOrReplaceChild("top_slice_2", CubeListBuilder.create().texOffs(56, 22).addBox(-7.0F, -4.0F, 0.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 66).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(7.0F, 23.0F, -7.0F));
		PartDefinition top_slice_3 = modelPartData.addOrReplaceChild("top_slice_3", CubeListBuilder.create().texOffs(56, 33).addBox(0.0F, -3.0F, 0.0F, 7.0F, 4.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 75).addBox(0.0F, -3.0F, 0.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 22.0F, 0.0F));
		PartDefinition pan = modelPartData.addOrReplaceChild("pan", CubeListBuilder.create().texOffs(0, 110).addBox(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	public static LayerDefinition getFullyRaisedTexturedMeshDefinition() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition bottom_slice_0 = modelPartData.addOrReplaceChild("bottom_slice_0", CubeListBuilder.create().texOffs(28, 0).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, 1.0F));
		PartDefinition bottom_slice_1 = modelPartData.addOrReplaceChild("bottom_slice_1", CubeListBuilder.create().texOffs(28, 8).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_2 = modelPartData.addOrReplaceChild("bottom_slice_2", CubeListBuilder.create().texOffs(28, 16).addBox(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		PartDefinition bottom_slice_3 = modelPartData.addOrReplaceChild("bottom_slice_3", CubeListBuilder.create().texOffs(28, 24).addBox(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 24.0F, 1.0F));
		PartDefinition filling_slice_0 = modelPartData.addOrReplaceChild("filling_slice_0", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition filling_slice_1 = modelPartData.addOrReplaceChild("filling_slice_1", CubeListBuilder.create().texOffs(0, 12).addBox(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_2 = modelPartData.addOrReplaceChild("filling_slice_2", CubeListBuilder.create().texOffs(0, 24).addBox(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 23.0F, -6.0F));
		PartDefinition filling_slice_3 = modelPartData.addOrReplaceChild("filling_slice_3", CubeListBuilder.create().texOffs(0, 36).addBox(1.0F, -4.0F, 0.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 22.0F, 0.0F));
		PartDefinition top_slice_0 = modelPartData.addOrReplaceChild("top_slice_0", CubeListBuilder.create().texOffs(56, 0).addBox(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 48).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, 1.0F));
		PartDefinition top_slice_1 = modelPartData.addOrReplaceChild("top_slice_1", CubeListBuilder.create().texOffs(56, 12).addBox(-6.0F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 57).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(-1.0F, 23.0F, -7.0F));
		PartDefinition top_slice_2 = modelPartData.addOrReplaceChild("top_slice_2", CubeListBuilder.create().texOffs(56, 24).addBox(-7.0F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 66).addBox(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(7.0F, 23.0F, -7.0F));
		PartDefinition top_slice_3 = modelPartData.addOrReplaceChild("top_slice_3", CubeListBuilder.create().texOffs(56, 36).addBox(0.0F, -4.0F, 0.0F, 7.0F, 5.0F, 7.0F, CubeDeformation.NONE)
				.texOffs(0, 75).addBox(0.0F, -3.0F, 0.0F, 8.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 22.0F, 0.0F));
		PartDefinition pan = modelPartData.addOrReplaceChild("pan", CubeListBuilder.create().texOffs(0, 110).addBox(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, CubeDeformation.NONE), PartPose.offset(6.0F, 24.0F, -6.0F));
		return LayerDefinition.create(modelData, 128, 128);
	}

	@Override
	public void render(PieBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.translate(-0.5f, -1.5f, 0.5f);
		ModelPart pan;
		ModelPart[] bottom;
		ModelPart[] filling;
		ModelPart[] top;
		ResourceLocation texture;
		ResourceLocation pieFlavorTexture;
		int topBakeTime = entity.getTopBakeTime();
		if (topBakeTime < 500) {
			pan = this.unraisedPan;
			bottom = this.unraisedBottom;
			filling = this.unraisedFilling;
			top = this.unraisedTop;
			texture = UNRAISED_TEXTURE;
			pieFlavorTexture = PIE_FLAVOR_UNRAISED_TEXTURE;
		} else if (topBakeTime < 1800 || topBakeTime > 3000) {
			pan = this.slightlyRaisedPan;
			bottom = this.slightlyRaisedBottom;
			filling = this.slightlyRaisedFilling;
			top = this.slightlyRaisedTop;
			texture = SLIGHTLY_RAISED_TEXTURE;
			pieFlavorTexture = PIE_FLAVOR_SLIGHTLY_RAISED_TEXTURE;
		} else {
			pan = this.fullyRaisedPan;
			bottom = this.fullyRaisedBottom;
			filling = this.fullyRaisedFilling;
			top = this.fullyRaisedTop;
			texture = FULLY_RAISED_TEXTURE;
			pieFlavorTexture = PIE_FLAVOR_FULLY_RAISED_TEXTURE;
		}

		pan.render(matrices, vertexConsumers.getBuffer(RenderType.entityCutoutNoCullZOffset(texture)), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

		int slices = entity.getSlices();
		if (slices == 0) return;
		int layers = entity.getLayers();
		if (layers == 0) return;

		int bottomBakeTimeColor = getBakeTimeColor(entity.getBottomBakeTime(), 0xFFFFFFFF);
        int topBakeTimeColor = getBakeTimeColor(entity.getTopBakeTime(), 0xFFFFFFFF);
		for (int i = 0; i < slices; i++) {
			bottom[i].render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(texture)), light, getBakeTimeOverlay(entity.getBottomBakeTime(), overlay), ((bottomBakeTimeColor >> 16) & 0xFF) / 255.0f, ((bottomBakeTimeColor >> 8) & 0xFF) / 255.0f, (bottomBakeTimeColor & 0xFF) / 255.0f, ((bottomBakeTimeColor >> 24) & 0xFF) / 255.0f);
		}
		if (layers >= 2) {
			TextColor color = PBClientHelpers.getPieColor(entity.getFillingItem().isEmpty() ? new ItemStack(Items.APPLE) : entity.getFillingItem(), entity.getLevel(), null, 0);
			int fillingColor = color == null ? 0xFFFF00FF : (color.getValue() | 0xFF000000);
			for (int i = 0; i < slices; i++) {
				if (entity.getFillingItem().is(PBBlocks.PIE.get().asItem()) || entity.getFillingItem().is(PBItems.DOUGH.get().asItem())) {
					filling[i].render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(pieFlavorTexture)), light, getBakeTimeOverlay(entity.getTopBakeTime(), overlay), ((topBakeTimeColor >> 16) & 0xFF) / 255.0f, ((topBakeTimeColor >> 8) & 0xFF) / 255.0f, (topBakeTimeColor & 0xFF) / 255.0f, ((topBakeTimeColor >> 24) & 0xFF) / 255.0f);
				} else {
					filling[i].render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(texture)), light, overlay, ((fillingColor >> 16) & 0xFF) / 255.0f, ((fillingColor >> 8) & 0xFF) / 255.0f, (fillingColor & 0xFF) / 255.0f, ((fillingColor >> 24) & 0xFF) / 255.0f);
				}
			}
		}
		if (layers == 3) {
			for (int i = 0; i < slices; i++) {
				float off = 3.5f / 16.0f;
				matrices.pushPose();
				matrices.translate((i == 0 || i == 1) ? -off : off, 1.5f, (i == 0 || i == 3) ? off : -off);
				matrices.scale(1.0005f, 1.0005f, 1.0005f);
				matrices.translate((i == 0 || i == 1) ? off : -off, -1.5f, (i == 0 || i == 3) ? -off : off);
				top[i].render(matrices, vertexConsumers.getBuffer(RenderType.entityCutout(texture)), light, getBakeTimeOverlay(entity.getTopBakeTime(), overlay), ((topBakeTimeColor >> 16) & 0xFF) / 255.0f, ((topBakeTimeColor >> 8) & 0xFF) / 255.0f, (topBakeTimeColor & 0xFF) / 255.0f, ((topBakeTimeColor >> 24) & 0xFF) / 255.0f);
				matrices.popPose();
			}
		}
	}

	public static int getBakeTimeColor(int bakeTime, int color) {
		if (bakeTime <= PedrosBakery.CONFIG.ticksUntilPieBaked.get()) {
			return color;
		}

		int alpha = color >> 24 & 0xFF;
		int red = color >> 16 & 0xFF;
		int green = color >> 8 & 0xFF;
		int blue = color & 0xFF;

		float factor = Math.max(-1.0f/(2.0f * PedrosBakery.CONFIG.ticksUntilPieBaked.get()) * bakeTime + 1.5f, 0.25f);

		red = (int) Mth.clamp(red * factor, 0, 255);
		green = (int) Mth.clamp(green * factor, 0, 255);
		blue = (int) Mth.clamp(blue * factor, 0, 255);

		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	public static int getBakeTimeOverlay(int bakeTime, int overlay) {
		if (bakeTime > PedrosBakery.CONFIG.ticksUntilPieBaked.get()) {
			return overlay;
		}
		return OverlayTexture.pack(((float) PedrosBakery.CONFIG.ticksUntilPieBaked.get() - bakeTime) / (2.0f * PedrosBakery.CONFIG.ticksUntilPieBaked.get()), false);
	}
}