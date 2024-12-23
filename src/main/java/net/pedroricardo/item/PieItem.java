package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pedroricardo.PBHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PieItem extends BlockItem {
    public PieItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);
        PieDataComponent pieDataComponent = PBHelpers.getOrDefault(stack, PBComponentTypes.PIE_DATA.get(), PieDataComponent.EMPTY);
        if (!pieDataComponent.filling().isEmpty() && pieDataComponent.layers() >= 2) {
            tooltip.add(Component.translatable(this.getDescriptionId() + ".flavor", pieDataComponent.filling().getHoverName()).withStyle(ChatFormatting.GRAY));
            if (pieDataComponent.filling().is(this)) {
                tooltip.add(Component.translatable(this.getDescriptionId() + ".pie_flavor").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            }
        }
    }
}
