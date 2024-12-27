package net.pedroricardo.item.recipes;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;

public class PBRecipeSerializers {
    public static final RecipeSerializer<BakingTrayIncreaseRecipe> BAKING_TRAY_INCREASE = register("baking_tray_increase_recipe", new SpecialRecipeSerializer<>(BakingTrayIncreaseRecipe::new));
    public static final RecipeSerializer<ExpandableBakingTrayRecipe> EXPANDABLE_BAKING_TRAY = register("expandable_baking_tray_recipe", new SpecialRecipeSerializer<>(ExpandableBakingTrayRecipe::new));
    public static final RecipeSerializer<FrostedItemRecipe> FROSTED_ITEM = register("frosted_item_recipe", new SpecialRecipeSerializer<>(FrostedItemRecipe::new));

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(PedrosBakery.MOD_ID, id), serializer);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering recipe serializers");
    }
}
