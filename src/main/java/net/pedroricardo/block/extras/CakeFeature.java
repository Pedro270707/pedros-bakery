package net.pedroricardo.block.extras;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CakeFeature {
    @NotNull
    public String getTranslationKey() {
        return "feature.pedrosbakery." + CakeFeatures.registrySupplier.get().getKey(this).getPath();
    }

    public InteractionResult onTryEat(CakeBatter<?> batter, Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity) {
        return InteractionResult.PASS;
    }

    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    public boolean canBeApplied(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        return !layer.getFeatures().contains(this);
    }

    public void onPlaced(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
    }

    public boolean is(TagKey<CakeFeature> tag) {
        if (CakeFeatures.registrySupplier.get() == null || CakeFeatures.registrySupplier.get().getHolder(this).isEmpty()) return false;
        return CakeFeatures.registrySupplier.get().getHolder(this).get().is(tag);
    }

    public CompoundTag getNbt(CakeBatter<?> batter) {
        return batter.getFeatureMap().get(this);
    }

    public void writeNbt(CakeBatter<?> batter, CompoundTag data) {
        if (batter.getFeatureMap().containsKey(this)) {
            batter.getFeatureMap().put(this, data);
        }
    }
}
