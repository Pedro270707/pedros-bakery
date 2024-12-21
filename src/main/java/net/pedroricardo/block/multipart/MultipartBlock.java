package net.pedroricardo.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public interface MultipartBlock<C extends BlockEntity & MultipartBlockEntity, E extends MultipartBlockEntityPart<C>, P extends MultipartBlockPart<C, E>> {
    /**
     * The shape of the entire structure with its parts.
     * It is recommended to use {@link net.minecraft.world.phys.shapes.Shapes#join(VoxelShape, VoxelShape, BooleanOp)} with {@link Shapes#block()} and {@link BooleanOp#AND} in your normal shape methods to limit this to a single block.
     * In the part block's class, make sure to offset this with {@code part.getParentPos().subtract(pos)}.
     * If a seamless shape is desired, do not combine and simplify.
     */
    VoxelShape getFullShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
    List<BlockPos> getParts(LevelAccessor world, BlockPos pos);

    /**
     * Called when block is replaced in order to remove all the current parts.
     */
    default void removePartsWhenReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).hasProperty(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockAndUpdate(partPos, world.getBlockState(partPos).setValue(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else {
                world.destroyBlock(partPos, false);
            }
        }
    }

    P getPart();
}
