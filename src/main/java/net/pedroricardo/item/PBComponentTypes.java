package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.*;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class PBComponentTypes {
    public static final ResourceKey<Registry<DataComponentType<?>>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PedrosBakery.MOD_ID, "data_component_type"));
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(REGISTRY_KEY, PedrosBakery.MOD_ID);
    public static Supplier<IForgeRegistry<DataComponentType<?>>> registrySupplier; // Note: registry supplier value may be NULL

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void newRegistryEvent(@NotNull NewRegistryEvent event) {
        registrySupplier = event.create(new RegistryBuilder<DataComponentType<?>>()
                .setName(REGISTRY_KEY.location())
                .hasTags());
    }

    public static final RegistryObject<DataComponentType<CakeTop>> TOP = register("top", CakeTops.registrySupplier.get().getCodec());
    public static final RegistryObject<DataComponentType<CakeBatter<FixedBatterSizeContainer>>> FIXED_SIZE_BATTER = register("fixed_size_batter", CakeBatter.FIXED_SIZE_CODEC);
    public static final RegistryObject<DataComponentType<CakeBatter<HeightOnlyBatterSizeContainer>>> HEIGHT_ONLY_BATTER = register("height_only_batter", CakeBatter.WITH_HEIGHT_CODEC);
    public static final RegistryObject<DataComponentType<CakeBatter<FullBatterSizeContainer>>> FULL_BATTER = register("full_batter", CakeBatter.FULL_CODEC);
    public static final RegistryObject<DataComponentType<List<CakeBatter<FullBatterSizeContainer>>>> BATTER_LIST = register("batter_list", CakeBatter.FULL_CODEC.listOf());
    public static final RegistryObject<DataComponentType<Integer>> SIZE = register("size", Codec.INT);
    public static final RegistryObject<DataComponentType<Integer>> HEIGHT = register("height", Codec.INT);
    public static final RegistryObject<DataComponentType<List<CakeFeature>>> FEATURES = register("features", CakeFeatures.registrySupplier.get().getCodec().listOf());
    public static final RegistryObject<DataComponentType<CupcakeTrayBatter>> CUPCAKE_TRAY_BATTER = register("cupcake_tray_batter", CupcakeTrayBatter.CODEC);
    public static final RegistryObject<DataComponentType<PieDataComponent>> PIE_DATA = register("pie_data", PieDataComponent.CODEC);
    public static final RegistryObject<DataComponentType<Set<Vector2i>>> COOKIE_SHAPE = register("cookie_shape", PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new));

    private static <T> RegistryObject<DataComponentType<T>> register(String id, Codec<T> codec) {
        return DATA_COMPONENT_TYPES.register(id, () -> new DataComponentType<>(new ResourceLocation(PedrosBakery.MOD_ID, id), codec));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering component types");
    }
}
