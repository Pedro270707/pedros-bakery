package net.pedroricardo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FrostingBottleItem extends Item {
    public FrostingBottleItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        Player playerEntity = user instanceof Player ? (Player) user : null;
        if (playerEntity instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)playerEntity, stack);
        }
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (top != null) {
            top.onDrink(stack, world, user);
        }
        if (playerEntity != null) {
            playerEntity.awardStat(Stats.ITEM_USED.get(this));
            if (!playerEntity.isCreative()) {
                stack.shrink(1);
            }
        }
        if (playerEntity == null || !playerEntity.isCreative()) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (playerEntity != null) {
                playerEntity.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        user.gameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (top != null && top.is(PBTags.Tops.INEDIBLE)) {
            return InteractionResultHolder.fail(stack);
        }
        return ItemUtils.startUsingInstantly(world, user, hand);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (top != null) {
            tooltip.add(Component.translatable(top.getTranslationKey()).withStyle(ChatFormatting.GRAY));
        }
    }
}
