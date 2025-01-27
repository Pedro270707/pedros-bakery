package net.pedroricardo.item.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

public class ExpandableBakingTrayRecipe extends SpecialCraftingRecipe {
    public ExpandableBakingTrayRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        if (!this.fits(input.getWidth(), input.getHeight())) {
            return false;
        }
        for (int i = 0; i < input.getSize(); ++i) {
            ItemStack stack = input.getStackInSlot(i);
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                    if (!stack.isOf(Items.DIAMOND)) return false;
                    break;
                case 4:
                    if (!stack.isOf(PBBlocks.BAKING_TRAY.asItem()) || !stack.getOrDefault(PBComponentTypes.FULL_BATTER, CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
                    break;
                default: {
                    if (!stack.isOf(Items.AIR)) return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        return input.getStackInSlot(4).copyComponentsToNewStack(PBBlocks.EXPANDABLE_BAKING_TRAY, 1);
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
