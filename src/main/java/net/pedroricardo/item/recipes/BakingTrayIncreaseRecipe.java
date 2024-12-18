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
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

import java.util.OptionalInt;

public class BakingTrayIncreaseRecipe extends SpecialCraftingRecipe {
    public BakingTrayIncreaseRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        if (!this.fits(inventory.getWidth(), inventory.getHeight())) {
            return false;
        }
        OptionalInt trayIndex = OptionalInt.empty();
        boolean hasIronIngots = false;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
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
        ItemStack trayStack = inventory.getStack(trayIndexInt).copy();
        if (!PBHelpers.getOrDefault(trayStack, PBComponentTypes.FULL_BATTER, CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.IRON_INGOT)) {
                if (i < (trayIndexInt - trayIndexInt % inventory.getWidth()) || i > trayIndexInt - trayIndexInt % inventory.getWidth() + 2) {
                    PBHelpers.set(trayStack, PBComponentTypes.HEIGHT, PBHelpers.getOrDefault(trayStack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) + 2);
                } else {
                    PBHelpers.set(trayStack, PBComponentTypes.SIZE, PBHelpers.getOrDefault(trayStack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get()) + 2);
                }
            }
        }
        return PBHelpers.getOrDefault(trayStack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) <= PedrosBakery.CONFIG.bakingTrayMaxHeight.get() && PBHelpers.getOrDefault(trayStack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get()) <= PedrosBakery.CONFIG.bakingTrayMaxSize.get();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack trayStack = ItemStack.EMPTY;
        int trayIndex = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(PBBlocks.BAKING_TRAY.asItem())) {
                trayStack = stack.copy();
                trayIndex = i;
                break;
            }
        }
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.IRON_INGOT)) {
                if (i < (trayIndex - trayIndex % inventory.getWidth()) || i > trayIndex - trayIndex % inventory.getWidth() + 2) {
                    PBHelpers.set(trayStack, PBComponentTypes.HEIGHT, Math.min(PBHelpers.getOrDefault(trayStack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) + 2, 16));
                } else {
                    PBHelpers.set(trayStack, PBComponentTypes.SIZE, Math.min(PBHelpers.getOrDefault(trayStack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get()) + 2, 16));
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
