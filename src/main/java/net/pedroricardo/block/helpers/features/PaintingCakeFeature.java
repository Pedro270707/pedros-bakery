package net.pedroricardo.block.helpers.features;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.PaintingVariantTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeature;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Optional;

public class PaintingCakeFeature extends CakeFeature {
    @Override
    public void onPlaced(PlayerEntity player, ItemStack stack, CakeBatter layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (world.isClient()) return;
        NbtComponent nbt = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT);
        nbt.get(world.getRegistryManager().getOps(NbtOps.INSTANCE), PaintingEntity.VARIANT_MAP_CODEC).result().ifPresentOrElse(variant ->
                this.saveData(layer, (NbtCompound) PaintingEntity.VARIANT_ENTRY_CODEC.encodeStart(NbtOps.INSTANCE, variant).result().orElse(new NbtCompound())), () -> {
            List<RegistryEntry<PaintingVariant>> list = Lists.newArrayList();
            world.getRegistryManager().get(RegistryKeys.PAINTING_VARIANT).iterateEntries(PaintingVariantTags.PLACEABLE).forEach(list::add);
            if (list.isEmpty()) {
                return;
            }
            Optional<RegistryEntry<PaintingVariant>> optional = Util.getRandomOrEmpty(list, world.getRandom());
            if (optional.isEmpty()) {
                return;
            }
            this.saveData(layer, (NbtCompound) PaintingEntity.VARIANT_ENTRY_CODEC.encodeStart(NbtOps.INSTANCE, optional.get()).result().orElse(new NbtCompound()));
        });
    }

    @Override
    public boolean canBeApplied(PlayerEntity player, ItemStack stack, CakeBatter layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        if (super.canBeApplied(player, stack, layer, world, pos, state, blockEntity)) return true;
        RegistryEntry<PaintingVariant> registryEntry = stack.getOrDefault(DataComponentTypes.ENTITY_DATA, NbtComponent.DEFAULT).get(world.getRegistryManager().getOps(NbtOps.INSTANCE), PaintingEntity.VARIANT_MAP_CODEC).result().orElse(null);
        if (registryEntry == null) return true;
        RegistryEntry<PaintingVariant> entryOnLayer = PaintingEntity.VARIANT_ENTRY_CODEC.parse(NbtOps.INSTANCE, this.getData(layer)).result().orElse(null);
        return entryOnLayer == null || !entryOnLayer.value().equals(registryEntry.value());
    }

    public RegistryEntry<PaintingVariant> getPainting(CakeBatter layer, DynamicRegistryManager registry) {
        return PaintingEntity.VARIANT_ENTRY_CODEC.parse(NbtOps.INSTANCE, this.getData(layer)).result().orElse(registry == null ? null : registry.get(RegistryKeys.PAINTING_VARIANT).getDefaultEntry().orElseThrow());
    }
}
