package net.pedroricardo.datagen;

import net.minecraft.item.ItemConvertible;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;

public interface PieColorOverrideExporter {
    void accept(Identifier id, ItemConvertible item, TextColor textColor);
}
