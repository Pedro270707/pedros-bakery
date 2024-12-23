package net.pedroricardo.block.extras;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CakeTop {
    private final CakeTop base;
    private final Ingredient ingredient;
    private final int color;

    public CakeTop(CakeTop base, Ingredient ingredient, int color) {
        this.base = base;
        this.ingredient = ingredient;
        this.color = color;
    }

    @NotNull
    public ResourceLocation getCakeTextureLocation() {
        return CakeTops.registrySupplier.get().getKey(this).withPrefix("textures/entity/cake/top/").withSuffix(".png");
    }

    @NotNull
    public ResourceLocation getCupcakeTextureLocation() {
        return CakeTops.registrySupplier.get().getKey(this).withPrefix("textures/entity/cupcake/top/").withSuffix(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "top.pedrosbakery." + CakeTops.registrySupplier.get().getKey(this).getPath();
    }

    public InteractionResult onTryEat(CakeBatter<?> layer, Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity) {
        return InteractionResult.PASS;
    }

    public void onDrink(ItemStack stack, Level world, LivingEntity user) {
    }

    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
    }

    public final CakeTop base() {
        return this.base;
    }

    public final Ingredient ingredient() {
        return this.ingredient;
    }

    public final int color() {
        return this.color;
    }

    public boolean is(TagKey<CakeTop> tag) {
        if (CakeTops.registrySupplier.get() == null || CakeTops.registrySupplier.get().getHolder(this).isEmpty()) return false;
        return CakeTops.registrySupplier.get().getHolder(this).get().is(tag);
    }
}
