package net.pedroricardo;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class PBHelpers {
    private PBHelpers() {}

    public static void update(BlockEntity blockEntity, ServerWorld world) {
        update(world, blockEntity.getPos(), blockEntity);
    }

    public static void update(ServerWorld world, BlockPos pos, BlockEntity blockEntity) {
        blockEntity.markDirty();
        world.getChunkManager().markForUpdate(pos);
    }
}
