package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.data.server.recipe.ComplexRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBItems;
import net.pedroricardo.item.recipes.BakingTrayIncreaseRecipe;
import net.pedroricardo.item.recipes.ExpandableBakingTrayRecipe;
import net.pedroricardo.item.recipes.FrostedItemRecipe;

import java.util.concurrent.CompletableFuture;

public class PBRecipeProvider extends FabricRecipeProvider {
    public PBRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
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

        ComplexRecipeJsonBuilder.create(BakingTrayIncreaseRecipe::new).offerTo(exporter, "baking_tray_increase");
        ComplexRecipeJsonBuilder.create(ExpandableBakingTrayRecipe::new).offerTo(exporter, "expandable_baking_tray");
        ComplexRecipeJsonBuilder.create(FrostedItemRecipe::new).offerTo(exporter, "frosted_donut");
    }
}
