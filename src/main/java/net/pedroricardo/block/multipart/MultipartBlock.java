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
     * Called when block is replaced in order to remove all the current parts.
     */
    default void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).contains(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockState(partPos, world.getBlockState(partPos).with(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else {
                world.breakBlock(partPos, false);
            }
        }
    }

    P getPart();
}
