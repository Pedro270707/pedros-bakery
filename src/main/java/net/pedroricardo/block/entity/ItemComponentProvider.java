package net.pedroricardo.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ItemComponentProvider {
    void updateParts(Level world, BlockPos pos, BlockState state);

    void addComponents(ItemStack stack);
}
