package net.pedroricardo.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.item.PBItems;

import java.util.List;

public class ButterChurnBlock extends Block {
    public static final EnumProperty<ChurnState> CHURN_STATE = EnumProperty.of("churn_state", ChurnState.class);

    public ButterChurnBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(CHURN_STATE, ChurnState.EMPTY));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CHURN_STATE);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(CHURN_STATE) == ChurnState.EMPTY && stack.isOf(Items.MILK_BUCKET)) {
            if (world.isClient()) {
                return ItemActionResult.success(true);
            }
            player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.BUCKET)));
            player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            world.setBlockState(pos, state.with(CHURN_STATE, ChurnState.MILK));
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
            world.emitGameEvent(null, GameEvent.FLUID_PLACE, pos);
            return ItemActionResult.success(false);
        } else if (state.get(CHURN_STATE) == ChurnState.MILK) {
            if (stack.isOf(PBItems.BUTTER_CHURN_STAFF)) {
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.5f, 1.0f);
                if (world.isClient()) {
                    return ItemActionResult.success(true);
                }
                float probability = 0.05f;
                if (world.getRandom().nextFloat() < probability) {
                    player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                    world.setBlockState(pos, state.with(CHURN_STATE, ChurnState.BUTTER));
                    stack.damage(1, player, PlayerEntity.getSlotForHand(hand));
                }
                return ItemActionResult.success(false);
            } else if (stack.isOf(Items.BUCKET)) {
                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                if (world.isClient()) {
                    return ItemActionResult.success(true);
                }
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.MILK_BUCKET)));
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                world.setBlockState(pos, state.with(CHURN_STATE, ChurnState.EMPTY));
                world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
                return ItemActionResult.success(false);
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        } else if (state.get(CHURN_STATE) == ChurnState.BUTTER && stack.isEmpty()) {
            if (world.isClient()) {
                return ItemActionResult.success(true);
            }
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(PBItems.BUTTER));
            world.setBlockState(pos, state.with(CHURN_STATE, ChurnState.EMPTY));
            return ItemActionResult.success(false);
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    public enum ChurnState implements StringIdentifiable {
        EMPTY("empty"),
        MILK("milk"),
        BUTTER("butter");

        private final String name;

        ChurnState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
