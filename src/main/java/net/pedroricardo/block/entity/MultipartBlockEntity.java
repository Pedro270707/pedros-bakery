package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.MultipartBlockPart;

import java.util.List;

public interface MultipartBlockEntity {
    List<BlockPos> getParts();

    /**
     * Removes all current parts (if any) and places new parts in the world.
     * Call this in your ticker method.
     * @param world
     * @param pos
     * @param state
     */
    void updateParts(World world, BlockPos pos, BlockState state);

    /**
     * Silently removes all the parts (without delegation or particles).
     * @param world
     */
    default void removeAllParts(World world) {
        for (BlockPos pos : this.getParts()) {
            if (world.getBlockState(pos).getBlock() instanceof MultipartBlockPart<?> && world.getBlockState(pos).contains(MultipartBlockPart.DELEGATE)) {
                world.setBlockState(pos, world.getBlockState(pos).with(MultipartBlockPart.DELEGATE, false));
            }
            world.removeBlock(pos, false);
        }
        this.getParts().clear();
    }
}
