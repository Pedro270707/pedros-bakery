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

public record CupcakeBatters(Optional<SimpleCakeBatter> topLeft, Optional<SimpleCakeBatter> topRight, Optional<SimpleCakeBatter> bottomLeft, Optional<SimpleCakeBatter> bottomRight) {
    public static final Codec<CupcakeBatters> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SimpleCakeBatter.CODEC.optionalFieldOf("top_left").forGetter(CupcakeBatters::topLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("top_right").forGetter(CupcakeBatters::topRight),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_left").forGetter(CupcakeBatters::bottomLeft),
            SimpleCakeBatter.CODEC.optionalFieldOf("bottom_right").forGetter(CupcakeBatters::bottomRight)
    ).apply(instance, CupcakeBatters::new));
    public static final PacketCodec<RegistryByteBuf, CupcakeBatters> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatters::topLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatters::topRight, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatters::bottomLeft, PacketCodecs.optional(SimpleCakeBatter.PACKET_CODEC), CupcakeBatters::bottomRight, CupcakeBatters::new);
    private static final CupcakeBatters EMPTY = new CupcakeBatters(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());

    public static CupcakeBatters getEmpty() {
        return EMPTY.copy();
    }

    public CupcakeBatters(List<Optional<SimpleCakeBatter>> list) {
        this(list.isEmpty() ? Optional.empty() : list.getFirst(), list.size() > 1 ? list.get(1) : Optional.empty(), list.size() > 2 ? list.get(2) : Optional.empty(), list.size() > 3 ? list.get(3) : Optional.empty());
    }

    public List<Optional<SimpleCakeBatter>> stream() {
        return Stream.of(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight).toList();
    }

    public CupcakeBatters copy() {
        return new CupcakeBatters(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public static CupcakeBatters fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("cupcake_batters", NbtElement.COMPOUND_TYPE)) {
            return CupcakeBatters.getEmpty();
        }
        return CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("cupcake_batters")).result().orElse(CupcakeBatters.getEmpty());
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        if (this.equals(CupcakeBatters.getEmpty())) {
            return nbt;
        }
        nbt.put("cupcake_batters", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CupcakeBatters that = (CupcakeBatters) o;
        return this.stream().equals(that.stream());
    }

    public CupcakeBatters withBatter(int i, @Nullable SimpleCakeBatter batter) {
        return switch (i) {
            case 0 -> this.withTopLeft(batter);
            case 1 -> this.withTopRight(batter);
            case 2 -> this.withBottomLeft(batter);
            case 3 -> this.withBottomRight(batter);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public CupcakeBatters withTopLeft(@Nullable SimpleCakeBatter batter) {
        return new CupcakeBatters(Optional.ofNullable(batter), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public CupcakeBatters withTopRight(@Nullable SimpleCakeBatter batter) {
        return new CupcakeBatters(this.topLeft(), Optional.ofNullable(batter), this.bottomLeft(), this.bottomRight());
    }

    public CupcakeBatters withBottomLeft(@Nullable SimpleCakeBatter batter) {
        return new CupcakeBatters(this.topLeft(), this.topRight(), Optional.ofNullable(batter), this.bottomRight());
    }

    public CupcakeBatters withBottomRight(@Nullable SimpleCakeBatter batter) {
        return new CupcakeBatters(this.topLeft(), this.topRight(), this.bottomLeft(), Optional.ofNullable(batter));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }
}
