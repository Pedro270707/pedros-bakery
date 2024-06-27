package net.pedroricardo.block.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CakeFlavor {
    private final CakeFlavor base;
    private final ItemConvertible item;
    private final RegistryEntry.Reference<CakeFlavor> registryEntry = CakeFlavors.REGISTRY.createEntry(this);

    public CakeFlavor(CakeFlavor base, ItemConvertible item) {
        this.base = base;
        this.item = item;
    }

    @NotNull
    public Identifier getTextureLocation() {
        return CakeFlavors.REGISTRY.getId(this).withPrefixedPath("textures/entity/cake/").withSuffixedPath(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "flavor.pedrosbakery." + CakeFlavors.REGISTRY.getId(this).getPath();
    }

    public void onTryEat(CakeLayer layer, World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake) {
    }

    public void bakeTick(CakeBatter batter, World world, BlockPos pos, BlockState state) {
    }

    public void tick(CakeLayer layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
    }

    public final CakeFlavor base() {
        return this.base;
    }

    public final ItemConvertible item() {
        return this.item;
    }

    public RegistryEntry.Reference<CakeFlavor> getRegistryEntry() {
        return this.registryEntry;
    }

    public boolean isIn(TagKey<CakeFlavor> tag) {
        return this.getRegistryEntry().isIn(tag);
    }
}
