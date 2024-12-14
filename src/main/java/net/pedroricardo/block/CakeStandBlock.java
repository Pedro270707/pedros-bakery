package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.pedroricardo.block.entity.CakeStandBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CakeStandBlock extends ItemStandBlock<CakeStandBlockEntity> {
    protected CakeStandBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.createCuboidShape(1.0, 1.0, 1.0, 15.0, 16.0, 15.0));
    }

    @Override
    public boolean canContain(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return stack.isIn(PBTags.Items.CAKE_STAND_ITEM);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CakeStandBlockEntity(pos, state);
    }
}
