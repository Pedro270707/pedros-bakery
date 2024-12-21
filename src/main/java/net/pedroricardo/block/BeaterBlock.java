package net.pedroricardo.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeaterBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HAS_LIQUID, Properties.POWERED, Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(HAS_LIQUID, false).with(Properties.POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(Properties.POWERED)) {
            world.setBlockState(pos, state.with(Properties.POWERED, bl), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> VoxelShapes.empty();
        };
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PBBlockEntities.BEATER, BeaterBlockEntity::tick);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult useWithItem = this.onUseWithItem(player.getStackInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != ActionResult.PASS) return useWithItem;
        if (!state.contains(HAS_LIQUID) || !state.get(HAS_LIQUID) || !(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) || beater.getItems().isEmpty()) return ActionResult.FAIL;
        else if (!world.isClient()) {
            ItemStack stack = beater.getItems().get(beater.getItems().size() - 1);
            player.giveItemStack(stack);
            beater.getItems().remove(beater.getItems().size() - 1);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
        return ActionResult.success(world.isClient());
    }

    private ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) || stack.isEmpty()) return ActionResult.PASS;
        if (!beater.updateLiquid()) {
            BlockState newState = state;
            if (stack.isOf(Items.MILK_BUCKET)) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }
                newState = newState.with(BeaterBlock.HAS_LIQUID, true);
                beater.setLiquid(new Liquid.Milk());
                world.setBlockState(pos, newState);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Emitter.of(player, newState));
                return ActionResult.SUCCESS;
            } else if (stack.isOf(PBItems.FROSTING_BOTTLE)) {
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                    serverPlayer.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }
                beater.setLiquid(new Liquid.Frosting(PBHelpers.get(stack, PBComponentTypes.TOP)));
                newState = newState.with(BeaterBlock.HAS_LIQUID, true);
                world.setBlockState(pos, newState);
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                world.emitGameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Emitter.of(player, newState));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }
        return beater.getLiquid().onUse(state, world, pos, player, hand, hit, beater);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater)) return 0;
        if (beater.getMixTime() == 0 && beater.getLiquid() != null) return beater.getLiquid().getType() == Liquid.Type.MILK ? 7 : 15;
        return Math.min(beater.getMixTime() * 15 / 200, 15);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.HORIZONTAL_FACING, rotation.rotate(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(Properties.HORIZONTAL_FACING)));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) {
            beater.getItems().forEach(stack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), stack));
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}