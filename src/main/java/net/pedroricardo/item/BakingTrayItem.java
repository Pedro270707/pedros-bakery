package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFlavor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingTrayItem extends BlockItem implements BatterContainerItem {
    public BakingTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int size = stack.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize());
        int height = stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight());
        CakeBatter batter = stack.getOrDefault(PBComponentTypes.BATTER, CakeBatter.getEmpty());

        tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        if (batter.getHeight() != height && batter.getHeight() != 0) {
            tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getHeight() / (float) height)).formatted(Formatting.YELLOW));
        }
    }

    public BakingTrayBlockEntity asBlockEntity(ItemStack stack) {
        BakingTrayBlockEntity blockEntity = new BakingTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.BAKING_TRAY.getDefaultState());
        blockEntity.readComponents(stack);
        return blockEntity;
    }

    public boolean addBatter(PlayerEntity player, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.isOf(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter batter = stack.getOrDefault(PBComponentTypes.BATTER, CakeBatter.getEmpty());
        if (batter.isEmpty()) {
            newStack.set(PBComponentTypes.BATTER, new CakeBatter(0, Math.min(amount, stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight())), flavor, false));
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getHeight() < stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()) && flavor == batter.getFlavor()) {
            batter.withHeight(Math.min(batter.getHeight() + amount, stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight())));
            newStack.set(PBComponentTypes.BATTER, batter);
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        }
        return false;
    }
}
