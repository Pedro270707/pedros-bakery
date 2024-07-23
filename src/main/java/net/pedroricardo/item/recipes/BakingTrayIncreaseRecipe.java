package net.pedroricardo.item.recipes;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

import java.util.OptionalInt;

public class BakingTrayIncreaseRecipe extends SpecialCraftingRecipe {
    public BakingTrayIncreaseRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput inventory, World world) {
        if (!this.fits(inventory.getWidth(), inventory.getHeight())) {
            return false;
        }
        OptionalInt trayIndex = OptionalInt.empty();
        boolean hasIronIngots = false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isOf(Items.AIR)) continue;
            if (stack.isOf(Items.IRON_INGOT)) {
                hasIronIngots = true;
                continue;
            }
            if (stack.isOf(PBBlocks.BAKING_TRAY.asItem())) {
                if (trayIndex.isPresent()) return false;
                else {
                    trayIndex = OptionalInt.of(i);
                }
            }
        }
        if (!hasIronIngots || trayIndex.isEmpty()) return false;
        int trayIndexInt = trayIndex.getAsInt();
        ItemStack trayStack = inventory.getStackInSlot(trayIndexInt).copy();
        if (!trayStack.getOrDefault(PBComponentTypes.FULL_BATTER, CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isOf(Items.IRON_INGOT)) {
                if (i < (trayIndexInt - trayIndexInt % inventory.getWidth()) || i > trayIndexInt - trayIndexInt % inventory.getWidth() + 2) {
                    trayStack.set(PBComponentTypes.HEIGHT, trayStack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()) + 2);
                } else {
                    trayStack.set(PBComponentTypes.SIZE, trayStack.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize()) + 2);
                }
            }
        }
        return trayStack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()) <= PedrosBakery.CONFIG.bakingTrayMaxHeight() && trayStack.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize()) <= PedrosBakery.CONFIG.bakingTrayMaxSize();
    }

    @Override
    public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup lookup) {
        ItemStack trayStack = ItemStack.EMPTY;
        int trayIndex = 0;
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isOf(PBBlocks.BAKING_TRAY.asItem())) {
                trayStack = stack.copy();
                trayIndex = i;
                break;
            }
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isOf(Items.IRON_INGOT)) {
                if (i < (trayIndex - trayIndex % inventory.getWidth()) || i > trayIndex - trayIndex % inventory.getWidth() + 2) {
                    trayStack.set(PBComponentTypes.HEIGHT, Math.min(trayStack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()) + 2, 16));
                } else {
                    trayStack.set(PBComponentTypes.SIZE, Math.min(trayStack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultSize()) + 2, 16));
                }
            }
        }
        return trayStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.BAKING_TRAY_INCREASE;
    }
}
