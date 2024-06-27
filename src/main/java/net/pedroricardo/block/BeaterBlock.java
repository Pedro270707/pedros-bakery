package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.block.entity.PBBlockEntities;
import org.jetbrains.annotations.Nullable;

public class BeaterBlock extends BlockWithEntity {
    public static final EnumProperty<BeaterLiquids> LIQUID = EnumProperty.of("liquid", BeaterLiquids.class);
    public static final MapCodec<BeaterBlock> CODEC = createCodec(BeaterBlock::new);
    private static final VoxelShape EAST_SHAPE = VoxelShapes.union(Block.createCuboidShape(5, 9, 0, 11, 14, 12), Block.createCuboidShape(6, 0, 1, 10, 10, 4), Block.createCuboidShape(3, 0, 3, 13, 1, 13), Block.createCuboidShape(4, 1, 4, 12, 7, 12));
    private static final VoxelShape WEST_SHAPE = VoxelShapes.union(Block.createCuboidShape(5, 9, 4, 11, 14, 16), Block.createCuboidShape(6, 0, 12, 10, 10, 15), Block.createCuboidShape(3, 0, 3, 13, 1, 13), Block.createCuboidShape(4, 1, 4, 12, 7, 12));
    private static final VoxelShape NORTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(0, 9, 5, 12, 14, 11), Block.createCuboidShape(1, 0, 6, 4, 10, 10), Block.createCuboidShape(3, 0, 3, 13, 1, 13), Block.createCuboidShape(4, 1, 4, 12, 7, 12));
    private static final VoxelShape SOUTH_SHAPE = VoxelShapes.union(Block.createCuboidShape(4, 9, 5, 16, 14, 11), Block.createCuboidShape(12, 0, 6, 15, 10, 10), Block.createCuboidShape(3, 0, 3, 13, 1, 13), Block.createCuboidShape(4, 1, 4, 12, 7, 12));

    protected BeaterBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(LIQUID, BeaterLiquids.EMPTY).with(Properties.POWERED, false).with(LIQUID, BeaterLiquids.EMPTY));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeaterBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIQUID, Properties.POWERED, Properties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(LIQUID, BeaterLiquids.EMPTY).with(Properties.POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos())).with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient()) {
            return;
        }
        boolean bl = world.isReceivingRedstonePower(pos);
        if (bl != state.get(Properties.POWERED)) {
            world.setBlockState(pos, state.with(Properties.POWERED, bl), Block.NOTIFY_LISTENERS);
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(Properties.HORIZONTAL_FACING)) {
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
            default -> VoxelShapes.empty();
        };
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.BEATER, BeaterBlockEntity::tick);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!state.contains(LIQUID) || state.get(LIQUID) == BeaterLiquids.EMPTY || !(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater) || beater.getItem().isEmpty()) return ActionResult.FAIL;
        else {
            player.giveItemStack(beater.getItem().copyAndEmpty());
            return ActionResult.SUCCESS;
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!state.contains(LIQUID) || !(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return state.get(LIQUID).use(stack, state, world, pos, player, hand, hit, beater);
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof BeaterBlockEntity beater)) return 0;
        if (beater.getMixTime() == 0 && (beater.getTop() != null || beater.getFlavor() != null)) return 15;
        return Math.min(beater.getMixTime() * 15 / 200, 15);
    }
}