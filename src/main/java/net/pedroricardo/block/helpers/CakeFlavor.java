package net.pedroricardo.block.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CakeFlavor {
    private final CakeFlavor base;
    private final Ingredient ingredient;
    private final RegistryEntry.Reference<CakeFlavor> registryEntry = CakeFlavors.REGISTRY.createEntry(this);

    public CakeFlavor(CakeFlavor base, Ingredient ingredient) {
        this.base = base;
        this.ingredient = ingredient;
    }

    @NotNull
    public Identifier getCakeTextureLocation() {
        return CakeFlavors.REGISTRY.getId(this).withPrefixedPath("textures/entity/cake/").withSuffixedPath(".png");
    }

    @NotNull
    public Identifier getCupcakeTextureLocation() {
        return CakeFlavors.REGISTRY.getId(this).withPrefixedPath("textures/entity/cupcake/").withSuffixedPath(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "flavor.pedrosbakery." + CakeFlavors.REGISTRY.getId(this).getPath();
    }

    public void onTryEat(CakeBatter batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    public void bakeTick(CakeBatter batter, World world, BlockPos pos, BlockState state) {
    }

    public void tick(CakeBatter layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
    }

    public final CakeFlavor base() {
        return this.base;
    }

    public final Ingredient ingredient() {
        return this.ingredient;
    }

    public RegistryEntry.Reference<CakeFlavor> getRegistryEntry() {
        return this.registryEntry;
    }

    public boolean isIn(TagKey<CakeFlavor> tag) {
        return this.getRegistryEntry().isIn(tag);
    }
}
