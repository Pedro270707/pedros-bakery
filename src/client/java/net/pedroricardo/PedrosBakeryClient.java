package net.pedroricardo;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.features.BlockCakeFeature;
import net.pedroricardo.block.extras.features.PaintingCakeFeature;
import net.pedroricardo.block.extras.features.PlayerHeadCakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.model.PBModelLayers;
import net.pedroricardo.network.PBClientNetworkRegistry;
import net.pedroricardo.registry.CakeFeatureRenderer;
import net.pedroricardo.registry.CakeFeatureRendererRegistry;
import net.pedroricardo.render.*;
import net.pedroricardo.render.item.PixelData;
import net.pedroricardo.render.item.ShapedCookieItemRenderer;
import net.pedroricardo.screen.CookieTableScreen;
import net.pedroricardo.screen.PBScreenHandlerTypes;
import org.joml.Vector2i;

public class PedrosBakeryClient implements ClientModInitializer {
	public static boolean isRenderingInWorld = false;

	public static final ShapedCookieItemRenderer SHAPED_COOKIE_RENDERER = new ShapedCookieItemRenderer((stack, pixel, shape) -> {
		CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
		int topColor = top == null ? 0xFFFFFFFF : top.color();
		if ((!shape.contains(new Vector2i(pixel.x() - 1, pixel.y())) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) || !shape.contains(new Vector2i(pixel.x(), pixel.y() - 1)) || !shape.contains(new Vector2i(pixel.x(), pixel.y() - 1)) || (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y() - 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() - 2)) && shape.contains(new Vector2i(pixel.x() - 2, pixel.y())))) {
			return new PixelData(Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_border" + (top == null ? "" : "_frosted") + ".png"), topColor);
		} else if (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y()))
				|| !shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))
				|| (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y() + 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 2)) && shape.contains(new Vector2i(pixel.x() + 2, pixel.y())))
				|| (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y() - 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() - 2)) && shape.contains(new Vector2i(pixel.x() + 2, pixel.y())))
				|| (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y() + 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 2)) && shape.contains(new Vector2i(pixel.x() - 2, pixel.y())))) {
			return new PixelData(Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_border.png"), 0xFFFFFFFF);
		} else if (!shape.contains(new Vector2i(pixel.x(), pixel.y() + 2))) {
			return new PixelData(Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_dark_inner.png"), 0xFFFFFFFF);
		}
		return new PixelData(Identifier.of(PedrosBakery.MOD_ID, "textures/item/cookie_light_inner" + (top == null ? "" : "_frosted") + ".png"), topColor);
	});

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
		WorldRenderEvents.START.register(context -> isRenderingInWorld = true);

		PBModelLayers.init();
		PBClientNetworkRegistry.init();
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE, (ctx) -> new PBCakeBlockRenderer());
		BlockEntityRendererFactories.register(PBBlockEntities.BEATER, BeaterBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.BAKING_TRAY, BakingTrayBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE_STAND, CakeStandBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.PLATE, PlateBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CUPCAKE_TRAY, CupcakeTrayBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CUPCAKE, CupcakeBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.PIE, PieBlockRenderer::new);

		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CAKE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			PBCakeBlockRenderer.RENDER_CAKE.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(PBCakeBlockRenderer.RENDER_CAKE, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_TRAY.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.EXPANDABLE_BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CUPCAKE_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CUPCAKE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			CupcakeBlockRenderer.RENDER_CUPCAKE.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(CupcakeBlockRenderer.RENDER_CUPCAKE, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.PIE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			PieBlockRenderer.RENDER_PIE.readFrom(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(PieBlockRenderer.RENDER_PIE, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBItems.SHAPED_COOKIE, SHAPED_COOKIE_RENDERER::render);

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
			if (tintIndex > 0 || top == null) {
				return -1;
			}
			return top.color();
		}, PBItems.FROSTING_BOTTLE);
		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "empty"), (stack, world, entity, seed) -> {
			if (!stack.isOf(PBBlocks.CUPCAKE.asItem())) return 0.0f;
			return PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty() ? 1.0f : 0.0f;
		});
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
			if (tintIndex != 1 || top == null) {
				return -1;
			}
			return top.color();
		}, PBItems.DONUT);
		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "frosted"), (stack, world, entity, seed) -> {
			if (!stack.isOf(PBItems.DONUT)) return 0.0f;
			return PBHelpers.contains(stack, PBComponentTypes.TOP) ? 1.0f : 0.0f;
		});

		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
				PBBlocks.COOKIE_JAR,
				PBBlocks.BUTTER_CHURN);

		CakeFeatureRenderer cakeLayerFeatureRenderer = (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			Identifier id = CakeFeatures.REGISTRY.getId(feature);
			if (id == null) return;
			PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), batter, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(Identifier.of(id.getNamespace(), "textures/entity/cake/feature/" + id.getPath() + ".png"))), light, overlay, 0xFFFFFFFF);
		};
		CakeFeatureRenderer blockOnTopFeatureRenderer = new CakeFeatureRenderer() {
			@Override
			public void render(CakeFeature feature, PBCakeBlockEntity entity, CakeBatter<FullBatterSizeContainer> batter, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
				if (batter.getSizeContainer().getBites() >= batter.getSizeContainer().getSize() / 2.0f || !(feature instanceof BlockCakeFeature blockFeature)) return;
				matrices.push();
				matrices.translate(0.0f, batter.getSizeContainer().getHeight() / 16.0f, 0.0f);
				MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockFeature.getBlockState(), matrices, vertexConsumers, light, overlay);
				matrices.pop();
			}

			@Override
			public boolean needsIrisFix() {
				return false;
			}
		};
		CakeFeatureRendererRegistry.register(CakeFeatures.GLINT, new CakeFeatureRenderer() {
			@Override
			public void render(CakeFeature feature, PBCakeBlockEntity entity, CakeBatter<FullBatterSizeContainer> batter, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
				PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), batter, matrices, vertexConsumers.getBuffer(RenderLayer.getDirectEntityGlint()), light, overlay, 0xFFFFFFFF, PBConfig.CakeRenderQuality.SIMPLE);
			}

			@Override
			public boolean needsIrisFix() {
				return false;
			}
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.SWEET_BERRIES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLOW_BERRIES, (feature, entity, batter, matrices, vertexConsumers, light, overlay) ->
			cakeLayerFeatureRenderer.render(feature, entity, batter, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay)
		);
		CakeFeatureRendererRegistry.register(CakeFeatures.HONEY, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PAINTING, (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			RegistryEntry<PaintingVariant> painting = ((PaintingCakeFeature) feature).getPainting(batter);
			if (painting == null) return;
			Sprite sprite = MinecraftClient.getInstance().getPaintingManager().getPaintingSprite(painting.value());

			PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(sprite.getAtlasId())), (16.0f - batter.getSizeContainer().getSize()) / 2.0f + batter.getSizeContainer().getBites(), (16.0f - batter.getSizeContainer().getSize()) / 2.0f, batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), sprite.getMinU() + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * (sprite.getMaxU() - sprite.getMinU()), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 1.0f, 1.0f, light, overlay, 0xFFFFFFFF);
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.DANDELION, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.TORCHFLOWER, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.POPPY, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLUE_ORCHID, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ALLIUM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.AZURE_BLUET, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_TULIP, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ORANGE_TULIP, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WHITE_TULIP, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PINK_TULIP, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.OXEYE_DAISY, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.CORNFLOWER, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WITHER_ROSE, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LILY_OF_THE_VALLEY, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WHITE_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ORANGE_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.MAGENTA_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIGHT_BLUE_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.YELLOW_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIME_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PINK_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GRAY_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIGHT_GRAY_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.CYAN_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PURPLE_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLUE_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GREEN_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLACK_SPRINKLES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLASS, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PLAYER_HEAD, (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			GameProfile profile = ((PlayerHeadCakeFeature) feature).getProfile(batter);
			drawPlayerHead(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(profile == null ? DefaultSkinHelper.getTexture() : MinecraftClient.getInstance().getSkinProvider().loadSkin(profile))), batter, light, overlay, MinecraftClient.getInstance().world != null && profile != null && LivingEntityRenderer.shouldFlipUpsideDown(new OtherClientPlayerEntity(MinecraftClient.getInstance().world, profile)));
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.GRASS, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.FERN, blockOnTopFeatureRenderer);

		HandledScreens.register(PBScreenHandlerTypes.COOKIE_TABLE, CookieTableScreen::new);
	}

	public static void drawPlayerHead(MatrixStack matrices, VertexConsumer vertexConsumer, CakeBatter<FullBatterSizeContainer> batter, int light, int overlay, boolean upsideDown) {
		int i = 8 + (upsideDown ? 8 : 0);
		int j = 8 + (upsideDown ? 0 : 8);
		PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, (8.0f - batter.getSizeContainer().getSize() / 2.0f) + batter.getSizeContainer().getBites(), (8.0f - batter.getSizeContainer().getSize() / 2.0f), batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), 8 + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * 8, i, 16, j, 64, 64, light, overlay, 0xFFFFFFFF);
		drawPlayerHat(matrices, vertexConsumer, batter, light, overlay, upsideDown);
	}

	private static void drawPlayerHat(MatrixStack matrices, VertexConsumer vertexConsumer, CakeBatter<FullBatterSizeContainer> batter, int light, int overlay, boolean upsideDown) {
		int i = 8 + (upsideDown ? 8 : 0);
		int j = 8 + (upsideDown ? 0 : 8);
		RenderSystem.enableBlend();
		PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, (8.0f - batter.getSizeContainer().getSize() / 2.0f) + batter.getSizeContainer().getBites(), (8.0f - batter.getSizeContainer().getSize() / 2.0f), batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), 40 + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * 8, i, 48, j, 64, 64, light, overlay, 0xFFFFFFFF);
		RenderSystem.disableBlend();
	}
}