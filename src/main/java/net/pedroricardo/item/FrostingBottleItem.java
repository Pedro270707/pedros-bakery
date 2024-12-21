package net.pedroricardo.item;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.item.Item;
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
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
        if (top != null) {
            top.onDrink(stack, world, user);
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.isCreative()) {
                stack.decrement(1);
            }
        }
        if (playerEntity == null || !playerEntity.isCreative()) {
            if (stack.isEmpty()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
            if (playerEntity != null) {
                playerEntity.getInventory().insertStack(new ItemStack(Items.GLASS_BOTTLE));
            }
        }
        user.emitGameEvent(GameEvent.DRINK);
        return stack;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
        if (top != null && top.isIn(PBTags.Tops.INEDIBLE)) {
            return TypedActionResult.fail(stack);
        }
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 24;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP);
        if (top != null) {
            tooltip.add(Text.translatable(top.getTranslationKey()).formatted(Formatting.GRAY));
        }
    }
}
