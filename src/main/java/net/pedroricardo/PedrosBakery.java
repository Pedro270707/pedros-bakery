package net.pedroricardo;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.item.Items;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.helpers.CakeFeatures;
import net.pedroricardo.block.helpers.CakeFlavors;
import net.pedroricardo.block.helpers.CakeTops;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.PBRecipeSerializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class PedrosBakery implements ModInitializer {
	public static final String MOD_ID = "pedrosbakery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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
		});
	}
}