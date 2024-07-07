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

public record CupcakeTrayBatter(Optional<SimpleCakeBatter> topLeft, Optional<SimpleCakeBatter> topRight, Optional<SimpleCakeBatter> bottomLeft, Optional<SimpleCakeBatter> bottomRight) {
    public static final Codec<CupcakeTrayBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SimpleCakeBatter.CODEC.optionalFieldOf("top_left").forGetter(CupcakeTrayBatter::topLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("top_right").forGetter(CupcakeTrayBatter::topRight),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_left").forGetter(CupcakeTrayBatter::bottomLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_right").forGetter(CupcakeTrayBatter::bottomRight)
    ).apply(instance, CupcakeTrayBatter::new));
    public static final PacketCodec<RegistryByteBuf, CupcakeTrayBatter> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeTrayBatter::topLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeTrayBatter::topRight, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeTrayBatter::bottomLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeTrayBatter::bottomRight, CupcakeTrayBatter::new);
    private static final CupcakeTrayBatter EMPTY = new CupcakeTrayBatter(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static CupcakeTrayBatter getEmpty() {
        return EMPTY.copy();
    }

    public CupcakeTrayBatter(List<Optional<SimpleCakeBatter>> list) {
        this(list.isEmpty() ? Optional.empty() : list.getFirst(), list.size() > 1 ? list.get(1) : Optional.empty(), list.size() > 2 ? list.get(2) : Optional.empty(), list.size() > 3 ? list.get(3) : Optional.empty());
    }

    public List<Optional<SimpleCakeBatter>> stream() {
        return Stream.of(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight).toList();
    }

    public CupcakeTrayBatter copy() {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public static CupcakeTrayBatter fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("cupcake_tray_batter", NbtElement.COMPOUND_TYPE)) {
            return CupcakeTrayBatter.getEmpty();
        }
        return CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("cupcake_tray_batter")).result().orElse(CupcakeTrayBatter.getEmpty());
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        if (this.equals(CupcakeTrayBatter.getEmpty())) {
            return nbt;
        }
        nbt.put("cupcake_tray_batter", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CupcakeTrayBatter that = (CupcakeTrayBatter) o;
        return this.stream().equals(that.stream());
    }

    public CupcakeTrayBatter withBatter(int i, @Nullable SimpleCakeBatter batter) {
        return switch (i) {
            case 0 -> this.withTopLeft(batter);
            case 1 -> this.withTopRight(batter);
            case 2 -> this.withBottomLeft(batter);
            case 3 -> this.withBottomRight(batter);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public CupcakeTrayBatter withTopLeft(@Nullable SimpleCakeBatter batter) {
        return new CupcakeTrayBatter(Optional.ofNullable(batter), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public CupcakeTrayBatter withTopRight(@Nullable SimpleCakeBatter batter) {
        return new CupcakeTrayBatter(this.topLeft(), Optional.ofNullable(batter), this.bottomLeft(), this.bottomRight());
    }

    public CupcakeTrayBatter withBottomLeft(@Nullable SimpleCakeBatter batter) {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), Optional.ofNullable(batter), this.bottomRight());
    }

    public CupcakeTrayBatter withBottomRight(@Nullable SimpleCakeBatter batter) {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), this.bottomLeft(), Optional.ofNullable(batter));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }
}
