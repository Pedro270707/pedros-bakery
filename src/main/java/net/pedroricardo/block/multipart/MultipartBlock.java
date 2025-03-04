package net.pedroricardo.block.multipart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public abstract class MultipartBlock<T extends MultipartBlockEntity> extends BlockWithEntity {
    public static final BooleanProperty IS_MAIN_PART = BooleanProperty.of("is_main_part");

    protected MultipartBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(IS_MAIN_PART, true));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(IS_MAIN_PART);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(IS_MAIN_PART, true);
    }

    @Override
    public @Nullable T createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.combineAndSimplify(this.getFullShape(state, world, pos, context), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    public abstract VoxelShape getFullShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    public Set<BlockPos> getParts(BlockView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof MultipartBlockEntity multipartBlockEntity) {
            return multipartBlockEntity.getPartPositions();
        }
        return new HashSet<>();
    }

    public void remove(World world, BlockPos pos, boolean removeMain) {
        if (world.getBlockEntity(pos) instanceof MultipartBlockEntity multipartBlockEntity) {
            multipartBlockEntity.remove(removeMain);
        }
    }

    public void createPart(World world, BlockPos pos, BlockPos partPos) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntity part)) {
            return;
        }
        BlockState state = world.getBlockState(pos);
        world.setBlockState(partPos, state);
        part.addPartPosition(partPos);
    }

    public BlockPos getMainPartPosition(BlockView world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntity part)) return null;
        return part.getMainPartPosition();
    }
}
