package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.List;

public interface MultipartBlock<C extends BlockEntity & MultipartBlockEntity, E extends MultipartBlockEntityPart<C>, P extends MultipartBlockPart<C, E>> {
    VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    List<BlockPos> getParts(WorldView world, BlockPos pos);

    /**
     * Call this in the block's {@link net.minecraft.block.AbstractBlock#onStateReplaced(BlockState, World, BlockPos, BlockState, boolean)} method
     * alongside calling the super method afterward.
     * <p>
     * Remember to update listeners with {@link net.minecraft.block.Block#NOTIFY_ALL_AND_REDRAW} at the end of the method.
     */
    void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved);
    P getPart();
}
