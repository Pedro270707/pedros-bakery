package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.*;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.PBRecipeSerializers;

import java.util.function.Consumer;

public class PBRecipeProvider extends FabricRecipeProvider {
    public PBRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBItems.DONUT).input('w', Items.WHEAT).pattern(" w ").pattern("w w").pattern(" w ").criterion(hasItem(Items.WHEAT), conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.WHITE_SPRINKLES).input(Items.COCOA_BEANS).input(Items.WHITE_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.ORANGE_SPRINKLES).input(Items.COCOA_BEANS).input(Items.ORANGE_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.MAGENTA_SPRINKLES).input(Items.COCOA_BEANS).input(Items.MAGENTA_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.LIGHT_BLUE_SPRINKLES).input(Items.COCOA_BEANS).input(Items.LIGHT_BLUE_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.YELLOW_SPRINKLES).input(Items.COCOA_BEANS).input(Items.YELLOW_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.LIME_SPRINKLES).input(Items.COCOA_BEANS).input(Items.LIME_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.PINK_SPRINKLES).input(Items.COCOA_BEANS).input(Items.PINK_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.GRAY_SPRINKLES).input(Items.COCOA_BEANS).input(Items.GRAY_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.LIGHT_GRAY_SPRINKLES).input(Items.COCOA_BEANS).input(Items.LIGHT_GRAY_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.CYAN_SPRINKLES).input(Items.COCOA_BEANS).input(Items.CYAN_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.PURPLE_SPRINKLES).input(Items.COCOA_BEANS).input(Items.PURPLE_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.BLUE_SPRINKLES).input(Items.COCOA_BEANS).input(Items.BLUE_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.BROWN_SPRINKLES).input(Items.COCOA_BEANS).input(Items.BROWN_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.GREEN_SPRINKLES).input(Items.COCOA_BEANS).input(Items.GREEN_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.RED_SPRINKLES).input(Items.COCOA_BEANS).input(Items.RED_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.BLACK_SPRINKLES).input(Items.COCOA_BEANS).input(Items.BLACK_DYE).criterion(hasItem(Items.COCOA_BEANS), conditionsFromItem(Items.COCOA_BEANS)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.APPLE_COOKIE).input('w', Items.WHEAT).input('a', Items.APPLE).pattern("waw").criterion(hasItem(Items.APPLE), conditionsFromItem(Items.APPLE)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PBItems.CHEESE).input(PBTags.Items.CURDLES_CHEESE).input(Items.MILK_BUCKET).criterion("has_item_that_unlocks_cheese_recipes", conditionsFromTag(PBTags.Items.UNLOCKS_CHEESE_RECIPES)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, PBItems.HARD_CHEESE).input(PBTags.Items.CURDLES_CHEESE).input(PBItems.CHEESE).criterion("has_item_that_unlocks_cheese_recipes", conditionsFromTag(PBTags.Items.UNLOCKS_CHEESE_RECIPES)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.BEATER).input('i', Items.IRON_INGOT).input('r', Items.REDSTONE).pattern(" ii").pattern(" ri").pattern("iii").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.BAKING_TRAY).input('i', Items.IRON_INGOT).input('c', Items.COPPER_INGOT).pattern("i i").pattern("iii").pattern("ccc").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.CAKE_STAND).input('g', Items.GLASS_PANE).input('p', PBBlocks.PLATE).pattern("ggg").pattern("gpg").criterion(hasItem(PBBlocks.PLATE), conditionsFromItem(PBBlocks.PLATE)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.PLATE).input('q', Items.QUARTZ).pattern("qqq").criterion(hasItem(Items.QUARTZ), conditionsFromItem(Items.QUARTZ)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.CUPCAKE_TRAY).input('i', Items.IRON_INGOT).input('c', Items.COPPER_INGOT).pattern("iii").pattern("ccc").criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT)).criterion(hasItem(Items.COPPER_INGOT), conditionsFromItem(Items.COPPER_INGOT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.CUPCAKE).input('p', Items.PAPER).pattern("p p").pattern("ppp").criterion(hasItem(Items.PAPER), conditionsFromItem(Items.PAPER)).offerTo(exporter, getRecipeIdentifier(Identifier.of(PedrosBakery.MOD_ID, "cupcake_liner")));
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.COOKIE_JAR).input('s', ItemTags.WOODEN_SLABS).input('g', Blocks.GLASS).pattern("s").pattern("g").criterion("has_cookie", conditionsFromTag(PBTags.Items.COOKIES)).offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBItems.BUTTER_CHURN_STAFF).input('|', Items.STICK).input('s', ItemTags.WOODEN_SLABS).pattern("|").pattern("|").pattern("s").criterion("has_milk_bucket", conditionsFromItem(Items.MILK_BUCKET)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.BUTTER_CHURN).input('l', ItemTags.LOGS).pattern("l").pattern("l").criterion("has_milk_bucket", conditionsFromItem(Items.MILK_BUCKET)).offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.FOOD, PBItems.DOUGH, 3).input(Items.WHEAT).input(PBItems.BUTTER).input(Items.WATER_BUCKET).criterion("has_wheat", conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.PIE).input('i', Items.IRON_NUGGET).pattern("i i").pattern("iii").criterion("has_wheat", conditionsFromItem(Items.WHEAT)).offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, PBBlocks.COOKIE_TABLE).input('l', ItemTags.LOGS).input('_', ItemTags.WOODEN_SLABS).pattern("__").pattern("ll").criterion("has_wheat", conditionsFromItem(Items.WHEAT)).offerTo(exporter);

        offer2x2CompactingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICKS, PBItems.HARD_CHEESE);
        createSlabRecipe(RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICK_SLAB, Ingredient.ofItems(PBBlocks.CHEESE_BRICKS)).criterion("has_cheese_bricks", VanillaRecipeProvider.conditionsFromItem(PBBlocks.CHEESE_BRICKS)).offerTo(exporter);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICK_SLAB, PBBlocks.CHEESE_BRICKS, 2);
        createStairsRecipe(PBBlocks.CHEESE_BRICK_STAIRS, Ingredient.ofItems(PBBlocks.CHEESE_BRICKS)).criterion("has_cheese_bricks", VanillaRecipeProvider.conditionsFromItem(PBBlocks.CHEESE_BRICKS)).offerTo(exporter);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICK_STAIRS, PBBlocks.CHEESE_BRICKS, 1);
        offerWallRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICK_WALL, PBBlocks.CHEESE_BRICKS);
        offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, PBBlocks.CHEESE_BRICK_WALL, PBBlocks.CHEESE_BRICKS, 1);

        ComplexRecipeJsonBuilder.create(PBRecipeSerializers.BAKING_TRAY_INCREASE).offerTo(exporter, "baking_tray_increase");
        ComplexRecipeJsonBuilder.create(PBRecipeSerializers.EXPANDABLE_BAKING_TRAY).offerTo(exporter, "expandable_baking_tray");
        ComplexRecipeJsonBuilder.create(PBRecipeSerializers.FROSTED_DONUT).offerTo(exporter, "frosted_donut");
    }
}
