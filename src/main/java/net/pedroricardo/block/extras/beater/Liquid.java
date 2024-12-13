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
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
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

import java.util.Optional;

public interface Liquid {
    Codec<Liquid> CODEC = Type.CODEC.dispatch(Liquid::getType, Type::getCodec);

    // TODO: get rid of this and move to data-driven recipes
    default boolean onMix(World world, BlockState state, BlockPos pos, BeaterBlockEntity beater) {
        return false;
    }

    ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater);

    Type getType();

    default NbtCompound toNbt(NbtCompound nbt) {
        nbt.put("liquid", CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
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
        MIXTURE("mixture", Mixture.CODEC),
        FROSTING("frosting", Frosting.CODEC),
        MILK("milk", Milk.CODEC);

        public static final Codec<Type> CODEC;
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
        public static final MapCodec<Mixture> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").forGetter(Mixture::flavor)).apply(instance, Mixture::new));

        @Override
        public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            Item item = stack.getItem();
            if (item instanceof BatterContainerItem container && container.addBatter(player, stack, this.flavor(), PedrosBakery.CONFIG.beaterBatterAmount())) {
                BlockState newState = state.with(BeaterBlock.HAS_LIQUID, false);
                world.setBlockState(pos, newState);
                beater.setLiquid(null);
                world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
                return ItemActionResult.SUCCESS;
            }
            Optional<CakeFlavor> flavor = CakeFlavors.from(stack);
            if (flavor.isPresent() && flavor.get().base() == this.flavor() && beater.getItem().isEmpty()) {
                beater.setItem(stack.copyWithCount(1));
                stack.decrementUnlessCreative(1, player);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public Type getType() {
            return Type.MIXTURE;
        }
    }

    record Frosting(CakeTop top) implements Liquid {
        public static final MapCodec<Frosting> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(CakeTops.REGISTRY.getCodec().fieldOf("top").forGetter(Frosting::top)).apply(instance, Frosting::new));

        @Override
        public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            if (stack.isOf(Items.GLASS_BOTTLE)) {
                ItemStack frostingStack = new ItemStack(PBItems.FROSTING_BOTTLE);
                frostingStack.set(PBComponentTypes.TOP, this.top());
                PBHelpers.decrementStackAndAdd(player, stack, frostingStack);
                BlockState newState = state.with(BeaterBlock.HAS_LIQUID, false);
                world.setBlockState(pos, newState);
                beater.setLiquid(null);
                world.emitGameEvent(GameEvent.FLUID_PICKUP, pos, GameEvent.Emitter.of(player, newState));
                world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ItemActionResult.SUCCESS;
            }
            CakeTop top = CakeTops.from(stack);
            if (top != null && top.base() == this.top() && beater.getItem().isEmpty()) {
                beater.setItem(stack.copyWithCount(1));
                stack.decrementUnlessCreative(1, player);
                world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                return ItemActionResult.SUCCESS;
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public Type getType() {
            return Type.FROSTING;
        }
    }

    record Milk() implements Liquid {
        public static final MapCodec<Milk> CODEC = MapCodec.unit(Milk::new);

        @Override
        public ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, BeaterBlockEntity beater) {
            Optional<CakeFlavor> flavor = CakeFlavors.from(stack);
            if (stack.isOf(Items.BUCKET)) {
                if (!world.isClient()) {
                    player.setStackInHand(hand, ItemUsage.exchangeStack(stack, player, new ItemStack(Items.MILK_BUCKET)));
                    world.setBlockState(pos, state.with(BeaterBlock.HAS_LIQUID, false));
                    beater.setLiquid(null);
                    world.emitGameEvent(player, GameEvent.FLUID_PICKUP, pos);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                }
                return ItemActionResult.success(world.isClient());
            }
            if (CakeTops.from(stack) != null || (flavor.isPresent() && flavor.get().base() == null)) {
                if (!world.isClient()) {
                    if (!beater.getItem().isEmpty()) {
                        player.giveItemStack(beater.getItem());
                        beater.setItem(ItemStack.EMPTY);
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    } else {
                        beater.setItem(stack.copyWithCount(1));
                        stack.decrementUnlessCreative(1, player);
                        world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                    }
                }
                return ItemActionResult.success(world.isClient());
            }
            return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public Type getType() {
            return Type.MILK;
        }

        @Override
        public boolean onMix(World world, BlockState state, BlockPos pos, BeaterBlockEntity beater) {
            if (beater.getMixTime() <= 200) return false;
            CakeTop top = CakeTops.from(beater.getItem());
            if (top != null) {
                beater.setItem(ItemStack.EMPTY);
                beater.setLiquid(new Frosting(top));
                return true;
            }
            Optional<CakeFlavor> flavor = CakeFlavors.from(beater.getItem());
            if (flavor.isPresent() && flavor.get().base() == null) {
                beater.setItem(ItemStack.EMPTY);
                beater.setLiquid(new Mixture(flavor.get()));
                return true;
            }
            return false;
        }
    }
}
