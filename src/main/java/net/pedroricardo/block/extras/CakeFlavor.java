package net.pedroricardo.block.extras;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

    public ActionResult onTryEat(CakeBatter<?> batter, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return ActionResult.PASS;
    }

    public void bakeTick(CakeBatter<?> batter, World world, BlockPos pos, BlockState state) {
    }

    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
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
