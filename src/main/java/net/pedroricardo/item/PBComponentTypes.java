package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.*;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.UnaryOperator;

public class PBComponentTypes extends DataComponentTypes {
    public static final ComponentType<CakeTop> TOP = register("top", (builder) -> builder.codec(CakeTops.REGISTRY.getCodec()).packetCodec(PacketCodecs.registryCodec(CakeTops.REGISTRY.getCodec())).cache());
    public static final ComponentType<CakeBatter<FixedBatterSizeContainer>> FIXED_SIZE_BATTER = register("fixed_size_batter", (builder) -> builder.codec(CakeBatter.FIXED_SIZE_CODEC).packetCodec(CakeBatter.FIXED_SIZE_PACKET_CODEC).cache());
    public static final ComponentType<CakeBatter<HeightOnlyBatterSizeContainer>> HEIGHT_ONLY_BATTER = register("height_only_batter", (builder) -> builder.codec(CakeBatter.WITH_HEIGHT_CODEC).packetCodec(CakeBatter.HEIGHT_ONLY_PACKET_CODEC).cache());
    public static final ComponentType<CakeBatter<FullBatterSizeContainer>> FULL_BATTER = register("full_batter", (builder) -> builder.codec(CakeBatter.FULL_CODEC).packetCodec(CakeBatter.FULL_PACKET_CODEC).cache());
    public static final ComponentType<List<CakeBatter<FullBatterSizeContainer>>> BATTER_LIST = register("batter_list", (builder) -> builder.codec(CakeBatter.FULL_CODEC.listOf()).packetCodec(PacketCodecs.codec(CakeBatter.FULL_CODEC.listOf())).cache());
    public static final ComponentType<Integer> SIZE = register("size", (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).cache());
    public static final ComponentType<Integer> HEIGHT = register("height", (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).cache());
    public static final ComponentType<List<CakeFeature>> FEATURES = register("features", (builder) -> builder.codec(CakeFeatures.REGISTRY.getCodec().listOf()).packetCodec(PacketCodecs.codec(CakeFeatures.REGISTRY.getCodec().listOf())).cache());
    public static final ComponentType<CupcakeTrayBatter> CUPCAKE_TRAY_BATTER = register("cupcake_tray_batter", (builder) -> builder.codec(CupcakeTrayBatter.CODEC).packetCodec(CupcakeTrayBatter.PACKET_CODEC).cache());
    public static final ComponentType<PieDataComponent> PIE_DATA = register("pie_data", (builder) -> builder.codec(PieDataComponent.CODEC).packetCodec(PieDataComponent.PACKET_CODEC).cache());
    public static final ComponentType<Set<Vector2i>> COOKIE_SHAPE = register("cookie_shape", (builder) -> builder.codec(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new)).packetCodec(PBCodecs.PACKET_VECTOR_2I.collect(PacketCodecs.toList()).xmap(HashSet::new, ArrayList::new)).cache());

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(PedrosBakery.MOD_ID, id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing component type registry");
    }
}
