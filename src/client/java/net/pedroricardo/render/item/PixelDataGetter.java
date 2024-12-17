package net.pedroricardo.render.item;

import org.joml.Vector2i;

import java.util.Set;

public interface PixelDataGetter {
    PixelData get(Vector2i pixel, Set<Vector2i> shape);
}
