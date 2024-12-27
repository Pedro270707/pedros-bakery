package net.pedroricardo.client;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.pedroricardo.PBConfig;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
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
import net.pedroricardo.client.model.PBModelLayers;
import net.pedroricardo.client.registry.CakeFeatureRenderer;
import net.pedroricardo.client.registry.CakeFeatureRendererRegistry;
import net.pedroricardo.client.render.*;
import net.pedroricardo.client.render.item.PixelData;
import net.pedroricardo.client.render.item.ShapedCookieItemRenderer;
import net.pedroricardo.client.screen.CookieTableScreen;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.screen.PBScreenHandlerTypes;
import org.joml.Vector2i;

@Mod.EventBusSubscriber(modid = PedrosBakery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PedrosBakeryClient {
	public static boolean isRenderingInWorld = false;

	public static final ShapedCookieItemRenderer SHAPED_COOKIE_RENDERER = new ShapedCookieItemRenderer((pixel, shape) -> {
		if ((!shape.contains(new Vector2i(pixel.x() - 1, pixel.y())) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))) || !shape.contains(new Vector2i(pixel.x(), pixel.y() - 1)) || !shape.contains(new Vector2i(pixel.x(), pixel.y() - 1)) || (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y() - 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() - 2)) && shape.contains(new Vector2i(pixel.x() - 2, pixel.y())))) {
			return new PixelData(new ResourceLocation(PedrosBakery.MOD_ID, "textures/item/cookie_light_border.png"), 0xFFFFFFFF);
		} else if (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y()))
				|| !shape.contains(new Vector2i(pixel.x(), pixel.y() + 1))
				|| (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y() + 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 2)) && shape.contains(new Vector2i(pixel.x() + 2, pixel.y())))
				|| (!shape.contains(new Vector2i(pixel.x() + 1, pixel.y() - 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() - 2)) && shape.contains(new Vector2i(pixel.x() + 2, pixel.y())))
				|| (!shape.contains(new Vector2i(pixel.x() - 1, pixel.y() + 1)) && shape.contains(new Vector2i(pixel.x(), pixel.y() + 2)) && shape.contains(new Vector2i(pixel.x() - 2, pixel.y())))) {
			return new PixelData(new ResourceLocation(PedrosBakery.MOD_ID, "textures/item/cookie_dark_border.png"), 0xFFFFFFFF);
		} else if (!shape.contains(new Vector2i(pixel.x(), pixel.y() + 2))) {
			return new PixelData(new ResourceLocation(PedrosBakery.MOD_ID, "textures/item/cookie_dark_inner.png"), 0xFFFFFFFF);
		}
		return new PixelData(new ResourceLocation(PedrosBakery.MOD_ID, "textures/item/cookie_light_inner.png"), 0xFFFFFFFF);
	});

	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
//		WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
//		WorldRenderEvents.START.register(context -> isRenderingInWorld = true);

		PBModelLayers.init();
		BlockEntityRenderers.register(PBBlockEntities.CAKE.get(), (ctx) -> new PBCakeBlockRenderer());
		BlockEntityRenderers.register(PBBlockEntities.BEATER.get(), BeaterBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.BAKING_TRAY.get(), BakingTrayBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.CAKE_STAND.get(), CakeStandBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.PLATE.get(), PlateBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.CUPCAKE_TRAY.get(), CupcakeTrayBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.CUPCAKE.get(), CupcakeBlockRenderer::new);
		BlockEntityRenderers.register(PBBlockEntities.PIE.get(), PieBlockRenderer::new);

