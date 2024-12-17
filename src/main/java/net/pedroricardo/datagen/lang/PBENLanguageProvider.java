package net.pedroricardo.datagen.lang;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.util.Util;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeFlavors;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.block.tags.PBTags;
import net.pedroricardo.item.PBItems;

public class PBENLanguageProvider extends FabricLanguageProvider {
    public PBENLanguageProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(PBBlocks.CAKE, "Cake");
        builder.add(PBBlocks.CANDLE_CAKE, "Candle Cake");
        builder.add(PBBlocks.WHITE_CANDLE_CAKE, "White Candle Cake");
        builder.add(PBBlocks.ORANGE_CANDLE_CAKE, "Orange Candle Cake");
        builder.add(PBBlocks.MAGENTA_CANDLE_CAKE, "Magenta Candle Cake");
        builder.add(PBBlocks.LIGHT_BLUE_CANDLE_CAKE, "Light Blue Candle Cake");
        builder.add(PBBlocks.YELLOW_CANDLE_CAKE, "Yellow Candle Cake");
        builder.add(PBBlocks.LIME_CANDLE_CAKE, "Lime Candle Cake");
        builder.add(PBBlocks.PINK_CANDLE_CAKE, "Pink Candle Cake");
        builder.add(PBBlocks.GRAY_CANDLE_CAKE, "Gray Candle Cake");
        builder.add(PBBlocks.LIGHT_GRAY_CANDLE_CAKE, "Light Gray Candle Cake");
        builder.add(PBBlocks.CYAN_CANDLE_CAKE, "Cyan Candle Cake");
        builder.add(PBBlocks.PURPLE_CANDLE_CAKE, "Purple Candle Cake");
        builder.add(PBBlocks.BLUE_CANDLE_CAKE, "Blue Candle Cake");
        builder.add(PBBlocks.BROWN_CANDLE_CAKE, "Brown Candle Cake");
        builder.add(PBBlocks.GREEN_CANDLE_CAKE, "Green Candle Cake");
        builder.add(PBBlocks.RED_CANDLE_CAKE, "Red Candle Cake");
        builder.add(PBBlocks.BLACK_CANDLE_CAKE, "Black Candle Cake");
        builder.add(PBBlocks.CAKE_PART, "Cake Part");
        builder.add(PBBlocks.BEATER, "Beater");
        builder.add(PBBlocks.BAKING_TRAY, "Baking Tray");
        builder.add(PBBlocks.BAKING_TRAY_PART, "Baking Tray Part");
        builder.add(PBBlocks.BAKING_TRAY.getTranslationKey() + ".size", "%sx%sx%s");
        builder.add(PBBlocks.BAKING_TRAY.getTranslationKey() + ".full", "%s%% full");
        builder.add(PBBlocks.CAKE_STAND, "Cake Stand");
        builder.add(PBBlocks.PLATE, "Plate");
        builder.add(PBBlocks.EXPANDABLE_BAKING_TRAY, "Expandable Baking Tray");
        builder.add(PBBlocks.CUPCAKE_TRAY, "Cupcake Tray");
        builder.add(PBBlocks.CUPCAKE, "Cupcake");
        builder.add(PBBlocks.CUPCAKE.getTranslationKey() + ".empty", "Cupcake Liner");
        builder.add(PBBlocks.COOKIE_JAR, "Cookie Jar");
        builder.add(PBBlocks.BUTTER_CHURN, "Butter Churn");
        builder.add(PBBlocks.PIE, "Pie");
        builder.add(PBBlocks.PIE.getTranslationKey() + ".flavor", "%s");
        builder.add(PBBlocks.PIE.getTranslationKey() + ".pie_flavor", "'Oh, boy, what flavor?'");
        builder.add(PBItems.FROSTING_BOTTLE, "Frosting Bottle");
        builder.add(PBItems.DONUT, "Donut");
        builder.add(PBItems.DONUT.getTranslationKey() + ".top", "%s");
        builder.add(PBItems.WHITE_SPRINKLES, "White Sprinkles");
        builder.add(PBItems.ORANGE_SPRINKLES, "Orange Sprinkles");
        builder.add(PBItems.MAGENTA_SPRINKLES, "Magenta Sprinkles");
        builder.add(PBItems.LIGHT_BLUE_SPRINKLES, "Light Blue Sprinkles");
        builder.add(PBItems.YELLOW_SPRINKLES, "Yellow Sprinkles");
        builder.add(PBItems.LIME_SPRINKLES, "Lime Sprinkles");
        builder.add(PBItems.PINK_SPRINKLES, "Pink Sprinkles");
        builder.add(PBItems.GRAY_SPRINKLES, "Gray Sprinkles");
        builder.add(PBItems.LIGHT_GRAY_SPRINKLES, "Light Gray Sprinkles");
        builder.add(PBItems.CYAN_SPRINKLES, "Cyan Sprinkles");
        builder.add(PBItems.PURPLE_SPRINKLES, "Purple Sprinkles");
        builder.add(PBItems.BLUE_SPRINKLES, "Blue Sprinkles");
        builder.add(PBItems.BROWN_SPRINKLES, "Brown Sprinkles");
        builder.add(PBItems.GREEN_SPRINKLES, "Green Sprinkles");
        builder.add(PBItems.RED_SPRINKLES, "Red Sprinkles");
        builder.add(PBItems.BLACK_SPRINKLES, "Black Sprinkles");
        builder.add(PBItems.APPLE_COOKIE, "Apple Cookie");
        builder.add(PBItems.BUTTER, "Butter");
        builder.add(PBItems.BUTTER_CHURN_STAFF, "Butter Churn Staff");
        builder.add(PBItems.DOUGH, "Dough");
        builder.add(PBItems.SHAPED_COOKIE, "Shaped Cookie");
        builder.add(PBBlocks.CAKE.getTranslationKey() + ".flavor_and_top", "%s, %s");
        builder.add(PBBlocks.CAKE.getTranslationKey() + ".flavor", "%s");
        builder.add(CakeFlavors.VANILLA.getTranslationKey(), "Vanilla");
        builder.add(CakeFlavors.CHOCOLATE.getTranslationKey(), "Chocolate");
        builder.add(CakeFlavors.SWEET_BERRY.getTranslationKey(), "Sweet Berry");
        builder.add(CakeFlavors.COAL.getTranslationKey(), "Coal");
        builder.add(CakeFlavors.TNT.getTranslationKey(), "TNT");
        builder.add(CakeFlavors.PUMPKIN.getTranslationKey(), "Pumpkin");
        builder.add(CakeFlavors.MELON.getTranslationKey(), "Melon");
        builder.add(CakeFlavors.BREAD.getTranslationKey(), "Bread");
        builder.add(CakeTops.SUGAR.getTranslationKey(), "Sugar Top");
        builder.add(CakeTops.CHOCOLATE.getTranslationKey(), "Chocolate Top");
        builder.add(CakeTops.SCULK.getTranslationKey(), "Sculk Top");
        builder.add(CakeTops.CHORUS.getTranslationKey(), "Chorus Top");
        builder.add(CakeTops.RED_MUSHROOM.getTranslationKey(), "Red Mushroom Top");
        builder.add(CakeTops.BROWN_MUSHROOM.getTranslationKey(), "Brown Mushroom Top");
        builder.add(CakeTops.SWEET_BERRY.getTranslationKey(), "Sweet Berry Top");
        builder.add(CakeTops.DIRT.getTranslationKey(), "Dirt Top");
        builder.add(CakeTops.GRASS.getTranslationKey(), "Grass Top");
        builder.add(CakeFeatures.GLINT.getTranslationKey(), "Glint");
        builder.add(CakeFeatures.SOULS.getTranslationKey(), "Souls");
        builder.add(CakeFeatures.SWEET_BERRIES.getTranslationKey(), "Sweet Berries");
        builder.add(CakeFeatures.GLOW_BERRIES.getTranslationKey(), "Glow Berries");
        builder.add(CakeFeatures.RED_MUSHROOM.getTranslationKey(), "Red Mushroom");
        builder.add(CakeFeatures.BROWN_MUSHROOM.getTranslationKey(), "Brown Mushroom");
        builder.add(CakeFeatures.END_DUST.getTranslationKey(), "End Dust");
        builder.add(CakeFeatures.HONEY.getTranslationKey(), "Honey");
        builder.add(CakeFeatures.PAINTING.getTranslationKey(), "Painting");
        builder.add(CakeFeatures.DANDELION.getTranslationKey(), "Dandelion");
        builder.add(CakeFeatures.TORCHFLOWER.getTranslationKey(), "Torchflower");
        builder.add(CakeFeatures.POPPY.getTranslationKey(), "Poppy");
        builder.add(CakeFeatures.BLUE_ORCHID.getTranslationKey(), "Blue Orchid");
        builder.add(CakeFeatures.ALLIUM.getTranslationKey(), "Allium");
        builder.add(CakeFeatures.AZURE_BLUET.getTranslationKey(), "Azure Bluet");
        builder.add(CakeFeatures.RED_TULIP.getTranslationKey(), "Red Tulip");
        builder.add(CakeFeatures.ORANGE_TULIP.getTranslationKey(), "Orange Tulip");
        builder.add(CakeFeatures.WHITE_TULIP.getTranslationKey(), "White Tulip");
        builder.add(CakeFeatures.PINK_TULIP.getTranslationKey(), "Pink Tulip");
        builder.add(CakeFeatures.OXEYE_DAISY.getTranslationKey(), "Oxeye Daisy");
        builder.add(CakeFeatures.CORNFLOWER.getTranslationKey(), "Cornflower");
        builder.add(CakeFeatures.WITHER_ROSE.getTranslationKey(), "Wither Rose");
        builder.add(CakeFeatures.LILY_OF_THE_VALLEY.getTranslationKey(), "Lily of the Valley");
        builder.add(CakeFeatures.WHITE_SPRINKLES.getTranslationKey(), "White Sprinkles");
        builder.add(CakeFeatures.ORANGE_SPRINKLES.getTranslationKey(), "Orange Sprinkles");
        builder.add(CakeFeatures.MAGENTA_SPRINKLES.getTranslationKey(), "Magenta Sprinkles");
        builder.add(CakeFeatures.LIGHT_BLUE_SPRINKLES.getTranslationKey(), "Light Blue Sprinkles");
        builder.add(CakeFeatures.YELLOW_SPRINKLES.getTranslationKey(), "Yellow Sprinkles");
        builder.add(CakeFeatures.LIME_SPRINKLES.getTranslationKey(), "Lime Sprinkles");
        builder.add(CakeFeatures.PINK_SPRINKLES.getTranslationKey(), "Pink Sprinkles");
        builder.add(CakeFeatures.GRAY_SPRINKLES.getTranslationKey(), "Gray Sprinkles");
        builder.add(CakeFeatures.LIGHT_GRAY_SPRINKLES.getTranslationKey(), "Light Gray Sprinkles");
        builder.add(CakeFeatures.CYAN_SPRINKLES.getTranslationKey(), "Cyan Sprinkles");
        builder.add(CakeFeatures.PURPLE_SPRINKLES.getTranslationKey(), "Purple Sprinkles");
        builder.add(CakeFeatures.BLUE_SPRINKLES.getTranslationKey(), "Blue Sprinkles");
        builder.add(CakeFeatures.BROWN_SPRINKLES.getTranslationKey(), "Brown Sprinkles");
        builder.add(CakeFeatures.GREEN_SPRINKLES.getTranslationKey(), "Green Sprinkles");
        builder.add(CakeFeatures.RED_SPRINKLES.getTranslationKey(), "Red Sprinkles");
        builder.add(CakeFeatures.BLACK_SPRINKLES.getTranslationKey(), "Black Sprinkles");
        builder.add(CakeFeatures.GLASS.getTranslationKey(), "Glass");
        builder.add(CakeFeatures.PLAYER_HEAD.getTranslationKey(), "Player Head");
        builder.add(CakeFeatures.GRASS.getTranslationKey(), "Short Grass");
        builder.add(CakeFeatures.FERN.getTranslationKey(), "Fern");
        builder.add(Util.createTranslationKey("tag.block", PBTags.Blocks.BAKES_CAKE.id()), "Bakes Cake");
        builder.add(Util.createTranslationKey("tag.block", PBTags.Blocks.CAKES.id()), "Cakes");
        builder.add(Util.createTranslationKey("tag.block", PBTags.Blocks.CANDLE_CAKES.id()), "Candle Cakes");
        builder.add(Util.createTranslationKey("tag.item", PBTags.Items.CAKE_STAND_ITEM.id()), "Cake Stand Item");
        builder.add(Util.createTranslationKey("tag.item", PBTags.Items.COOKIES.id()), "Cookies");
        builder.add(Util.createTranslationKey("tag.pedrosbakery.cake_flavor", PBTags.Flavors.INEDIBLE.id()), "Inedible");
        builder.add(Util.createTranslationKey("tag.pedrosbakery.cake_feature", PBTags.Features.INEDIBLE.id()), "Inedible");
        builder.add(Util.createTranslationKey("tag.pedrosbakery.cake_top", PBTags.Tops.INEDIBLE.id()), "Inedible");
        builder.add("container.cookie_table", "Cookie Table");
        builder.add("container.cookie_table.clear_canvas", "Clear canvas");
        builder.add("subtitles." + PBBlocks.BAKING_TRAY.getTranslationKey() + ".done", "Cake baked");
        builder.add("subtitles." + PBBlocks.PIE.getTranslationKey() + ".done", "Pie baked");
        builder.add("itemGroup." + PedrosBakery.MOD_ID, "Pedro's Bakery");
        builder.add("itemGroup." + PedrosBakery.MOD_ID + ".baking_trays", "Pedro's Bakery: Baking Trays");
        builder.add("text.config.pedrosbakery.option.bakingTrayMinSize", "Baking Tray Minimum Size");
        builder.add("text.config.pedrosbakery.option.bakingTrayMaxSize", "Baking Tray Maximum Size");
        builder.add("text.config.pedrosbakery.option.bakingTrayDefaultSize", "Baking Tray Default Size");
        builder.add("text.config.pedrosbakery.option.bakingTrayMinHeight", "Baking Tray Minimum Height");
        builder.add("text.config.pedrosbakery.option.bakingTrayMaxHeight", "Baking Tray Maximum Height");
        builder.add("text.config.pedrosbakery.option.bakingTrayDefaultHeight", "Baking Tray Default Height");
        builder.add("text.config.pedrosbakery.option.maxCakeHeight", "Maximum Cake Height");
        builder.add("text.config.pedrosbakery.option.maxCakeHeight.tooltip", "Cakes turn into multi-blocks if\nhigher than 16 blocks.");
        builder.add("text.config.pedrosbakery.option.beaterBatterAmount", "Beater Batter Amount");
        builder.add("text.config.pedrosbakery.option.biteSize", "Bite Size");
        builder.add("text.config.pedrosbakery.option.ticksUntilCakeBaked", "Ticks Until Cake Baked");
        builder.add("text.config.pedrosbakery.option.ticksUntilPieBaked", "Ticks Until Pie Baked");
        builder.add("text.config.pedrosbakery.option.cakeBiteFood", "Cake Bite Food");
        builder.add("text.config.pedrosbakery.option.cakeBiteSaturation", "Cake Bite Saturation");
        builder.add("text.config.pedrosbakery.option.cupcakeFood", "Cupcake Food");
        builder.add("text.config.pedrosbakery.option.cupcakeSaturation", "Cupcake Saturation");
        builder.add("text.config.pedrosbakery.option.pieSliceFood", "Pie Slice Food");
        builder.add("text.config.pedrosbakery.option.pieSliceSaturation", "Pie Slice Saturation");
        builder.add("text.config.pedrosbakery.option.cakeRenderQuality", "Cake Render Quality");
        builder.add("text.config.pedrosbakery.enum.cakeRenderQuality.simple", "Simple");
        builder.add("text.config.pedrosbakery.enum.cakeRenderQuality.borders_on_sides", "Borders on Sides");
        builder.add("text.config.pedrosbakery.enum.cakeRenderQuality.borders_on_sides_and_top", "Borders on Sides and Top");
        builder.add("text.config.pedrosbakery.enum.cakeRenderQuality.all_borders", "All Borders");
    }
}
