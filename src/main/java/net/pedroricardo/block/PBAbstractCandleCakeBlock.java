package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public abstract class PBAbstractCandleCakeBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected PBAbstractCandleCakeBlock(Properties settings) {
        super(settings);
    }

    protected abstract Iterable<Vec3> getParticleOffsets(BlockState state, LevelAccessor world, BlockPos pos);

    @Override
    public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!world.isClientSide() && projectile.isOnFire() && this.isNotLit(state)) {
            setLit(world, state, hit.getBlockPos(), true);
        }
    }

    protected boolean isNotLit(BlockState state) {
        return !state.getValue(LIT);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT)) {
            return;
        }
        this.getParticleOffsets(state, world, pos).forEach(offset -> spawnCandleParticles(world, offset.add(pos.getX(), pos.getY(), pos.getZ()), random));
    }

    private static void spawnCandleParticles(Level world, Vec3 vec3d, RandomSource random) {
        float f = random.nextFloat();
        if (f < 0.3f) {
            world.addParticle(ParticleTypes.SMOKE, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
            if (f < 0.17f) {
                world.playLocalSound(vec3d.x + 0.5, vec3d.y + 0.5, vec3d.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0f + random.nextFloat(), random.nextFloat() * 0.7f + 0.3f, false);
            }
        }
        world.addParticle(ParticleTypes.SMALL_FLAME, vec3d.x, vec3d.y, vec3d.z, 0.0, 0.0, 0.0);
    }

    public static void extinguish(@Nullable Player player, BlockState state, LevelAccessor world, BlockPos pos) {
        setLit(world, state, pos, false);
        if (state.getBlock() instanceof PBAbstractCandleCakeBlock) {
            ((PBAbstractCandleCakeBlock)state.getBlock()).getParticleOffsets(state, world, pos).forEach(offset -> world.addParticle(ParticleTypes.SMOKE, (double)pos.getX() + offset.x(), (double)pos.getY() + offset.y(), (double)pos.getZ() + offset.z(), 0.0, 0.1f, 0.0));
        }
        world.playSound(null, pos, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
        world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
    }

    private static void setLit(LevelAccessor world, BlockState state, BlockPos pos, boolean lit) {
        world.setBlock(pos, state.setValue(LIT, lit), Block.UPDATE_ALL & Block.UPDATE_IMMEDIATE);
    }
}
