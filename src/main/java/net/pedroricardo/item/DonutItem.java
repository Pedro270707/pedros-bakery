package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (top == null) {
            return;
        }
        tooltip.add(Component.translatable("item.pedrosbakery.donut.top", Component.translatable(top.getTranslationKey())).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (top != null && top.is(PBTags.Tops.INEDIBLE)) {
            return InteractionResultHolder.fail(stack);
        }
        return super.use(world, user, hand);
    }
}
