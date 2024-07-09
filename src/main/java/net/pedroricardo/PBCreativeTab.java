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
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeatures;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.helpers.CakeTops;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

import java.util.Collections;

public class PBCreativeTab {
    public static final ItemGroup ITEM_GROUP = FabricItemGroup.builder().icon(() -> {
        CakeBatter layer = CakeBatter.getDefault();
        layer.withBakeTime(PedrosBakery.CONFIG.ticksUntilBaked());
        layer.withTop(CakeTops.SUGAR);
        layer.withFeature(CakeFeatures.SWEET_BERRIES);
        return PBCakeBlock.of(Collections.singletonList(layer));
    }).displayName(Text.translatable("itemGroup.pedrosbakery"))
            .entries((ctx, entries) -> {
                entries.add(new ItemStack(PBBlocks.BEATER));
                addFrostingBottles(entries);
                entries.add(new ItemStack(PBBlocks.CAKE_STAND));
                entries.add(new ItemStack(PBBlocks.EXPANDABLE_BAKING_TRAY));
                entries.add(new ItemStack(PBBlocks.CUPCAKE));
                entries.add(new ItemStack(PBBlocks.CUPCAKE_TRAY));
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
        PedrosBakery.LOGGER.debug("Registering item group");
    }
}
