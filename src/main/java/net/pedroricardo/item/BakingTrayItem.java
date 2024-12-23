package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BakingTrayItem extends BlockItem implements BatterContainerItem {
    public BakingTrayItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        int size = PBHelpers.getOrDefault(stack, PBComponentTypes.SIZE.get(), PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        int height = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());

        tooltip.add(Component.translatable("block.pedrosbakery.baking_tray.size", size, size, height));
        if (batter.isEmpty()) {
            return;
        }
        tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor", Component.translatable(batter.getFlavor().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        if (batter.getSizeContainer().getHeight() != height && batter.getSizeContainer().getHeight() != 0) {
            tooltip.add(Component.translatable("block.pedrosbakery.baking_tray.full", (int)(100.0f * batter.getSizeContainer().getHeight() / (float) height)).withStyle(ChatFormatting.YELLOW));
        }
    }

    public boolean addBatter(Player player, InteractionHand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.is(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());
        if (batter.isEmpty()) {
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(Math.min(amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()))), flavor, false));
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
            return true;
        } else if (batter.getBakeTime() < 200 && batter.getSizeContainer().getHeight() < PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) && flavor == batter.getFlavor()) {
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight() + amount, PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get())));
            PBHelpers.set(newStack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), batter);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, newStack));
            return true;
        }
        return false;
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        PBHelpers.set(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());
        PBHelpers.set(stack, PBComponentTypes.SIZE.get(), PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        PBHelpers.set(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get());
        return stack;
    }
}
