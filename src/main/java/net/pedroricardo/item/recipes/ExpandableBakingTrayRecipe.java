package net.pedroricardo.item.recipes;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

public class ExpandableBakingTrayRecipe extends SpecialCraftingRecipe {
    public ExpandableBakingTrayRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        if (!this.fits(inventory.getWidth(), inventory.getHeight())) {
            return false;
        }
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack stack = inventory.getStack(i);
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                    if (!stack.isOf(Items.DIAMOND)) return false;
                    break;
                case 4:
                    if (!stack.isOf(PBBlocks.BAKING_TRAY.asItem()) || !PBHelpers.getOrDefault(stack, PBComponentTypes.FULL_BATTER, CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
                    break;
                default: {
                    if (!stack.isOf(Items.AIR)) return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        return PBHelpers.copyNbtToNewStack(inventory.getStack(4), PBBlocks.EXPANDABLE_BAKING_TRAY, 1);
    }

    @Override
    public boolean fits(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.EXPANDABLE_BAKING_TRAY;
    }
}
