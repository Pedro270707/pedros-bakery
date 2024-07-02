package net.pedroricardo.block.helpers;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.features.BerriesCakeFeature;
import net.pedroricardo.block.helpers.features.BlockCakeFeature;
import net.pedroricardo.block.helpers.features.PaintingCakeFeature;
import net.pedroricardo.block.helpers.features.ParticleCakeFeature;

public class CakeFeatures {
    public static final RegistryKey<Registry<CakeFeature>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "cake_feature"));
    public static SimpleRegistry<CakeFeature> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable(), true)).buildAndRegister();

    private static CakeFeature register(String name, CakeFeature top) {
        return Registry.register(REGISTRY, Identifier.of(PedrosBakery.MOD_ID, name), top);
    }

    public static final CakeFeature GLINT = register("glint", new CakeFeature());
    public static final CakeFeature SOULS = register("souls", new ParticleCakeFeature(ParticleTypes.SOUL, 0.06125f));
    public static final CakeFeature SWEET_BERRIES = register("sweet_berries", new BerriesCakeFeature());
    public static final CakeFeature GLOW_BERRIES = register("glow_berries", new BerriesCakeFeature());
    public static final CakeFeature RED_MUSHROOM = register("red_mushroom", new BlockCakeFeature(Blocks.RED_MUSHROOM.getDefaultState()));
    public static final CakeFeature BROWN_MUSHROOM = register("brown_mushroom", new BlockCakeFeature(Blocks.BROWN_MUSHROOM.getDefaultState()));
    public static final CakeFeature END_DUST = register("end_dust", new ParticleCakeFeature(ParticleTypes.PORTAL, 0.125f));
    public static final CakeFeature HONEY = register("honey", new CakeFeature());
    public static final CakeFeature PAINTING = register("painting", new PaintingCakeFeature());
    public static final CakeFeature DANDELION = register("dandelion", new BlockCakeFeature(Blocks.DANDELION.getDefaultState()));
    public static final CakeFeature TORCHFLOWER = register("torchflower", new BlockCakeFeature(Blocks.TORCHFLOWER.getDefaultState()));
    public static final CakeFeature POPPY = register("poppy", new BlockCakeFeature(Blocks.POPPY.getDefaultState()));
    public static final CakeFeature BLUE_ORCHID = register("blue_orchid", new BlockCakeFeature(Blocks.BLUE_ORCHID.getDefaultState()));
    public static final CakeFeature ALLIUM = register("allium", new BlockCakeFeature(Blocks.ALLIUM.getDefaultState()));
    public static final CakeFeature AZURE_BLUET = register("azure_bluet", new BlockCakeFeature(Blocks.AZURE_BLUET.getDefaultState()));
    public static final CakeFeature RED_TULIP = register("red_tulip", new BlockCakeFeature(Blocks.RED_TULIP.getDefaultState()));
    public static final CakeFeature ORANGE_TULIP = register("orange_tulip", new BlockCakeFeature(Blocks.ORANGE_TULIP.getDefaultState()));
    public static final CakeFeature WHITE_TULIP = register("white_tulip", new BlockCakeFeature(Blocks.WHITE_TULIP.getDefaultState()));
    public static final CakeFeature PINK_TULIP = register("pink_tulip", new BlockCakeFeature(Blocks.PINK_TULIP.getDefaultState()));
    public static final CakeFeature OXEYE_DAISY = register("oxeye_daisy", new BlockCakeFeature(Blocks.OXEYE_DAISY.getDefaultState()));
    public static final CakeFeature CORNFLOWER = register("cornflower", new BlockCakeFeature(Blocks.CORNFLOWER.getDefaultState()));
    public static final CakeFeature WITHER_ROSE = register("wither_rose", new BlockCakeFeature(Blocks.WITHER_ROSE.getDefaultState()));
    public static final CakeFeature LILY_OF_THE_VALLEY = register("lily_of_the_valley", new BlockCakeFeature(Blocks.LILY_OF_THE_VALLEY.getDefaultState()));

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing feature registry");
    }
}