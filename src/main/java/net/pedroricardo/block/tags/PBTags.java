package net.pedroricardo.block.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.*;

public class PBTags {
    public static class Blocks {
        public static final TagKey<Block> BAKES_CAKE = of(Registries.BLOCK, "bakes_cake");
        public static final TagKey<Block> CAKES = of(Registries.BLOCK, "cakes");
        public static final TagKey<Block> CANDLE_CAKES = of(Registries.BLOCK, "candle_cakes");
    }

    public static class Items {
        public static final TagKey<Item> CAKE_STAND_ITEM = of(Registries.ITEM, "cake_stand_item");
        public static final TagKey<Item> COOKIES = of(Registries.ITEM, "cookies");
        public static final TagKey<Item> COOKIE_INGREDIENTS = of(Registries.ITEM, "cookie_ingredients");
    }

    public static class Flavors {
        public static final TagKey<CakeFlavor> INEDIBLE = of(CakeFlavors.REGISTRY_KEY, "inedible");
    }

    public static class Tops {
        public static final TagKey<CakeTop> INEDIBLE = of(CakeTops.REGISTRY_KEY, "inedible");
    }

    public static class Features {
        public static final TagKey<CakeFeature> INEDIBLE = of(CakeFeatures.REGISTRY_KEY, "inedible");
    }

    private static <T> TagKey<T> of(ResourceKey<Registry<T>> registryKey, String id) {
        return TagKey.create(registryKey, new ResourceLocation(PedrosBakery.MOD_ID, id));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing tags");
    }
}
