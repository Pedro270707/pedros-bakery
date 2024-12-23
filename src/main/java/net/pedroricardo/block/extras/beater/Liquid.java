package net.pedroricardo.block.extras.beater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.BeaterBlock;
import net.pedroricardo.block.entity.BeaterBlockEntity;
import net.pedroricardo.block.extras.*;
import net.pedroricardo.item.BatterContainerItem;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface Liquid {
    Codec<Liquid> CODEC = Type.CODEC.dispatch(Liquid::getType, type -> type.getCodec().codec());

    InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, BeaterBlockEntity beater);

    Type getType();

    default CompoundTag toNbt(CompoundTag nbt) {
        nbt.put("liquid", CODEC.encodeStart(NbtOps.INSTANCE, this).get().orThrow());
        return nbt;
    }

    static Optional<Liquid> fromNbt(@Nullable CompoundTag nbt) {
        if (nbt == null || !nbt.contains("liquid", Tag.TAG_COMPOUND)) {
            return Optional.empty();
        }
        DataResult<Liquid> dataResult = CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("liquid"));
        return dataResult.result();
    }

    enum Type implements StringRepresentable {
        MIXTURE("mixture", Mixture.getCodec()),
        FROSTING("frosting", Frosting.getCodec()),
        MILK("milk", Milk.getCodec());

        public static final StringRepresentable.EnumCodec<Type> CODEC;
        private final String name;
        private final MapCodec<? extends Liquid> codec;

        Type(String name, MapCodec<? extends Liquid> codec) {
            this.name = name;
            this.codec = codec;
        }

        public String getSerializedName() {
            return this.name;
        }

        public MapCodec<? extends Liquid> getCodec() {
            return this.codec;
        }

        static {
            CODEC = StringRepresentable.fromEnum(Type::values);
        }
    }

    record Mixture(CakeFlavor flavor) implements Liquid {
        @Override
        public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getItemInHand(hand);
            Item item = stack.getItem();
            if (item instanceof BatterContainerItem container && container.addBatter(player, hand, stack, this.flavor(), PedrosBakery.CONFIG.beaterBatterAmount.get())) {
                BlockState newState = state.setValue(BeaterBlock.HAS_LIQUID, false);
                world.setBlockAndUpdate(pos, newState);
                beater.setLiquid(null);
                beater.getItems().forEach(beaterStack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                beater.setItems(List.of());
                world.gameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Context.of(player, newState));
                return InteractionResult.SUCCESS;
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.SUCCESS;
        }

        @Override
        public Type getType() {
            return Type.MIXTURE;
        }

        public static MapCodec<Mixture> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(CakeFlavors.registrySupplier.get().getCodec().fieldOf("flavor").forGetter(Mixture::flavor)).apply(instance, Mixture::new));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Mixture mixture = (Mixture) o;
            return Objects.equals(flavor(), mixture.flavor());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(flavor());
        }
    }

    record Frosting(CakeTop top) implements Liquid {
        @Override
        public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.GLASS_BOTTLE)) {
                ItemStack frostingStack = new ItemStack(PBItems.FROSTING_BOTTLE.get());
                PBHelpers.set(frostingStack, PBComponentTypes.TOP.get(), this.top());
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, frostingStack));
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                BlockState newState = state.setValue(BeaterBlock.HAS_LIQUID, false);
                world.setBlockAndUpdate(pos, newState);
                beater.setLiquid(null);
                beater.getItems().forEach(beaterStack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                beater.setItems(List.of());
                world.gameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Context.of(player, newState));
                world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.SUCCESS;
        }

        @Override
        public Type getType() {
            return Type.FROSTING;
        }

        public static MapCodec<Frosting> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(CakeTops.registrySupplier.get().getCodec().fieldOf("top").forGetter(Frosting::top)).apply(instance, Frosting::new));
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Frosting frosting = (Frosting) o;
            return Objects.equals(top(), frosting.top());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(top());
        }
    }

    record Milk() implements Liquid {
        @Override
        public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.is(Items.BUCKET)) {
                if (!world.isClientSide()) {
                    player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.MILK_BUCKET)));
                    world.setBlockAndUpdate(pos, state.setValue(BeaterBlock.HAS_LIQUID, false));
                    beater.setLiquid(null);
                    beater.getItems().forEach(beaterStack -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                    beater.setItems(List.of());
                    world.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
                    world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return InteractionResult.sidedSuccess(world.isClientSide());
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return InteractionResult.SUCCESS;
        }

        @Override
        public Type getType() {
            return Type.MILK;
        }

        public static MapCodec<Milk> getCodec() {
            return MapCodec.unit(Milk::new);
        }

        @Override
        public boolean equals(Object o) {
            return o != null && getClass() == o.getClass();
        }
    }
}
