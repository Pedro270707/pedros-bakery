package net.pedroricardo.block.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.explosion.Explosion;
import net.pedroricardo.PBHelpers;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MultipartBlockPart<C extends BlockEntity & MultipartBlockEntity, E extends MultipartBlockEntityPart<C>> extends BlockWithEntity {
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
    protected void onProjectileHit(World world, BlockState state, BlockHitResult hit, ProjectileEntity projectile) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(hit.getBlockPos()), blockEntity -> world.getBlockState(blockEntity.getPos()).onProjectileHit(world, blockEntity.getCachedState(), hit, projectile));
        }
        super.onProjectileHit(world, state, hit, projectile);
    }

    @Override
    protected void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onExploded(world, blockEntity.getPos(), explosion, stackMerger));
        }
        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    @Override
    protected boolean onSyncedBlockEvent(BlockState state, World world, BlockPos pos, int type, int data) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onSyncedBlockEvent(world, blockEntity.getPos(), type, data));
        }
        return super.onSyncedBlockEvent(state, world, pos, type, data);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (state.getOrEmpty(DELEGATE).orElse(false)) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> {
                world.breakBlock(blockEntity.getPos(), player.canHarvest(blockEntity.getCachedState()) && !player.isInCreativeMode(), player);
//                blockEntity.getCachedState().getBlock().afterBreak(world, player, blockEntity.getPos(), blockEntity.getCachedState(), blockEntity, player.getMainHandStack());
                PBHelpers.updateListeners(blockEntity);
            });
        }
        return super.onBreak(world, pos, state, player);
    }

    @Override
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        if (state.getOrEmpty(DELEGATE).orElse(state.getOrEmpty(DELEGATE).orElse(false))) {
            this.parentConsumer(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).onStacksDropped(world, blockEntity.getPos(), tool, dropExperience));
        }
        super.onStacksDropped(state, world, pos, tool, dropExperience);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? ActionResult.FAIL : this.parentFunction(world.getBlockEntity(pos), blockEntity -> {
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), hit.getSide(), blockEntity.getPos(), true);
            return world.getBlockState(blockEntity.getPos()).onUse(world, player, newHit);
        }).orElse(ActionResult.FAIL);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? ItemActionResult.FAIL : this.parentFunction(world.getBlockEntity(pos), blockEntity -> {
            BlockHitResult newHit = new BlockHitResult(hit.getPos(), hit.getSide(), blockEntity.getPos(), true);
            return world.getBlockState(blockEntity.getPos()).onUseWithItem(stack, world, player, hand, newHit);
        }).orElse(ItemActionResult.FAIL);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
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
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.parentFunction(world.getBlockEntity(pos), blockEntity -> world.getBlockState(blockEntity.getPos()).getBlock().getPickStack(world, blockEntity.getPos(), blockEntity.getCachedState())).orElse(ItemStack.EMPTY);
    }

    public abstract E createBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos);
}
