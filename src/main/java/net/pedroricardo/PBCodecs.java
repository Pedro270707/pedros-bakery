package net.pedroricardo;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import org.joml.Vector2i;

import java.util.stream.IntStream;

public class PBCodecs {
    public static final Codec<Vector2i> VECTOR_2I = Codec.INT_STREAM.comapFlatMap(stream -> Util.fixedSize(stream, 2).map(values -> new Vector2i(values[0], values[1])), vector -> IntStream.of(vector.x(), vector.y()));
}
