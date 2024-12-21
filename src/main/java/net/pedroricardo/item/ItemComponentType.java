package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public record ItemComponentType<T>(ResourceLocation key, Codec<T> codec) {
}
