package net.pedroricardo.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

import java.util.List;

public interface MultipartBlock {
    VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    List<BlockPos> getParts(WorldView world, BlockPos pos);
}
