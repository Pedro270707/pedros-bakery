package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFlavor;
import net.pedroricardo.block.extras.CupcakeTrayBatter;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
        List<CakeBatter<FixedBatterSizeContainer>> batterList = Lists.newArrayList(batter.stream().iterator());
        boolean changed = false;
        for (int i = 0; i < batter.stream().size(); i++) {
            if (batter.stream().get(i).isEmpty()) {
                batterList.set(i, new CakeBatter<>(0, new FixedBatterSizeContainer(false), flavor, false));
                changed = true;
            }
        }
        if (changed) {
            newStack.set(PBComponentTypes.CUPCAKE_TRAY_BATTER, new CupcakeTrayBatter(batterList));
            PBHelpers.decrementStackAndAdd(player, stack, newStack, false);
            return true;
        }
        return false;
    }
}
