package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingTrayItem extends BlockItem implements BatterContainerItem {
    public BakingTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        int size = PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize());
        int height = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight());
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());

        tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        if (batter.getSizeContainer().getHeight() != height && batter.getSizeContainer().getHeight() != 0) {
            tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getSizeContainer().getHeight() / (float) height)).formatted(Formatting.YELLOW));
        }
    }

    public boolean addBatter(PlayerEntity player, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.isOf(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
        if (batter.isEmpty()) {
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER, new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(Math.min(amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()))), flavor, false));
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getSizeContainer().getHeight() < PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()) && flavor == batter.getFlavor()) {
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight() + amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight())));
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER, batter);
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
        PBHelpers.set(stack, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight());
        return stack;
    }
}
