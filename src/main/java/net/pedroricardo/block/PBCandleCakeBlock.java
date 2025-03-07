package net.pedroricardo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.multipart.MultipartBlock;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;

public class PBCandleCakeBlock extends PBAbstractCandleCakeBlock implements BlockEntityProvider {
    private final CandleBlock candle;
    private static final Map<CandleBlock, PBCandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.newHashMap();

    protected PBCandleCakeBlock(CandleBlock candle, Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        CANDLES_TO_CANDLE_CAKES.put(candle, this);
        this.candle = candle;
    }

    @Override
    protected Iterable<Vec3d> getParticleOffsets(BlockState state, WorldAccess world, BlockPos pos) {
        return ImmutableList.of(new Vec3d(0.5, world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake ? (cake.getHeight() + 8) / 16.0 : 1.0, 0.5));
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public @Nullable PBCakeBlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(LIT).add(Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, PBBlockEntities.CAKE, PBCakeBlockEntity::tick);
    }

    private ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE) || !(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return ActionResult.FAIL;
        }
        if (hit.getPos().y - (double)hit.getBlockPos().getY() > cake.getHeight() / 16.0f && stack.isEmpty() && state.get(LIT)) {
            extinguish(player, state, world, pos);
            return ActionResult.success(world.isClient());
        }
        return ActionResult.PASS;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult useWithItem = this.onUseWithItem(player.getStackInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != ActionResult.PASS) return useWithItem;
        ActionResult tryEatResult = PBCakeBlock.tryUsing(world, pos, PBBlocks.CAKE.getDefaultState().with(Properties.HORIZONTAL_FACING, state.get(Properties.HORIZONTAL_FACING)), player, hit);
        if (tryEatResult.isAccepted()) {
            dropStacks(state, world, pos);
        }
        return tryEatResult;
    }

    public static BlockState getCandleCakeFromCandle(CandleBlock candle) {
        return CANDLES_TO_CANDLE_CAKES.get(candle).getDefaultState();
    }

    public CandleBlock getCandle() {
        return this.candle;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return PBCakeBlock.of(cake.getBatterList());
        }
        return PBCakeBlock.of(Collections.singletonList(CakeBatter.getFullSizeDefault()));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        this.remove(world, pos, true);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, @Nullable BlockEntity blockEntity, ShapeContext context) {
        if (!(blockEntity instanceof PBCakeBlockEntity cake)) {
            return VoxelShapes.fullCube();
        }
        return VoxelShapes.union(cake.toShape(), Block.createCuboidShape(7.0f, cake.getHeight(), 7.0f, 9.0f, cake.getHeight() + 6.0f, 9.0f));
    }
}
