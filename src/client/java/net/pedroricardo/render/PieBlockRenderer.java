package net.pedroricardo.render;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.pedroricardo.PBClientHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PieBlockEntity;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.model.PBModelLayers;

public class PieBlockRenderer implements BlockEntityRenderer<PieBlockEntity> {
	public static final Identifier UNRAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/unraised.png");
	public static final Identifier SLIGHTLY_RAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/slightly_raised.png");
	public static final Identifier FULLY_RAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/fully_raised.png");
	public static final Identifier PIE_FLAVOR_UNRAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_unraised.png");
	public static final Identifier PIE_FLAVOR_SLIGHTLY_RAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_slightly_raised.png");
	public static final Identifier PIE_FLAVOR_FULLY_RAISED_TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie/pie_flavor_fully_raised.png");
	public static final PieBlockEntity RENDER_PIE = new PieBlockEntity(BlockPos.ORIGIN, PBBlocks.PIE.getDefaultState());

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

	public PieBlockRenderer(BlockEntityRendererFactory.Context ctx) {
		ModelPart unraisedModelPart = ctx.getLayerModelPart(PBModelLayers.UNRAISED_PIE);
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

		ModelPart slightlyRaisedModelPart = ctx.getLayerModelPart(PBModelLayers.SLIGHTLY_RAISED_PIE);
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

		ModelPart fullyRaisedModelPart = ctx.getLayerModelPart(PBModelLayers.FULLY_RAISED_PIE);
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

	public static TexturedModelData getUnraisedTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bottom_slice_0 = modelPartData.addChild("bottom_slice_0", ModelPartBuilder.create().uv(28, 0).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 24.0F, 1.0F));
		ModelPartData bottom_slice_1 = modelPartData.addChild("bottom_slice_1", ModelPartBuilder.create().uv(28, 8).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_2 = modelPartData.addChild("bottom_slice_2", ModelPartBuilder.create().uv(28, 16).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_3 = modelPartData.addChild("bottom_slice_3", ModelPartBuilder.create().uv(28, 24).cuboid(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-2.0F, 24.0F, 1.0F));
		ModelPartData filling_slice_0 = modelPartData.addChild("filling_slice_0", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData filling_slice_1 = modelPartData.addChild("filling_slice_1", ModelPartBuilder.create().uv(0, 10).cuboid(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_2 = modelPartData.addChild("filling_slice_2", ModelPartBuilder.create().uv(0, 20).cuboid(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(6.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_3 = modelPartData.addChild("filling_slice_3", ModelPartBuilder.create().uv(0, 30).cuboid(1.0F, -2.0F, 0.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 22.0F, 0.0F));
		ModelPartData top_slice_0 = modelPartData.addChild("top_slice_0", ModelPartBuilder.create().uv(56, 0).cuboid(-6.0F, -3.0F, -1.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData top_slice_1 = modelPartData.addChild("top_slice_1", ModelPartBuilder.create().uv(56, 10).cuboid(-6.0F, -3.0F, 0.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, -7.0F));
		ModelPartData top_slice_2 = modelPartData.addChild("top_slice_2", ModelPartBuilder.create().uv(56, 20).cuboid(-7.0F, -3.0F, 0.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
		ModelPartData top_slice_3 = modelPartData.addChild("top_slice_3", ModelPartBuilder.create().uv(56, 30).cuboid(0.0F, -2.0F, 0.0F, 7.0F, 3.0F, 7.0F, Dilation.NONE), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
		ModelPartData pan = modelPartData.addChild("pan", ModelPartBuilder.create().uv(0, 110).cuboid(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, Dilation.NONE), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	public static TexturedModelData getSlightlyRaisedTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bottom_slice_0 = modelPartData.addChild("bottom_slice_0", ModelPartBuilder.create().uv(28, 0).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 24.0F, 1.0F));
		ModelPartData bottom_slice_1 = modelPartData.addChild("bottom_slice_1", ModelPartBuilder.create().uv(28, 8).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_2 = modelPartData.addChild("bottom_slice_2", ModelPartBuilder.create().uv(28, 16).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_3 = modelPartData.addChild("bottom_slice_3", ModelPartBuilder.create().uv(28, 24).cuboid(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 24.0F, 1.0F));
		ModelPartData filling_slice_0 = modelPartData.addChild("filling_slice_0", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData filling_slice_1 = modelPartData.addChild("filling_slice_1", ModelPartBuilder.create().uv(0, 11).cuboid(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_2 = modelPartData.addChild("filling_slice_2", ModelPartBuilder.create().uv(0, 22).cuboid(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_3 = modelPartData.addChild("filling_slice_3", ModelPartBuilder.create().uv(0, 33).cuboid(1.0F, -3.0F, 0.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 22.0F, 0.0F));
		ModelPartData top_slice_0 = modelPartData.addChild("top_slice_0", ModelPartBuilder.create().uv(56, 0).cuboid(-6.0F, -4.0F, -1.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 48).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData top_slice_1 = modelPartData.addChild("top_slice_1", ModelPartBuilder.create().uv(56, 11).cuboid(-6.0F, -4.0F, 0.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 57).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, -7.0F));
		ModelPartData top_slice_2 = modelPartData.addChild("top_slice_2", ModelPartBuilder.create().uv(56, 22).cuboid(-7.0F, -4.0F, 0.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 66).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
		ModelPartData top_slice_3 = modelPartData.addChild("top_slice_3", ModelPartBuilder.create().uv(56, 33).cuboid(0.0F, -3.0F, 0.0F, 7.0F, 4.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 75).cuboid(0.0F, -3.0F, 0.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
		ModelPartData pan = modelPartData.addChild("pan", ModelPartBuilder.create().uv(0, 110).cuboid(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	public static TexturedModelData getFullyRaisedTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData bottom_slice_0 = modelPartData.addChild("bottom_slice_0", ModelPartBuilder.create().uv(28, 0).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 24.0F, 1.0F));
		ModelPartData bottom_slice_1 = modelPartData.addChild("bottom_slice_1", ModelPartBuilder.create().uv(28, 8).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_2 = modelPartData.addChild("bottom_slice_2", ModelPartBuilder.create().uv(28, 16).cuboid(-6.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		ModelPartData bottom_slice_3 = modelPartData.addChild("bottom_slice_3", ModelPartBuilder.create().uv(28, 24).cuboid(2.0F, -1.0F, -1.0F, 7.0F, 1.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-2.0F, 24.0F, 1.0F));
		ModelPartData filling_slice_0 = modelPartData.addChild("filling_slice_0", ModelPartBuilder.create().uv(0, 0).cuboid(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData filling_slice_1 = modelPartData.addChild("filling_slice_1", ModelPartBuilder.create().uv(0, 12).cuboid(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_2 = modelPartData.addChild("filling_slice_2", ModelPartBuilder.create().uv(0, 24).cuboid(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 23.0F, -6.0F));
		ModelPartData filling_slice_3 = modelPartData.addChild("filling_slice_3", ModelPartBuilder.create().uv(0, 36).cuboid(1.0F, -4.0F, 0.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 22.0F, 0.0F));
		ModelPartData top_slice_0 = modelPartData.addChild("top_slice_0", ModelPartBuilder.create().uv(56, 0).cuboid(-6.0F, -5.0F, -1.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 48).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData top_slice_1 = modelPartData.addChild("top_slice_1", ModelPartBuilder.create().uv(56, 12).cuboid(-6.0F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 57).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(-1.0F, 23.0F, -7.0F));
		ModelPartData top_slice_2 = modelPartData.addChild("top_slice_2", ModelPartBuilder.create().uv(56, 24).cuboid(-7.0F, -5.0F, 0.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 66).cuboid(-7.0F, -4.0F, -1.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
		ModelPartData top_slice_3 = modelPartData.addChild("top_slice_3", ModelPartBuilder.create().uv(56, 36).cuboid(0.0F, -4.0F, 0.0F, 7.0F, 5.0F, 7.0F, new Dilation(0.0F))
				.uv(0, 75).cuboid(0.0F, -3.0F, 0.0F, 8.0F, 1.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
		ModelPartData pan = modelPartData.addChild("pan", ModelPartBuilder.create().uv(0, 110).cuboid(-13.0F, -4.0F, -1.0F, 14.0F, 4.0F, 14.0F, new Dilation(0.0F)), ModelTransform.pivot(6.0F, 24.0F, -6.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(PieBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.translate(-0.5f, -1.5f, 0.5f);
		ModelPart pan;
		ModelPart[] bottom;
		ModelPart[] filling;
		ModelPart[] top;
		Identifier texture;
		Identifier pieFlavorTexture;
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

		pan.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(texture)), light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);

		int slices = entity.getSlices();
		if (slices == 0) return;
		int layers = entity.getLayers();
		if (layers == 0) return;

		int bottomBakeTimeColor = getBakeTimeColor(entity.getBottomBakeTime(), 0xFFFFFFFF);
        int topBakeTimeColor = getBakeTimeColor(entity.getTopBakeTime(), 0xFFFFFFFF);
		for (int i = 0; i < slices; i++) {
			bottom[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture)), light, getBakeTimeOverlay(entity.getBottomBakeTime(), overlay), ((bottomBakeTimeColor >> 16) & 0xFF) / 255.0f, ((bottomBakeTimeColor >> 8) & 0xFF) / 255.0f, (bottomBakeTimeColor & 0xFF) / 255.0f, ((bottomBakeTimeColor >> 24) & 0xFF) / 255.0f);
		}
		if (layers >= 2) {
			TextColor color = PBClientHelpers.getPieColor(entity.getFillingItem().isEmpty() ? new ItemStack(Items.APPLE) : entity.getFillingItem(), entity.getWorld(), null, 0);
			int fillingColor = color == null ? 0xFFFF00FF : (color.getRgb() | 0xFF000000);
			for (int i = 0; i < slices; i++) {
				if (entity.getFillingItem().isOf(PBBlocks.PIE.asItem()) || entity.getFillingItem().isOf(PBItems.DOUGH.asItem())) {
					filling[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(pieFlavorTexture)), light, getBakeTimeOverlay(entity.getTopBakeTime(), overlay), ((topBakeTimeColor >> 16) & 0xFF) / 255.0f, ((topBakeTimeColor >> 8) & 0xFF) / 255.0f, (topBakeTimeColor & 0xFF) / 255.0f, ((topBakeTimeColor >> 24) & 0xFF) / 255.0f);
				} else {
					filling[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture)), light, overlay, ((fillingColor >> 16) & 0xFF) / 255.0f, ((fillingColor >> 8) & 0xFF) / 255.0f, (fillingColor & 0xFF) / 255.0f, ((fillingColor >> 24) & 0xFF) / 255.0f);
				}
			}
		}
		if (layers == 3) {
			for (int i = 0; i < slices; i++) {
				float off = 3.5f / 16.0f;
				matrices.push();
				matrices.translate((i == 0 || i == 1) ? -off : off, 1.5f, (i == 0 || i == 3) ? off : -off);
				matrices.scale(1.0005f, 1.0005f, 1.0005f);
				matrices.translate((i == 0 || i == 1) ? off : -off, -1.5f, (i == 0 || i == 3) ? -off : off);
				top[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture)), light, getBakeTimeOverlay(entity.getTopBakeTime(), overlay), ((topBakeTimeColor >> 16) & 0xFF) / 255.0f, ((topBakeTimeColor >> 8) & 0xFF) / 255.0f, (topBakeTimeColor & 0xFF) / 255.0f, ((topBakeTimeColor >> 24) & 0xFF) / 255.0f);
				matrices.pop();
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

		red = (int) MathHelper.clamp(red * factor, 0, 255);
		green = (int)MathHelper.clamp(green * factor, 0, 255);
		blue = (int)MathHelper.clamp(blue * factor, 0, 255);

		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	public static int getBakeTimeOverlay(int bakeTime, int overlay) {
		if (bakeTime > PedrosBakery.CONFIG.ticksUntilPieBaked.get()) {
			return overlay;
		}
		return OverlayTexture.getUv(((float) PedrosBakery.CONFIG.ticksUntilPieBaked.get() - bakeTime) / (2.0f * PedrosBakery.CONFIG.ticksUntilPieBaked.get()), false);
	}
}