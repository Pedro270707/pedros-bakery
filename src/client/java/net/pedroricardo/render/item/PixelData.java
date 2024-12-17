package net.pedroricardo.render.item;

import net.minecraft.util.Identifier;

import java.util.Objects;

public record PixelData(Identifier texture, int color) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PixelData pixelData = (PixelData) o;
        return color() == pixelData.color() && Objects.equals(texture(), pixelData.texture());
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture(), color());
    }
}
