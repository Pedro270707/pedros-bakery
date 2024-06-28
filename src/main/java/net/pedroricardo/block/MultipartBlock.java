package net.pedroricardo.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.pedroricardo.block.tags.PBTags;

import java.util.List;

public interface MultipartBlock {
    VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    List<BlockPos> getParts(WorldView world, BlockPos pos);

    /**
     * Call this in the block's {@link net.minecraft.block.AbstractBlock#onStateReplaced(BlockState, World, BlockPos, BlockState, boolean)} method
     * alongside calling the super method afterward.
     * <p>
     * Remember to update listeners with {@link net.minecraft.block.Block#NOTIFY_ALL_AND_REDRAW} at the end of the method.
     * @param state
     * @param world
     * @param pos
     * @param newState
     * @param moved
     */
    default void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?> && world.getBlockState(partPos).contains(MultipartBlockPart.DELEGATE)) {
                world.setBlockState(partPos, world.getBlockState(partPos).with(MultipartBlockPart.DELEGATE, false));
            }
            if (moved) {
                world.removeBlock(partPos, true);
            } else if (newState.isIn(PBTags.Blocks.CAKES)) {
                world.removeBlock(partPos, false);
            } else {
                world.breakBlock(partPos, false);
            }
        }
    }
}
