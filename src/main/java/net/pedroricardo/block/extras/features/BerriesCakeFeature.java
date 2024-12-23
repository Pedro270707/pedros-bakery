package net.pedroricardo.block.extras.features;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;

public class BerriesCakeFeature extends CakeFeature {
    @Override
    public boolean canBeApplied(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        for (CakeFeature feature : layer.getFeatures()) {
            if (feature instanceof BerriesCakeFeature) return false;
        }
        return super.canBeApplied(player, stack, layer, world, pos, state, blockEntity);
    }
}
