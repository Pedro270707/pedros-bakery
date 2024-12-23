package net.pedroricardo.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface MultipartBlockEntity {
    List<BlockPos> getParts();

    /**
     * Removes all current parts (if any) and places new parts in the world.
     * Call this in your ticker method.
     */
    void updateParts(Level world, BlockPos pos, BlockState state);

    /**
     * Silently removes all the parts (without delegation or particles).
     */
    default void removeAllParts(Level world) {
        for (BlockPos pos : this.getParts()) {
            BlockState state = world.getBlockState(pos);
            if (state.getBlock() instanceof MultipartBlockPart<?, ?> && state.hasProperty(MultipartBlockPart.DELEGATE)) {
                world.setBlockAndUpdate(pos, state.setValue(MultipartBlockPart.DELEGATE, false));
                world.removeBlock(pos, false);
            }
        }
        this.getParts().clear();
        ((BlockEntity)this).setChanged();
    }

    default void createPart(Level world, MultipartBlock<?, ?, ?> block, BlockPos pos, BlockPos parentPos) {
        BlockState state = block.getPart().defaultBlockState();
        MultipartBlockEntityPart<?> blockEntity = block.getPart().newBlockEntity(pos, state, parentPos);
        world.setBlockAndUpdate(pos, state);
        world.setBlockEntity(blockEntity);
        this.getParts().add(pos);
    }
}
