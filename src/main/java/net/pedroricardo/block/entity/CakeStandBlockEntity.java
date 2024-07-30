package net.pedroricardo.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class CakeStandBlockEntity extends ItemStandBlockEntity {
    public CakeStandBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE_STAND, pos, state);
    }
}
