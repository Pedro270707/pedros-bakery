package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.block.entity.CakeStandBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CakeStandBlock extends ItemStandBlock<CakeStandBlockEntity> {
    protected CakeStandBlock(Properties settings) {
        super(settings);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.or(Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0), Block.box(1.0, 1.0, 1.0, 15.0, 16.0, 15.0));
    }

    @Override
    public boolean canContain(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return stack.is(PBTags.Items.CAKE_STAND_ITEM);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CakeStandBlockEntity(pos, state);
    }
}
