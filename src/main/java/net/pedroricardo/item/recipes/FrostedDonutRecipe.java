package net.pedroricardo.item.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.item.FrostingBottleItem;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

public class FrostedDonutRecipe extends CustomRecipe {
    public FrostedDonutRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemStack2 = inventory.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.is(PBItems.DONUT.get())) {
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
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        ItemStack donutStack = ItemStack.EMPTY;
        ItemStack bottleStack = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack itemStack2 = inventory.getItem(i);
            if (itemStack2.isEmpty()) continue;
            if (itemStack2.is(PBItems.DONUT.get())) {
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
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.FROSTED_DONUT.get();
    }
}
