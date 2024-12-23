package net.pedroricardo.item.recipes;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;

public class PBRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, PedrosBakery.MOD_ID);

    public static final RegistryObject<RecipeSerializer<BakingTrayIncreaseRecipe>> BAKING_TRAY_INCREASE = RECIPE_SERIALIZERS.register("baking_tray_increase_recipe", () -> new SimpleCraftingRecipeSerializer<>(BakingTrayIncreaseRecipe::new));
    public static final RegistryObject<RecipeSerializer<ExpandableBakingTrayRecipe>> EXPANDABLE_BAKING_TRAY = RECIPE_SERIALIZERS.register("expandable_baking_tray_recipe", () -> new SimpleCraftingRecipeSerializer<>(ExpandableBakingTrayRecipe::new));
    public static final RegistryObject<RecipeSerializer<FrostedDonutRecipe>> FROSTED_DONUT = RECIPE_SERIALIZERS.register("frosted_donut_recipe", () -> new SimpleCraftingRecipeSerializer<>(FrostedDonutRecipe::new));

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering recipe serializers");
    }
}
