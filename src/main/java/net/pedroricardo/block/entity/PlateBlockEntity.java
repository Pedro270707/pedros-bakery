package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class PlateBlockEntity extends ItemStandBlockEntity {
    public PlateBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.PLATE, pos, state);
    }
}
