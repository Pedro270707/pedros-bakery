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

public class BlockCakeFeature extends CakeFeature {
    private final BlockState blockState;
    public BlockCakeFeature(BlockState blockState) {
        this.blockState = blockState;
    }

    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        for (CakeFeature feature : layer.getFeatures()) {
            if (feature instanceof BlockCakeFeature) return false;
        }
        return super.canBeApplied(player, stack, layer, world, pos, state, blockEntity);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }
}
