package net.pedroricardo.block.extras.features;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeature;

import java.util.List;

public class ParticleCakeFeature extends CakeFeature {
    private final ParticleOptions effect;
    private final float chance;

    public ParticleCakeFeature(ParticleOptions effect, float chance) {
        this.effect = effect;
        this.chance = chance;
    }

    @Override
    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        float height = 0;
        for (CakeBatter<?> layer1 : batterList) {
            height += (float) layer1.getShape(state, world, pos, CollisionContext.empty()).max(Direction.Axis.Y);
            if (layer1 == batter) {
                break;
            }
        }
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return;
        if (world.isClientSide() && world.getRandom().nextFloat() < this.chance) {
            VoxelShape shape = batter.getShape(state, world, pos, CollisionContext.empty());
            world.addParticle(this.effect, pos.getX() + shape.min(Direction.Axis.X) + (shape.max(Direction.Axis.X) - shape.min(Direction.Axis.X)) * world.getRandom().nextFloat(), pos.getY() + height / 16.0f, pos.getZ() + shape.min(Direction.Axis.Z) + (shape.max(Direction.Axis.Z) - shape.min(Direction.Axis.Z)) * world.getRandom().nextFloat(), 0.0, 0.06125, 0.0);
        }
    }
}
