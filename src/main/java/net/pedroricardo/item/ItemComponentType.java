package net.pedroricardo.item;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public record ItemComponentType<T>(Identifier key, Codec<T> codec) {
}
