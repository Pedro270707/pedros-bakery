package net.pedroricardo.block.extras.size;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class BatterSizeContainer {
    public BatterSizeContainer() {
    }

    public abstract boolean bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize);
    public abstract boolean isEmpty();
    public abstract BatterSizeContainer copy();
    public abstract VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
}
