package net.pedroricardo.block.extras.features;

import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.decoration.painting.PaintingVariants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class PaintingCakeFeature extends CakeFeature {
    @Override
    public void onPlaced(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> batter, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (world.isClient()) return;
        NbtCompound nbt = this.getNbt(batter);
        if (stack.getOrCreateNbt().contains(PaintingEntity.VARIANT_NBT_KEY)) {
            nbt.putString("variant", stack.getNbt().getString(PaintingEntity.VARIANT_NBT_KEY));
        } else {
            ArrayList<RegistryEntry<PaintingVariant>> list = new ArrayList<>();
            Registries.PAINTING_VARIANT.iterateEntries(PaintingVariantTags.PLACEABLE).forEach(list::add);
            if (list.isEmpty()) {
                Map<CakeFeature, NbtCompound> featureMap = batter.getFeatureMap();
                featureMap.remove(this);
                batter.withFeatures(featureMap);
                return;
            }
            Optional<RegistryEntry<PaintingVariant>> optional = Util.getRandomOrEmpty(list, world.getRandom());
            if (optional.isEmpty()) {
                return;
            }
            nbt.putString("variant", optional.get().getKey().orElse(PaintingVariants.KEBAB).getValue().toString());
        }
        this.writeNbt(batter, nbt);
    }

    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter<FullBatterSizeContainer> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        ArrayList<RegistryEntry<PaintingVariant>> list = new ArrayList<>();
        Registries.PAINTING_VARIANT.iterateEntries(PaintingVariantTags.PLACEABLE).forEach(list::add);
        if (list.isEmpty()) {
            return false;
        }
        if (super.canBeApplied(player, stack, layer, world, pos, state, blockEntity)) return true;
        if (!stack.getOrCreateNbt().contains(PaintingEntity.VARIANT_NBT_KEY)) return true;
        String variantOnLayer = this.getNbt(layer).getString("variant");
        return variantOnLayer == null || !variantOnLayer.equals(stack.getOrCreateNbt().getString(PaintingEntity.VARIANT_NBT_KEY));
    }

    public RegistryEntry<PaintingVariant> getPainting(CakeBatter<?> layer) {
        return PaintingEntity.readVariantFromNbt(this.getNbt(layer)).orElse(Registries.PAINTING_VARIANT.entryOf(PaintingVariants.KEBAB));
    }
}
