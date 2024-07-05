package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.block.Block;
import net.minecraft.block.AbstractBlock;

import java.util.List;

public interface MultipartBlock<C extends BlockEntity & MultipartBlockEntity, E extends MultipartBlockEntityPart<C>, P extends MultipartBlockPart<C, E>> {
    /**
     * The shape of the entire structure with its parts.
     * It is recommended to use {@link VoxelShapes#combineAndSimplify(VoxelShape, VoxelShape, BooleanBiFunction)} with {@link VoxelShapes#fullCube()} and {@link BooleanBiFunction#AND} in your normal shape methods to limit this to a single block.
     * In the part block's class, make sure to offset this with {@code part.getParentPos().subtract(pos)}.
     * If a seamless shape is desired, do not combine and simplify.
     */
    VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);
    List<BlockPos> getParts(WorldView world, BlockPos pos);

    /**
     * Call this in the block's {@link AbstractBlock#onStateReplaced(BlockState, World, BlockPos, BlockState, boolean)} method
     * alongside calling the super method afterward.
     * <p>
     * Remember to update listeners with {@link Block#NOTIFY_ALL_AND_REDRAW} at the end of the method.
     */
    void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved);
    P getPart();
}
