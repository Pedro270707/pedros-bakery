package net.pedroricardo.render;

import com.anthonyhilyard.prism.item.ItemColors;
import com.anthonyhilyard.prism.util.ImageAnalysis;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PieBlock;
import net.pedroricardo.block.entity.PieBlockEntity;
import net.pedroricardo.model.PBModelLayers;

public class PieBlockRenderer implements BlockEntityRenderer<PieBlockEntity> {
	public static final Identifier TEXTURE = Identifier.of(PedrosBakery.MOD_ID, "textures/entity/pie.png");
	public static final PieBlockEntity RENDER_PIE = new PieBlockEntity(BlockPos.ORIGIN, PBBlocks.PIE.getDefaultState());

	private final ModelPart[] bottom;
	private final ModelPart[] filling;
	private final ModelPart[] top;
	private final ModelPart pan;

	public PieBlockRenderer(BlockEntityRendererFactory.Context ctx) {
		ModelPart modelPart = ctx.getLayerModelPart(PBModelLayers.PIE);
		this.pan = modelPart.getChild("pan");
		this.bottom = new ModelPart[4];
		this.filling = new ModelPart[4];
		this.top = new ModelPart[4];
		for (int i = 0; i < 4; i++) {
			this.bottom[i] = modelPart.getChild("bottom_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.filling[i] = modelPart.getChild("filling_slice_" + i);
		}
		for (int i = 0; i < 4; i++) {
			this.top[i] = modelPart.getChild("top_slice_" + i);
		}
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData pan = modelPartData.addChild("pan", ModelPartBuilder.create().uv(0, 0).cuboid(-15.0F, -4.0F, -1.0F, 16.0F, 4.0F, 16.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 24.0F, -7.0F));
		ModelPartData bottom_slice_0 = modelPartData.addChild("bottom_slice_0", ModelPartBuilder.create().uv(32, 20).cuboid(-7.0F, -1.0F, -1.0F, 8.0F, 1.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 24.0F, 1.0F));
		ModelPartData bottom_slice_1 = modelPartData.addChild("bottom_slice_1", ModelPartBuilder.create().uv(32, 29).cuboid(-7.0F, -1.0F, -1.0F, 8.0F, 1.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 24.0F, -7.0F));
		ModelPartData bottom_slice_2 = modelPartData.addChild("bottom_slice_2", ModelPartBuilder.create().uv(32, 38).cuboid(-7.0F, -1.0F, -1.0F, 8.0F, 1.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 24.0F, -7.0F));
		ModelPartData bottom_slice_3 = modelPartData.addChild("bottom_slice_3", ModelPartBuilder.create().uv(32, 47).cuboid(1.0F, -1.0F, -1.0F, 8.0F, 1.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 24.0F, 1.0F));
		ModelPartData filling_slice_0 = modelPartData.addChild("filling_slice_0", ModelPartBuilder.create().uv(0, 20).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData filling_slice_1 = modelPartData.addChild("filling_slice_1", ModelPartBuilder.create().uv(0, 31).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, -7.0F));
		ModelPartData filling_slice_2 = modelPartData.addChild("filling_slice_2", ModelPartBuilder.create().uv(0, 42).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
		ModelPartData filling_slice_3 = modelPartData.addChild("filling_slice_3", ModelPartBuilder.create().uv(0, 53).cuboid(0.0F, -2.0F, 0.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
		ModelPartData top_slice_0 = modelPartData.addChild("top_slice_0", ModelPartBuilder.create().uv(64, 20).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, 1.0F));
		ModelPartData top_slice_1 = modelPartData.addChild("top_slice_1", ModelPartBuilder.create().uv(64, 31).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(-1.0F, 23.0F, -7.0F));
		ModelPartData top_slice_2 = modelPartData.addChild("top_slice_2", ModelPartBuilder.create().uv(64, 42).cuboid(-7.0F, -3.0F, -1.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(7.0F, 23.0F, -7.0F));
		ModelPartData top_slice_3 = modelPartData.addChild("top_slice_3", ModelPartBuilder.create().uv(64, 53).cuboid(0.0F, -2.0F, 0.0F, 8.0F, 3.0F, 8.0F, Dilation.NONE), ModelTransform.pivot(0.0F, 22.0F, 0.0F));
		return TexturedModelData.of(modelData, 128, 128);
	}

	@Override
	public void render(PieBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		BlockState state = entity.getCachedState();
		matrices.scale(-1.0f, -1.0f, 1.0f);
		matrices.translate(-0.5f, -1.5f, 0.5f);

		this.pan.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCullZOffset(TEXTURE)), light, overlay, 0xFFFFFFFF);

		int slices = entity.getSlices();
		if (slices == 0) return;
		int layers = entity.getLayers();
		if (layers == 0) return;

		for (int i = 0; i < slices; i++) {
			this.bottom[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, getBakeTimeOverlay(entity.getBottomBakeTime(), overlay), getBakeTimeColor(entity.getBottomBakeTime(), 0xFFFFFFFF));
		}
		if (layers >= 2) {
			Sprite sprite = MinecraftClient.getInstance().getItemRenderer().getModel(entity.getFillingItem().isEmpty() ? new ItemStack(Items.APPLE) : entity.getFillingItem(), entity.getWorld(), null, 0).getParticleSprite();
			TextColor color = ImageAnalysis.getDominantColor(sprite.getContents().getId().withPrefixedPath("textures/").withSuffixedPath(".png"), new Rect2i(0, 0, sprite.getContents().getWidth(), sprite.getContents().getHeight()));
			for (int i = 0; i < slices; i++) {
				this.filling[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, overlay, color == null ? 0xFFFF00FF : (color.getRgb() | 0xFF000000));
			}
		}
		if (layers == 3) {
			for (int i = 0; i < slices; i++) {
				this.top[i].render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, getBakeTimeOverlay(entity.getTopBakeTime(), overlay), getBakeTimeColor(entity.getTopBakeTime(), 0xFFFFFFFF));
			}
		}
	}

	public static int getBakeTimeColor(int bakeTime, int color) {
		if (bakeTime <= PedrosBakery.CONFIG.ticksUntilPieBaked()) {
			return color;
		}

		int alpha = color >> 24 & 0xFF;
		int red = color >> 16 & 0xFF;
		int green = color >> 8 & 0xFF;
		int blue = color & 0xFF;

		float factor = Math.max(-1.0f/(2.0f * PedrosBakery.CONFIG.ticksUntilPieBaked()) * bakeTime + 1.5f, 0.25f);

		red = (int) MathHelper.clamp(red * factor, 0, 255);
		green = (int)MathHelper.clamp(green * factor, 0, 255);
		blue = (int)MathHelper.clamp(blue * factor, 0, 255);

		return alpha << 24 | red << 16 | green << 8 | blue;
	}

	public static int getBakeTimeOverlay(int bakeTime, int overlay) {
		if (bakeTime > PedrosBakery.CONFIG.ticksUntilPieBaked()) {
			return overlay;
		}
		return OverlayTexture.getUv(((float) PedrosBakery.CONFIG.ticksUntilPieBaked() - bakeTime) / (2.0f * PedrosBakery.CONFIG.ticksUntilPieBaked()), false);
	}
}