package net.pedroricardo.item.recipes;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.item.FrostingBottleItem;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

public class FrostedDonutRecipe extends SpecialCraftingRecipe {
    public FrostedDonutRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack2 = inventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(PBItems.DONUT)) {
                if (!donutStack.isEmpty()) {
                    return false;
                }
                donutStack = itemStack2;
                continue;
            }
            if (itemStack2.getItem() instanceof FrostingBottleItem && PBHelpers.contains(itemStack2, PBComponentTypes.TOP)) {
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
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack2 = inventory.getStack(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.isOf(PBItems.DONUT)) {
                donutStack = itemStack2;
            } else if (itemStack2.getItem() instanceof FrostingBottleItem && PBHelpers.contains(itemStack2, PBComponentTypes.TOP)) {
                bottleStack = itemStack2;
            }
        }
        ItemStack newStack = donutStack.copyWithCount(1);
        PBHelpers.set(newStack, PBComponentTypes.TOP, PBHelpers.get(bottleStack, PBComponentTypes.TOP));
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
