package net.pedroricardo.block.helpers.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Objects;

public class HeightOnlyBatterSizeContainer extends BatterSizeContainer {
    private float height = 0.0f;

    public static final Codec<HeightOnlyBatterSizeContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("height").forGetter(HeightOnlyBatterSizeContainer::getHeight)
    ).apply(instance, HeightOnlyBatterSizeContainer::new));
    public static final PacketCodec<RegistryByteBuf, HeightOnlyBatterSizeContainer> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, HeightOnlyBatterSizeContainer::getHeight, HeightOnlyBatterSizeContainer::new);

    public HeightOnlyBatterSizeContainer() {
    }

    public HeightOnlyBatterSizeContainer(float height) {
        this.height = height;
    }

    @Override
    public boolean bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.getHeight() == 0;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public HeightOnlyBatterSizeContainer copy() {
        return new HeightOnlyBatterSizeContainer(this.getHeight());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, this.getHeight(), 16.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        HeightOnlyBatterSizeContainer that = (HeightOnlyBatterSizeContainer) o;
        return Float.compare(this.getHeight(), that.getHeight()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getHeight());
    }
}
