package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PieItem extends BlockItem {
    public PieItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        PieDataComponent pieDataComponent = PBHelpers.getOrDefault(stack, PBComponentTypes.PIE_DATA, PieDataComponent.EMPTY);
        if (!pieDataComponent.filling().isEmpty() && pieDataComponent.layers() >= 2) {
            tooltip.add(Text.translatable(this.getTranslationKey() + ".flavor", pieDataComponent.filling().getName()).formatted(Formatting.GRAY));
            if (pieDataComponent.filling().isOf(this)) {
                tooltip.add(Text.translatable(this.getTranslationKey() + ".pie_flavor").formatted(Formatting.GRAY, Formatting.ITALIC));
            }
        }
    }
}
