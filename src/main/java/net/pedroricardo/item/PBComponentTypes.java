package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.*;

import java.util.List;
import java.util.function.UnaryOperator;

public class PBComponentTypes extends DataComponentTypes {
    public static final ComponentType<CakeTop> TOP = register("top", (builder) -> builder.codec(CakeTops.REGISTRY.getCodec()).packetCodec(PacketCodecs.registryCodec(CakeTops.REGISTRY.getCodec())).cache());
    public static final ComponentType<CakeBatter> BATTER = register("batter", (builder) -> builder.codec(CakeBatter.CODEC).packetCodec(CakeBatter.PACKET_CODEC).cache());
    public static final ComponentType<List<CakeBatter>> BATTER_LIST = register("batter_list", (builder) -> builder.codec(CakeBatter.CODEC.listOf()).packetCodec(PacketCodecs.codec(CakeBatter.CODEC.listOf())).cache());
    public static final ComponentType<Integer> SIZE = register("size", (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).cache());
    public static final ComponentType<Integer> HEIGHT = register("height", (builder) -> builder.codec(Codec.INT).packetCodec(PacketCodecs.INTEGER).cache());
    public static final ComponentType<List<CakeFeature>> FEATURES = register("features", (builder) -> builder.codec(CakeFeatures.REGISTRY.getCodec().listOf()).packetCodec(PacketCodecs.codec(CakeFeatures.REGISTRY.getCodec().listOf())).cache());
    public static final ComponentType<CupcakeTrayBatter> CUPCAKE_TRAY_BATTER = register("cupcake_tray_batter", (builder) -> builder.codec(CupcakeTrayBatter.CODEC).packetCodec(CupcakeTrayBatter.PACKET_CODEC).cache());

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(PedrosBakery.MOD_ID, id), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering component types");
    }
}
