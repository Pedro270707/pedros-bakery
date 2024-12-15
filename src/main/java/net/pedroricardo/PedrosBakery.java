package net.pedroricardo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeFlavors;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class PedrosBakery implements ModInitializer {
	public static final String MOD_ID = "pedrosbakery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final net.pedroricardo.PBConfig CONFIG = net.pedroricardo.PBConfig.createAndLoad();

	public static final MixingPatternManager MIXING_PATTERN_MANAGER = new MixingPatternManager();
	public static final PieColorOverrides PIE_COLOR_OVERRIDES = new PieColorOverrides();

	@Override
	public void onInitialize() {
		PBSounds.init();
		PBBlocks.init();
		PBItems.init();
		CakeFlavors.init();
		CakeTops.init();
		PBComponentTypes.init();
		PBCreativeTab.init();
		PBRecipeSerializers.init();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MIXING_PATTERN_MANAGER);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(PIE_COLOR_OVERRIDES);

		DefaultItemComponentEvents.MODIFY.register((ctx) -> {
			ctx.modify(Items.ENCHANTED_GOLDEN_APPLE, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLINT))
			);

			ctx.modify(Items.NETHER_STAR, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLINT))
			);

			ctx.modify(Items.SOUL_SOIL, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SOULS))
			);

			ctx.modify(Items.SOUL_SAND, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SOULS))
			);

			ctx.modify(Items.SWEET_BERRIES, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SWEET_BERRIES))
			);

			ctx.modify(Items.RED_MUSHROOM, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.RED_MUSHROOM))
			);

			ctx.modify(Items.BROWN_MUSHROOM, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BROWN_MUSHROOM))
			);

			ctx.modify(Items.GLOW_BERRIES, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLOW_BERRIES))
			);

			ctx.modify(Items.ENDER_EYE, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.END_DUST))
			);

			ctx.modify(Items.HONEY_BOTTLE, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.HONEY))
			);

			ctx.modify(Items.PAINTING, builder ->
				builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PAINTING))
			);

			ctx.modify(Items.DANDELION, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.DANDELION))
			);

			ctx.modify(Items.TORCHFLOWER, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.TORCHFLOWER))
			);

			ctx.modify(Items.POPPY, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.POPPY))
			);

			ctx.modify(Items.BLUE_ORCHID, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BLUE_ORCHID))
			);

			ctx.modify(Items.ALLIUM, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.ALLIUM))
			);

			ctx.modify(Items.AZURE_BLUET, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.AZURE_BLUET))
			);

			ctx.modify(Items.RED_TULIP, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.RED_TULIP))
			);

			ctx.modify(Items.ORANGE_TULIP, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.ORANGE_TULIP))
			);

			ctx.modify(Items.WHITE_TULIP, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.WHITE_TULIP))
			);

			ctx.modify(Items.PINK_TULIP, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PINK_TULIP))
			);

			ctx.modify(Items.OXEYE_DAISY, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.OXEYE_DAISY))
			);

			ctx.modify(Items.CORNFLOWER, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.CORNFLOWER))
			);

			ctx.modify(Items.WITHER_ROSE, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.WITHER_ROSE))
			);

			ctx.modify(Items.LILY_OF_THE_VALLEY, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.LILY_OF_THE_VALLEY))
			);

			ctx.modify(Items.GLASS, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLASS))
			);

			ctx.modify(Items.PLAYER_HEAD, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PLAYER_HEAD))
			);

			ctx.modify(Items.SHORT_GRASS, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SHORT_GRASS))
			);

			ctx.modify(Items.FERN, builder ->
					builder.add(PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.FERN))
			);
		});
	}
}