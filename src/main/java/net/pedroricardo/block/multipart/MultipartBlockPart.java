package net.pedroricardo.block.multipart;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
    public void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(hit.getBlockPos()), blockEntity -> world.getBlockState(blockEntity.getPos()).onProjectileHit(world, blockEntity.getCachedState(), hit, projectile));
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    public void onDestroyedByExplosion(World world, BlockPos pos, Explosion explosion) {
        BlockState state = world.getBlockState(pos);
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).getBlock().onDestroyedByExplosion(world, blockEntity.getPos(), explosion));
        }
        super.onDestroyedByExplosion(world, pos, explosion);
    }

    @Override
    public boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onSyncedBlockEvent(world, blockEntity.getPos(), type, data));
        }
        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> {
                world.breakBlock(blockEntity.getPos(), player.canHarvest(blockEntity.getCachedState()) && !player.isCreative(), player);
//                blockEntity.getCachedState().getBlock().afterBreak(world, player, blockEntity.getPos(), blockEntity.getCachedState(), blockEntity, player.getMainHandStack());
                if (!world.isClient()) {
                    PBHelpers.update(blockEntity, (ServerWorld) world);
                }
            });
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (state.getOrEmpty(DELEGATE).orElse(state.getOrEmpty(DELEGATE).orElse(false))) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onStacksDropped(world, blockEntity.getPos(), tool, dropExperience));
        }
        super.onStacksDropped(state, world, pos, tool, dropExperience);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? ActionResult.FAIL : this.parentFunction(world.getBlockEntity(pos), blockEntity -> {
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), hit.getSide(), blockEntity.getPos(), true);
            return world.getBlockState(blockEntity.getPos()).onUse(world, player, hand, newHit);
        }).orElse(ActionResult.FAIL);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onEntityCollision(world, blockEntity.getPos(), entity));
        } else {
            super.onEntityCollision(state, world, pos, entity);
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return this.parentFunction(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).getBlock().getPickStack(world, blockEntity.getPos(), blockEntity.getCachedState())).orElse(ItemStack.EMPTY);
    }

    public abstract E createBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos);
}
