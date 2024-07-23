package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeTrayBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CupcakeTrayBatter;
import net.pedroricardo.block.helpers.size.FixedBatterSizeContainer;

import java.util.List;

public class CupcakeItem extends BlockItem {
    public CupcakeItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        CakeBatter<FixedBatterSizeContainer> batter = stack.getOrDefault(PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty());
        if (batter.isEmpty()) {
            return;
        }
        if (batter.getTop().isPresent()) {
            tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor_and_top", Text.translatable(batter.getFlavor().getTranslationKey()), Text.translatable(batter.getTop().get().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        } else {
            tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor", Text.translatable(batter.getFlavor().getTranslationKey())).formatted(batter.isWaxed() ? Formatting.GOLD : Formatting.GRAY));
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getStack().getOrDefault(PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty() || !(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof CupcakeTrayBlockEntity tray)) return super.useOnBlock(context);
        Vec3d hitVector = context.getHitPos().subtract(context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ());
        int i = (hitVector.getX() > 0.5 ? 2 : 0) | (hitVector.getZ() > 0.5 ? 1 : 0);
        CupcakeTrayBatter trayBatter = tray.getBatter();
        CakeBatter<FixedBatterSizeContainer> batter = trayBatter.stream().get(i);
        if (batter.isEmpty() || batter.getBakeTime() < PedrosBakery.CONFIG.ticksUntilBaked()) return super.useOnBlock(context);
        tray.setBatter(trayBatter.withBatter(i, CakeBatter.getFixedSizeEmpty()));
        context.getWorld().emitGameEvent(context.getPlayer(), GameEvent.FLUID_PICKUP, context.getBlockPos());
        ItemStack newStack = new ItemStack(PBBlocks.CUPCAKE);
        newStack.set(PBComponentTypes.FIXED_SIZE_BATTER, batter);
        PBHelpers.decrementStackAndAdd(context.getPlayer(), context.getStack(), newStack);
        return ActionResult.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (stack.getOrDefault(PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty()) return super.getTranslationKey(stack) + ".empty";
        return super.getTranslationKey(stack);
    }
}
