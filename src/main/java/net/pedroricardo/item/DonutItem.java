package net.pedroricardo.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.world.item.Item;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DonutItem extends Item {
    public DonutItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
        if (top == null) {
            return;
        }
        tooltip.add(Text.translatable("item.pedrosbakery.donut.top", Text.translatable(top.getTranslationKey())).formatted(Formatting.GRAY));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
        if (top != null && top.isIn(PBTags.Tops.INEDIBLE)) {
            return TypedActionResult.fail(stack);
        }
        return super.use(world, user, hand);
    }
}
