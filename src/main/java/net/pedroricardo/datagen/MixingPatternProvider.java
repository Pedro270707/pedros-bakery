package net.pedroricardo.datagen;

import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeFlavors;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.block.extras.beater.Liquid;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.MixingPattern;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MixingPatternProvider extends AbstractMixingPatternProvider {
    public MixingPatternProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    public static final Identifier VANILLA = Identifier.of(PedrosBakery.MOD_ID, "vanilla");
    public static final Identifier CHOCOLATE = Identifier.of(PedrosBakery.MOD_ID, "chocolate");
    public static final Identifier CHOCOLATE_FROM_VANILLA = Identifier.of(PedrosBakery.MOD_ID, "chocolate_from_vanilla");
    public static final Identifier SWEET_BERRY = Identifier.of(PedrosBakery.MOD_ID, "sweet_berry");
    public static final Identifier SWEET_BERRY_FROM_VANILLA = Identifier.of(PedrosBakery.MOD_ID, "sweet_berry_from_vanilla");
    public static final Identifier COAL = Identifier.of(PedrosBakery.MOD_ID, "coal");
    public static final Identifier TNT = Identifier.of(PedrosBakery.MOD_ID, "tnt");
    public static final Identifier PUMPKIN = Identifier.of(PedrosBakery.MOD_ID, "pumpkin");
    public static final Identifier MELON = Identifier.of(PedrosBakery.MOD_ID, "melon");
    public static final Identifier MELON_FROM_SLICE = Identifier.of(PedrosBakery.MOD_ID, "melon_from_slice");
    public static final Identifier BREAD = Identifier.of(PedrosBakery.MOD_ID, "bread");

    public static final Identifier SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "sugar_top");
    public static final Identifier CHOCOLATE_TOP = Identifier.of(PedrosBakery.MOD_ID, "chocolate_top");
    public static final Identifier CHOCOLATE_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "chocolate_top_from_sugar_top");
    public static final Identifier SCULK_TOP = Identifier.of(PedrosBakery.MOD_ID, "sculk_top");
    public static final Identifier SCULK_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "sculk_top_from_sugar_top");
    public static final Identifier CHORUS_TOP = Identifier.of(PedrosBakery.MOD_ID, "chorus_top");
    public static final Identifier CHORUS_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "chorus_top_from_sugar_top");
    public static final Identifier RED_MUSHROOM_TOP = Identifier.of(PedrosBakery.MOD_ID, "red_mushroom_top");
    public static final Identifier RED_MUSHROOM_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "red_mushroom_top_from_sugar_top");
    public static final Identifier BROWN_MUSHROOM_TOP = Identifier.of(PedrosBakery.MOD_ID, "brown_mushroom_top");
    public static final Identifier BROWN_MUSHROOM_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "brown_mushroom_top_from_sugar_top");
    public static final Identifier SWEET_BERRY_TOP = Identifier.of(PedrosBakery.MOD_ID, "sweet_berry_top");
    public static final Identifier SWEET_BERRY_TOP_FROM_SUGAR_TOP = Identifier.of(PedrosBakery.MOD_ID, "sweet_berry_top_from_sugar_top");
    public static final Identifier DIRT_TOP = Identifier.of(PedrosBakery.MOD_ID, "dirt_top");
    public static final Identifier GRASS_TOP = Identifier.of(PedrosBakery.MOD_ID, "grass_top");

    @Override
    public void generate(MixingPatternExporter exporter) {
        exporter.accept(VANILLA, new MixingPattern(List.of(Ingredient.ofItems(Items.WHEAT), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.VANILLA)));
        exporter.accept(CHOCOLATE, new MixingPattern(List.of(Ingredient.ofItems(Items.WHEAT), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER), Ingredient.ofItems(Items.COCOA_BEANS)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.CHOCOLATE)));
        exporter.accept(CHOCOLATE_FROM_VANILLA, new MixingPattern(List.of(Ingredient.ofItems(Items.COCOA_BEANS)), new Liquid.Mixture(CakeFlavors.VANILLA), new Liquid.Mixture(CakeFlavors.CHOCOLATE)));
        exporter.accept(SWEET_BERRY, new MixingPattern(List.of(Ingredient.ofItems(Items.WHEAT), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER), Ingredient.ofItems(Items.SWEET_BERRIES)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.SWEET_BERRY)));
        exporter.accept(SWEET_BERRY_FROM_VANILLA, new MixingPattern(List.of(Ingredient.ofItems(Items.SWEET_BERRIES)), new Liquid.Mixture(CakeFlavors.VANILLA), new Liquid.Mixture(CakeFlavors.SWEET_BERRY)));
        exporter.accept(COAL, new MixingPattern(List.of(Ingredient.ofItems(Items.COAL)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.COAL)));
        exporter.accept(TNT, new MixingPattern(List.of(Ingredient.ofItems(Items.TNT)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.TNT)));
        exporter.accept(PUMPKIN, new MixingPattern(List.of(Ingredient.ofItems(Items.PUMPKIN), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.PUMPKIN)));
        exporter.accept(MELON, new MixingPattern(List.of(Ingredient.ofItems(Items.MELON), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.PUMPKIN)));
        exporter.accept(MELON_FROM_SLICE, new MixingPattern(List.of(Ingredient.ofItems(Items.MELON_SLICE), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.PUMPKIN)));
        exporter.accept(BREAD, new MixingPattern(List.of(Ingredient.ofItems(Items.BREAD), Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.EGG), Ingredient.ofItems(PBItems.BUTTER)), new Liquid.Milk(), new Liquid.Mixture(CakeFlavors.BREAD)));

        exporter.accept(SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.SUGAR)));
        exporter.accept(CHOCOLATE_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.COCOA_BEANS)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.CHOCOLATE)));
        exporter.accept(CHOCOLATE_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.COCOA_BEANS)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.CHOCOLATE)));
        exporter.accept(SCULK_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.SCULK, Items.SCULK_CATALYST, Items.SCULK_SENSOR, Items.SCULK_SHRIEKER, Items.SCULK_VEIN)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.SCULK)));
        exporter.accept(SCULK_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SCULK, Items.SCULK_CATALYST, Items.SCULK_SENSOR, Items.SCULK_SHRIEKER, Items.SCULK_VEIN)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.SCULK)));
        exporter.accept(CHORUS_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.CHORUS)));
        exporter.accept(CHORUS_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.CHORUS_FRUIT, Items.POPPED_CHORUS_FRUIT)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.CHORUS)));
        exporter.accept(RED_MUSHROOM_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.RED_MUSHROOM)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.RED_MUSHROOM)));
        exporter.accept(RED_MUSHROOM_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.RED_MUSHROOM)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.RED_MUSHROOM)));
        exporter.accept(BROWN_MUSHROOM_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.BROWN_MUSHROOM)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.BROWN_MUSHROOM)));
        exporter.accept(BROWN_MUSHROOM_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.BROWN_MUSHROOM)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.BROWN_MUSHROOM)));
        exporter.accept(SWEET_BERRY_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SUGAR), Ingredient.ofItems(Items.SWEET_BERRIES)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.SWEET_BERRY)));
        exporter.accept(SWEET_BERRY_TOP_FROM_SUGAR_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SWEET_BERRIES)), new Liquid.Frosting(CakeTops.SUGAR), new Liquid.Frosting(CakeTops.SWEET_BERRY)));
        exporter.accept(DIRT_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.DIRT)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.DIRT)));
        exporter.accept(GRASS_TOP, new MixingPattern(List.of(Ingredient.ofItems(Items.SHORT_GRASS, Items.TALL_GRASS)), new Liquid.Milk(), new Liquid.Frosting(CakeTops.GRASS)));
    }
}
