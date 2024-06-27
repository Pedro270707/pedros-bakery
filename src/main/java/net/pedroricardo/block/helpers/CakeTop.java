package net.pedroricardo.block.helpers;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CakeTop {
    private final CakeTop base;
    private final ItemConvertible item;
    private final int color;
    private final RegistryEntry.Reference<CakeTop> registryEntry = CakeTops.REGISTRY.createEntry(this);

    public CakeTop(CakeTop base, ItemConvertible item, int color) {
        this.base = base;
        this.item = item;
        this.color = color;
    }

    @NotNull
    public Identifier getTextureLocation() {
        return CakeTops.REGISTRY.getId(this).withPrefixedPath("textures/entity/cake/top/").withSuffixedPath(".png");
    }

    @NotNull
    public String getTranslationKey() {
        return "top.pedrosbakery." + CakeTops.REGISTRY.getId(this).getPath();
    }

    public void onTryEat(CakeLayer layer, World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake) {
    }

    public void onDrink(ItemStack stack, World world, LivingEntity user) {
    }

    public void tick(CakeLayer layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
    }

    public final CakeTop base() {
        return this.base;
    }

    public final ItemConvertible item() {
        return this.item;
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
