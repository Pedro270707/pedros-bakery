package net.pedroricardo.block.tags;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.*;

public class PBTags {
    public static class Blocks {
        public static final TagKey<Block> BAKES_CAKE = of(RegistryKeys.BLOCK, "bakes_cake");
        public static final TagKey<Block> CAKES = of(RegistryKeys.BLOCK, "cakes");
        public static final TagKey<Block> CANDLE_CAKES = of(RegistryKeys.BLOCK, "candle_cakes");
    }

    public static class Items {
        public static final TagKey<Item> CAKE_STAND_ITEM = of(RegistryKeys.ITEM, "cake_stand_item");
        public static final TagKey<Item> COOKIES = of(RegistryKeys.ITEM, "cookies");
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

    private static <T> TagKey<T> of(RegistryKey<Registry<T>> registryKey, String id) {
        return TagKey.of(registryKey, Identifier.of(PedrosBakery.MOD_ID, id));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing tags");
    }
}
