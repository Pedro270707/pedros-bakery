package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class PieItem extends BlockItem {
    public PieItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        PieDataComponent pieDataComponent = stack.getOrDefault(PBComponentTypes.PIE_DATA, PieDataComponent.EMPTY);
        if (!pieDataComponent.filling().isEmpty() && pieDataComponent.layers() >= 2) {
            tooltip.add(Text.translatable(this.getTranslationKey() + ".flavor", pieDataComponent.filling().getName()).formatted(Formatting.GRAY));
            if (pieDataComponent.filling().isOf(this)) {
                tooltip.add(Text.translatable(this.getTranslationKey() + ".pie_flavor").formatted(Formatting.GRAY, Formatting.ITALIC));
            }
        }
    }
}
