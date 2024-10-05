package net.pedroricardo.block.extras.features;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;

public class SprinklesCakeFeature extends CakeFeature {
    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        for (CakeFeature feature : layer.getFeatures()) {
            if (feature instanceof SprinklesCakeFeature) return false;
        }
        return super.canBeApplied(player, stack, layer, world, pos, state, blockEntity);
    }
}
