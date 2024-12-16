package net.pedroricardo.block.extras.beater;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
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

    ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater);

    Type getType();

    default NbtCompound toNbt(NbtCompound nbt) {
        nbt.put("liquid", CODEC.encodeStart(NbtOps.INSTANCE, this).get().orThrow());
        return nbt;
    }

    static Optional<Liquid> fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("liquid", NbtElement.COMPOUND_TYPE)) {
            return Optional.empty();
        }
        DataResult<Liquid> dataResult = CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("liquid"));
        return dataResult.result();
    }

    enum Type implements StringIdentifiable {
        MIXTURE("mixture", Mixture.getCodec()),
        FROSTING("frosting", Frosting.getCodec()),
        MILK("milk", Milk.getCodec());

        public static final StringIdentifiable.Codec<Type> CODEC;
        private final String name;
        private final MapCodec<? extends Liquid> codec;

        Type(String name, MapCodec<? extends Liquid> codec) {
            this.name = name;
            this.codec = codec;
        }

        public String asString() {
            return this.name;
        }

        public MapCodec<? extends Liquid> getCodec() {
            return this.codec;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Type::values);
        }
    }

    record Mixture(CakeFlavor flavor) implements Liquid {
        @Override
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getStackInHand(hand);
            Item item = stack.getItem();
            if (item instanceof BatterContainerItem container && container.addBatter(player, hand, stack, this.flavor(), PedrosBakery.CONFIG.beaterBatterAmount())) {
                BlockState newState = state.with(BeaterBlock.HAS_LIQUID, false);
                world.setBlockState(pos, newState);
                beater.setLiquid(null);
                beater.getItems().forEach(beaterStack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                beater.setItems(List.of());
                world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
                return ActionResult.SUCCESS;
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.SUCCESS;
        }

        @Override
        public Type getType() {
            return Type.MIXTURE;
        }

        public static MapCodec<Mixture> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").forGetter(Mixture::flavor)).apply(instance, Mixture::new));
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
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isOf(Items.GLASS_BOTTLE)) {
                ItemStack frostingStack = new ItemStack(PBItems.FROSTING_BOTTLE);
                PBHelpers.set(frostingStack, PBComponentTypes.TOP, this.top());
                player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, frostingStack));
                player.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));

                BlockState newState = state.with(BeaterBlock.HAS_LIQUID, false);
                world.setBlockState(pos, newState);
                beater.setLiquid(null);
                beater.getItems().forEach(beaterStack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                beater.setItems(List.of());
                world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.SUCCESS;
        }

        @Override
        public Type getType() {
            return Type.FROSTING;
        }

        public static MapCodec<Frosting> getCodec() {
            return RecordCodecBuilder.mapCodec(instance -> instance.group(CakeTops.REGISTRY.getCodec().fieldOf("top").forGetter(Frosting::top)).apply(instance, Frosting::new));
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
        public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            ItemStack stack = player.getStackInHand(hand);
            if (stack.isOf(Items.BUCKET)) {
                if (!world.isClient()) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.MILK_BUCKET)));
                    world.setBlockState(pos, state.with(BeaterBlock.HAS_LIQUID, false));
                    beater.setLiquid(null);
                    beater.getItems().forEach(beaterStack -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), beaterStack));
                    beater.setItems(List.of());
                    world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                return ActionResult.success(world.isClient());
            }
            beater.addItem(PBHelpers.splitUnlessCreative(stack, 1, player));
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
            return ActionResult.SUCCESS;
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
