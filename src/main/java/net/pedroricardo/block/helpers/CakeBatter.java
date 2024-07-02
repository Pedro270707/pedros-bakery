package net.pedroricardo.block.helpers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class CakeBatter {
    private int bakeTime;
    private float height;
    private CakeFlavor flavor;

    public CakeBatter(int bakeTime, float height, CakeFlavor flavor) {
        this.bakeTime = bakeTime;
        this.height = height;
        this.flavor = flavor;
    }

    private static final CakeBatter DEFAULT = new CakeBatter(0, 8, CakeFlavors.VANILLA);
    private static final CakeBatter EMPTY = new CakeBatter(0, 0, CakeFlavors.VANILLA);
    public static final Codec<CakeBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    Codec.FLOAT.fieldOf("height").orElse(8.0f).forGetter(CakeBatter::getHeight),
                    Identifier.CODEC.fieldOf("flavor").orElse(CakeFlavors.REGISTRY.getDefaultId()).forGetter((batter) -> CakeFlavors.REGISTRY.getId(batter.getFlavor())))
            .apply(instance, (bakeTime, height, flavorId) ->
                    new CakeBatter(bakeTime, height, CakeFlavors.REGISTRY.get(flavorId))
            ));
    public static final PacketCodec<RegistryByteBuf, CakeBatter> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, CakeBatter::getBakeTime, PacketCodecs.FLOAT, CakeBatter::getHeight, PacketCodecs.registryValue(CakeFlavors.REGISTRY_KEY), CakeBatter::getFlavor, CakeBatter::new);

    public static CakeBatter getDefault() {
        return DEFAULT.copy();
    }

    public static CakeBatter getEmpty() {
        return EMPTY.copy();
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        NbtElement result = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();

        if (result instanceof NbtCompound compound) {
            return compound;
        }
        return nbt;
    }

    public static CakeBatter fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return getDefault();
        }
        return CODEC.parse(NbtOps.INSTANCE, nbt).result().orElse(getDefault());
    }

    public int getBakeTime() {
        return this.bakeTime;
    }

    public void setBakeTime(int time) {
        this.bakeTime = Math.max(time, 0);
    }

    public final void bakeTick(World world, BlockPos pos, BlockState state) {
        if (this.getBakeTime() != Integer.MAX_VALUE) {
            this.setBakeTime(this.getBakeTime() + 1);
        }
        this.getFlavor().bakeTick(this, world, pos, state);
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public boolean isEmpty() {
        return this.getHeight() == 0;
    }

    public CakeLayer toLayer(int size) {
        return new CakeLayer(this.getBakeTime(), this.getHeight(), 0, size, this.getFlavor(), Optional.empty(), Map.of());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CakeBatter that = (CakeBatter) o;
        return this.bakeTime == that.bakeTime && this.height == that.height && Objects.equals(this.flavor, that.flavor);
    }

    public CakeBatter copy() {
        return new CakeBatter(this.getBakeTime(), this.getHeight(), this.getFlavor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bakeTime, this.height, this.flavor);
    }
}
