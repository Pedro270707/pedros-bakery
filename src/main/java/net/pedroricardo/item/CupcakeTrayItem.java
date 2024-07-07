package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.helpers.CakeFlavor;
import net.pedroricardo.block.helpers.CupcakeTrayBatter;
import net.pedroricardo.block.helpers.SimpleCakeBatter;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CupcakeTrayItem extends BlockItem implements BatterContainerItem {
    public CupcakeTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
    }

    public boolean addBatter(PlayerEntity player, ItemStack stack, @Nullable CakeFlavor flavor, int amount) {
        if (flavor == null || !stack.isOf(this)) return false;
        ItemStack newStack = stack.copyWithCount(1);
        CupcakeTrayBatter batter = stack.getOrDefault(PBComponentTypes.CUPCAKE_TRAY_BATTER, CupcakeTrayBatter.getEmpty());
        List<Optional<SimpleCakeBatter>> flavors = Lists.newArrayList(batter.stream().iterator());
        boolean changed = false;
        for (int i = 0; i < batter.stream().size(); i++) {
            if (batter.stream().get(i).isEmpty()) {
                flavors.set(i, Optional.of(new SimpleCakeBatter(0, flavor)));
                changed = true;
            }
        }
        if (changed) {
            newStack.set(PBComponentTypes.CUPCAKE_TRAY_BATTER, new CupcakeTrayBatter(flavors));
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        }
        return false;
    }
}
