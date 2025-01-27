package net.pedroricardo.block.extras;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CakeFeature {
    private final RegistryEntry.Reference<CakeFeature> registryEntry = CakeFeatures.REGISTRY.createEntry(this);

    @NotNull
    public String getTranslationKey() {
        return "feature.pedrosbakery." + CakeFeatures.REGISTRY.getId(this).getPath();
    }

    public ActionResult onTryEat(CakeBatter<?> batter, World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity) {
        return ActionResult.PASS;
    }

    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        return !layer.getFeatures().contains(this);
    }

    public void onPlaced(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
    }

    public RegistryEntry.Reference<CakeFeature> getRegistryEntry() {
        return this.registryEntry;
    }

    public boolean isIn(TagKey<CakeFeature> tag) {
        return this.getRegistryEntry().isIn(tag);
    }

    public NbtCompound getNbt(CakeBatter<?> batter) {
        return batter.getFeatureMap().get(this);
    }

    public void writeNbt(CakeBatter<?> batter, NbtCompound data) {
        if (batter.getFeatureMap().containsKey(this)) {
            batter.getFeatureMap().put(this, data);
        }
    }
}
