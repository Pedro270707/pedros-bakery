package net.pedroricardo.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;

public class PBItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, PedrosBakery.MOD_ID);

    public static final RegistryObject<Item> FROSTING_BOTTLE = ITEMS.register("frosting_bottle", () -> new FrostingBottleItem(new Item.Properties().stacksTo(1).craftRemainder(Items.GLASS_BOTTLE)));
    public static final RegistryObject<Item> DONUT = ITEMS.register("donut", () -> new DonutItem(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build())));
    public static final RegistryObject<Item> WHITE_SPRINKLES = ITEMS.register("white_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_SPRINKLES = ITEMS.register("orange_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MAGENTA_SPRINKLES = ITEMS.register("magenta_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_BLUE_SPRINKLES = ITEMS.register("light_blue_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_SPRINKLES = ITEMS.register("yellow_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIME_SPRINKLES = ITEMS.register("lime_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PINK_SPRINKLES = ITEMS.register("pink_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GRAY_SPRINKLES = ITEMS.register("gray_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LIGHT_GRAY_SPRINKLES = ITEMS.register("light_gray_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CYAN_SPRINKLES = ITEMS.register("cyan_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_SPRINKLES = ITEMS.register("purple_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_SPRINKLES = ITEMS.register("blue_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BROWN_SPRINKLES = ITEMS.register("brown_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_SPRINKLES = ITEMS.register("green_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RED_SPRINKLES = ITEMS.register("red_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLACK_SPRINKLES = ITEMS.register("black_sprinkles", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> APPLE_COOKIE = ITEMS.register("apple_cookie", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build())));
    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(1).saturationMod(0.1f).build())));
    public static final RegistryObject<Item> BUTTER_CHURN_STAFF = ITEMS.register("butter_churn_staff", () -> new Item(new Item.Properties().stacksTo(1).durability(64)));
    public static final RegistryObject<Item> DOUGH = ITEMS.register("dough", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHAPED_COOKIE = ITEMS.register("shaped_cookie", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build())));

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering items");
    }
}
