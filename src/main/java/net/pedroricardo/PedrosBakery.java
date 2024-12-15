package net.pedroricardo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceType;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeFlavors;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.MixingPatternManager;
import net.pedroricardo.item.recipes.PBRecipeSerializers;
import net.pedroricardo.item.recipes.PieColorOverrides;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PedrosBakery implements ModInitializer {
	public static final String MOD_ID = "pedrosbakery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final net.pedroricardo.PBConfig CONFIG = net.pedroricardo.PBConfig.createAndLoad();

	public static final MixingPatternManager MIXING_PATTERN_MANAGER = new MixingPatternManager();
	public static final Map<Item, List<CakeFeature>> ITEM_TO_FEATURES = new HashMap<>();
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

		ITEM_TO_FEATURES.put(Items.ENCHANTED_GOLDEN_APPLE, Collections.singletonList(CakeFeatures.GLINT));
		ITEM_TO_FEATURES.put(Items.NETHER_STAR, Collections.singletonList(CakeFeatures.GLINT));
		ITEM_TO_FEATURES.put(Items.SOUL_SOIL, Collections.singletonList(CakeFeatures.SOULS));
		ITEM_TO_FEATURES.put(Items.SOUL_SAND, Collections.singletonList(CakeFeatures.SOULS));
		ITEM_TO_FEATURES.put(Items.SWEET_BERRIES, Collections.singletonList(CakeFeatures.SWEET_BERRIES));
		ITEM_TO_FEATURES.put(Items.RED_MUSHROOM, Collections.singletonList(CakeFeatures.RED_MUSHROOM));
		ITEM_TO_FEATURES.put(Items.BROWN_MUSHROOM, Collections.singletonList(CakeFeatures.BROWN_MUSHROOM));
		ITEM_TO_FEATURES.put(Items.GLOW_BERRIES, Collections.singletonList(CakeFeatures.GLOW_BERRIES));
		ITEM_TO_FEATURES.put(Items.ENDER_EYE, Collections.singletonList(CakeFeatures.END_DUST));
		ITEM_TO_FEATURES.put(Items.HONEY_BOTTLE, Collections.singletonList(CakeFeatures.HONEY));
		ITEM_TO_FEATURES.put(Items.PAINTING, Collections.singletonList(CakeFeatures.PAINTING));
		ITEM_TO_FEATURES.put(Items.DANDELION, Collections.singletonList(CakeFeatures.DANDELION));
		ITEM_TO_FEATURES.put(Items.TORCHFLOWER, Collections.singletonList(CakeFeatures.DANDELION));
		ITEM_TO_FEATURES.put(Items.POPPY, Collections.singletonList(CakeFeatures.POPPY));
		ITEM_TO_FEATURES.put(Items.BLUE_ORCHID, Collections.singletonList(CakeFeatures.BLUE_ORCHID));
		ITEM_TO_FEATURES.put(Items.AZURE_BLUET, Collections.singletonList(CakeFeatures.AZURE_BLUET));
		ITEM_TO_FEATURES.put(Items.RED_TULIP, Collections.singletonList(CakeFeatures.RED_TULIP));
		ITEM_TO_FEATURES.put(Items.ORANGE_TULIP, Collections.singletonList(CakeFeatures.ORANGE_TULIP));
		ITEM_TO_FEATURES.put(Items.WHITE_TULIP, Collections.singletonList(CakeFeatures.WHITE_TULIP));
		ITEM_TO_FEATURES.put(Items.PINK_TULIP, Collections.singletonList(CakeFeatures.PINK_TULIP));
		ITEM_TO_FEATURES.put(Items.OXEYE_DAISY, Collections.singletonList(CakeFeatures.OXEYE_DAISY));
		ITEM_TO_FEATURES.put(Items.CORNFLOWER, Collections.singletonList(CakeFeatures.CORNFLOWER));
		ITEM_TO_FEATURES.put(Items.WITHER_ROSE, Collections.singletonList(CakeFeatures.WITHER_ROSE));
		ITEM_TO_FEATURES.put(Items.LILY_OF_THE_VALLEY, Collections.singletonList(CakeFeatures.LILY_OF_THE_VALLEY));
		ITEM_TO_FEATURES.put(Items.GLASS, Collections.singletonList(CakeFeatures.GLASS));
		ITEM_TO_FEATURES.put(Items.PLAYER_HEAD, Collections.singletonList(CakeFeatures.PLAYER_HEAD));
		ITEM_TO_FEATURES.put(Items.GRASS, Collections.singletonList(CakeFeatures.GRASS));
		ITEM_TO_FEATURES.put(Items.FERN, Collections.singletonList(CakeFeatures.FERN));
		ITEM_TO_FEATURES.put(PBItems.WHITE_SPRINKLES, Collections.singletonList(CakeFeatures.WHITE_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.ORANGE_SPRINKLES, Collections.singletonList(CakeFeatures.ORANGE_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.MAGENTA_SPRINKLES, Collections.singletonList(CakeFeatures.MAGENTA_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.LIGHT_BLUE_SPRINKLES, Collections.singletonList(CakeFeatures.LIGHT_BLUE_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.YELLOW_SPRINKLES, Collections.singletonList(CakeFeatures.YELLOW_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.LIME_SPRINKLES, Collections.singletonList(CakeFeatures.LIME_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.PINK_SPRINKLES, Collections.singletonList(CakeFeatures.PINK_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.GRAY_SPRINKLES, Collections.singletonList(CakeFeatures.GRAY_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.LIGHT_GRAY_SPRINKLES, Collections.singletonList(CakeFeatures.LIGHT_GRAY_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.CYAN_SPRINKLES, Collections.singletonList(CakeFeatures.CYAN_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.PURPLE_SPRINKLES, Collections.singletonList(CakeFeatures.PURPLE_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.BLUE_SPRINKLES, Collections.singletonList(CakeFeatures.BLUE_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.BROWN_SPRINKLES, Collections.singletonList(CakeFeatures.BROWN_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.GREEN_SPRINKLES, Collections.singletonList(CakeFeatures.GREEN_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.RED_SPRINKLES, Collections.singletonList(CakeFeatures.RED_SPRINKLES));
		ITEM_TO_FEATURES.put(PBItems.BLACK_SPRINKLES, Collections.singletonList(CakeFeatures.BLACK_SPRINKLES));
	}
}