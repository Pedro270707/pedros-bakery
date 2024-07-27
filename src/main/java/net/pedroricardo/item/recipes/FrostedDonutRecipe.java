package net.pedroricardo.item.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.pedroricardo.item.FrostingBottleItem;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

public class FrostedDonutRecipe extends SpecialCraftingRecipe {
    public FrostedDonutRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput inventory, World world) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack itemStack2 = inventory.getStackInSlot(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(PBItems.DONUT)) {
                if (!donutStack.isEmpty()) {
                    return false;
                }
                donutStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() instanceof FrostingBottleItem && itemStack2.contains(PBComponentTypes.TOP)) {
                if (!bottleStack.isEmpty()) {
                    return false;
                }
                bottleStack = itemStack2;
                continue;
            }
            return false;
        }
        return !donutStack.isEmpty() && !bottleStack.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getSize(); ++i) {
            ItemStack itemStack2 = inventory.getStackInSlot(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(PBItems.DONUT)) {
                donutStack = itemStack2;
            } else if (itemStack2.getItem() instanceof FrostingBottleItem && itemStack2.contains(PBComponentTypes.TOP)) {
                bottleStack = itemStack2;
            }
        }
        ItemStack newStack = donutStack.copyWithCount(1);
        newStack.set(PBComponentTypes.TOP, bottleStack.get(PBComponentTypes.TOP));
        return newStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.FROSTED_DONUT;
    }
}
