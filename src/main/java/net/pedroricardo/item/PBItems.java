package net.pedroricardo.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.helpers.CakeBatter;

import java.util.Collections;

public class PBItems {
    public static final Item CAKE = register("cake", new PBCakeBlockItem(PBBlocks.CAKE, new Item.Settings().component(PBComponentTypes.BATTER_LIST, Collections.singletonList(CakeBatter.getDefault().withBakeTime(2000)))));
    public static final Item FROSTING_BOTTLE = register("frosting_bottle", new FrostingBottleItem(new Item.Settings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE)));

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PedrosBakery.MOD_ID, id), item);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering items");
    }
}
