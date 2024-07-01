package net.pedroricardo;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.OptionalInt;

public class PBHelpers {
    private PBHelpers() {}

    public static void decrementStackAndAdd(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        decrementStackAndAdd(player, oldStack, newStack, true);
    }

    public static void decrementStackAndAdd(PlayerEntity player, ItemStack oldStack, ItemStack newStack, boolean decrementUnlessCreative) {
        OptionalInt oldStackSlot = OptionalInt.empty();
        for (int i = 0; i < player.getInventory().main.size(); ++i) {
            if (player.getInventory().main.get(i).isEmpty() || player.getInventory().main.get(i) != oldStack) continue;
            oldStackSlot = OptionalInt.of(i);
            break;
        }
        if (decrementUnlessCreative) {
            oldStack.decrementUnlessCreative(1, player);
        } else {
            oldStack.decrement(1);
        }
        if (oldStack.isEmpty() && oldStackSlot.isPresent()) {
            player.getInventory().insertStack(oldStackSlot.getAsInt(), newStack);
        } else if (!player.giveItemStack(newStack)) {
            player.dropItem(newStack, false);
        }
    }

    public static void updateListeners(BlockEntity blockEntity) {
        updateListeners(blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState(), blockEntity);
    }

    public static void updateListeners(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        blockEntity.markDirty();
        if (world != null) {
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL_AND_REDRAW);
        }
    }
}
