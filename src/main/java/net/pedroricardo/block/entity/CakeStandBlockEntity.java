package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CakeStandBlockEntity extends ItemStandBlockEntity {
    public CakeStandBlockEntity(BlockPos pos, BlockState state) {
        super(PBBlockEntities.CAKE_STAND.get(), pos, state);
    }
}
