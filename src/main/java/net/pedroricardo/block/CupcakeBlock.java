package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.CupcakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlock extends BlockWithEntity {
    public static final MapCodec<CupcakeBlock> CODEC = createCodec(CupcakeBlock::new);

    protected CupcakeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(Properties.ROTATION, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.ROTATION);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(Properties.ROTATION, rotation.rotate(state.get(Properties.ROTATION), RotationPropertyHelper.getMax() + 1));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(Properties.ROTATION, mirror.mirror(state.get(Properties.ROTATION), RotationPropertyHelper.getMax() + 1));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape;
        if (world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake) {
            shape = cupcake.getBatter().getShape(state, world, pos, context).offset(0.0, 0.15625, 0.0);
        } else {
            shape = VoxelShapes.empty();
        }
        return VoxelShapes.union(createCuboidShape(6.0, 0.0, 6.0, 10.0, 4.0, 10.0), shape);
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake)) return ActionResult.PASS;
        if ((player.isCreative() || player.canConsume(false)) && !cupcake.getBatter().isEmpty()) {
            CakeBatter<FixedBatterSizeContainer> batter = cupcake.getBatter();
            ActionResult result = batter.bite(world, pos, state, player, cupcake, 0);
            if (result.isAccepted()) {
                player.incrementStat(Stats.EAT_CAKE_SLICE);
                world.emitGameEvent(player, GameEvent.EAT, pos);
                world.playSound(player, pos, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.BLOCKS, 1.0f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                player.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, 1.0f);
                player.getHungerManager().add(PedrosBakery.CONFIG.cupcakeFood(), PedrosBakery.CONFIG.cupcakeSaturation());
            }
            return result;
        }
        return ActionResult.FAIL;
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake)) return ItemActionResult.FAIL;
        CakeTop top = stack.get(PBComponentTypes.TOP);
        if (stack.isOf(PBItems.FROSTING_BOTTLE) && !cupcake.getBatter().isEmpty() && cupcake.getBatter().getTop().orElse(null) != top) {
            cupcake.getBatter().withTop(top);
            PBHelpers.decrementStackAndAdd(player, stack, new ItemStack(Items.GLASS_BOTTLE));
            return ItemActionResult.SUCCESS;
        }
        if (stack.isOf(Items.HONEYCOMB) && !cupcake.getBatter().isEmpty() && cupcake.getBatter().setWaxed(true)) {
            world.syncWorldEvent(player, WorldEvents.BLOCK_WAXED, pos, 0);
            return ItemActionResult.SUCCESS;
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static ItemStack of(CakeBatter<FixedBatterSizeContainer> batter) {
        ItemStack stack = new ItemStack(PBBlocks.CUPCAKE);
        stack.set(PBComponentTypes.FIXED_SIZE_BATTER, batter.copy());
        return stack;
    }

    @Override
    public ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake) {
            return of(cupcake.getBatter());
        }
        return super.getPickStack(world, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CupcakeBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
