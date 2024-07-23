package net.pedroricardo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeatures;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.helpers.features.BlockCakeFeature;
import net.pedroricardo.block.helpers.features.PaintingCakeFeature;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.model.PBModelLayers;
import net.pedroricardo.registry.CakeFeatureRenderer;
import net.pedroricardo.registry.CakeFeatureRendererRegistry;
import net.pedroricardo.render.*;

public class PedrosBakeryClient implements ClientModInitializer {
	public static boolean isRenderingInWorld = false;

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
		WorldRenderEvents.START.register(context -> isRenderingInWorld = true);

		PBModelLayers.init();
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE, (ctx) -> new PBCakeBlockRenderer());
		BlockEntityRendererFactories.register(PBBlockEntities.BEATER, BeaterBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.BAKING_TRAY, BakingTrayBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE_STAND, CakeStandBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CUPCAKE_TRAY, CupcakeTrayBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CUPCAKE, CupcakeBlockRenderer::new);

		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CAKE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			PBCakeBlockRenderer.RENDER_CAKE.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(PBCakeBlockRenderer.RENDER_CAKE, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.EXPANDABLE_BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CUPCAKE_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CUPCAKE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			CupcakeBlockRenderer.RENDER_CUPCAKE.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(CupcakeBlockRenderer.RENDER_CUPCAKE, matrices, vertexConsumers, light, overlay);
		});

		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			CakeTop top = stack.get(PBComponentTypes.TOP);
			if (tintIndex > 0 || top == null) {
				return -1;
			}
			return top.color();
		}, PBItems.FROSTING_BOTTLE);
		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "empty"), (stack, world, entity, seed) -> {
			if (!stack.isOf(PBBlocks.CUPCAKE.asItem())) return 0.0f;
			return stack.getOrDefault(PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty() ? 1.0f : 0.0f;
		});
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			CakeTop top = stack.get(PBComponentTypes.TOP);
			if (tintIndex != 1 || top == null) {
				return -1;
			}
			return top.color();
		}, PBItems.DONUT);
		ModelPredicateProviderRegistry.register(Identifier.of(PedrosBakery.MOD_ID, "frosted"), (stack, world, entity, seed) -> {
			if (!stack.isOf(PBItems.DONUT)) return 0.0f;
			return stack.get(PBComponentTypes.TOP) == null ? 0.0f : 1.0f;
		});

		BlockRenderLayerMap.INSTANCE.putBlock(PBBlocks.COOKIE_JAR, RenderLayer.getCutout());

		CakeFeatureRenderer cakeLayerFeatureRenderer = (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			Identifier id = CakeFeatures.REGISTRY.getId(feature);
			if (id == null) return;
			PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(PBCakeBlockRenderer.getTopRenderLayer(Identifier.of(id.getNamespace(), "textures/entity/cake/feature/" + id.getPath() + ".png"))), light, overlay, 0xFFFFFFFF);
		};
		CakeFeatureRenderer blockOnTopFeatureRenderer = (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			if (layer.getSizeContainer().getBites() >= layer.getSizeContainer().getSize() / 2.0f || !(feature instanceof BlockCakeFeature blockFeature)) return;
			matrices.push();
			matrices.translate(0.0f, layer.getSizeContainer().getHeight() / 16.0f, 0.0f);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockFeature.getBlockState(), matrices, vertexConsumers, light, overlay);
			matrices.pop();
		};
		CakeFeatureRendererRegistry.register(CakeFeatures.GLINT, (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			PBCakeBlockRenderer.renderCakeBatter(entity.getBatterList(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getDirectEntityGlint()), light, overlay, 0xFFFFFFFF, PBConfigModel.CakeRenderQuality.SIMPLE);
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.SWEET_BERRIES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLOW_BERRIES, (feature, entity, layer, matrices, vertexConsumers, light, overlay) ->
			cakeLayerFeatureRenderer.render(feature, entity, layer, matrices, vertexConsumers, LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay)
		);
		CakeFeatureRendererRegistry.register(CakeFeatures.HONEY, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PAINTING, (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			RegistryEntry<PaintingVariant> painting = ((PaintingCakeFeature) feature).getPainting(layer, entity.hasWorld() ? entity.getWorld().getRegistryManager() : null);
			if (painting == null) return;
			Sprite sprite = MinecraftClient.getInstance().getPaintingManager().getPaintingSprite(painting.value());

			PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(PBCakeBlockRenderer.getTopRenderLayer(sprite.getAtlasId())), (16.0f - layer.getSizeContainer().getSize()) / 2.0f + layer.getSizeContainer().getBites(), (16.0f - layer.getSizeContainer().getSize()) / 2.0f, layer.getSizeContainer().getHeight(), layer.getSizeContainer().getSize() - layer.getSizeContainer().getBites(), layer.getSizeContainer().getSize(), sprite.getMinU() + (layer.getSizeContainer().getBites() / layer.getSizeContainer().getSize()) * (sprite.getMaxU() - sprite.getMinU()), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 1.0f, 1.0f, light, overlay, 0xFFFFFFFF);
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
	}
}