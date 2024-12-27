package net.pedroricardo;

import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
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
import net.pedroricardo.util.PBLootFunctionTypes;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.MixingPatternManager;
import net.pedroricardo.item.recipes.PBRecipeSerializers;
import net.pedroricardo.item.recipes.PieColorOverrides;
import org.joml.Vector2i;
import net.pedroricardo.network.PBNetworkRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

public class PedrosBakery implements ModInitializer {
	public static final String MOD_ID = "pedrosbakery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final PBConfig CONFIG = ConfigApi.registerAndLoadConfig((Supplier<PBConfig>) PBConfig::new);

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
		PBLootFunctionTypes.init();
		PBNetworkRegistry.init();

		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(MIXING_PATTERN_MANAGER);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(PIE_COLOR_OVERRIDES);

		PBHelpers.addDefaultComponent(Items.ENCHANTED_GOLDEN_APPLE, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLINT));
		PBHelpers.addDefaultComponent(Items.NETHER_STAR, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLINT));
		PBHelpers.addDefaultComponent(Items.SOUL_SOIL, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SOULS));
		PBHelpers.addDefaultComponent(Items.SOUL_SAND, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SOULS));
		PBHelpers.addDefaultComponent(Items.SWEET_BERRIES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.SWEET_BERRIES));
		PBHelpers.addDefaultComponent(Items.RED_MUSHROOM, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.RED_MUSHROOM));
		PBHelpers.addDefaultComponent(Items.BROWN_MUSHROOM, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BROWN_MUSHROOM));
		PBHelpers.addDefaultComponent(Items.GLOW_BERRIES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLOW_BERRIES));
		PBHelpers.addDefaultComponent(Items.ENDER_EYE, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.END_DUST));
		PBHelpers.addDefaultComponent(Items.HONEY_BOTTLE, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.HONEY));
		PBHelpers.addDefaultComponent(Items.PAINTING, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PAINTING));
		PBHelpers.addDefaultComponent(Items.DANDELION, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.DANDELION));
		PBHelpers.addDefaultComponent(Items.TORCHFLOWER, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.DANDELION));
		PBHelpers.addDefaultComponent(Items.POPPY, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.POPPY));
		PBHelpers.addDefaultComponent(Items.BLUE_ORCHID, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BLUE_ORCHID));
		PBHelpers.addDefaultComponent(Items.AZURE_BLUET, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.AZURE_BLUET));
		PBHelpers.addDefaultComponent(Items.RED_TULIP, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.RED_TULIP));
		PBHelpers.addDefaultComponent(Items.ORANGE_TULIP, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.ORANGE_TULIP));
		PBHelpers.addDefaultComponent(Items.WHITE_TULIP, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.WHITE_TULIP));
		PBHelpers.addDefaultComponent(Items.PINK_TULIP, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PINK_TULIP));
		PBHelpers.addDefaultComponent(Items.OXEYE_DAISY, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.OXEYE_DAISY));
		PBHelpers.addDefaultComponent(Items.CORNFLOWER, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.CORNFLOWER));
		PBHelpers.addDefaultComponent(Items.WITHER_ROSE, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.WITHER_ROSE));
		PBHelpers.addDefaultComponent(Items.LILY_OF_THE_VALLEY, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.LILY_OF_THE_VALLEY));
		PBHelpers.addDefaultComponent(Items.GLASS, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GLASS));
		PBHelpers.addDefaultComponent(Items.PLAYER_HEAD, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PLAYER_HEAD));
		PBHelpers.addDefaultComponent(Items.GRASS, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GRASS));
		PBHelpers.addDefaultComponent(Items.FERN, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.FERN));
		PBHelpers.addDefaultComponent(PBItems.WHITE_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.WHITE_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.ORANGE_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.ORANGE_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.MAGENTA_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.MAGENTA_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.LIGHT_BLUE_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.LIGHT_BLUE_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.YELLOW_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.YELLOW_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.LIME_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.LIME_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.PINK_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PINK_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.GRAY_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GRAY_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.LIGHT_GRAY_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.LIGHT_GRAY_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.CYAN_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.CYAN_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.PURPLE_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.PURPLE_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.BLUE_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BLUE_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.BROWN_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BROWN_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.GREEN_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.GREEN_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.RED_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.RED_SPRINKLES));
		PBHelpers.addDefaultComponent(PBItems.BLACK_SPRINKLES, PBComponentTypes.FEATURES, Collections.singletonList(CakeFeatures.BLACK_SPRINKLES));

		Set<Vector2i> set = new HashSet<>();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (Math.pow(Math.abs(x - 7.5f), 2.0f) + Math.pow(Math.abs(y - 7.5f), 2.0f) <= 60.0f) {
					set.add(new Vector2i(x, y));
				}
			}
		}
		PBHelpers.addDefaultComponent(PBItems.SHAPED_COOKIE, PBComponentTypes.COOKIE_SHAPE, set);
	}
}