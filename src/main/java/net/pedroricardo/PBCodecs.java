package net.pedroricardo;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Util;
import org.joml.Vector2i;

import java.util.stream.IntStream;

public class PBCodecs {
    public static final Codec<Vector2i> VECTOR_2I = Codec.INT_STREAM.comapFlatMap(stream -> Util.decodeFixedLengthArray(stream, 2).map(values -> new Vector2i(values[0], values[1])), vector -> IntStream.of(vector.x(), vector.y()));
    public static final PacketCodec<ByteBuf, Vector2i> PACKET_VECTOR_2I = PacketCodecs.unlimitedCodec(VECTOR_2I);
}