//		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
//			CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
//			if (tintIndex > 0 || top == null) {
//				return -1;
//			}
//			return top.color();
//		}, PBItems.FROSTING_BOTTLE);
//		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "empty"), (stack, world, entity, seed) -> {
//			if (!stack.is(PBBlocks.CUPCAKE.asItem())) return 0.0f;
//			return PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty() ? 1.0f : 0.0f;
//		});
//		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
//			CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
//			if (tintIndex != 1 || top == null) {
//				return -1;
//			}
//			return top.color();
//		}, PBItems.DONUT);
//		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "frosted"), (stack, world, entity, seed) -> {
//			if (!stack.is(PBItems.DONUT)) return 0.0f;
//			return PBHelpers.contains(stack, PBComponentTypes.TOP) ? 1.0f : 0.0f;
//		});
//
//		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
//				PBBlocks.COOKIE_JAR,
//				PBBlocks.BUTTER_CHURN);

		CakeFeatureRenderer cakeLayerFeatureRenderer = (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			ResourceLocation id = CakeFeatures.registrySupplier.get().getKey(feature);
			if (id == null) return;
			PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), batter, matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(new ResourceLocation(id.getNamespace(), "textures/entity/cake/feature/" + id.getPath() + ".png"))), light, overlay, 0xFFFFFFFF);
		};
		CakeFeatureRenderer blockOnTopFeatureRenderer = new CakeFeatureRenderer() {
			@Override
			public void render(CakeFeature feature, PBCakeBlockEntity entity, CakeBatter<FullBatterSizeContainer> batter, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
				if (batter.getSizeContainer().getBites() >= batter.getSizeContainer().getSize() / 2.0f || !(feature instanceof BlockCakeFeature blockFeature)) return;
				matrices.pushPose();
				matrices.translate(0.0f, batter.getSizeContainer().getHeight() / 16.0f, 0.0f);
				Minecraft.getInstance().getBlockRenderer().renderSingleBlock(blockFeature.getBlockState(), matrices, vertexConsumers, light, overlay);
				matrices.popPose();
			}

			@Override
			public boolean needsIrisFix() {
				return false;
			}
		};
		CakeFeatureRendererRegistry.register(CakeFeatures.GLINT.get(), new CakeFeatureRenderer() {
			@Override
			public void render(CakeFeature feature, PBCakeBlockEntity entity, CakeBatter<FullBatterSizeContainer> batter, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
				PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), batter, matrices, vertexConsumers.getBuffer(RenderType.entityGlintDirect()), light, overlay, 0xFFFFFFFF, PBConfig.CakeRenderQuality.SIMPLE);
			}

			@Override
			public boolean needsIrisFix() {
				return false;
			}
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.SWEET_BERRIES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_MUSHROOM.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_MUSHROOM.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLOW_BERRIES.get(), (feature, entity, batter, matrices, vertexConsumers, light, overlay) ->
			cakeLayerFeatureRenderer.render(feature, entity, batter, matrices, vertexConsumers, LightTexture.FULL_BRIGHT, overlay)
		);
		CakeFeatureRendererRegistry.register(CakeFeatures.HONEY.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PAINTING.get(), (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			Holder<PaintingVariant> painting = ((PaintingCakeFeature) feature).getPainting(batter);
			if (painting == null) return;
			TextureAtlasSprite sprite = Minecraft.getInstance().getPaintingTextures().get(painting.value());

			PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(sprite.atlasLocation())), (16.0f - batter.getSizeContainer().getSize()) / 2.0f + batter.getSizeContainer().getBites(), (16.0f - batter.getSizeContainer().getSize()) / 2.0f, batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), sprite.getU0() + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * (sprite.getU1() - sprite.getU0()), sprite.getV0(), sprite.getU1(), sprite.getV1(), 1.0f, 1.0f, light, overlay, 0xFFFFFFFF);
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.DANDELION.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.TORCHFLOWER.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.POPPY.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLUE_ORCHID.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ALLIUM.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.AZURE_BLUET.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_TULIP.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ORANGE_TULIP.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WHITE_TULIP.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PINK_TULIP.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.OXEYE_DAISY.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.CORNFLOWER.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WITHER_ROSE.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LILY_OF_THE_VALLEY.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.WHITE_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.ORANGE_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.MAGENTA_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIGHT_BLUE_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.YELLOW_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIME_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PINK_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GRAY_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.LIGHT_GRAY_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.CYAN_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PURPLE_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLUE_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GREEN_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BLACK_SPRINKLES.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLASS.get(), cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PLAYER_HEAD.get(), (feature, entity, batter, matrices, vertexConsumers, light, overlay) -> {
			GameProfile profile = ((PlayerHeadCakeFeature) feature).getProfile(batter);
			drawPlayerHead(matrices, vertexConsumers.getBuffer(RenderType.entityTranslucent(profile == null ? DefaultPlayerSkin.getDefaultSkin() : Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile))), batter, light, overlay, Minecraft.getInstance().level != null && profile != null && LivingEntityRenderer.isEntityUpsideDown(new RemotePlayer(Minecraft.getInstance().level, profile)));
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.GRASS.get(), blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.FERN.get(), blockOnTopFeatureRenderer);

		MenuScreens.register(PBScreenHandlerTypes.COOKIE_TABLE.get(), CookieTableScreen::new);
	}

	public static void drawPlayerHead(PoseStack matrices, VertexConsumer vertexConsumer, CakeBatter<FullBatterSizeContainer> batter, int light, int overlay, boolean upsideDown) {
		int i = 8 + (upsideDown ? 8 : 0);
		int j = 8 + (upsideDown ? 0 : 8);
		PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, (8.0f - batter.getSizeContainer().getSize() / 2.0f) + batter.getSizeContainer().getBites(), (8.0f - batter.getSizeContainer().getSize() / 2.0f), batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), 8 + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * 8, i, 16, j, 64, 64, light, overlay, 0xFFFFFFFF);
		drawPlayerHat(matrices, vertexConsumer, batter, light, overlay, upsideDown);
	}

	private static void drawPlayerHat(PoseStack matrices, VertexConsumer vertexConsumer, CakeBatter<FullBatterSizeContainer> batter, int light, int overlay, boolean upsideDown) {
		int i = 8 + (upsideDown ? 8 : 0);
		int j = 8 + (upsideDown ? 0 : 8);
		RenderSystem.enableBlend();
		PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumer, (8.0f - batter.getSizeContainer().getSize() / 2.0f) + batter.getSizeContainer().getBites(), (8.0f - batter.getSizeContainer().getSize() / 2.0f), batter.getSizeContainer().getHeight(), batter.getSizeContainer().getSize() - batter.getSizeContainer().getBites(), batter.getSizeContainer().getSize(), 40 + (batter.getSizeContainer().getBites() / batter.getSizeContainer().getSize()) * 8, i, 48, j, 64, 64, light, overlay, 0xFFFFFFFF);
		RenderSystem.disableBlend();
	}
}