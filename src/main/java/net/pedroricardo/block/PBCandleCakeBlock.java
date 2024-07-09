package net.pedroricardo.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakeBlockEntityPart;
import net.pedroricardo.block.helpers.CakeLayer;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PBCandleCakeBlock extends PBAbstractCandleCakeBlock implements BlockEntityProvider, MultipartBlock<PBCakeBlockEntity, PBCakeBlockEntityPart, PBCakeBlockPart> {
    public static final MapCodec<PBCandleCakeBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec<? super CandleBlock>)Registries.BLOCK.getCodec().fieldOf("candle")).forGetter(block -> block.candle), CandleCakeBlock.createSettingsCodec()).apply(instance, (block, settings) -> new PBCandleCakeBlock((CandleBlock) block, settings)));
    private final CandleBlock candle;
    private static final Map<CandleBlock, PBCandleCakeBlock> CANDLES_TO_CANDLE_CAKES = Maps.newHashMap();

    protected PBCandleCakeBlock(CandleBlock candle, Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(Properties.HORIZONTAL_FACING, Direction.NORTH));
        CANDLES_TO_CANDLE_CAKES.put(candle, this);
        this.candle = candle;
    }

    @Override
    protected MapCodec<? extends PBAbstractCandleCakeBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected Iterable<Vec3d> getParticleOffsets(BlockState state, WorldAccess world, BlockPos pos) {
        return ImmutableList.of(new Vec3d(0.5, world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake ? (cake.getHeight() + 8) / 16.0 : 1.0, 0.5));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakeBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT).add(Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.CAKE, PBCakeBlockEntity::tick);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE) || !(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        if (hit.getPos().y - (double)hit.getBlockPos().getY() > cake.getHeight() / 16.0f && stack.isEmpty() && state.get(LIT)) {
            extinguish(player, state, world, pos);
            return ItemActionResult.success(world.isClient());
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
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
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return PBCakeBlock.of(cake.getLayers());
        }
        return PBCakeBlock.of(Collections.singletonList(CakeLayer.getDefault()));
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake)) {
            return VoxelShapes.fullCube();
        }
        return VoxelShapes.union(cake.toShape(state.get(Properties.HORIZONTAL_FACING)), Block.createCuboidShape(7.0f, cake.getHeight(), 7.0f, 9.0f, cake.getHeight() + 6.0f, 9.0f));
    }

    @Override
    public List<BlockPos> getParts(WorldView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof PBCakeBlockEntity cake) {
            return cake.getParts();
        }
        return List.of();
    }

    @Override
    public void removePartsWhenReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        for (BlockPos partPos : this.getParts(world, pos)) {
            if (!(world.getBlockState(partPos).getBlock() instanceof MultipartBlockPart<?, ?>) || !world.getBlockState(partPos).contains(MultipartBlockPart.DELEGATE)) {
                return;
            }
            world.setBlockState(partPos, world.getBlockState(partPos).with(MultipartBlockPart.DELEGATE, false));
            if (moved) {
                world.removeBlock(partPos, true);
            } else if (newState.isIn(PBTags.Blocks.CAKES)) {
                world.removeBlock(partPos, false);
            } else {
                world.breakBlock(partPos, false);
            }
        }
    }

    @Override
    public PBCakeBlockPart getPart() {
        return (PBCakeBlockPart) PBBlocks.CAKE_PART;
    }
}
