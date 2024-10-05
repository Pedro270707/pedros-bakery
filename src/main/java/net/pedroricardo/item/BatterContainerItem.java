package net.pedroricardo.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.pedroricardo.block.extras.CakeFlavor;
import org.jetbrains.annotations.Nullable;

public interface BatterContainerItem {
    boolean addBatter(PlayerEntity player, ItemStack stack, @Nullable CakeFlavor flavor, int amount);
}
