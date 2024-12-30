package net.pedroricardo.block.extras;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.size.BatterSizeContainer;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.block.tags.PBTags;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CakeBatter<S extends BatterSizeContainer> {
    private int bakeTime;
    private S sizeContainer;
    private CakeFlavor flavor;
    private Optional<CakeTop> top;
    private Map<CakeFeature, NbtCompound> features;
    private boolean waxed;

    public CakeBatter(int bakeTime, S sizeContainer, Optional<CakeTop> top, CakeFlavor cakeFlavor, boolean waxed) {
        this(bakeTime, sizeContainer, cakeFlavor, top, Maps.newHashMap(), waxed);
    }

    public CakeBatter(int bakeTime, S sizeContainer, CakeFlavor cakeFlavor, boolean waxed) {
        this(bakeTime, sizeContainer, cakeFlavor, Optional.empty(), Maps.newHashMap(), waxed);
    }

    public CakeBatter(int bakeTime, S sizeContainer, CakeFlavor flavor, Optional<CakeTop> top, Map<CakeFeature, NbtCompound> features, boolean waxed) {
        this.bakeTime = bakeTime;
        this.sizeContainer = sizeContainer;
        this.flavor = flavor;
        this.top = top;
        this.features = features;
        this.waxed = waxed;
    }

    public static final Codec<CakeBatter<FixedBatterSizeContainer>> FIXED_SIZE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    FixedBatterSizeContainer.CODEC.fieldOf("batter_size").orElse(new FixedBatterSizeContainer()).forGetter(CakeBatter::getSizeContainer),
                    CakeTops.REGISTRY.getCodec().optionalFieldOf("top").forGetter(CakeBatter::getTop),
                    CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").orElse(CakeFlavors.REGISTRY.get(CakeFlavors.REGISTRY.getDefaultId())).forGetter(CakeBatter::getFlavor),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));
    public static final Codec<CakeBatter<HeightOnlyBatterSizeContainer>> WITH_HEIGHT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    HeightOnlyBatterSizeContainer.CODEC.fieldOf("batter_size").orElse(new HeightOnlyBatterSizeContainer()).forGetter(CakeBatter::getSizeContainer),
                    CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").orElse(CakeFlavors.REGISTRY.get(CakeFlavors.REGISTRY.getDefaultId())).forGetter(CakeBatter::getFlavor),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));
    public static final Codec<CakeBatter<FullBatterSizeContainer>> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    FullBatterSizeContainer.CODEC.fieldOf("batter_size").orElse(new FullBatterSizeContainer()).forGetter(CakeBatter::getSizeContainer),
                    CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").orElse(CakeFlavors.REGISTRY.get(CakeFlavors.REGISTRY.getDefaultId())).forGetter(CakeBatter::getFlavor),
                    CakeTops.REGISTRY.getCodec().optionalFieldOf("top").forGetter(CakeBatter::getTop),
                    Codec.unboundedMap(CakeFeatures.REGISTRY.getCodec(), NbtCompound.CODEC).fieldOf("features").orElse(Map.of()).forGetter(CakeBatter::getFeatureMap),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));

    public static CakeBatter<FixedBatterSizeContainer> getFixedSizeDefault() {
        return new CakeBatter<>(0, new FixedBatterSizeContainer(false), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<HeightOnlyBatterSizeContainer> getHeightOnlyDefault() {
        return new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(8.0f), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FullBatterSizeContainer> getFullSizeDefault() {
        return new CakeBatter<>(0, new FullBatterSizeContainer(14.0f, 8.0f), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FixedBatterSizeContainer> getFixedSizeEmpty() {
        return new CakeBatter<>(0, new FixedBatterSizeContainer(true), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<HeightOnlyBatterSizeContainer> getHeightOnlyEmpty() {
        return new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FullBatterSizeContainer> getFullSizeEmpty() {
        return new CakeBatter<>(0, new FullBatterSizeContainer(), CakeFlavors.VANILLA, Optional.empty(), Map.of(), false);
    }

    public NbtCompound toNbt(NbtCompound nbt, Codec<CakeBatter<S>> codec) {
        NbtElement result = codec.encodeStart(NbtOps.INSTANCE, this).get().orThrow();

        if (result instanceof NbtCompound compound) {
            return compound;
        }
        return nbt;
    }

    public static <S extends BatterSizeContainer> CakeBatter<S> fromNbt(@Nullable NbtCompound nbt, Codec<CakeBatter<S>> codec, CakeBatter<S> defaultValue) {
        if (nbt == null) {
            return defaultValue;
        }
        DataResult<CakeBatter<S>> dataResult = codec.parse(NbtOps.INSTANCE, nbt);
        return dataResult.result().orElse(defaultValue);
    }

    public static List<CakeBatter<FullBatterSizeContainer>> listFrom(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("batter", NbtElement.LIST_TYPE)) {
            return Collections.emptyList();
        }
        return nbt.getList("batter", NbtList.COMPOUND_TYPE).stream()
                .filter(layer -> layer.getType() == NbtElement.COMPOUND_TYPE)
                .map(layer -> CakeBatter.fromNbt((NbtCompound) layer, FULL_CODEC, getFullSizeDefault())).collect(Collectors.toCollection(ArrayList::new));
    }

    public ActionResult bite(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockEntity blockEntity, float biteSize) {
        if (this.isWaxed()) return ActionResult.FAIL;
        ActionResult tryEatFlavor = this.getFlavor().onTryEat(this, world, pos, state, player);
        ActionResult tryEatTop = ActionResult.PASS;
        if (this.getTop().isPresent()) {
            tryEatTop = this.getTop().get().onTryEat(this, world, pos, state, player, blockEntity);
        }
        ActionResult tryEatFeature = ActionResult.PASS;
        for (CakeFeature feature : this.getFeatures()) {
            tryEatFeature = feature.onTryEat(this, world, pos, state, player, blockEntity);
        }
        if (this.getFlavor().isIn(PBTags.Flavors.INEDIBLE) || (this.getTop().isPresent() && this.getTop().get().isIn(PBTags.Tops.INEDIBLE)) || this.getFeatures().stream().anyMatch(feature -> feature.isIn(PBTags.Features.INEDIBLE))) {
            return tryEatFlavor.isAccepted() || tryEatTop.isAccepted() || tryEatFeature.isAccepted() ? ActionResult.SUCCESS : ActionResult.PASS;
        }
        if (this.getSizeContainer().bite(world, pos, state, player, blockEntity, biteSize)) {
            player.incrementStat(Stats.EAT_CAKE_SLICE);
            world.emitGameEvent(player, GameEvent.EAT, pos);
            player.getHungerManager().add(PedrosBakery.CONFIG.cakeBiteFood.get(), PedrosBakery.CONFIG.cakeBiteSaturation.get());
            if (!world.isClient()) {
                PBHelpers.update(blockEntity, (ServerWorld) world);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.FAIL;
    }

    public static void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, World world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        batter.getFlavor().tick(batter, batterList, world, pos, state, blockEntity);
        if (batter.getTop().isPresent()) {
            batter.getTop().get().tick(batter, batterList, world, pos, state, blockEntity);
        }
        for (CakeFeature feature : batter.getFeatures()) {
            feature.tick(batter, batterList, world, pos, state, blockEntity);
        }
    }

    public int getBakeTime() {
        return this.bakeTime;
    }

    public CakeBatter<S> withBakeTime(int bakeTime) {
        this.bakeTime = bakeTime;
        return this;
    }

    public S getSizeContainer() {
        return this.sizeContainer;
    }

    public CakeBatter<S> withSizeContainer(S sizeContainer) {
        this.sizeContainer = sizeContainer;
        return this;
    }

    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public CakeBatter<S> withFlavor(CakeFlavor flavor) {
        this.flavor = flavor;
        return this;
    }

    public Optional<CakeTop> getTop() {
        return this.top;
    }

    public CakeBatter<S> withTop(@Nullable CakeTop top) {
        this.top = Optional.ofNullable(top);
        return this;
    }

    public List<CakeFeature> getFeatures() {
        return this.features.keySet().stream().toList();
    }

    public Map<CakeFeature, NbtCompound> getFeatureMap() {
        return this.features;
    }

    public CakeBatter<S> withFeatures(Map<CakeFeature, NbtCompound> features) {
        this.features = features;
        return this;
    }

    public CakeBatter<S> withFeature(CakeFeature feature, NbtCompound nbt) {
        Map<CakeFeature, NbtCompound> features = Maps.newHashMap(this.getFeatureMap());
        features.put(feature, nbt);
        return this.withFeatures(features);
    }

    public CakeBatter<S> withFeature(CakeFeature feature) {
        return this.withFeature(feature, new NbtCompound());
    }

    public VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getSizeContainer().getShape(state, world, pos, context);
    }

    public boolean isEmpty() {
        return this.getSizeContainer().isEmpty();
    }

    public boolean isWaxed() {
        return this.waxed;
    }

    public boolean setWaxed(boolean waxed) {
        if (this.waxed != waxed) {
            this.waxed = waxed;
            return true;
        }
        return false;
    }

    public void bakeTick(World world, BlockPos pos, BlockState state) {
        if (this.getBakeTime() != Integer.MAX_VALUE) {
            this.withBakeTime(this.getBakeTime() + 1);
        }
        this.getFlavor().bakeTick(this, world, pos, state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CakeBatter<?> batter = (CakeBatter<?>) o;
        return getBakeTime() == batter.getBakeTime() && isWaxed() == batter.isWaxed() && Objects.equals(getSizeContainer(), batter.getSizeContainer()) && Objects.equals(getFlavor(), batter.getFlavor()) && Objects.equals(getTop(), batter.getTop()) && Objects.equals(getFeatures(), batter.getFeatures());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getBakeTime(), this.getSizeContainer(), this.getFlavor(), this.getTop(), this.getFeatures(), this.isWaxed());
    }

    public CakeBatter<S> copy() {
        return new CakeBatter<>(this.getBakeTime(), ((S) this.getSizeContainer().copy()), this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), NbtCompound::copy)), this.isWaxed());
    }

    public <T extends BatterSizeContainer> CakeBatter<T> copy(T sizeContainer) {
        return new CakeBatter<>(this.getBakeTime(), sizeContainer, this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), NbtCompound::copy)), this.isWaxed());
    }
}
