package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.CupcakeBlockEntity;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

public class CupcakeBlock extends BaseEntityBlock {
    protected CupcakeBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockStateProperties.ROTATION_16, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.ROTATION_16);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return super.getStateForPlacement(ctx).setValue(BlockStateProperties.ROTATION_16, RotationSegment.convertToSegment(ctx.getRotation()));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(BlockStateProperties.ROTATION_16, rotation.rotate(state.getValue(BlockStateProperties.ROTATION_16), RotationSegment.getMaxSegmentIndex() + 1));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(BlockStateProperties.ROTATION_16, mirror.mirror(state.getValue(BlockStateProperties.ROTATION_16), RotationSegment.getMaxSegmentIndex() + 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape shape;
        if (world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake) {
            shape = cupcake.getBatter().getShape(state, world, pos, context).move(0.0, 0.15625, 0.0);
        } else {
            shape = Shapes.empty();
        }
        return Shapes.or(box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0), shape);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (!(world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake)) return InteractionResult.PASS;
        if ((player.isCreative() || player.canEat(false)) && !cupcake.getBatter().isEmpty()) {
            CakeBatter<FixedBatterSizeContainer> batter = cupcake.getBatter();
            InteractionResult result = batter.bite(world, pos, state, player, cupcake, 0);
            if (result.consumesAction()) {
                player.awardStat(Stats.EAT_CAKE_SLICE);
                world.gameEvent(player, GameEvent.EAT, pos);
                world.playSound(player, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0f, 0.8f + world.getRandom().nextFloat() * 0.4f);
                player.playSound(SoundEvents.PLAYER_BURP, 0.5f, 1.0f);
                player.getFoodData().eat(PedrosBakery.CONFIG.cupcakeFood.get(), PedrosBakery.CONFIG.cupcakeSaturation.get());
            }
            return result;
        }
        return InteractionResult.FAIL;
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake)) return InteractionResult.FAIL;
        CakeTop top = PBHelpers.get(stack, PBComponentTypes.TOP.get());
        if (stack.is(PBItems.FROSTING_BOTTLE.get()) && !cupcake.getBatter().isEmpty() && cupcake.getBatter().getTop().orElse(null) != top) {
            cupcake.getBatter().withTop(top);
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.HONEYCOMB) && !cupcake.getBatter().isEmpty() && cupcake.getBatter().setWaxed(true)) {
            world.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public static ItemStack of(CakeBatter<FixedBatterSizeContainer> batter) {
        ItemStack stack = new ItemStack(PBBlocks.CUPCAKE.get());
        PBHelpers.set(stack, PBComponentTypes.FIXED_SIZE_BATTER.get(), batter.copy());
        return stack;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
        if (world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake) {
            return of(cupcake.getBatter());
        }
        return super.getCloneItemStack(world, pos, state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CupcakeBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof CupcakeBlockEntity cupcake) {
            cupcake.readFrom(stack);
        }
    }
}
