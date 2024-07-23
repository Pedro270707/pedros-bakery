package net.pedroricardo.block.helpers.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class FixedBatterSizeContainer extends BatterSizeContainer {
    private boolean empty = true;

    public static final Codec<FixedBatterSizeContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("empty").forGetter(FixedBatterSizeContainer::isEmpty)
    ).apply(instance, FixedBatterSizeContainer::new));
    public static final PacketCodec<RegistryByteBuf, FixedBatterSizeContainer> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.BOOL, FixedBatterSizeContainer::isEmpty, FixedBatterSizeContainer::new);

    public FixedBatterSizeContainer() {
    }

    public FixedBatterSizeContainer(boolean empty) {
        this.empty = empty;
    }

    @Override
    public boolean bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize) {
        if (!this.isEmpty()) {
            this.setEmpty(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    public FixedBatterSizeContainer copy() {
        return new FixedBatterSizeContainer(this.isEmpty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FixedBatterSizeContainer that = (FixedBatterSizeContainer) o;
        return this.isEmpty() == that.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.isEmpty());
    }
}
