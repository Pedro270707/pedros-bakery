package net.pedroricardo.block.entity;

import net.minecraft.item.ItemStack;

public interface StackReadingBlockEntity {
    void readFrom(ItemStack stack);
}
