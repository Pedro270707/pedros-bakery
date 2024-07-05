package net.pedroricardo.block.helpers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record CupcakeBatter(Optional<SimpleCakeBatter> topLeft, Optional<SimpleCakeBatter> topRight, Optional<SimpleCakeBatter> bottomLeft, Optional<SimpleCakeBatter> bottomRight) {
    public static final Codec<CupcakeBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SimpleCakeBatter.CODEC.optionalFieldOf("top_left").forGetter(CupcakeBatter::topLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("top_right").forGetter(CupcakeBatter::topRight),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_left").forGetter(CupcakeBatter::bottomLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_right").forGetter(CupcakeBatter::bottomRight)
    ).apply(instance, CupcakeBatter::new));
    public static final PacketCodec<RegistryByteBuf, CupcakeBatter> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatter::topLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatter::topRight, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatter::bottomLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatter::bottomRight, CupcakeBatter::new);
    private static final CupcakeBatter EMPTY = new CupcakeBatter(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static CupcakeBatter getEmpty() {
        return EMPTY.copy();
    }

    public CupcakeBatter(List<Optional<SimpleCakeBatter>> list) {
        this(list.isEmpty() ? Optional.empty() : list.getFirst(), list.size() > 1 ? list.get(1) : Optional.empty(), list.size() > 2 ? list.get(2) : Optional.empty(), list.size() > 3 ? list.get(3) : Optional.empty());
    }

    public List<Optional<SimpleCakeBatter>> stream() {
        return Stream.of(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight).toList();
    }

    public CupcakeBatter copy() {
        return new CupcakeBatter(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public static CupcakeBatter fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            return CupcakeBatter.getEmpty();
        }
        return CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("batter")).result().orElse(CupcakeBatter.getEmpty());
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        if (this.equals(CupcakeBatter.getEmpty())) {
            return nbt;
        }
        nbt.put("batter", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CupcakeBatter that = (CupcakeBatter) o;
        return this.stream().equals(that.stream());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight);
    }
}
//public record CupcakeBatter(CakeBatter topLeft, CakeBatter topRight, CakeBatter bottomLeft, CakeBatter bottomRight) {
//    public static final Codec<CupcakeBatter> CODEC = CakeBatter.CODEC.sizeLimitedListOf(4).xmap(CupcakeBatter::new, CupcakeBatter::stream);
//    public static final PacketCodec<RegistryByteBuf, CupcakeBatter> PACKET_CODEC = CakeBatter.PACKET_CODEC.collect(PacketCodecs.toList(4)).xmap(CupcakeBatter::new, CupcakeBatter::stream);
//    private static final CupcakeBatter EMPTY = new CupcakeBatter(CakeBatter.getEmpty(), CakeBatter.getEmpty(), CakeBatter.getEmpty(), CakeBatter.getEmpty());
//
//    public static CupcakeBatter getEmpty() {
//        return EMPTY.copy();
//    }
//
//    private CupcakeBatter(List<CakeBatter> list) {
//        this(list.get(0), list.get(1), list.get(2), list.get(3));
//    }
//
//    public List<CakeBatter> stream() {
//        return Stream.of(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight).toList();
//    }
//
//    public CupcakeBatter copy() {
//        return new CupcakeBatter(this.topLeft().copy(), this.topRight().copy(), this.bottomLeft().copy(), this.bottomRight().copy());
//    }
//
//    public static CupcakeBatter fromNbt(@Nullable NbtCompound nbt) {
//        if (nbt == null) {
//            return CupcakeBatter.getEmpty();
//        }
//        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(CupcakeBatter.getEmpty());
//    }
//
//    public NbtCompound toNbt(NbtCompound nbt) {
//        if (this.equals(CupcakeBatter.getEmpty())) {
//            return nbt;
//        }
//        nbt.put("batter", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
//        return nbt;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        CupcakeBatter that = (CupcakeBatter) o;
//        return this.stream().equals(that.stream());
//    }
//}
