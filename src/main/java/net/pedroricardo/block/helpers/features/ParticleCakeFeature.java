package net.pedroricardo.block.helpers.features;

import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.helpers.CakeFeature;
import net.pedroricardo.block.helpers.CakeBatter;

public class ParticleCakeFeature extends CakeFeature {
    private final ParticleEffect effect;
    private final float chance;

    public ParticleCakeFeature(ParticleEffect effect, float chance) {
        this.effect = effect;
        this.chance = chance;
    }

    @Override
    public void tick(CakeBatter layer, World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        float height = 0;
        for (CakeBatter layer1 : blockEntity.getLayers()) {
            height += layer1.getHeight();
            if (layer1 == layer) {
                break;
            }
        }
        if (!state.contains(Properties.HORIZONTAL_FACING)) return;
        Direction direction = state.get(Properties.HORIZONTAL_FACING);
        if (world.isClient() && world.getRandom().nextFloat() < this.chance) {
            // maybe use bounding box later?
            world.addParticle(this.effect, pos.getX() + world.getRandom().nextFloat() * (layer.getSize() - (direction.getAxis() == Direction.Axis.Z ? layer.getBites() : 0)) / 16.0f + 0.5f - (layer.getSize()) / 32.0f + (direction == Direction.SOUTH ? layer.getBites() / 16.0f : 0.0f), pos.getY() + height / 16.0f, pos.getZ() + world.getRandom().nextFloat() * (layer.getSize() - (direction.getAxis() == Direction.Axis.X ? layer.getBites() : 0)) / 16.0f + 0.5f - layer.getSize() / 32.0f + (direction == Direction.WEST ? layer.getBites() / 16.0f : 0.0f), 0.0, 0.06125, 0.0);
        }
    }
}
