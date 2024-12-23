package net.pedroricardo.item.recipes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.pedroricardo.PedrosBakery;

public class MixingPatterns {
    public static final ResourceKey<Registry<MixingPattern>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PedrosBakery.MOD_ID, "mixing_pattern"));

    public static final ResourceKey<MixingPattern> VANILLA = of("vanilla");
    public static final ResourceKey<MixingPattern> CHOCOLATE = of("chocolate");
    public static final ResourceKey<MixingPattern> CHOCOLATE_FROM_VANILLA = of("chocolate_from_vanilla");
    public static final ResourceKey<MixingPattern> SWEET_BERRY = of("sweet_berry");
    public static final ResourceKey<MixingPattern> SWEET_BERRY_FROM_VANILLA = of("sweet_berry_from_vanilla");
    public static final ResourceKey<MixingPattern> COAL = of("coal");
    public static final ResourceKey<MixingPattern> TNT = of("tnt");
    public static final ResourceKey<MixingPattern> PUMPKIN = of("pumpkin");
    public static final ResourceKey<MixingPattern> MELON = of("melon");
    public static final ResourceKey<MixingPattern> MELON_FROM_SLICE = of("melon_from_slice");
    public static final ResourceKey<MixingPattern> BREAD = of("bread");

    public static final ResourceKey<MixingPattern> SUGAR_TOP = of("sugar_top");
    public static final ResourceKey<MixingPattern> CHOCOLATE_TOP = of("chocolate_top");
    public static final ResourceKey<MixingPattern> CHOCOLATE_TOP_FROM_SUGAR_TOP = of("chocolate_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> SCULK_TOP = of("sculk_top");
    public static final ResourceKey<MixingPattern> SCULK_TOP_FROM_SUGAR_TOP = of("sculk_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> CHORUS_TOP = of("chorus_top");
    public static final ResourceKey<MixingPattern> CHORUS_TOP_FROM_SUGAR_TOP = of("chorus_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> RED_MUSHROOM_TOP = of("red_mushroom_top");
    public static final ResourceKey<MixingPattern> RED_MUSHROOM_TOP_FROM_SUGAR_TOP = of("red_mushroom_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> BROWN_MUSHROOM_TOP = of("brown_mushroom_top");
    public static final ResourceKey<MixingPattern> BROWN_MUSHROOM_TOP_FROM_SUGAR_TOP = of("brown_mushroom_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> SWEET_BERRY_TOP = of("sweet_berry_top");
    public static final ResourceKey<MixingPattern> SWEET_BERRY_TOP_FROM_SUGAR_TOP = of("sweet_berry_top_from_sugar_top");
    public static final ResourceKey<MixingPattern> DIRT_TOP = of("dirt_top");
    public static final ResourceKey<MixingPattern> GRASS_TOP = of("grass_top");

    private static ResourceKey<MixingPattern> of(String id) {
        return ResourceKey.create(REGISTRY_KEY, new ResourceLocation(PedrosBakery.MOD_ID, id));
    }
}
