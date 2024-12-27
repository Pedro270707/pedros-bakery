package net.pedroricardo.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;

public class PBLootFunctionTypes {
    public static final DeferredRegister<LootItemFunctionType> LOOT_ITEM_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, PedrosBakery.MOD_ID);

    public static final RegistryObject<LootItemFunctionType> COPY_COMPONENTS = register("copy_components", new CopyComponentsLootFunction.Serializer());

    public static RegistryObject<LootItemFunctionType> register(String id, Serializer<? extends LootItemFunction> jsonSerializer) {
        return LOOT_ITEM_FUNCTIONS.register(id, () -> new LootItemFunctionType(jsonSerializer));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering loot functions");
    }
}
