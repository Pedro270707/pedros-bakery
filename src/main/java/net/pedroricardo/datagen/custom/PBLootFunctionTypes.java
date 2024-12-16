package net.pedroricardo.datagen.custom;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonSerializer;
import net.pedroricardo.PedrosBakery;

public class PBLootFunctionTypes {
    public static final LootFunctionType COPY_COMPONENTS = register("copy_components", new CopyComponentsLootFunction.Serializer());

    public static LootFunctionType register(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        return Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier(PedrosBakery.MOD_ID, id), new LootFunctionType(jsonSerializer));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering loot functions");
    }
}
