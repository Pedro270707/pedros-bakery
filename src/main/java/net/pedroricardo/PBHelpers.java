package net.pedroricardo;

import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Function8;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;

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
        if (decrementUnlessCreative) {
            oldStack.decrementUnlessCreative(1, player);
        } else {
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
            world.updateListeners(pos, state, state, Block.NOTIFY_ALL_AND_REDRAW);
        }
    }

    public static <T> T firstOrElse(List<T> list, T fallback) {
        return list.isEmpty() ? fallback : list.getFirst();
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7> PacketCodec<B, C> tuplePacketCodec(final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1, final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2, final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3, final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4, final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5, final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6, final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7, final Function7<T1, T2, T3, T4, T5, T6, T7, C> to) {
        return new PacketCodec<>(){

            @Override
            public C decode(B object) {
                T1 object2 = codec1.decode(object);
                T2 object3 = codec2.decode(object);
                T3 object4 = codec3.decode(object);
                T4 object5 = codec4.decode(object);
                T5 object6 = codec5.decode(object);
                T6 object7 = codec6.decode(object);
                T7 object8 = codec7.decode(object);
                return to.apply(object2, object3, object4, object5, object6, object7, object8);
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
            }
        };
    }

    public static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> PacketCodec<B, C> tuplePacketCodec(final PacketCodec<? super B, T1> codec1, final Function<C, T1> from1, final PacketCodec<? super B, T2> codec2, final Function<C, T2> from2, final PacketCodec<? super B, T3> codec3, final Function<C, T3> from3, final PacketCodec<? super B, T4> codec4, final Function<C, T4> from4, final PacketCodec<? super B, T5> codec5, final Function<C, T5> from5, final PacketCodec<? super B, T6> codec6, final Function<C, T6> from6, final PacketCodec<? super B, T7> codec7, final Function<C, T7> from7, final PacketCodec<? super B, T8> codec8, final Function<C, T8> from8, final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> to) {
        return new PacketCodec<>(){

            @Override
            public C decode(B object) {
                T1 object2 = codec1.decode(object);
                T2 object3 = codec2.decode(object);
                T3 object4 = codec3.decode(object);
                T4 object5 = codec4.decode(object);
                T5 object6 = codec5.decode(object);
                T6 object7 = codec6.decode(object);
                T7 object8 = codec7.decode(object);
                T8 object9 = codec8.decode(object);
                return to.apply(object2, object3, object4, object5, object6, object7, object8, object9);
            }

            @Override
            public void encode(B object, C object2) {
                codec1.encode(object, from1.apply(object2));
                codec2.encode(object, from2.apply(object2));
                codec3.encode(object, from3.apply(object2));
                codec4.encode(object, from4.apply(object2));
                codec5.encode(object, from5.apply(object2));
                codec6.encode(object, from6.apply(object2));
                codec7.encode(object, from7.apply(object2));
                codec8.encode(object, from8.apply(object2));
            }
        };
    }
}
