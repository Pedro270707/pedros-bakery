package net.pedroricardo.block.helpers.features;

import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeLayer;

public class MushroomCakeFeature extends CakeFeature {
    private final MushroomPlantBlock mushroom;
    public MushroomCakeFeature(MushroomPlantBlock mushroom) {
        this.mushroom = mushroom;
    }

    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeLayer layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        for (CakeFeature feature : layer.getFeatures()) {
            if (feature instanceof MushroomCakeFeature) return false;
        }
        return super.canBeApplied(player, stack, layer, world, pos, state, blockEntity);
    }

    public MushroomPlantBlock getMushroom() {
        return this.mushroom;
    }
}
