package net.pedroricardo.datagen;

import net.minecraft.util.Identifier;
import net.pedroricardo.item.recipes.MixingPattern;

public interface MixingPatternExporter {
    void accept(Identifier id, MixingPattern pattern);
}
