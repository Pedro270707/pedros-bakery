package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.entity.PieBlockEntity;
import org.jetbrains.annotations.Nullable;

public class PieBlock extends BlockWithEntity {
    public static final MapCodec<PieBlock> CODEC = createCodec(PieBlock::new);
    public static final BooleanProperty BOTTOM = BooleanProperty.of("bottom");
    public static final BooleanProperty TOP = BooleanProperty.of("top");
    public static final IntProperty SLICES = IntProperty.of("slices", 0, 4);

    protected PieBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(BOTTOM, false).with(TOP, false).with(SLICES, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM).add(TOP).add(SLICES);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.PIE, PieBlockEntity::tick);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PieBlockEntity(pos, state);
    }
}
