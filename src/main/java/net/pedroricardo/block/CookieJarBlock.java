package net.pedroricardo.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.block.entity.CookieJarBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

public class CookieJarBlock extends BlockWithEntity {
    public static final MapCodec<CookieJarBlock> CODEC = createCodec(CookieJarBlock::new);
    public static final IntProperty COOKIES = IntProperty.of("cookies", 0, 12);

    public CookieJarBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(COOKIES, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COOKIES);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
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

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof CookieJarBlockEntity cookieJar) || !state.contains(COOKIES)) return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        int cookies = state.get(COOKIES);
        if (stack.isIn(PBTags.Items.COOKIES) && cookies < 12) {
            cookieJar.setStack(cookies, stack.splitUnlessCreative(1, player));
            world.setBlockState(pos, state.with(COOKIES, cookies + 1), Block.NOTIFY_ALL);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ItemActionResult.success(world.isClient());
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        ItemScatterer.onStateReplaced(state, newState, world, pos);
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CookieJarBlockEntity(pos, state);
    }
}
