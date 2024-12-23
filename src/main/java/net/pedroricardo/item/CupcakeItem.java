package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
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
    public CupcakeItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        CakeBatter<FixedBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER.get(), CakeBatter.getFixedSizeEmpty());
        if (batter.isEmpty()) {
            return;
        }
        if (batter.getTop().isPresent()) {
            tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor_and_top", Component.translatable(batter.getFlavor().getTranslationKey()), Component.translatable(batter.getTop().get().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        } else {
            tooltip.add(Component.translatable("block.pedrosbakery.cake.flavor", Component.translatable(batter.getFlavor().getTranslationKey())).withStyle(batter.isWaxed() ? ChatFormatting.GOLD : ChatFormatting.GRAY));
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!PBHelpers.getOrDefault(context.getItemInHand(), PBComponentTypes.FIXED_SIZE_BATTER.get(), CakeBatter.getFixedSizeEmpty()).isEmpty() || !(context.getLevel().getBlockEntity(context.getClickedPos()) instanceof CupcakeTrayBlockEntity tray)) return super.useOn(context);
        Vec3 hitVector = context.getClickLocation().subtract(context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
        int i = (hitVector.x() > 0.5 ? 2 : 0) | (hitVector.z() > 0.5 ? 1 : 0);
        CupcakeTrayBatter trayBatter = tray.getBatter();
        CakeBatter<FixedBatterSizeContainer> batter = trayBatter.stream().get(i);
        if (batter.isEmpty() || batter.getBakeTime() < PedrosBakery.CONFIG.ticksUntilCakeBaked.get()) return super.useOn(context);
        tray.setBatter(trayBatter.withBatter(i, CakeBatter.getFixedSizeEmpty()));
        context.getLevel().gameEvent(context.getPlayer(), GameEvent.FLUID_PICKUP, context.getClickedPos());
        ItemStack newStack = new ItemStack(PBBlocks.CUPCAKE.get());
        PBHelpers.set(newStack, PBComponentTypes.FIXED_SIZE_BATTER.get(), batter);
        if (context.getPlayer() != null) {
            context.getPlayer().setItemInHand(context.getHand(), ItemUtils.createFilledResult(context.getItemInHand(), context.getPlayer(), newStack));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public String getDescriptionId(ItemStack stack) {
        if (PBHelpers.getOrDefault(stack, PBComponentTypes.FIXED_SIZE_BATTER.get(), CakeBatter.getFixedSizeEmpty()).isEmpty()) return super.getDescriptionId(stack) + ".empty";
        return super.getDescriptionId(stack);
    }
}
