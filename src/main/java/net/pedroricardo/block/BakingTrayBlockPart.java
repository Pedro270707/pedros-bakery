package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.BakingTrayBlockEntity;
import net.pedroricardo.block.entity.BakingTrayBlockEntityPart;
import net.pedroricardo.block.entity.PBBlockEntities;
import net.pedroricardo.block.multipart.MultipartBlock;
import net.pedroricardo.block.multipart.MultipartBlockEntityPart;
import net.pedroricardo.block.multipart.MultipartBlockPart;
import org.jetbrains.annotations.Nullable;

public class BakingTrayBlockPart extends MultipartBlockPart<BakingTrayBlockEntity, BakingTrayBlockEntityPart> {
    public static final MapCodec<BakingTrayBlockPart> CODEC = createCodec(BakingTrayBlockPart::new);

    public BakingTrayBlockPart(Settings settings) {
        super(settings);
    }

    @Override
    public BakingTrayBlockEntityPart createBlockEntity(BlockPos pos, BlockState state, BlockPos parentPos) {
        return new BakingTrayBlockEntityPart(pos, state, parentPos);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (!(world.getBlockEntity(pos) instanceof MultipartBlockEntityPart<?> tray) || tray.getParentPos() == null || !(world.getBlockState(tray.getParentPos()).getBlock() instanceof MultipartBlock<?, ?, ?> block)) {
            return VoxelShapes.empty();
        }
        BlockPos offset = tray.getParentPos().subtract(pos);
        return VoxelShapes.combineAndSimplify(block.getFullShape(world.getBlockState(tray.getParentPos()), world, tray.getParentPos(), context).offset(offset.getX(), offset.getY(), offset.getZ()), VoxelShapes.fullCube(), BooleanBiFunction.AND);
    }

    @Override
    protected MapCodec<? extends MultipartBlockPart<BakingTrayBlockEntity, BakingTrayBlockEntityPart>> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BakingTrayBlockEntityPart(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, PBBlockEntities.BAKING_TRAY_PART, BakingTrayBlockEntityPart::tick);
    }
}
