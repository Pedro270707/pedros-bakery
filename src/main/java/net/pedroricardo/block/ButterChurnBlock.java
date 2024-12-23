package net.pedroricardo.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.item.PBItems;

public class ButterChurnBlock extends Block {
    public static final EnumProperty<ChurnState> CHURN_STATE = EnumProperty.create("churn_state", ChurnState.class);

    public ButterChurnBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CHURN_STATE, ChurnState.EMPTY));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHURN_STATE);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (state.getValue(CHURN_STATE) == ChurnState.EMPTY && stack.is(Items.MILK_BUCKET)) {
            if (world.isClientSide()) {
                return InteractionResult.sidedSuccess(true);
            }
            player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.BUCKET)));
            player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            world.setBlockAndUpdate(pos, state.setValue(CHURN_STATE, ChurnState.MILK));
            world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            world.gameEvent(null, GameEvent.FLUID_PLACE, pos);
            return InteractionResult.sidedSuccess(false);
        } else if (state.getValue(CHURN_STATE) == ChurnState.MILK) {
            if (stack.is(PBItems.BUTTER_CHURN_STAFF.get())) {
                world.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.5f, 1.0f);
                if (world.isClientSide()) {
                    return InteractionResult.sidedSuccess(true);
                }
                float probability = 0.05f;
                if (world.getRandom().nextFloat() < probability) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    world.setBlockAndUpdate(pos, state.setValue(CHURN_STATE, ChurnState.BUTTER));
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                }
                return InteractionResult.sidedSuccess(false);
            } else if (stack.is(Items.BUCKET)) {
                world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (world.isClientSide()) {
                    return InteractionResult.sidedSuccess(true);
                }
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.MILK_BUCKET)));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                world.setBlockAndUpdate(pos, state.setValue(CHURN_STATE, ChurnState.EMPTY));
                world.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                return InteractionResult.sidedSuccess(false);
            }
            return InteractionResult.PASS;
        } else if (state.getValue(CHURN_STATE) == ChurnState.BUTTER && stack.isEmpty()) {
            if (world.isClientSide()) {
                return InteractionResult.sidedSuccess(true);
            }
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(PBItems.BUTTER.get()));
            world.setBlockAndUpdate(pos, state.setValue(CHURN_STATE, ChurnState.EMPTY));
            return InteractionResult.sidedSuccess(false);
        }
        return InteractionResult.PASS;
    }

    public enum ChurnState implements StringRepresentable {
        EMPTY("empty"),
        MILK("milk"),
        BUTTER("butter");

        private final String name;

        ChurnState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
