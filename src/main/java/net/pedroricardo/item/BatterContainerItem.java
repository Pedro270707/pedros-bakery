package net.pedroricardo.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroricardo.block.extras.CakeFlavor;
import org.jetbrains.annotations.Nullable;

public interface BatterContainerItem {
    boolean addBatter(Player player, InteractionHand hand, ItemStack stack, @Nullable CakeFlavor flavor, int amount);
}
