package net.pedroricardo.item;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;

public class ExpandableBakingTrayItem extends BakingTrayItem {
    public ExpandableBakingTrayItem(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        float sin45 = Mth.sqrt(3)/2.0f;
        InteractionResultHolder<ItemStack> result = user.getLookAngle().y() > sin45 || user.getLookAngle().y() < -sin45 ? change(user, hand, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayMinHeight.get(), PedrosBakery.CONFIG.bakingTrayMaxHeight.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get()) : change(user, hand, PBComponentTypes.SIZE.get(), PedrosBakery.CONFIG.bakingTrayMinSize.get(), PedrosBakery.CONFIG.bakingTrayMaxSize.get(), PedrosBakery.CONFIG.bakingTrayDefaultSize.get());
        if (result.getResult().consumesAction()) {
            return result;
        }
        return super.use(world, user, hand);
    }

    private static InteractionResultHolder<ItemStack> change(Player user, InteractionHand hand, DataComponentType<Integer> componentType, int min, int max, int defaultValue) {
        ItemStack stack = user.getItemInHand(hand);
        int size = PBHelpers.getOrDefault(stack, componentType, defaultValue);
        if (user.isCrouching()) {
            PBHelpers.set(stack, componentType, Math.max(size - 1, min));
        } else {
            PBHelpers.set(stack, componentType, Math.min(size + 1, max));
        }
        if (PBHelpers.getOrDefault(stack, componentType, -1) != size) {
            CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER.get(), CakeBatter.getHeightOnlyEmpty());
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight(), PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT.get(), PedrosBakery.CONFIG.bakingTrayDefaultHeight.get())));
            user.setItemInHand(hand, stack);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        return InteractionResultHolder.pass(stack);
    }
}
