package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
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
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        int size = stack.getOrDefault(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        int height = stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        CakeBatter<HeightOnlyBatterSizeContainer> batter = stack.getOrDefault(PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());

        tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        if (batter.getSizeContainer().getHeight() != height && batter.getSizeContainer().getHeight() != 0) {
            tooltip.add(Text.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getSizeContainer().getHeight() / (float) height)).formatted(Formatting.YELLOW));
        }
    }

    public BakingTrayBlockEntity asBlockEntity(ItemStack stack) {
        BakingTrayBlockEntity blockEntity = new BakingTrayBlockEntity(BlockPos.ORIGIN, PBBlocks.BAKING_TRAY.getDefaultState());
        blockEntity.readComponents(stack);
        return blockEntity;
    }

    public boolean addBatter(PlayerEntity player, Hand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.isOf(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter<HeightOnlyBatterSizeContainer> batter = stack.getOrDefault(PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
        if (batter.isEmpty()) {
            newStack.set(PBComponentTypes.HEIGHT_ONLY_BATTER, new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(Math.min(amount, stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()))), flavor, false));
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getSizeContainer().getHeight() < stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) && flavor == batter.getFlavor()) {
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight() + amount, stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight.get())));
            newStack.set(PBComponentTypes.HEIGHT_ONLY_BATTER, batter);
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, newStack));
            return true;
        }
        return false;
    }
}
