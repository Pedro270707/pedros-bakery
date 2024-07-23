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

public class FullBatterSizeContainer extends BatterSizeContainer {
    private float size = 0.0f;
    private float height = 0.0f;
    private float bites = 0.0f;
    public static final Codec<FullBatterSizeContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("size").forGetter(FullBatterSizeContainer::getSize),
            Codec.FLOAT.fieldOf("height").forGetter(FullBatterSizeContainer::getHeight),
            Codec.FLOAT.fieldOf("bites").forGetter(FullBatterSizeContainer::getBites)
    ).apply(instance, FullBatterSizeContainer::new));
    public static final PacketCodec<RegistryByteBuf, FullBatterSizeContainer> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, FullBatterSizeContainer::getSize, PacketCodecs.FLOAT, FullBatterSizeContainer::getHeight, PacketCodecs.FLOAT, FullBatterSizeContainer::getBites, FullBatterSizeContainer::new);

    public FullBatterSizeContainer() {
    }

    public FullBatterSizeContainer(float size, float height) {
        this.size = size;
        this.height = height;
    }

    public FullBatterSizeContainer(float size, float height, float bites) {
        this(size, height);
        this.bites = bites;
    }

    @Override
    public boolean bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize) {
        if (this.isEmpty()) return false;
        this.bites += biteSize;
        if (this.bites > this.getSize()) {
            this.bites = this.getSize();
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.getSize() == 0 || this.getHeight() == 0 || this.getBites() >= this.getSize();
    }

    public float getSize() {
        return this.size;
    }

    public float getHeight() {
        return this.height;
    }

    public float getBites() {
        return this.bites;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setBites(float bites) {
        this.bites = bites;
    }

    @Override
    public FullBatterSizeContainer copy() {
        return new FullBatterSizeContainer(this.getSize(), this.getHeight(), this.getBites());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        FullBatterSizeContainer that = (FullBatterSizeContainer) o;
        return Float.compare(this.getSize(), that.getSize()) == 0 && Float.compare(this.getHeight(), that.getHeight()) == 0 && Float.compare(this.getBites(), that.getBites()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getSize(), this.getHeight(), this.getBites());
    }
}
