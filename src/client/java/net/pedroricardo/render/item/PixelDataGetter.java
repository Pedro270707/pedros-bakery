package net.pedroricardo.render.item;

import net.minecraft.item.ItemStack;
import org.joml.Vector2i;

import java.util.Set;

public interface PixelDataGetter {
    PixelData get(ItemStack stack, Vector2i pixel, Set<Vector2i> shape);
}
