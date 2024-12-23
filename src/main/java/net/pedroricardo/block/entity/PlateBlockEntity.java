package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class PlateBlockEntity extends ItemStandBlockEntity {
    public PlateBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.PLATE.get(), pos, state);
    }
}
