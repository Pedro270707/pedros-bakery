package net.pedroricardo.block.extras.size;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BatterSizeContainer {
    public BatterSizeContainer() {
    }

    public abstract boolean bite(Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity, float biteSize);
    public abstract boolean isEmpty();
    public abstract BatterSizeContainer copy();
    public abstract VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context);
}
