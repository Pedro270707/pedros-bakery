package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.CakeBatter;

public class ExpandableBakingTrayItem extends BakingTrayItem {
    public ExpandableBakingTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        TypedActionResult<ItemStack> result = user.getFacing().getAxis().isVertical() ? change(user, hand, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayMinHeight(), PedrosBakery.CONFIG.bakingTrayMaxHeight(), PedrosBakery.CONFIG.bakingTrayDefaultHeight()) : change(user, hand, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayMinSize(), PedrosBakery.CONFIG.bakingTrayMaxSize(), PedrosBakery.CONFIG.bakingTrayDefaultSize());
        if (result.getResult().isAccepted()) {
            return result;
        }
        return super.use(world, user, hand);
    }

    private static TypedActionResult<ItemStack> change(PlayerEntity user, Hand hand, ComponentType<Integer> componentType, int min, int max, int defaultValue) {
        ItemStack stack = user.getStackInHand(hand);
        int size = stack.getOrDefault(componentType, defaultValue);
        if (user.isSneaking()) {
            stack.set(componentType, Math.max(size - 1, min));
        } else {
            stack.set(componentType, Math.min(size + 1, max));
        }
        if (stack.get(componentType) != size) {
            CakeBatter batter = stack.getOrDefault(PBComponentTypes.BATTER, CakeBatter.getEmpty());
            batter.setHeight(Math.min(batter.getHeight(), stack.getOrDefault(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight())));
            user.setStackInHand(hand, stack);
            return TypedActionResult.success(stack, false);
        }
        return TypedActionResult.pass(stack);
    }
}
