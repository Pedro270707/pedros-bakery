package net.pedroricardo.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.tags.PBTags;

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
        tooltip.add(Text.translatable("item.pedrosbakery.donut.top", Text.translatable(top.getTranslationKey())).formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (top != null && top.isIn(PBTags.Tops.INEDIBLE)) {
            return TypedActionResult.fail(stack);
        }
        return super.use(world, user, hand);
    }
}
