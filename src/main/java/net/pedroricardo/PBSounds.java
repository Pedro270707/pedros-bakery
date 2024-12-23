package net.pedroricardo;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class PBSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, PedrosBakery.MOD_ID);

    public static final RegistryObject<SoundEvent> BAKING_TRAY_DONE = register("block.baking_tray.done");
    public static final RegistryObject<SoundEvent> PIE_DONE = register("block.pie.done");

    private static RegistryObject<SoundEvent> register(String id) {
        return SOUND_EVENTS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PedrosBakery.MOD_ID, id)));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing sounds");
    }
}
