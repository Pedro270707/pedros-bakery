package net.pedroricardo.item.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

public class ExpandableBakingTrayRecipe extends CustomRecipe {
    public ExpandableBakingTrayRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        if (!this.canCraftInDimensions(inventory.getWidth(), inventory.getHeight())) {
            return false;
        }
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                    if (!stack.is(Items.DIAMOND)) return false;
                    break;
                case 4:
                    if (!stack.is(PBBlocks.BAKING_TRAY.get().asItem()) || !PBHelpers.getOrDefault(stack, PBComponentTypes.FULL_BATTER.get(), CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
                    break;
                default: {
                    if (!stack.is(Items.AIR)) return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        return PBHelpers.copyNbtToNewStack(inventory.getItem(4), PBBlocks.EXPANDABLE_BAKING_TRAY.get(), 1);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width == 3 && height == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.EXPANDABLE_BAKING_TRAY.get();
    }
}
