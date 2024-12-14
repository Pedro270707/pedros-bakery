package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.*;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;

import java.util.List;

public class PBComponentTypes {
    public static final ItemComponentType<CakeTop> TOP = of("top", CakeTops.REGISTRY.getCodec());
    public static final ItemComponentType<CakeBatter<FixedBatterSizeContainer>> FIXED_SIZE_BATTER = of("fixed_size_batter", CakeBatter.FIXED_SIZE_CODEC);
    public static final ItemComponentType<CakeBatter<HeightOnlyBatterSizeContainer>> HEIGHT_ONLY_BATTER = of("height_only_batter", CakeBatter.WITH_HEIGHT_CODEC);
    public static final ItemComponentType<CakeBatter<FullBatterSizeContainer>> FULL_BATTER = of("full_batter", CakeBatter.FULL_CODEC);
    public static final ItemComponentType<List<CakeBatter<FullBatterSizeContainer>>> BATTER_LIST = of("batter_list", CakeBatter.FULL_CODEC.listOf());
    public static final ItemComponentType<Integer> SIZE = of("size", Codec.INT);
    public static final ItemComponentType<Integer> HEIGHT = of("height", Codec.INT);
    public static final ItemComponentType<List<CakeFeature>> FEATURES = of("features", CakeFeatures.REGISTRY.getCodec().listOf());
    public static final ItemComponentType<CupcakeTrayBatter> CUPCAKE_TRAY_BATTER = of("cupcake_tray_batter", CupcakeTrayBatter.CODEC);

    private static <T> ItemComponentType<T> of(String id, Codec<T> codec) {
        return new ItemComponentType<>(new Identifier(PedrosBakery.MOD_ID, id), codec);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering component types");
    }
}
