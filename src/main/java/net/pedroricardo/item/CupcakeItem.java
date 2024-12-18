package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.entity.CupcakeTrayBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CupcakeItem extends BlockItem {
    public CupcakeItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CakeBatter<FixedBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty());
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
        if (!PBHelpers.getOrDefault(context.getStack(), PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty() || !(context.getWorld().getBlockEntity(context.getBlockPos()) instanceof CupcakeTrayBlockEntity tray)) return super.useOnBlock(context);
        Vec3d hitVector = context.getHitPos().subtract(context.getBlockPos().getX(), context.getBlockPos().getY(), context.getBlockPos().getZ());
        int i = (hitVector.getX() > 0.5 ? 2 : 0) | (hitVector.getZ() > 0.5 ? 1 : 0);
        CupcakeTrayBatter trayBatter = tray.getBatter();
        CakeBatter<FixedBatterSizeContainer> batter = trayBatter.stream().get(i);
        if (batter.isEmpty() || batter.getBakeTime() < PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) return super.useOnBlock(context);
        tray.setBatter(trayBatter.withBatter(i, CakeBatter.getFixedSizeEmpty()));
        context.getWorld().emitGameEvent(context.getPlayer(), GameEvent.FLUID_PICKUP, context.getBlockPos());
        ItemStack newStack = new ItemStack(PBBlocks.CUPCAKE);
        PBHelpers.set(newStack, PBComponentTypes.FIXED_SIZE_BATTER, batter);
        if (context.getPlayer() != null) {
            context.getPlayer().setStackInHand(context.getHand(), ItemUsage.exchangeStack(context.getStack(), context.getPlayer(), newStack));
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        if (PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER, CakeBatter.getFixedSizeEmpty()).isEmpty()) return super.getTranslationKey(stack) + ".empty";
        return super.getTranslationKey(stack);
    }
}
