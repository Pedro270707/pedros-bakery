package net.pedroricardo;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PBCakeBlock;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

import java.util.Collections;

public class PBCreativeTab {
    public static final ItemGroup MISCELLANEOUS_ITEM_GROUP = FabricItemGroup.builder().icon(() ->
        new ItemStack(PBItems.CHEESE)
    ).displayName(Text.translatable("itemGroup.pedrosbakery.miscellaneous"))
            .entries((ctx, entries) -> {
                entries.add(new ItemStack(PBBlocks.PIE));
                entries.add(new ItemStack(PBItems.DONUT));
                addFrostingBottles(entries);
                entries.add(new ItemStack(PBItems.CHEESE));
                entries.add(new ItemStack(PBItems.HARD_CHEESE));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_SLAB));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_STAIRS));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_WALL));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_BRICKS));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_BRICK_SLAB));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_BRICK_STAIRS));
                entries.add(new ItemStack(PBBlocks.POLISHED_CHEESE_BRICK_WALL));
                entries.add(new ItemStack(PBItems.TORTILLA));
                entries.add(new ItemStack(PBItems.QUESADILLA));
                entries.add(new ItemStack(PBItems.DOUGH));
                addButter(entries);
            }).build();

    public static final ItemGroup BAKING_TRAY_ITEM_GROUP = FabricItemGroup.builder().icon(() -> {
                ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
                PBHelpers.set(stack, PBComponentTypes.SIZE, 14);
                PBHelpers.set(stack, PBComponentTypes.HEIGHT, 8);
                return stack;
            }).displayName(Text.translatable("itemGroup.pedrosbakery.baking_trays"))
            .entries((ctx, entries) -> {
                addBakingTrays(entries);
            }).build();

    public static final ItemGroup CAKES_AND_CUPCAKES_ITEM_GROUP = FabricItemGroup.builder().icon(() -> {
                CakeBatter<FullBatterSizeContainer> layer = CakeBatter.getFullSizeDefault();
                layer.withBakeTime(PedrosBakery.CONFIG.ticksUntilCakeBaked.get());
                layer.withTop(CakeTops.SUGAR);
                layer.withFeature(CakeFeatures.SWEET_BERRIES);
                return PBCakeBlock.of(Collections.singletonList(layer));
            }).displayName(Text.translatable("itemGroup.pedrosbakery.cakes_and_cupcakes"))
            .entries((ctx, entries) -> {
                entries.add(new ItemStack(PBBlocks.BEATER));
                entries.add(new ItemStack(PBBlocks.CAKE_STAND));
                entries.add(new ItemStack(PBBlocks.PLATE));
                entries.add(new ItemStack(PBBlocks.EXPANDABLE_BAKING_TRAY));
                entries.add(new ItemStack(PBBlocks.CUPCAKE));
                entries.add(new ItemStack(PBBlocks.CUPCAKE_TRAY));
                addSprinkles(entries);
                addFrostingBottles(entries);
                addButter(entries);
            }).build();

    public static final ItemGroup COOKIES_ITEM_GROUP = FabricItemGroup.builder().icon(() ->
                    new ItemStack(PBItems.APPLE_COOKIE)
            ).displayName(Text.translatable("itemGroup.pedrosbakery.cookies"))
            .entries((ctx, entries) -> {
                entries.add(new ItemStack(PBBlocks.COOKIE_TABLE));
                entries.add(new ItemStack(PBItems.APPLE_COOKIE));
                entries.add(new ItemStack(PBItems.SHAPED_COOKIE));
                addFrostingBottles(entries);
                entries.add(new ItemStack(PBBlocks.COOKIE_JAR));
                entries.add(new ItemStack(PBItems.DOUGH));
                addButter(entries);
            }).build();

    private static void addFrostingBottles(ItemGroup.Entries entries) {
        for (CakeTop top : CakeTops.REGISTRY.stream().toList()) {
            ItemStack stack = new ItemStack(PBItems.FROSTING_BOTTLE);
            PBHelpers.set(stack, PBComponentTypes.TOP, top);
            entries.add(stack);
        }
    }

    private static void addBakingTrays(ItemGroup.Entries entries) {
        for (int w = 8; w <= 16; ++w) {
            for (int h = 8; h <= 16; ++h) {
                ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
                PBHelpers.set(stack, PBComponentTypes.SIZE, w);
                PBHelpers.set(stack, PBComponentTypes.HEIGHT, h);
                entries.add(stack);
            }
        }
    }

    private static void addSprinkles(ItemGroup.Entries entries) {
        entries.add(new ItemStack(PBItems.WHITE_SPRINKLES));
        entries.add(new ItemStack(PBItems.ORANGE_SPRINKLES));
        entries.add(new ItemStack(PBItems.MAGENTA_SPRINKLES));
        entries.add(new ItemStack(PBItems.LIGHT_BLUE_SPRINKLES));
        entries.add(new ItemStack(PBItems.YELLOW_SPRINKLES));
        entries.add(new ItemStack(PBItems.LIME_SPRINKLES));
        entries.add(new ItemStack(PBItems.PINK_SPRINKLES));
        entries.add(new ItemStack(PBItems.GRAY_SPRINKLES));
        entries.add(new ItemStack(PBItems.LIGHT_GRAY_SPRINKLES));
        entries.add(new ItemStack(PBItems.CYAN_SPRINKLES));
        entries.add(new ItemStack(PBItems.PURPLE_SPRINKLES));
        entries.add(new ItemStack(PBItems.BLUE_SPRINKLES));
        entries.add(new ItemStack(PBItems.BROWN_SPRINKLES));
        entries.add(new ItemStack(PBItems.GREEN_SPRINKLES));
        entries.add(new ItemStack(PBItems.RED_SPRINKLES));
        entries.add(new ItemStack(PBItems.BLACK_SPRINKLES));
    }

    private static void addButter(ItemGroup.Entries entries) {
        entries.add(new ItemStack(PBBlocks.BUTTER_CHURN));
        entries.add(new ItemStack(PBItems.BUTTER_CHURN_STAFF));
        entries.add(new ItemStack(PBItems.BUTTER));
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, "baking_trays"), BAKING_TRAY_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, "cakes_and_cupcakes"), CAKES_AND_CUPCAKES_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, "cookies"), COOKIES_ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, "miscellaneous"), MISCELLANEOUS_ITEM_GROUP);
        PedrosBakery.LOGGER.debug("Initializing item group registry");
    }
}
