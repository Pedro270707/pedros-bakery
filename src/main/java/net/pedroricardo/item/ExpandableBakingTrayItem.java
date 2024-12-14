package net.pedroricardo.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;

public class ExpandableBakingTrayItem extends BakingTrayItem {
    public ExpandableBakingTrayItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        float sin45 = MathHelper.sqrt(3)/2.0f;
        TypedActionResult<ItemStack> result = user.getRotationVector().getY() > sin45 || user.getRotationVector().getY() < -sin45 ? change(user, hand, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayMinHeight(), PedrosBakery.CONFIG.bakingTrayMaxHeight(), PedrosBakery.CONFIG.bakingTrayDefaultHeight()) : change(user, hand, PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayMinSize(), PedrosBakery.CONFIG.bakingTrayMaxSize(), PedrosBakery.CONFIG.bakingTrayDefaultSize());
        if (result.getResult().isAccepted()) {
            return result;
        }
        return super.use(world, user, hand);
    }

    private static TypedActionResult<ItemStack> change(PlayerEntity user, Hand hand, ItemComponentType<Integer> componentType, int min, int max, int defaultValue) {
        ItemStack stack = user.getStackInHand(hand);
        int size = PBHelpers.getOrDefault(stack, componentType, defaultValue);
        if (user.isSneaking()) {
            PBHelpers.set(stack, componentType, Math.max(size - 1, min));
        } else {
            PBHelpers.set(stack, componentType, Math.min(size + 1, max));
        }
        if (PBHelpers.getOrDefault(stack, componentType, -1) != size) {
            CakeBatter<HeightOnlyBatterSizeContainer> batter = PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT_ONLY_BATTER, CakeBatter.getHeightOnlyEmpty());
            batter.getSizeContainer().setHeight(Math.min(batter.getSizeContainer().getHeight(), PBHelpers.getOrDefault(stack, PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight())));
            user.setStackInHand(hand, stack);
            return TypedActionResult.success(stack, false);
        }
        return TypedActionResult.pass(stack);
    }
}
