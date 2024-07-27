package net.pedroricardo.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.block.helpers.CakeFlavor;
import net.pedroricardo.block.helpers.CakeFlavors;
import net.pedroricardo.block.helpers.CakeTop;
import net.pedroricardo.block.helpers.CakeTops;
import net.pedroricardo.item.BatterContainerItem;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

import java.util.Optional;

public enum BeaterLiquids implements StringIdentifiable {
    EMPTY("empty", (stack, state, world, pos, player, hand, hit, beater) -> {
        BlockState newState = state;
        if (stack.isOf(Items.MILK_BUCKET)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            }
            newState = newState.with(BeaterBlock.LIQUID, BeaterLiquids.valueOf("MILK"));
            world.setBlockState(pos, newState);
            if (!player.isInCreativeMode()) {
                PBHelpers.decrementStackAndAdd(player, stack, new ItemStack(Items.BUCKET), false);
            }
            world.emitGameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Emitter.of(player, newState));
            return ItemActionResult.SUCCESS;
        } else if (stack.isOf(PBItems.FROSTING_BOTTLE)) {
            if (player instanceof ServerPlayerEntity serverPlayer) {
                Criteria.CONSUME_ITEM.trigger(serverPlayer, stack);
                serverPlayer.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
            }
            beater.setTop(stack.get(PBComponentTypes.TOP));
            newState = newState.with(BeaterBlock.LIQUID, BeaterLiquids.valueOf("FROSTING"));
            world.setBlockState(pos, newState);
            if (!player.isInCreativeMode()) {
                PBHelpers.decrementStackAndAdd(player, stack, new ItemStack(Items.GLASS_BOTTLE), false);
            }
            world.emitGameEvent(GameEvent.FLUID_PLACE, pos, GameEvent.Emitter.of(player, newState));
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }),
    FROSTING("frosting", (world, state, pos, beater) -> {
        if (beater.getMixTime() <= 200) return false;
        CakeTop top = CakeTops.from(beater.getItem());
        if (top != null && top.base() == beater.getTop()) {
            beater.setItem(ItemStack.EMPTY);
            beater.setTop(top);
            return true;
        }
        return false;
    }, (stack, state, world, pos, player, hand, hit, beater) -> {
        if (stack.isOf(Items.GLASS_BOTTLE)) {
            ItemStack frostingStack = new ItemStack(PBItems.FROSTING_BOTTLE);
            frostingStack.set(PBComponentTypes.TOP, beater.getTop());
            PBHelpers.decrementStackAndAdd(player, stack, frostingStack);
            BlockState newState = state.with(BeaterBlock.LIQUID, EMPTY);
            world.setBlockState(pos, newState);
            beater.setTop(null);
            world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
            return ItemActionResult.SUCCESS;
        }
        CakeTop top = CakeTops.from(stack);
        if (top != null && top.base() == beater.getTop() && beater.getItem().isEmpty()) {
            beater.setItem(stack.copyWithCount(1));
            stack.decrementUnlessCreative(1, player);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }),
    MIXTURE("mixture", (world, state, pos, beater) -> {
        if (beater.getMixTime() <= 200) return false;
        Optional<CakeFlavor> flavor = CakeFlavors.from(beater.getItem());
        if (flavor.isPresent() && flavor.get().base() == beater.getFlavor()) {
            beater.setItem(ItemStack.EMPTY);
            beater.setFlavor(flavor.get());
            return true;
        }
        return false;
    }, (stack, state, world, pos, player, hand, hit, beater) -> {
        Item item = stack.getItem();
        if (item instanceof BatterContainerItem container && container.addBatter(player, stack, beater.getFlavor(), PedrosBakery.CONFIG.beaterBatterAmount())) {
            BlockState newState = state.with(BeaterBlock.LIQUID, EMPTY);
            world.setBlockState(pos, newState);
            beater.setFlavor(null);
            world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
            return ItemActionResult.SUCCESS;
        }
        Optional<CakeFlavor> flavor = CakeFlavors.from(stack);
        if (flavor.isPresent() && flavor.get().base() == beater.getFlavor() && beater.getItem().isEmpty()) {
            beater.setItem(stack.copyWithCount(1));
            stack.decrementUnlessCreative(1, player);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }),
    MILK("milk", (world, state, pos, beater) -> {
        if (beater.getMixTime() <= 200) return false;
        CakeTop top = CakeTops.from(beater.getItem());
        if (top != null) {
            beater.setItem(ItemStack.EMPTY);
            beater.setTop(top);
            BlockState newState = state.with(BeaterBlock.LIQUID, FROSTING);
            world.setBlockState(pos, newState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(newState));
            return true;
        }
        Optional<CakeFlavor> flavor = CakeFlavors.from(beater.getItem());
        if (flavor.isPresent() && flavor.get().base() == null) {
            beater.setItem(ItemStack.EMPTY);
            beater.setFlavor(flavor.get());
            BlockState newState = state.with(BeaterBlock.LIQUID, MIXTURE);
            world.setBlockState(pos, newState);
            world.emitGameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Emitter.of(newState));
            return true;
        }
        return false;
    }, (stack, state, world, pos, player, hand, hit, beater) -> {
        Optional<CakeFlavor> flavor = CakeFlavors.from(stack);
        if (CakeTops.from(stack) != null || (flavor.isPresent() && flavor.get().base() == null)) {
            if (!beater.getItem().isEmpty()) {
                player.giveItemStack(beater.getItem());
                beater.setItem(ItemStack.EMPTY);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            } else {
                beater.setItem(stack.copyWithCount(1));
                stack.decrementUnlessCreative(1, player);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            }
            return ItemActionResult.SUCCESS;
        }
        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    });

    private final String id;
    private final MixAction mixAction;
    private final UseAction useAction;

    BeaterLiquids(String id, MixAction mixAction, UseAction useAction) {
        this.id = id;
        this.mixAction = mixAction;
        this.useAction = useAction;
    }

    BeaterLiquids(String id, MixAction mixAction) {
        this(id, mixAction, (stack, state, world, pos, player, hand, hit, beater) -> ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
    }

    BeaterLiquids(String id, UseAction useAction) {
        this(id, (world, state, pos, blockEntity) -> false, useAction);
    }

    BeaterLiquids(String id) {
        this(id, (world, state, pos, blockEntity) -> false);
    }

    @Override
    public String asString() {
        return this.id;
    }

    public boolean onMix(World world, BlockState state, BlockPos pos, BeaterBlockEntity beater) {
        return this.mixAction.onMix(world, state, pos, beater);
    }

    public ItemActionResult use(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
        return this.useAction.use(stack, state, world, pos, player, hand, hit, beater);
    }

    private interface MixAction {
        boolean onMix(World world, BlockState state, BlockPos pos, BeaterBlockEntity beater);
    }

    private interface UseAction {
        ItemActionResult use(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater);
    }
}