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

import java.util.Objects;

public class SimpleCakeBatter {
    private int bakeTime;
    private CakeFlavor flavor;

    public SimpleCakeBatter(int bakeTime, CakeFlavor flavor) {
        this.bakeTime = bakeTime;
        this.flavor = flavor;
    }

    private static final SimpleCakeBatter DEFAULT = new SimpleCakeBatter(0, CakeFlavors.VANILLA);
    public static final Codec<SimpleCakeBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.INT.fieldOf("bake_time").orElse(0).forGetter(SimpleCakeBatter::getBakeTime),
                    Identifier.CODEC.fieldOf("flavor").orElse(CakeFlavors.REGISTRY.getDefaultId()).forGetter((batter) -> CakeFlavors.REGISTRY.getId(batter.getFlavor())))
            .apply(instance, (bakeTime, flavorId) ->
                    new SimpleCakeBatter(bakeTime, CakeFlavors.REGISTRY.get(flavorId))
            ));
    public static final PacketCodec<RegistryByteBuf, SimpleCakeBatter> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, SimpleCakeBatter::getBakeTime, PacketCodecs.registryValue(CakeFlavors.REGISTRY_KEY), SimpleCakeBatter::getFlavor, SimpleCakeBatter::new);

    public static SimpleCakeBatter getDefault() {
        return DEFAULT.copy();
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        NbtElement result = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();

        if (result instanceof NbtCompound compound) {
            return compound;
        }
        return nbt;
    }

    public static SimpleCakeBatter fromNbt(@Nullable NbtCompound nbt) {
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

    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public CakeBatter toBatter(int height) {
        return new CakeBatter(this.getBakeTime(), height, this.getFlavor());
    }

    public CakeLayer toLayer(int height, int size) {
        return this.toBatter(height).toLayer(size);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleCakeBatter that = (SimpleCakeBatter) o;
        return this.bakeTime == that.bakeTime && Objects.equals(this.flavor, that.flavor);
    }

    public SimpleCakeBatter copy() {
        return new SimpleCakeBatter(this.getBakeTime(), this.getFlavor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bakeTime, this.flavor);
    }
}
