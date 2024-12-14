package net.pedroricardo;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.item.ItemComponentType;
import org.jetbrains.annotations.Nullable;

import java.time.Year;
import java.util.OptionalInt;

public class PBHelpers {
    private PBHelpers() {}

    public static void decrementStackAndAdd(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        decrementStackAndAdd(player, oldStack, newStack, true);
    }

    public static void decrementStackAndAdd(PlayerEntity player, ItemStack oldStack, ItemStack newStack, boolean decrementUnlessCreative) {
        OptionalInt oldStackSlot = OptionalInt.empty();
        for (int i = 0; i < player.getInventory().main.size(); ++i) {
            if (player.getInventory().main.get(i).isEmpty() || player.getInventory().main.get(i) != oldStack) continue;
            oldStackSlot = OptionalInt.of(i);
            break;
        }
        if (!decrementUnlessCreative || !player.isCreative()) {
            oldStack.decrement(1);
        }
        if (oldStack.isEmpty() && oldStackSlot.isPresent()) {
            player.getInventory().insertStack(oldStackSlot.getAsInt(), newStack);
        } else if (!player.giveItemStack(newStack)) {
            player.dropItem(newStack, false);
        }
    }

    public static void updateListeners(BlockEntity blockEntity) {
        updateListeners(blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState(), blockEntity);
    }

    public static void updateListeners(World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        blockEntity.markDirty();
        if (world != null) {
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL & Block.REDRAW_ON_MAIN_THREAD);
        }
    }

    public static ItemStack splitUnlessCreative(ItemStack stack, int amount, PlayerEntity player) {
        ItemStack stack1 = stack.copyWithCount(amount);
        if (!player.isCreative()) {
            stack.decrement(amount);
        }
        return stack1;
    }

    @Nullable
    public static <T> T get(NbtCompound compound, ItemComponentType<T> component) {
        return getOrDefault(compound, component, null);
    }

    public static <T> T getOrDefault(NbtCompound compound, ItemComponentType<T> component, @Nullable T defaultValue) {
        if (!compound.contains(component.key().toString())) {
            return defaultValue;
        }
        return component.codec().decode(NbtOps.INSTANCE, compound.get(component.key().toString())).get().map(Pair::getFirst, partialResult -> defaultValue);
    }

    public static <T> void set(NbtCompound compound, ItemComponentType<T> component, @Nullable T value) {
        compound.put(component.key().toString(), component.codec().encodeStart(NbtOps.INSTANCE, value).get().orThrow());
    }

    public static boolean contains(NbtCompound compound, ItemComponentType<?> component) {
        return get(compound, component) != null;
    }

    // Stack-specific methods for convenience
    @Nullable
    public static <T> T get(ItemStack stack, ItemComponentType<T> component) {
        return get(stack.getOrCreateNbt(), component);
    }

    public static <T> T getOrDefault(ItemStack stack, ItemComponentType<T> component, @Nullable T defaultValue) {
        return getOrDefault(stack.getOrCreateNbt(), component, defaultValue);
    }

    public static <T> void set(ItemStack stack, ItemComponentType<T> component, @Nullable T value) {
        NbtCompound compound = stack.getOrCreateNbt();
        set(compound, component, value);
        stack.setNbt(compound);
    }

    public static ItemStack copyNbtToNewStack(ItemStack stack, ItemConvertible newStackItem, int count) {
        ItemStack newStack = new ItemStack(newStackItem);
        newStack.setNbt(stack.getOrCreateNbt());
        newStack.setSubNbt("Count", NbtInt.of(count));
        return newStack;
    }

    public static boolean contains(ItemStack stack, ItemComponentType<?> component) {
        return get(stack, component) != null;
    }
}
