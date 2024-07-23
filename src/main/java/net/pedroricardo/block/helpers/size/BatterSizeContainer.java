package net.pedroricardo.block.helpers.size;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BatterSizeContainer {
    public BatterSizeContainer() {
    }

    public abstract boolean bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize);
    public abstract boolean isEmpty();
    public abstract BatterSizeContainer copy();
}
