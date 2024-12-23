package net.pedroricardo.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroricardo.PBHelpers;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MultipartBlockPart<C extends BlockEntity & MultipartBlockEntity, E extends MultipartBlockEntityPart<C>> extends BaseEntityBlock {
    public static final BooleanProperty DELEGATE = BooleanProperty.create("delegate");

    public MultipartBlockPart(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(DELEGATE, true));
    }

    public boolean stillValid(LevelAccessor world, BlockPos pos) {
        try {
            E blockEntity = (E) world.getBlockEntity(pos);
            return blockEntity != null && blockEntity.getParent() != null;
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DELEGATE);
    }

    @Override
    public void onProjectileHit(Level world, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (state.getOptionalValue(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(hit.getBlockPos()), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).onProjectileHit(world, blockEntity.getBlockState(), hit, projectile));
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public void wasExploded(Level world, BlockPos pos, Explosion explosion) {
        BlockState state = world.getBlockState(pos);
        if (state.getOptionalValue(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).getBlock().wasExploded(world, blockEntity.getBlockPos(), explosion));
        }
        super.wasExploded(world, pos, explosion);
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int type, int data) {
        if (state.getOptionalValue(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).triggerEvent(world, blockEntity.getBlockPos(), type, data));
        }
        return super.triggerEvent(state, world, pos, type, data);
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (state.getOptionalValue(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> {
                world.destroyBlock(blockEntity.getBlockPos(), player.hasCorrectToolForDrops(blockEntity.getBlockState()) && !player.isCreative(), player);
//                blockEntity.getCachedState().getBlock().afterBreak(world, player, blockEntity.getBlockPos(), blockEntity.getCachedState(), blockEntity, player.getMainHandStack());
                if (!world.isClientSide()) {
                    PBHelpers.update(blockEntity, (ServerLevel) world);
                }
            });
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (state.getOptionalValue(DELEGATE).orElse(state.getOptionalValue(DELEGATE).orElse(false))) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).spawnAfterBreak(world, blockEntity.getBlockPos(), tool, dropExperience));
        }
        super.spawnAfterBreak(state, world, pos, tool, dropExperience);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return !state.getOptionalValue(DELEGATE).orElse(false) ? InteractionResult.FAIL : this.parentFunction(world.getBlockEntity(pos), blockEntity -> {
            BlockHitResult newHit = new BlockHitResult(hit.getLocation(), hit.getDirection(), blockEntity.getBlockPos(), true);
            return world.getBlockState(blockEntity.getBlockPos()).use(world, player, hand, newHit);
        }).orElse(InteractionResult.FAIL);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (state.getOptionalValue(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).entityInside(world, blockEntity.getBlockPos(), entity));
        } else {
            super.entityInside(state, world, pos, entity);
        }
    }

    private <T> Optional<T> parentFunction(BlockEntity blockEntity, Function<C, T> function) {
        if (blockEntity == null) {
            return Optional.empty();
        }
        try {
            C parent = ((E) blockEntity).getParent();
            if (parent != null) {
                return Optional.ofNullable(function.apply(parent));
            }
            return Optional.empty();
        } catch (ClassCastException ignored) {
            return Optional.empty();
        }
    }

    private void parentConsumer(BlockEntity blockEntity, Consumer<C> function) {
        if (blockEntity == null) {
            return;
        }
        try {
            C parent = ((E) blockEntity).getParent();
            if (parent != null) {
                function.accept(parent);
            }
        } catch (ClassCastException ignored) {
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        return this.parentFunction(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getBlockPos()).getBlock().getCloneItemStack(world, blockEntity.getBlockPos(), blockEntity.getBlockState())).orElse(ItemStack.EMPTY);
    }

    public abstract E newBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos);
}
