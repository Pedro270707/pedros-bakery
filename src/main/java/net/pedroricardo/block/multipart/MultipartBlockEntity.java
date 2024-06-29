package net.pedroricardo.block.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface MultipartBlockEntity {
    List<BlockPos> getParts();

    /**
     * Removes all current parts (if any) and places new parts in the world.
     * Call this in your ticker method.
     */
    void updateParts(World world, BlockPos pos, BlockState state);

    /**
     * Silently removes all the parts (without delegation or particles).
     */
    default void removeAllParts(World world) {
        for (BlockPos pos : this.getParts()) {
            if (world.getBlockState(pos).getBlock() instanceof MultipartBlockPart<?, ?> && world.getBlockState(pos).contains(MultipartBlockPart.DELEGATE)) {
                world.setBlockState(pos, world.getBlockState(pos).with(MultipartBlockPart.DELEGATE, false));
            }
            world.removeBlock(pos, false);
        }
        this.getParts().clear();
    }

    default void createPart(World world, MultipartBlock<?, ?, ?> block, BlockPos pos, BlockPos parentPos) {
        BlockState state = block.getPart().getDefaultState();
        MultipartBlockEntityPart<?> blockEntity = block.getPart().createBlockEntity(pos, state, parentPos);
        world.setBlockState(pos, state);
        world.addBlockEntity(blockEntity);
        this.getParts().add(pos);
    }
}
