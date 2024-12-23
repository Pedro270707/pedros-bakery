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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Objects;

public class FixedBatterSizeContainer extends BatterSizeContainer {
    private boolean empty = true;

    public static final Codec<FixedBatterSizeContainer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("empty").forGetter(FixedBatterSizeContainer::isEmpty)
    ).apply(instance, FixedBatterSizeContainer::new));

    public FixedBatterSizeContainer() {
    }

    public FixedBatterSizeContainer(boolean empty) {
        this.empty = empty;
    }

    @Override
    public boolean bite(Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity, float biteSize) {
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
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.isEmpty() ? Shapes.empty() : Block.box(5.5, 0.0, 5.5, 10.5, 4.0, 10.5);
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
