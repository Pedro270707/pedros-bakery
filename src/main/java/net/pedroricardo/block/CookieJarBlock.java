package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.CookieJarBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CookieJarBlock extends BaseEntityBlock {
    public static final int MAX_COOKIES = 12;
    public static final IntegerProperty COOKIES = IntegerProperty.create("cookies", 0, MAX_COOKIES);

    public CookieJarBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(COOKIES, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COOKIES);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult useWithItem = this.onUseWithItem(player.getItemInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != InteractionResult.PASS) return useWithItem;
        if (!(world.getBlockEntity(pos) instanceof CookieJarBlockEntity cookieJar) || !state.hasProperty(COOKIES)) return InteractionResult.FAIL;
        int cookies = state.getValue(COOKIES);
        if (cookies > 0) {
            ItemStack stack = cookieJar.getStacks().get(cookies - 1);
            if (!player.addItem(stack)) {
                player.drop(stack, false);
            }
            world.setBlock(pos, state.setValue(COOKIES, cookies - 1), Block.UPDATE_ALL);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return InteractionResult.PASS;
    }

    private InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CookieJarBlockEntity cookieJar) || !state.hasProperty(COOKIES)) return InteractionResult.PASS;
        int cookies = state.getValue(COOKIES);
        if (stack.is(PBTags.Items.COOKIES) && cookies < MAX_COOKIES) {
            cookieJar.setItem(cookies, PBHelpers.splitUnlessCreative(stack, 1, player));
            world.setBlock(pos, state.setValue(COOKIES, cookies + 1), Block.UPDATE_ALL);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.sidedSuccess(world.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.is(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Container) {
            Containers.dropContents(world, pos, (Container) blockEntity);
            world.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CookieJarBlockEntity(pos, state);
    }
}
