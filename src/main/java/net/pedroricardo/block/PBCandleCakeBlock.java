package net.pedroricardo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PBCandleCakeBlock extends PBAbstractCandleCakeBlock implements EntityBlock, MultipartBlock<PBCakeBlockEntity, PBCakeBlockEntityPart, PBCakeBlockPart> {
    private final CandleBlock candle;
    private static final Map<CandleBlock, PBCandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.newHashMap();

    protected PBCandleCakeBlock(CandleBlock candle, Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LIT, false).setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        CANDLES_TO_CANDLE_CAKES.put(candle, this);
        this.candle = candle;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state, LevelAccessor world, BlockPos pos) {
        return ImmutableList.of(new Vec3(0.5, world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake ? (cake.getHeight() + 8) / 16.0 : 1.0, 0.5));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.join(this.getFullShape(state, world, pos, context), Shapes.block(), BooleanOp.AND);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT).add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, PBBlockEntities.CAKE.get(), PBCakeBlockEntity::tick);
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.FLINT_AND_STEEL) || stack.is(Items.FIRE_CHARGE) || !(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return InteractionResult.FAIL;
        }
        if (hit.getLocation().y - (double)hit.getBlockPos().getY() > cake.getHeight() / 16.0f && stack.isEmpty() && state.getValue(LIT)) {
            extinguish(player, state, world, pos);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        InteractionResult tryEatResult = PBCakeBlock.tryUsing(world, pos, PBBlocks.CAKE.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING)), player, hit);
        if (tryEatResult.consumesAction()) {
            dropResources(state, world, pos);
        }
        return tryEatResult;
    }

    public static BlockState getCandleCakeFromCandle(CandleBlock candle) {
        return CANDLES_TO_CANDLE_CAKES.get(candle).defaultBlockState();
    }

    public CandleBlock getCandle() {
        return this.candle;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return PBCakeBlock.of(cake.getBatterList());
        }
        return PBCakeBlock.of(Collections.singletonList(CakeBatter.getFullSizeDefault()));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return Shapes.block();
        }
        return Shapes.or(cake.toShape(), Block.box(7.0f, cake.getHeight(), 7.0f, 9.0f, cake.getHeight() + 6.0f, 9.0f));
    }

    @Override
    public List<BlockPos> getParts(LevelAccessor world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return cake.getParts();
        }
        return List.of();
    }

    @Override
    public void removePartsWhenReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).hasProperty(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockAndUpdate(partPos, world.getBlockState(partPos).setValue(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else if (newState.is(PBTags.Blocks.CAKES)) {
                world.removeBlock(partPos, false);
            } else {
                world.destroyBlock(partPos, false);
            }
        }
    }

    @Override
    public PBCakeBlockPart getPart() {
        return (PBCakeBlockPart) PBBlocks.CAKE_PART.get();
    }
}
