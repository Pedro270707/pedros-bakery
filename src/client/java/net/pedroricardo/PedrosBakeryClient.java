package net.pedroricardo;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
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
	@Override
	public void onInitializeClient() {
		PBModelLayers.init();
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE, (ctx) -> new PBCakeBlockRenderer());
		BuiltinItemRendererRegistry.INSTANCE.register(PBItems.CAKE, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			PBCakeBlockRenderer.RENDER_CAKE.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(PBCakeBlockRenderer.RENDER_CAKE, matrices, vertexConsumers, light, overlay);
		});
		BlockEntityRendererFactories.register(PBBlockEntities.BEATER, BeaterBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.BAKING_TRAY, BakingTrayBlockRenderer::new);
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.EXPANDABLE_BAKING_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(BakingTrayBlockRenderer.RENDER_EXPANDABLE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		BlockEntityRendererFactories.register(PBBlockEntities.CAKE_STAND, CakeStandBlockRenderer::new);
		BlockEntityRendererFactories.register(PBBlockEntities.CUPCAKE_TRAY, CupcakeTrayBlockRenderer::new);
		BuiltinItemRendererRegistry.INSTANCE.register(PBBlocks.CUPCAKE_TRAY, (stack, mode, matrices, vertexConsumers, light, overlay) -> {
			CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY.readComponents(stack);
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(CupcakeTrayBlockRenderer.RENDER_CUPCAKE_TRAY, matrices, vertexConsumers, light, overlay);
		});
		ColorProviderRegistry.ITEM.register((stack, tintIndex) -> {
			CakeTop top = stack.get(PBComponentTypes.TOP);
			if (tintIndex > 0 || top == null) {
				return -1;
			}
			return top.color();
		}, PBItems.FROSTING_BOTTLE);

		CakeFeatureRenderer cakeLayerFeatureRenderer = (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			Identifier id = CakeFeatures.REGISTRY.getId(feature);
			if (id == null) return;
			PBCakeBlockRenderer.renderCakeLayer(entity.getLayers(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(id.getNamespace(), "textures/entity/cake/feature/" + id.getPath() + ".png"))), light, overlay, 0xFFFFFFFF);
		};
		CakeFeatureRenderer blockOnTopFeatureRenderer = (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			if (layer.getBites() >= layer.getSize() / 2.0f || !(feature instanceof BlockCakeFeature blockFeature)) return;
			matrices.push();
			matrices.translate(0.0f, layer.getHeight() / 16.0f, 0.0f);
			MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockFeature.getBlockState(), matrices, vertexConsumers, light, overlay);
			matrices.pop();
		};

		CakeFeatureRendererRegistry.register(CakeFeatures.GLINT, (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			PBCakeBlockRenderer.renderCakeLayer(entity.getLayers(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getDirectEntityGlint()), light, overlay, 0xFFFFFFFF);
		});

		CakeFeatureRendererRegistry.register(CakeFeatures.SWEET_BERRIES, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.RED_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.BROWN_MUSHROOM, blockOnTopFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.GLOW_BERRIES, (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			Identifier id = CakeFeatures.REGISTRY.getId(feature);
			if (id == null) return;
			PBCakeBlockRenderer.renderCakeLayer(entity.getLayers(), layer, matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutout(Identifier.of(id.getNamespace(), "textures/entity/cake/feature/" + id.getPath() + ".png"))), LightmapTextureManager.MAX_LIGHT_COORDINATE, overlay, 0xFFFFFFFF);
		});
		CakeFeatureRendererRegistry.register(CakeFeatures.HONEY, cakeLayerFeatureRenderer);
		CakeFeatureRendererRegistry.register(CakeFeatures.PAINTING, (feature, entity, layer, matrices, vertexConsumers, light, overlay) -> {
			RegistryEntry<PaintingVariant> painting = ((PaintingCakeFeature) feature).getPainting(layer, entity.hasWorld() ? entity.getWorld().getRegistryManager() : null);
			if (painting == null) return;
			Sprite sprite = MinecraftClient.getInstance().getPaintingManager().getPaintingSprite(painting.value());

			PBRenderHelper.createFace(Direction.UP, matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(sprite.getAtlasId())), (16.0f - layer.getSize()) / 2.0f + layer.getBites(), (16.0f - layer.getSize()) / 2.0f, layer.getHeight(), layer.getSize() - layer.getBites(), layer.getSize(), sprite.getMinU() + (layer.getBites() / layer.getSize()) * (sprite.getMaxU() - sprite.getMinU()), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), 1.0f, 1.0f, light, overlay, 0xFFFFFFFF);
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
	}
}