package net.pedroricardo.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.block.entity.CookieJarBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CookieJarBlock extends BlockWithEntity {
    public static final int MAX_COOKIES = 12;
    public static final IntProperty COOKIES = IntProperty.of("cookies", 0, MAX_COOKIES);

    public CookieJarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(COOKIES, 0));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COOKIES);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult useWithItem = this.onUseWithItem(player.getStackInHand(hand), state, world, pos, player, hand, hit);
        if (useWithItem != ActionResult.PASS) return useWithItem;
        if (!(world.getBlockEntity(pos) instanceof CookieJarBlockEntity cookieJar) || !state.contains(COOKIES)) return ActionResult.FAIL;
        int cookies = state.get(COOKIES);
        if (cookies > 0) {
            ItemStack stack = cookieJar.getStacks().get(cookies - 1);
            if (!player.giveItemStack(stack)) {
                player.dropItem(stack, false);
            }
            world.setBlockState(pos, state.with(COOKIES, cookies - 1), Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.success(world.isClient());
        }
        return ActionResult.PASS;
    }

    private ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CookieJarBlockEntity cookieJar) || !state.contains(COOKIES)) return ActionResult.PASS;
        int cookies = state.get(COOKIES);
        if (stack.isIn(PBTags.Items.COOKIES) && cookies < MAX_COOKIES) {
            cookieJar.setStack(cookies, PBHelpers.splitUnlessCreative(stack, 1, player));
            world.setBlockState(pos, state.with(COOKIES, cookies + 1), Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.success(world.isClient());
        }
        return ActionResult.PASS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookieJarBlockEntity(pos, state);
    }
}
