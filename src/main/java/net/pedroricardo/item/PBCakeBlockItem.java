package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.pedroricardo.block.helpers.CakeLayer;

import java.util.List;

public class PBCakeBlockItem extends BlockItem {
    public PBCakeBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        NbtComponent nbtComponent = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
        if (nbtComponent == null) {
            return;
        }
        List<CakeLayer> layers = CakeLayer.listFrom(nbtComponent.copyNbt());
        for (CakeLayer layer : layers.reversed()) {
            if (layer.getTop().isPresent()) {
                tooltip.add(Text.translatable("block.pedrosbakery.cake.flavor_and_top", Text.translatable(layer.getFlavor().getTranslationKey()), Text.translatable(layer.getTop().get().getTranslationKey())).formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable(layer.getFlavor().getTranslationKey()).formatted(Formatting.GRAY));
            }
        }
    }
}
