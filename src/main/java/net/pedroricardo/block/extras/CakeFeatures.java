package net.pedroricardo.block.extras;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.features.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class CakeFeatures {
    public static final ResourceKey<Registry<CakeFeature>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PedrosBakery.MOD_ID, "cake_feature"));
    public static final DeferredRegister<CakeFeature> CAKE_FEATURES = DeferredRegister.create(REGISTRY_KEY, PedrosBakery.MOD_ID);
    public static Supplier<IForgeRegistry<CakeFeature>> registrySupplier; // Note: registry supplier value may be NULL

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void newRegistryEvent(@NotNull NewRegistryEvent event) {
        registrySupplier = event.create(new RegistryBuilder<CakeFeature>()
                .setName(REGISTRY_KEY.location())
                .hasTags());
    }

    public static final RegistryObject<CakeFeature> GLINT = CAKE_FEATURES.register("glint", () -> new CakeFeature());
    public static final RegistryObject<CakeFeature> SOULS = CAKE_FEATURES.register("souls", () -> new ParticleCakeFeature(ParticleTypes.SOUL, 0.06125f));
    public static final RegistryObject<CakeFeature> SWEET_BERRIES = CAKE_FEATURES.register("sweet_berries", () -> new BerriesCakeFeature());
    public static final RegistryObject<CakeFeature> GLOW_BERRIES = CAKE_FEATURES.register("glow_berries", () -> new BerriesCakeFeature());
    public static final RegistryObject<CakeFeature> RED_MUSHROOM = CAKE_FEATURES.register("red_mushroom", () -> new BlockCakeFeature(Blocks.RED_MUSHROOM.defaultBlockState()));
    public static final RegistryObject<CakeFeature> BROWN_MUSHROOM = CAKE_FEATURES.register("brown_mushroom", () -> new BlockCakeFeature(Blocks.BROWN_MUSHROOM.defaultBlockState()));
    public static final RegistryObject<CakeFeature> END_DUST = CAKE_FEATURES.register("end_dust", () -> new ParticleCakeFeature(ParticleTypes.PORTAL, 0.125f));
    public static final RegistryObject<CakeFeature> HONEY = CAKE_FEATURES.register("honey", () -> new CakeFeature());
    public static final RegistryObject<CakeFeature> PAINTING = CAKE_FEATURES.register("painting", () -> new PaintingCakeFeature());
    public static final RegistryObject<CakeFeature> DANDELION = CAKE_FEATURES.register("dandelion", () -> new BlockCakeFeature(Blocks.DANDELION.defaultBlockState()));
    public static final RegistryObject<CakeFeature> TORCHFLOWER = CAKE_FEATURES.register("torchflower", () -> new BlockCakeFeature(Blocks.TORCHFLOWER.defaultBlockState()));
    public static final RegistryObject<CakeFeature> POPPY = CAKE_FEATURES.register("poppy", () -> new BlockCakeFeature(Blocks.POPPY.defaultBlockState()));
    public static final RegistryObject<CakeFeature> BLUE_ORCHID = CAKE_FEATURES.register("blue_orchid", () -> new BlockCakeFeature(Blocks.BLUE_ORCHID.defaultBlockState()));
    public static final RegistryObject<CakeFeature> ALLIUM = CAKE_FEATURES.register("allium", () -> new BlockCakeFeature(Blocks.ALLIUM.defaultBlockState()));
    public static final RegistryObject<CakeFeature> AZURE_BLUET = CAKE_FEATURES.register("azure_bluet", () -> new BlockCakeFeature(Blocks.AZURE_BLUET.defaultBlockState()));
    public static final RegistryObject<CakeFeature> RED_TULIP = CAKE_FEATURES.register("red_tulip", () -> new BlockCakeFeature(Blocks.RED_TULIP.defaultBlockState()));
    public static final RegistryObject<CakeFeature> ORANGE_TULIP = CAKE_FEATURES.register("orange_tulip", () -> new BlockCakeFeature(Blocks.ORANGE_TULIP.defaultBlockState()));
    public static final RegistryObject<CakeFeature> WHITE_TULIP = CAKE_FEATURES.register("white_tulip", () -> new BlockCakeFeature(Blocks.WHITE_TULIP.defaultBlockState()));
    public static final RegistryObject<CakeFeature> PINK_TULIP = CAKE_FEATURES.register("pink_tulip", () -> new BlockCakeFeature(Blocks.PINK_TULIP.defaultBlockState()));
    public static final RegistryObject<CakeFeature> OXEYE_DAISY = CAKE_FEATURES.register("oxeye_daisy", () -> new BlockCakeFeature(Blocks.OXEYE_DAISY.defaultBlockState()));
    public static final RegistryObject<CakeFeature> CORNFLOWER = CAKE_FEATURES.register("cornflower", () -> new BlockCakeFeature(Blocks.CORNFLOWER.defaultBlockState()));
    public static final RegistryObject<CakeFeature> WITHER_ROSE = CAKE_FEATURES.register("wither_rose", () -> new BlockCakeFeature(Blocks.WITHER_ROSE.defaultBlockState()));
    public static final RegistryObject<CakeFeature> LILY_OF_THE_VALLEY = CAKE_FEATURES.register("lily_of_the_valley", () -> new BlockCakeFeature(Blocks.LILY_OF_THE_VALLEY.defaultBlockState()));
    public static final RegistryObject<CakeFeature> WHITE_SPRINKLES = CAKE_FEATURES.register("white_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> ORANGE_SPRINKLES = CAKE_FEATURES.register("orange_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> MAGENTA_SPRINKLES = CAKE_FEATURES.register("magenta_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> LIGHT_BLUE_SPRINKLES = CAKE_FEATURES.register("light_blue_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> YELLOW_SPRINKLES = CAKE_FEATURES.register("yellow_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> LIME_SPRINKLES = CAKE_FEATURES.register("lime_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> PINK_SPRINKLES = CAKE_FEATURES.register("pink_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> GRAY_SPRINKLES = CAKE_FEATURES.register("gray_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> LIGHT_GRAY_SPRINKLES = CAKE_FEATURES.register("light_gray_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> CYAN_SPRINKLES = CAKE_FEATURES.register("cyan_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> PURPLE_SPRINKLES = CAKE_FEATURES.register("purple_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> BLUE_SPRINKLES = CAKE_FEATURES.register("blue_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> BROWN_SPRINKLES = CAKE_FEATURES.register("brown_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> GREEN_SPRINKLES = CAKE_FEATURES.register("green_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> RED_SPRINKLES = CAKE_FEATURES.register("red_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> BLACK_SPRINKLES = CAKE_FEATURES.register("black_sprinkles", () -> new SprinklesCakeFeature());
    public static final RegistryObject<CakeFeature> GLASS = CAKE_FEATURES.register("glass", () -> new CakeFeature());
    public static final RegistryObject<CakeFeature> PLAYER_HEAD = CAKE_FEATURES.register("player_head", () -> new PlayerHeadCakeFeature());
    public static final RegistryObject<CakeFeature> GRASS = CAKE_FEATURES.register("short_grass", () -> new BlockCakeFeature(Blocks.GRASS.defaultBlockState()));
    public static final RegistryObject<CakeFeature> FERN = CAKE_FEATURES.register("fern", () -> new BlockCakeFeature(Blocks.FERN.defaultBlockState()));

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing feature registry");
    }
}