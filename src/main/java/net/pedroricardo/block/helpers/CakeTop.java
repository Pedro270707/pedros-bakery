package net.pedroricardo.block.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CakeTop {
    private final CakeTop base;
    private final Ingredient ingredient;
    private final int color;
    private final RegistryEntry.Reference<CakeTop> registryEntry = CakeTops.REGISTRY.createEntry(this);

    public CakeTop(CakeTop base, Ingredient ingredient, int color) {
        this.base = base;
        this.ingredient = ingredient;
        this.color = color;
    }

    @NotNull
    public Identifier getCakeTextureLocation() {
        return CakeTops.REGISTRY.getId(this).withPrefixedPath("textures/entity/cake/top/").withSuffixedPath(".png");
    }

    @NotNull
    public Identifier getCupcakeTextureLocation() {
        return CakeTops.REGISTRY.getId(this).withPrefixedPath("textures/entity/cupcake/top/").withSuffixedPath(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "top.pedrosbakery." + CakeTops.REGISTRY.getId(this).getPath();
    }

    public ActionResult onTryEat(CakeBatter<?> layer, World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity) {
        return ActionResult.PASS;
    }

    public void onDrink(ItemStack stack, World world, LivingEntity user) {
    }

    public void tick(CakeBatter<?> layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
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

    public RegistryEntry.Reference<CakeTop> getRegistryEntry() {
        return this.registryEntry;
    }

    public boolean isIn(TagKey<CakeTop> tag) {
        return this.getRegistryEntry().isIn(tag);
    }
}
