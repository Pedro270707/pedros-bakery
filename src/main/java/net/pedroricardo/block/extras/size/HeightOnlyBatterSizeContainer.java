package net.pedroricardo.block.extras.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class HeightOnlyBatterSizeContainer extends BatterSizeContainer {
    private float height = 0.0f;

    public static final Codec<HeightOnlyBatterSizeContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("height").forGetter(HeightOnlyBatterSizeContainer::getHeight)
    ).apply(instance, HeightOnlyBatterSizeContainer::new));

    public HeightOnlyBatterSizeContainer() {
    }

    public HeightOnlyBatterSizeContainer(float height) {
        this.height = height;
    }

    @Override
    public boolean bite(Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity, float biteSize) {
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
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(0.0, 0.0, 0.0, 16.0, this.getHeight(), 16.0);
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
