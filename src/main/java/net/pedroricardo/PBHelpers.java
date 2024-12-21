package net.pedroricardo;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroricardo.item.ItemComponentType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PBHelpers {
    private PBHelpers() {}

    private static final Map<Item, Map<ItemComponentType<?>, Object>> defaultComponents = new HashMap<>();

    public static void update(BlockEntity blockEntity, ServerLevel world) {
        update(world, blockEntity.getBlockPos(), blockEntity);
    }

    public static void update(ServerLevel world, BlockPos pos, BlockEntity blockEntity) {
        blockEntity.setChanged();
        world.getChunkSource().blockChanged(pos);
    }

    public static ItemStack splitUnlessCreative(ItemStack stack, int amount, Player player) {
        ItemStack stack1 = stack.copyWithCount(amount);
        if (!player.isCreative()) {
            stack.shrink(amount);
        }
        return stack1;
    }

    @Nullable
    public static <T> T addDefaultComponent(Item item, ItemComponentType<T> type, T component) {
        if (defaultComponents.containsKey(item)) {
            Object previousValue = defaultComponents.get(item).put(type, component);
            try {
                return (T) previousValue;
            } catch (ClassCastException e) {
                return null;
            }
        }
        HashMap<ItemComponentType<?>, Object> map = new HashMap<>();
        map.put(type, component);
        Object previousValue = defaultComponents.put(item, map);
        try {
            return (T) previousValue;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T removeDefaultComponent(Item item, ItemComponentType<T> type) {
        if (!defaultComponents.containsKey(item)) {
            return null;
        }
        Object previousValue = defaultComponents.get(item).remove(type);
        try {
            return (T) previousValue;
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T get(ItemStack stack, ItemComponentType<T> type) {
        return getOrDefault(stack, type, null);
    }

    public static <T> T getOrDefault(ItemStack stack, ItemComponentType<T> type, @Nullable T defaultValue) {
        if (stack.isEmpty()) return defaultValue;
        CompoundTag compound = stack.getOrCreateTag();
        if (!compound.contains(type.key().toString())) {
            if (defaultComponents.containsKey(stack.getItem()) && defaultComponents.get(stack.getItem()).containsKey(type)) {
                try {
                    return (T) defaultComponents.get(stack.getItem()).get(type);
                } catch (ClassCastException e) {
                    throw new IllegalStateException("Component " + type + " is not of type " + type.key());
                }
            }
            return defaultValue;
        }
        return type.codec().decode(NbtOps.INSTANCE, compound.get(type.key().toString())).get().map(Pair::getFirst, partialResult -> defaultValue);
    }

    public static <T> void set(ItemStack stack, ItemComponentType<T> type, @Nullable T value) {
        if (stack.isEmpty()) return;
        CompoundTag compound = stack.getOrCreateTag();
        compound.put(type.key().toString(), type.codec().encodeStart(NbtOps.INSTANCE, value).get().orThrow());
        stack.setTag(compound);
    }

    public static ItemStack copyNbtToNewStack(ItemStack stack, ItemLike newStackItem, int count) {
        ItemStack newStack = new ItemStack(newStackItem);
        newStack.setTag(stack.getOrCreateTag());
        newStack.addTagElement("Count", IntTag.valueOf(count));
        return newStack;
    }

    public static boolean contains(ItemStack stack, ItemComponentType<?> type) {
        return get(stack, type) != null;
    }
}
