package net.pedroricardo.item.recipes;

import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;

@SuppressWarnings("unused")
public class MixingPatterns {
    public static final RegistryKey<Registry<MixingPattern>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "mixing_pattern"));

    public static final RegistryKey<MixingPattern> VANILLA = of("vanilla");
    public static final RegistryKey<MixingPattern> CHOCOLATE = of("chocolate");
    public static final RegistryKey<MixingPattern> CHOCOLATE_FROM_VANILLA = of("chocolate_from_vanilla");
    public static final RegistryKey<MixingPattern> SWEET_BERRY = of("sweet_berry");
    public static final RegistryKey<MixingPattern> SWEET_BERRY_FROM_VANILLA = of("sweet_berry_from_vanilla");
    public static final RegistryKey<MixingPattern> COAL = of("coal");
    public static final RegistryKey<MixingPattern> TNT = of("tnt");
    public static final RegistryKey<MixingPattern> PUMPKIN = of("pumpkin");
    public static final RegistryKey<MixingPattern> MELON = of("melon");
    public static final RegistryKey<MixingPattern> MELON_FROM_SLICE = of("melon_from_slice");
    public static final RegistryKey<MixingPattern> BREAD = of("bread");

    public static final RegistryKey<MixingPattern> SUGAR_TOP = of("sugar_top");
    public static final RegistryKey<MixingPattern> CHOCOLATE_TOP = of("chocolate_top");
    public static final RegistryKey<MixingPattern> CHOCOLATE_TOP_FROM_SUGAR_TOP = of("chocolate_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> SCULK_TOP = of("sculk_top");
    public static final RegistryKey<MixingPattern> SCULK_TOP_FROM_SUGAR_TOP = of("sculk_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> CHORUS_TOP = of("chorus_top");
    public static final RegistryKey<MixingPattern> CHORUS_TOP_FROM_SUGAR_TOP = of("chorus_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> RED_MUSHROOM_TOP = of("red_mushroom_top");
    public static final RegistryKey<MixingPattern> RED_MUSHROOM_TOP_FROM_SUGAR_TOP = of("red_mushroom_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> BROWN_MUSHROOM_TOP = of("brown_mushroom_top");
    public static final RegistryKey<MixingPattern> BROWN_MUSHROOM_TOP_FROM_SUGAR_TOP = of("brown_mushroom_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> SWEET_BERRY_TOP = of("sweet_berry_top");
    public static final RegistryKey<MixingPattern> SWEET_BERRY_TOP_FROM_SUGAR_TOP = of("sweet_berry_top_from_sugar_top");
    public static final RegistryKey<MixingPattern> DIRT_TOP = of("dirt_top");
    public static final RegistryKey<MixingPattern> GRASS_TOP = of("grass_top");

    private static RegistryKey<MixingPattern> of(String id) {
        return RegistryKey.of(REGISTRY_KEY, Identifier.of(PedrosBakery.MOD_ID, id));
    }
}
