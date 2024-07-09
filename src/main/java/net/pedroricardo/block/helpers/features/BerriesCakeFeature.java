package net.pedroricardo.block.helpers.features;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeBatter;

public class BerriesCakeFeature extends CakeFeature {
    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        for (CakeFeature feature : layer.getFeatures()) {
            if (feature instanceof BerriesCakeFeature) return false;
        }
        return super.canBeApplied(player, stack, layer, world, pos, state, blockEntity);
    }
}
