package net.pedroricardo.item.recipes;

import net.minecraft.registry.*;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;

public class MixingPatterns {
    public static final RegistryKey<Registry<MixingPattern>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "mixing_pattern"));
}
