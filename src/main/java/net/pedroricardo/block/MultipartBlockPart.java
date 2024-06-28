package net.pedroricardo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import net.pedroricardo.block.entity.MultipartBlockEntityPart;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MultipartBlockPart<E extends MultipartBlockEntityPart> extends BlockWithEntity {
    public static final BooleanProperty DELEGATE = BooleanProperty.of("delegate");

    public MultipartBlockPart(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(DELEGATE, true));
    }

    public boolean stillValid(WorldView world, BlockPos pos) {
        try {
            E blockEntity = (E) world.getBlockEntity(pos);
            return blockEntity != null && blockEntity.getParent() != null;
        } catch (ClassCastException ignored) {
            return false;
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DELEGATE);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().afterBreak(world, player, cake.getPos(), cake.getCachedState(), cake, tool));
        }
        super.afterBreak(world, player, pos, state, blockEntity, tool);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().onSteppedOn(world, cake.getPos(), cake.getCachedState(), entity));
        }
        super.onSteppedOn(world, pos, state, entity);
    }

    @Override
    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().onLandedUpon(world, cake.getCachedState(), cake.getPos(), entity, fallDistance));
        }
        super.onLandedUpon(world, state, pos, entity, fallDistance);
    }

    @Override
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, hit.getBlockPos(), cake -> world.getBlockState(cake.getPos()).onProjectileHit(world, cake.getCachedState(), hit, projectile));
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onExploded(world, cake.getPos(), explosion, stackMerger));
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onSyncedBlockEvent(world, cake.getPos(), type, data));
        }
        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onBlockAdded(world, cake.getPos(), cake.getCachedState(), notify));
        }
        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().onBroken(world, cake.getPos(), cake.getCachedState()));
        }
        super.onBroken(world, pos, state);
    }

    @Override
    protected void onBlockBreakStart(BlockState state, World world, BlockPos pos, PlayerEntity player) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onBlockBreakStart(world, cake.getPos(), player));
        }
        super.onBlockBreakStart(state, world, pos, player);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockState onBreakSuper = super.onBreak(world, pos, state, player);
        return !state.getOrEmpty(DELEGATE).orElse(false) ? onBreakSuper : this.parentFunction(world, pos, cake -> cake.getCachedState().getBlock().onBreak(world, cake.getPos(), state, player)).orElse(state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (newState.getOrEmpty(DELEGATE).orElse(state.getOrEmpty(DELEGATE).orElse(false))) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onStateReplaced(world, cake.getPos(), newState, moved));
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? ActionResult.FAIL : this.parentFunction(world, pos, cake -> {
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), hit.getSide(), cake.getPos(), true);
            return world.getBlockState(cake.getPos()).onUse(world, player, newHit);
        }).orElse(ActionResult.FAIL);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? ItemActionResult.FAIL : this.parentFunction(world, pos, cake -> {
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), hit.getSide(), cake.getPos(), true);
            return world.getBlockState(cake.getPos()).onUseWithItem(stack, world, player, hand, newHit);
        }).orElse(ItemActionResult.FAIL);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world, pos, cake -> world.getBlockState(cake.getPos()).onEntityCollision(world, cake.getPos(), entity));
        } else {
            super.onEntityCollision(state, world, pos, entity);
        }
    }

    private <T> Optional<T> parentFunction(WorldView world, BlockPos pos, Function<PBCakeBlockEntity, T> function) {
        PBCakeBlockEntity blockEntity;
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntityPart part && (blockEntity = part.getParent()) != null) {
            return Optional.ofNullable(function.apply(blockEntity));
        }
        return Optional.empty();
    }

    private void parentConsumer(WorldView world, BlockPos pos, Consumer<PBCakeBlockEntity> function) {
        PBCakeBlockEntity blockEntity;
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntityPart part && (blockEntity = part.getParent()) != null) {
            function.accept(blockEntity);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.parentFunction(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().getPickStack(world, cake.getPos(), cake.getCachedState())).orElse(ItemStack.EMPTY);
    }
}
