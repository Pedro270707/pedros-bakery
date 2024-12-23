package net.pedroricardo.item.recipes;

import net.minecraft.resources.ResourceLocation;

public record MixingPatternEntry(ResourceLocation id, MixingPattern entry) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MixingPatternEntry pattern)) return false;
        return this.id().equals(pattern.id());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
