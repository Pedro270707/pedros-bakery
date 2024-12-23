package net.pedroricardo.block.extras;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CakeFlavor {
    private final CakeFlavor base;
    private final Ingredient ingredient;

    public CakeFlavor(CakeFlavor base, Ingredient ingredient) {
        this.base = base;
        this.ingredient = ingredient;
    }

    @NotNull
    public ResourceLocation getCakeTextureLocation() {
        return CakeFlavors.registrySupplier.get().getKey(this).withPrefix("textures/entity/cake/").withSuffix(".png");
    }

    @NotNull
    public ResourceLocation getCupcakeTextureLocation() {
        return CakeFlavors.registrySupplier.get().getKey(this).withPrefix("textures/entity/cupcake/").withSuffix(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "flavor.pedrosbakery." + CakeFlavors.registrySupplier.get().getKey(this).getPath();
    }

    public InteractionResult onTryEat(CakeBatter<?> batter, Level world, BlockPos pos, BlockState state, Player player) {
        return InteractionResult.PASS;
    }

    public void bakeTick(CakeBatter<?> batter, Level world, BlockPos pos, BlockState state) {
    }

    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    public final CakeFlavor base() {
        return this.base;
    }

    public final Ingredient ingredient() {
        return this.ingredient;
    }

    public boolean is(TagKey<CakeFlavor> tag) {
        if (CakeFlavors.registrySupplier.get() == null || CakeFlavors.registrySupplier.get().getHolder(this).isEmpty()) return false;
        return CakeFlavors.registrySupplier.get().getHolder(this).get().is(tag);
    }
}
