package net.pedroricardo.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.pedroricardo.block.helpers.CakeTop;

import java.util.List;

public class DonutItem extends Item {
    public DonutItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (top == null) {
            return;
        }
        tooltip.add(Text.translatable("item.pedrosbakery.donut.top", Text.translatable(top.getTranslationKey())));
    }
}
