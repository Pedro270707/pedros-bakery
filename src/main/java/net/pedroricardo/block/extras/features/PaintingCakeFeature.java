package net.pedroricardo.block.extras.features;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class PaintingCakeFeature extends CakeFeature {
    @Override
    public void onPlaced(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> batter, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (world.isClientSide()) return;
        CompoundTag nbt = this.getNbt(batter);
        if (stack.getOrCreateTag().contains(Painting.VARIANT_TAG)) {
            nbt.putString("variant", stack.getTag().getString(Painting.VARIANT_TAG));
        } else {
            ArrayList<Holder<PaintingVariant>> list = new ArrayList<>();
            BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
            if (list.isEmpty()) {
                Map<CakeFeature, CompoundTag> featureMap = batter.getFeatureMap();
                featureMap.remove(this);
                batter.withFeatures(featureMap);
                return;
            }
            Optional<Holder<PaintingVariant>> optional = Util.getRandomSafe(list, world.getRandom());
            if (optional.isEmpty()) {
                return;
            }
            nbt.putString("variant", optional.get().toString());
        }
        this.writeNbt(batter, nbt);
    }

    @Override
    public boolean canBeApplied(Player player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, Level world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        ArrayList<Holder<PaintingVariant>> list = new ArrayList<>();
        BuiltInRegistries.PAINTING_VARIANT.getTagOrEmpty(PaintingVariantTags.PLACEABLE).forEach(list::add);
        if (list.isEmpty()) {
            return false;
        }
        if (super.canBeApplied(player, stack, layer, world, pos, state, blockEntity)) return true;
        if (!stack.getOrCreateTag().contains(Painting.VARIANT_TAG)) return true;
        String variantOnLayer = this.getNbt(layer).getString("variant");
        return variantOnLayer == null || !variantOnLayer.equals(stack.getOrCreateTag().getString(Painting.VARIANT_TAG));
    }

    public Holder<PaintingVariant> getPainting(CakeBatter<?> layer) {
        return Painting.loadVariant(this.getNbt(layer)).orElse(BuiltInRegistries.PAINTING_VARIANT.getHolderOrThrow(PaintingVariants.KEBAB));
    }
}
