package net.pedroricardo.block.helpers.features;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.block.helpers.CakeFeature;

import java.util.List;

public class ParticleCakeFeature extends CakeFeature {
    private final ParticleEffect effect;
    private final float chance;

    public ParticleCakeFeature(ParticleEffect effect, float chance) {
        this.effect = effect;
        this.chance = chance;
    }

    @Override
    public void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        float height = 0;
        for (CakeBatter<?> layer1 : batterList) {
            height += (float) layer1.getShape(state, world, pos, ShapeContext.absent()).getMax(Direction.Axis.Y);
            if (layer1 == batter) {
                break;
            }
        }
        if (!state.contains(Properties.HORIZONTAL_FACING)) return;
        if (world.isClient() && world.getRandom().nextFloat() < this.chance) {
            VoxelShape shape = batter.getShape(state, world, pos, ShapeContext.absent());
            world.addParticle(this.effect, pos.getX() + shape.getMin(Direction.Axis.X) + (shape.getMax(Direction.Axis.X) - shape.getMin(Direction.Axis.X)) * world.getRandom().nextFloat(), pos.getY() + height / 16.0f, pos.getZ() + shape.getMin(Direction.Axis.Z) + (shape.getMax(Direction.Axis.Z) - shape.getMin(Direction.Axis.Z)) * world.getRandom().nextFloat(), 0.0, 0.06125, 0.0);
        }
    }
}
