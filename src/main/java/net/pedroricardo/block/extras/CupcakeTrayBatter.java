package net.pedroricardo.block.extras;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public record CupcakeTrayBatter(CakeBatter<FixedBatterSizeContainer> topLeft, CakeBatter<FixedBatterSizeContainer> topRight, CakeBatter<FixedBatterSizeContainer> bottomLeft, CakeBatter<FixedBatterSizeContainer> bottomRight) {
    public static final Codec<CupcakeTrayBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CakeBatter.FIXED_SIZE_CODEC.fieldOf("top_left").orElse(CakeBatter.getFixedSizeEmpty()).forGetter(CupcakeTrayBatter::topLeft),
            CakeBatter.FIXED_SIZE_CODEC.fieldOf("top_right").orElse(CakeBatter.getFixedSizeEmpty()).forGetter(CupcakeTrayBatter::topRight),
            CakeBatter.FIXED_SIZE_CODEC.fieldOf("bottom_left").orElse(CakeBatter.getFixedSizeEmpty()).forGetter(CupcakeTrayBatter::bottomLeft),
            CakeBatter.FIXED_SIZE_CODEC.fieldOf("bottom_right").orElse(CakeBatter.getFixedSizeEmpty()).forGetter(CupcakeTrayBatter::bottomRight)
    ).apply(instance, CupcakeTrayBatter::new));
    private static final CupcakeTrayBatter EMPTY = new CupcakeTrayBatter(CakeBatter.getFixedSizeEmpty(), CakeBatter.getFixedSizeEmpty(), CakeBatter.getFixedSizeEmpty(), CakeBatter.getFixedSizeEmpty());

    public static CupcakeTrayBatter getEmpty() {
        return EMPTY.copy();
    }

    public CupcakeTrayBatter(List<CakeBatter<FixedBatterSizeContainer>> list) {
        this(list.isEmpty() ? CakeBatter.getFixedSizeEmpty() : list.get(0), list.size() > 1 ? list.get(1) : CakeBatter.getFixedSizeEmpty(), list.size() > 2 ? list.get(2) : CakeBatter.getFixedSizeEmpty(), list.size() > 3 ? list.get(3) : CakeBatter.getFixedSizeEmpty());
    }

    public List<CakeBatter<FixedBatterSizeContainer>> stream() {
        return Stream.of(this.topLeft, this.topRight, this.bottomLeft, this.bottomRight).toList();
    }

    public CupcakeTrayBatter copy() {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public static CupcakeTrayBatter fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("batter", NbtElement.COMPOUND_TYPE)) {
            return CupcakeTrayBatter.getEmpty();
        }
        return CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("batter")).result().orElse(CupcakeTrayBatter.getEmpty());
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        if (this.equals(CupcakeTrayBatter.getEmpty())) {
            return nbt;
        }
        nbt.put("batter", CODEC.encodeStart(NbtOps.INSTANCE, this).get().orThrow());
        return nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CupcakeTrayBatter that = (CupcakeTrayBatter) o;
        return this.stream().equals(that.stream());
    }

    public CupcakeTrayBatter withBatter(int i, @Nullable CakeBatter<FixedBatterSizeContainer> batter) {
        return switch (i) {
            case 0 -> this.withTopLeft(batter);
            case 1 -> this.withTopRight(batter);
            case 2 -> this.withBottomLeft(batter);
            case 3 -> this.withBottomRight(batter);
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public CupcakeTrayBatter withTopLeft(@Nullable CakeBatter<FixedBatterSizeContainer> batter) {
        return new CupcakeTrayBatter(batter, this.topRight(), this.bottomLeft(), this.bottomRight());
    }

    public CupcakeTrayBatter withTopRight(@Nullable CakeBatter<FixedBatterSizeContainer> batter) {
        return new CupcakeTrayBatter(this.topLeft(), batter, this.bottomLeft(), this.bottomRight());
    }

    public CupcakeTrayBatter withBottomLeft(@Nullable CakeBatter<FixedBatterSizeContainer> batter) {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), batter, this.bottomRight());
    }

    public CupcakeTrayBatter withBottomRight(@Nullable CakeBatter<FixedBatterSizeContainer> batter) {
        return new CupcakeTrayBatter(this.topLeft(), this.topRight(), this.bottomLeft(), batter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.topLeft(), this.topRight(), this.bottomLeft(), this.bottomRight());
    }
}
