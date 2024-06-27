package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.entity.PBCakePartBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PBCakePartBlock extends BlockWithEntity {
    public static final MapCodec<PBCakePartBlock> CODEC = createCodec(PBCakePartBlock::new);
    public static final BooleanProperty DELEGATE = BooleanProperty.of("delegate");

    protected PBCakePartBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(DELEGATE, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(DELEGATE);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof PBCakePartBlockEntity cake) || cake.getParent() == null || !(world.getBlockState(cake.getParentPos()).getBlock() instanceof MultipartBlock)) {
            return VoxelShapes.empty();
        }
        BlockPos offset = cake.getParentPos().subtract(pos);
        return VoxelShapes.combineAndSimplify(((MultipartBlock) world.getBlockState(cake.getParentPos()).getBlock()).getFullShape(world.getBlockState(cake.getParentPos()), world, cake.getParentPos(), context).offset(offset.getX(), offset.getY(), offset.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PBCakePartBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.CAKE_PART, PBCakePartBlockEntity::tick);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        return !state.getOrEmpty(DELEGATE).orElse(false) ? super.onBreak(world, pos, state, player) : this.parentFunction(world, pos, cake -> cake.getCachedState().getBlock().onBreak(world, cake.getPos(), state, player)).orElse(state);
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
        if (world.getBlockEntity(pos) instanceof PBCakePartBlockEntity part && (blockEntity = part.getParent()) != null) {
            return Optional.ofNullable(function.apply(blockEntity));
        }
        return Optional.empty();
    }

    private void parentConsumer(WorldView world, BlockPos pos, Consumer<PBCakeBlockEntity> function) {
        PBCakeBlockEntity blockEntity;
        if (world.getBlockEntity(pos) instanceof PBCakePartBlockEntity part && (blockEntity = part.getParent()) != null) {
            function.accept(blockEntity);
        }
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        return this.parentFunction(world, pos, cake -> world.getBlockState(cake.getPos()).getBlock().getPickStack(world, cake.getPos(), cake.getCachedState())).orElse(ItemStack.EMPTY);
    }
}
