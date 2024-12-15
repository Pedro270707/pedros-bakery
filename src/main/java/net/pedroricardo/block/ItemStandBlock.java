package net.pedroricardo.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.ItemStandBlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class ItemStandBlock<T extends ItemStandBlockEntity> extends BlockWithEntity {
    protected ItemStandBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(Properties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    public abstract boolean canContain(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit);

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack stack = player.getStackInHand(hand);
        try {
            @SuppressWarnings("unchecked")
            T stand = (T) world.getBlockEntity(pos);
            if (stand == null) return ActionResult.PASS;
            if (stand.getStack().isEmpty() && canContain(stack, state, world, pos, player, hand, hit)) {
                stand.setStack(PBHelpers.splitUnlessCreative(stack, 1, player));
                world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(player, state));
                return ActionResult.SUCCESS;
            } else if (!stand.getStack().isEmpty()) {
                player.giveItemStack(stand.getStack());
                stand.setStack(ItemStack.EMPTY);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        } catch (ClassCastException ignored) {
            return ActionResult.PASS;
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.dropItem(world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    private void dropItem(World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ItemStandBlockEntity stand && !world.isClient()) {
            ItemStack stack = stand.getStack().copy();
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + 0.5, pos.getY() + 1, (double)pos.getZ() + 0.5, stack);
            itemEntity.setToDefaultPickupDelay();
            world.spawnEntity(itemEntity);
            stand.clear();
            PBHelpers.update(stand, (ServerWorld) world);
        }
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ItemStandBlockEntity stand && !stand.getStack().isEmpty() ? 15 : 0;
    }
}
