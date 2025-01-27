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
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

import java.util.Collections;

public class PBCreativeTab {
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder().icon(() -> {
        CakeBatter layer = CakeBatter.getFullSizeDefault();
        layer.withBakeTime(PedrosBakery.CONFIG.ticksUntilCakeBaked.get());
        layer.withTop(CakeTops.SUGAR);
        layer.withFeature(CakeFeatures.SWEET_BERRIES);
        return PBCakeBlock.of(Collections.singletonList(layer));
    }).displayName(Text.translatable("itemGroup.pedrosbakery"))
            .entries((ctx, entries) -> {
                entries.add(new ItemStack(PBBlocks.BEATER));
                addFrostingBottles(entries);
                entries.add(new ItemStack(PBBlocks.CAKE_STAND));
                entries.add(new ItemStack(PBBlocks.PLATE));
                entries.add(new ItemStack(PBBlocks.EXPANDABLE_BAKING_TRAY));
                entries.add(new ItemStack(PBBlocks.CUPCAKE));
                entries.add(new ItemStack(PBBlocks.CUPCAKE_TRAY));
                entries.add(new ItemStack(PBBlocks.COOKIE_JAR));
                entries.add(new ItemStack(PBBlocks.BUTTER_CHURN));
                entries.add(new ItemStack(PBBlocks.PIE));
                entries.add(new ItemStack(PBBlocks.COOKIE_TABLE));
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
                entries.add(new ItemStack(PBItems.DONUT));
                entries.add(new ItemStack(PBItems.APPLE_COOKIE));
                entries.add(new ItemStack(PBItems.BUTTER));
                entries.add(new ItemStack(PBItems.BUTTER_CHURN_STAFF));
                entries.add(new ItemStack(PBItems.DOUGH));
                entries.add(new ItemStack(PBItems.SHAPED_COOKIE));
            }).build();

    public static final ItemGroup BAKING_TRAY_ITEM_GROUP = FabricItemGroup.builder().icon(() -> {
                ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
                stack.set(PBComponentTypes.SIZE, 14);
                stack.set(PBComponentTypes.HEIGHT, 8);
                return stack;
            }).displayName(Text.translatable("itemGroup.pedrosbakery.baking_trays"))
            .entries((ctx, entries) -> {
                addBakingTrays(entries);
            }).build();

    private static void addFrostingBottles(ItemGroup.Entries entries) {
        for (CakeTop top : CakeTops.REGISTRY.stream().toList()) {
            ItemStack stack = new ItemStack(PBItems.FROSTING_BOTTLE);
            stack.set(PBComponentTypes.TOP, top);
            entries.add(stack);
        }
    }

    private static void addBakingTrays(ItemGroup.Entries entries) {
        for (int w = 8; w <= 16; ++w) {
            for (int h = 8; h <= 16; ++h) {
                ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.asItem());
                stack.set(PBComponentTypes.SIZE, w);
                stack.set(PBComponentTypes.HEIGHT, h);
                entries.add(stack);
            }
        }
    }

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, PedrosBakery.MOD_ID), ITEM_GROUP);
        Registry.register(Registries.ITEM_GROUP, Identifier.of(PedrosBakery.MOD_ID, "baking_trays"), BAKING_TRAY_ITEM_GROUP);
        PedrosBakery.LOGGER.debug("Initializing item group registry");
    }
}
