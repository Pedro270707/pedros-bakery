package net.pedroricardo.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.extras.beater.Liquid;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

public class BeaterBlock extends BaseEntityBlock {
    public static final BooleanProperty HAS_LIQUID = BooleanProperty.create("has_liquid");
    private static final VoxelShape EAST_SHAPE = Shapes.or(Block.box(5, 9, 0, 11, 14, 12), Block.box(6, 0, 1, 10, 10, 4), Block.box(3, 0, 3, 13, 1, 13), Block.box(4, 1, 4, 12, 7, 12));
    private static final VoxelShape WEST_SHAPE = Shapes.or(Block.box(5, 9, 4, 11, 14, 16), Block.box(6, 0, 12, 10, 10, 15), Block.box(3, 0, 3, 13, 1, 13), Block.box(4, 1, 4, 12, 7, 12));
    private static final VoxelShape NORTH_SHAPE = Shapes.or(Block.box(0, 9, 5, 12, 14, 11), Block.box(1, 0, 6, 4, 10, 10), Block.box(3, 0, 3, 13, 1, 13), Block.box(4, 1, 4, 12, 7, 12));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(Block.box(4, 9, 5, 16, 14, 11), Block.box(12, 0, 6, 15, 10, 10), Block.box(3, 0, 3, 13, 1, 13), Block.box(4, 1, 4, 12, 7, 12));

    protected BeaterBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(HAS_LIQUID, false).setValue(BlockStateProperties.POWERED, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeaterBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HAS_LIQUID, BlockStateProperties.POWERED, BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(HAS_LIQUID, false).setValue(BlockStateProperties.POWERED, ctx.getLevel().hasNeighborSignal(ctx.getClickedPos())).setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClientSide()) {
            return;
        }
        boolean bl = world.hasNeighborSignal(pos);
        if (bl != state.getValue(BlockStateProperties.POWERED)) {
            world.setBlock(pos, state.setValue(BlockStateProperties.POWERED, bl), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> Shapes.empty();
        };
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.BEATER.get(), BeaterBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (!state.hasProperty(HAS_LIQUID) || !state.getValue(HAS_LIQUID) || !(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) || beater.getItems().isEmpty()) return InteractionResult.FAIL;
        else if (!world.isClientSide()) {
            ItemStack stack = beater.getItems().get(beater.getItems().size() - 1);
            player.addItem(stack);
            beater.getItems().remove(beater.getItems().size() - 1);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return InteractionResult.sidedSuccess(world.isClientSide());
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) || stack.isEmpty()) return InteractionResult.PASS;
        if (!beater.updateLiquid()) {
            BlockState newState = state;
            if (stack.is(Items.MILK_BUCKET)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                newState = newState.setValue(BeaterBlock.HAS_LIQUID, true);
                beater.setLiquid(new Liquid.Milk());
                world.setBlockAndUpdate(pos, newState);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
                world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Context.of(player, newState));
                return InteractionResult.SUCCESS;
            } else if (stack.is(PBItems.FROSTING_BOTTLE.get())) {
                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                }
                beater.setLiquid(new Liquid.Frosting(PBHelpers.get(stack, PBComponentTypes.TOP.get())));
                newState = newState.setValue(BeaterBlock.HAS_LIQUID, true);
                world.setBlockAndUpdate(pos, newState);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                world.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                world.gameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Context.of(player, newState));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        return beater.getLiquid().use(state, world, pos, player, hand, hit, beater);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater)) return 0;
        if (beater.getMixTime() == 0 && beater.getLiquid() != null) return beater.getLiquid().getType() == Liquid.Type.MILK ? 7 : 15;
        return Math.min(beater.getMixTime() * 15 / 200, 15);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) {
            beater.getItems().forEach(stack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onRemove(state, world, pos, newState, moved);
    }
}