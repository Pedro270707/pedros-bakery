package net.pedroricardo.block.helpers;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.features.BerriesCakeFeature;
import net.pedroricardo.block.helpers.features.MushroomCakeFeature;
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
    public static final CakeFeature RED_MUSHROOM = register("red_mushroom", new MushroomCakeFeature((MushroomPlantBlock) Blocks.RED_MUSHROOM));
    public static final CakeFeature BROWN_MUSHROOM = register("brown_mushroom", new MushroomCakeFeature((MushroomPlantBlock) Blocks.BROWN_MUSHROOM));
    public static final CakeFeature END_DUST = register("end_dust", new ParticleCakeFeature(ParticleTypes.PORTAL, 0.125f));
    public static final CakeFeature HONEY = register("honey", new CakeFeature());
    public static final CakeFeature PAINTING = register("painting", new PaintingCakeFeature());

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing feature registry");
    }
}