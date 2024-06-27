package net.pedroricardo;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class PBSounds {
    public static final SoundEvent BAKING_TRAY_DONE = register("block.baking_tray.done");

    private static SoundEvent register(String id) {
        return Registry.register(Registries.SOUND_EVENT, Identifier.of(PedrosBakery.MOD_ID, id), SoundEvent.of(Identifier.of(PedrosBakery.MOD_ID, id)));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing sounds");
    }
}
