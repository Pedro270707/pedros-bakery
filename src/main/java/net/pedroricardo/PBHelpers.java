package net.pedroricardo;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.pedroricardo.item.ItemComponentType;
import org.jetbrains.annotations.Nullable;

public class PBHelpers {
    private PBHelpers() {}

    public static void update(BlockEntity blockEntity, ServerWorld world) {
        update(world, blockEntity.getPos(), blockEntity);
    }

    public static void update(ServerWorld world, BlockPos pos, BlockEntity blockEntity) {
        blockEntity.markDirty();
        world.getChunkManager().markForUpdate(pos);
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
