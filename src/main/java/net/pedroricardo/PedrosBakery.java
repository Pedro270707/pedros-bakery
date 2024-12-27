package net.pedroricardo;

import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod;
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
import net.pedroricardo.util.PBLootFunctionTypes;
import org.joml.Vector2i;
import net.pedroricardo.network.PBNetworkRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

@Mod(value = PedrosBakery.MOD_ID)
@Mod.EventBusSubscriber(modid = PedrosBakery.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PedrosBakery {
	public static final String MOD_ID = "pedrosbakery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final PBConfig CONFIG = ConfigApi.registerAndLoadConfig((Supplier<PBConfig>) PBConfig::new);

	public static final MixingPatternManager MIXING_PATTERN_MANAGER = new MixingPatternManager();
	public static final Map<Item, List<CakeFeature>> ITEM_TO_FEATURES = new HashMap<>();
    public static final PieColorOverrides PIE_COLOR_OVERRIDES = new PieColorOverrides();

	@SubscribeEvent
	public void reloadListenerEvent(AddReloadListenerEvent event) {
		event.addListener(MIXING_PATTERN_MANAGER);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public void clientReloadListenerEvent(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(PIE_COLOR_OVERRIDES);
	}

	public PedrosBakery(IEventBus eventBus, ModContainer container) {
		PBSounds.init();
		PBBlocks.init();
		PBItems.init();
		CakeFlavors.init();
		CakeTops.init();
		PBRecipeSerializers.init();
		PBLootFunctionTypes.init();
		PBComponentTypes.init();
		PBCreativeTab.init();
		PBNetworkRegistry.init();

		PBHelpers.addDefaultComponent(Items.ENCHANTED_GOLDEN_APPLE, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GLINT.get()));
		PBHelpers.addDefaultComponent(Items.NETHER_STAR, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GLINT.get()));
		PBHelpers.addDefaultComponent(Items.SOUL_SOIL, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.SOULS.get()));
		PBHelpers.addDefaultComponent(Items.SOUL_SAND, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.SOULS.get()));
		PBHelpers.addDefaultComponent(Items.SWEET_BERRIES, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.SWEET_BERRIES.get()));
		PBHelpers.addDefaultComponent(Items.RED_MUSHROOM, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.RED_MUSHROOM.get()));
		PBHelpers.addDefaultComponent(Items.BROWN_MUSHROOM, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.BROWN_MUSHROOM.get()));
		PBHelpers.addDefaultComponent(Items.GLOW_BERRIES, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GLOW_BERRIES.get()));
		PBHelpers.addDefaultComponent(Items.ENDER_EYE, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.END_DUST.get()));
		PBHelpers.addDefaultComponent(Items.HONEY_BOTTLE, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.HONEY.get()));
		PBHelpers.addDefaultComponent(Items.PAINTING, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.PAINTING.get()));
		PBHelpers.addDefaultComponent(Items.DANDELION, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.DANDELION.get()));
		PBHelpers.addDefaultComponent(Items.TORCHFLOWER, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.DANDELION.get()));
		PBHelpers.addDefaultComponent(Items.POPPY, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.POPPY.get()));
		PBHelpers.addDefaultComponent(Items.BLUE_ORCHID, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.BLUE_ORCHID.get()));
		PBHelpers.addDefaultComponent(Items.AZURE_BLUET, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.AZURE_BLUET.get()));
		PBHelpers.addDefaultComponent(Items.RED_TULIP, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.RED_TULIP.get()));
		PBHelpers.addDefaultComponent(Items.ORANGE_TULIP, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.ORANGE_TULIP.get()));
		PBHelpers.addDefaultComponent(Items.WHITE_TULIP, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.WHITE_TULIP.get()));
		PBHelpers.addDefaultComponent(Items.PINK_TULIP, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.PINK_TULIP.get()));
		PBHelpers.addDefaultComponent(Items.OXEYE_DAISY, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.OXEYE_DAISY.get()));
		PBHelpers.addDefaultComponent(Items.CORNFLOWER, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.CORNFLOWER.get()));
		PBHelpers.addDefaultComponent(Items.WITHER_ROSE, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.WITHER_ROSE.get()));
		PBHelpers.addDefaultComponent(Items.LILY_OF_THE_VALLEY, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.LILY_OF_THE_VALLEY.get()));
		PBHelpers.addDefaultComponent(Items.GLASS, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GLASS.get()));
		PBHelpers.addDefaultComponent(Items.PLAYER_HEAD, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.PLAYER_HEAD.get()));
		PBHelpers.addDefaultComponent(Items.GRASS, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GRASS.get()));
		PBHelpers.addDefaultComponent(Items.FERN, PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.FERN.get()));
		PBHelpers.addDefaultComponent(PBItems.WHITE_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.WHITE_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.ORANGE_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.ORANGE_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.MAGENTA_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.MAGENTA_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.LIGHT_BLUE_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.LIGHT_BLUE_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.YELLOW_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.YELLOW_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.LIME_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.LIME_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.PINK_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.PINK_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.GRAY_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GRAY_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.LIGHT_GRAY_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.LIGHT_GRAY_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.CYAN_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.CYAN_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.PURPLE_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.PURPLE_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.BLUE_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.BLUE_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.BROWN_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.BROWN_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.GREEN_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.GREEN_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.RED_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.RED_SPRINKLES.get()));
		PBHelpers.addDefaultComponent(PBItems.BLACK_SPRINKLES.get(), PBComponentTypes.FEATURES.get(), Collections.singletonList(CakeFeatures.BLACK_SPRINKLES.get()));

		Set<Vector2i> set = new HashSet<>();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (Math.pow(Math.abs(x - 7.5f), 2.0f) + Math.pow(Math.abs(y - 7.5f), 2.0f) <= 60.0f) {
					set.add(new Vector2i(x, y));
				}
			}
		}
		PBHelpers.addDefaultComponent(PBItems.SHAPED_COOKIE.get(), PBComponentTypes.COOKIE_SHAPE.get(), set);

		eventBus.addListener(PBCreativeTab::buildCreativeTab);
	}
}