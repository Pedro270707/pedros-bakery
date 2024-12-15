package net.pedroricardo.item;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;

public class PBItems {
    public static final Item FROSTING_BOTTLE = register("frosting_bottle", new FrostingBottleItem(new Item.Settings().maxCount(1).recipeRemainder(Items.GLASS_BOTTLE)));
    public static final Item DONUT = register("donut", new DonutItem(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build())));
    public static final Item WHITE_SPRINKLES = register("white_sprinkles", new Item(new Item.Settings()));
    public static final Item ORANGE_SPRINKLES = register("orange_sprinkles", new Item(new Item.Settings()));
    public static final Item MAGENTA_SPRINKLES = register("magenta_sprinkles", new Item(new Item.Settings()));
    public static final Item LIGHT_BLUE_SPRINKLES = register("light_blue_sprinkles", new Item(new Item.Settings()));
    public static final Item YELLOW_SPRINKLES = register("yellow_sprinkles", new Item(new Item.Settings()));
    public static final Item LIME_SPRINKLES = register("lime_sprinkles", new Item(new Item.Settings()));
    public static final Item PINK_SPRINKLES = register("pink_sprinkles", new Item(new Item.Settings()));
    public static final Item GRAY_SPRINKLES = register("gray_sprinkles", new Item(new Item.Settings()));
    public static final Item LIGHT_GRAY_SPRINKLES = register("light_gray_sprinkles", new Item(new Item.Settings()));
    public static final Item CYAN_SPRINKLES = register("cyan_sprinkles", new Item(new Item.Settings()));
    public static final Item PURPLE_SPRINKLES = register("purple_sprinkles", new Item(new Item.Settings()));
    public static final Item BLUE_SPRINKLES = register("blue_sprinkles", new Item(new Item.Settings()));
    public static final Item BROWN_SPRINKLES = register("brown_sprinkles", new Item(new Item.Settings()));
    public static final Item GREEN_SPRINKLES = register("green_sprinkles", new Item(new Item.Settings()));
    public static final Item RED_SPRINKLES = register("red_sprinkles", new Item(new Item.Settings()));
    public static final Item BLACK_SPRINKLES = register("black_sprinkles", new Item(new Item.Settings()));
    public static final Item APPLE_COOKIE = register("apple_cookie", new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(2).saturationModifier(0.1f).build())));
    public static final Item BUTTER = register("butter", new Item(new Item.Settings().food(new FoodComponent.Builder().hunger(1).saturationModifier(0.1f).build())));
    public static final Item BUTTER_CHURN_STAFF = register("butter_churn_staff", new Item(new Item.Settings().maxCount(1).maxDamage(64)));
    public static final Item DOUGH = register("dough", new Item(new Item.Settings()));

    public static Item register(String id, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PedrosBakery.MOD_ID, id), item);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering items");
    }
}
