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
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.item.PBComponentTypes;

import java.util.OptionalInt;

public class BakingTrayIncreaseRecipe extends CustomRecipe {
    public BakingTrayIncreaseRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level world) {
        if (!this.canCraftInDimensions(inventory.getWidth(), inventory.getHeight())) {
            return false;
        }
        OptionalInt trayIndex = OptionalInt.empty();
        boolean hasIronIngots = false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.AIR)) continue;
            if (stack.is(Items.IRON_INGOT)) {
                hasIronIngots = true;
                continue;
            }
            if (stack.is(PBBlocks.BAKING_TRAY.get().asItem())) {
                if (trayIndex.isPresent()) return false;
                else {
                    trayIndex = OptionalInt.of(i);
                }
            }
        }
        if (!hasIronIngots || trayIndex.isEmpty()) return false;
        int trayIndexInt = trayIndex.getAsInt();
        ItemStack trayStack = inventory.getItem(trayIndexInt).copy();
        if (!PBHelpers.getOrDefault(trayStack, PBComponentTypes.FULL_BATTER, CakeBatter.getFullSizeEmpty()).isEmpty()) return false;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.IRON_INGOT)) {
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
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        ItemStack trayStack = ItemStack.EMPTY;
        int trayIndex = 0;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(PBBlocks.BAKING_TRAY.get().asItem())) {
                trayStack = stack.copy();
                trayIndex = i;
                break;
            }
        }
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.is(Items.IRON_INGOT)) {
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
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PBRecipeSerializers.BAKING_TRAY_INCREASE.get();
    }
}
