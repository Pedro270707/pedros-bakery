package net.pedroricardo.block.extras;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.size.BatterSizeContainer;
import net.pedroricardo.block.extras.size.FixedBatterSizeContainer;
import net.pedroricardo.block.extras.size.FullBatterSizeContainer;
import net.pedroricardo.block.extras.size.HeightOnlyBatterSizeContainer;
import net.pedroricardo.block.tags.PBTags;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CakeBatter<S extends BatterSizeContainer> {
    private int bakeTime;
    private S sizeContainer;
    private CakeFlavor flavor;
    private Optional<CakeTop> top;
    private Map<CakeFeature, CompoundTag> features;
    private boolean waxed;

    public CakeBatter(int bakeTime, S sizeContainer, Optional<CakeTop> top, CakeFlavor cakeFlavor, boolean waxed) {
        this(bakeTime, sizeContainer, cakeFlavor, top, Maps.newHashMap(), waxed);
    }

    public CakeBatter(int bakeTime, S sizeContainer, CakeFlavor cakeFlavor, boolean waxed) {
        this(bakeTime, sizeContainer, cakeFlavor, Optional.empty(), Maps.newHashMap(), waxed);
    }

    public CakeBatter(int bakeTime, S sizeContainer, CakeFlavor flavor, Optional<CakeTop> top, Map<CakeFeature, CompoundTag> features, boolean waxed) {
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
                    CakeTops.registrySupplier.get().getCodec().optionalFieldOf("top").forGetter(CakeBatter::getTop),
                    CakeFlavors.registrySupplier.get().getCodec().fieldOf("flavor").orElse(CakeFlavors.registrySupplier.get().getValue(CakeFlavors.registrySupplier.get().getDefaultKey())).forGetter(CakeBatter::getFlavor),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));
    public static final Codec<CakeBatter<HeightOnlyBatterSizeContainer>> WITH_HEIGHT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    HeightOnlyBatterSizeContainer.CODEC.fieldOf("batter_size").orElse(new HeightOnlyBatterSizeContainer()).forGetter(CakeBatter::getSizeContainer),
                    CakeFlavors.registrySupplier.get().getCodec().fieldOf("flavor").orElse(CakeFlavors.registrySupplier.get().getValue(CakeFlavors.registrySupplier.get().getDefaultKey())).forGetter(CakeBatter::getFlavor),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));
    public static final Codec<CakeBatter<FullBatterSizeContainer>> FULL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    FullBatterSizeContainer.CODEC.fieldOf("batter_size").orElse(new FullBatterSizeContainer()).forGetter(CakeBatter::getSizeContainer),
                    CakeFlavors.registrySupplier.get().getCodec().fieldOf("flavor").orElse(CakeFlavors.registrySupplier.get().getValue(CakeFlavors.registrySupplier.get().getDefaultKey())).forGetter(CakeBatter::getFlavor),
                    CakeTops.registrySupplier.get().getCodec().optionalFieldOf("top").forGetter(CakeBatter::getTop),
                    Codec.unboundedMap(CakeFeatures.registrySupplier.get().getCodec(), CompoundTag.CODEC).fieldOf("features").orElse(Map.of()).forGetter(CakeBatter::getFeatureMap),
                    Codec.BOOL.fieldOf("is_waxed").orElse(false).forGetter(CakeBatter::isWaxed))
            .apply(instance, CakeBatter::new));

    public static CakeBatter<FixedBatterSizeContainer> getFixedSizeDefault() {
        return new CakeBatter<>(0, new FixedBatterSizeContainer(false), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<HeightOnlyBatterSizeContainer> getHeightOnlyDefault() {
        return new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(8.0f), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FullBatterSizeContainer> getFullSizeDefault() {
        return new CakeBatter<>(0, new FullBatterSizeContainer(14.0f, 8.0f), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FixedBatterSizeContainer> getFixedSizeEmpty() {
        return new CakeBatter<>(0, new FixedBatterSizeContainer(true), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<HeightOnlyBatterSizeContainer> getHeightOnlyEmpty() {
        return new CakeBatter<>(0, new HeightOnlyBatterSizeContainer(), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public static CakeBatter<FullBatterSizeContainer> getFullSizeEmpty() {
        return new CakeBatter<>(0, new FullBatterSizeContainer(), CakeFlavors.VANILLA.get(), Optional.empty(), Map.of(), false);
    }

    public CompoundTag toNbt(CompoundTag nbt, Codec<CakeBatter<S>> codec) {
        Tag result = codec.encodeStart(NbtOps.INSTANCE, this).get().orThrow();

        if (result instanceof CompoundTag compound) {
            return compound;
        }
        return nbt;
    }

    public static <S extends BatterSizeContainer> CakeBatter<S> fromNbt(@Nullable CompoundTag nbt, Codec<CakeBatter<S>> codec, CakeBatter<S> defaultValue) {
        if (nbt == null) {
            return defaultValue;
        }
        DataResult<CakeBatter<S>> dataResult = codec.parse(NbtOps.INSTANCE, nbt);
        return dataResult.result().orElse(defaultValue);
    }

    public static List<CakeBatter<FullBatterSizeContainer>> listFrom(@Nullable CompoundTag nbt) {
        if (nbt == null || !nbt.contains("batter", Tag.TAG_LIST)) {
            return Collections.emptyList();
        }
        return nbt.getList("batter", ListTag.TAG_COMPOUND).stream()
                .filter(layer -> layer.getType() == CompoundTag.TYPE)
                .map(layer -> CakeBatter.fromNbt((CompoundTag) layer, FULL_CODEC, getFullSizeDefault())).collect(Collectors.toCollection(Lists::newArrayList));
    }

    public InteractionResult bite(Level world, BlockPos pos, BlockState state, Player player, BlockEntity blockEntity, float biteSize) {
        if (this.isWaxed()) return InteractionResult.FAIL;
        InteractionResult tryEatFlavor = this.getFlavor().onTryEat(this, world, pos, state, player);
        InteractionResult tryEatTop = InteractionResult.PASS;
        if (this.getTop().isPresent()) {
            tryEatTop = this.getTop().get().onTryEat(this, world, pos, state, player, blockEntity);
        }
        InteractionResult tryEatFeature = InteractionResult.PASS;
        for (CakeFeature feature : this.getFeatures()) {
            tryEatFeature = feature.onTryEat(this, world, pos, state, player, blockEntity);
        }
        if (this.getFlavor().is(PBTags.Flavors.INEDIBLE) || (this.getTop().isPresent() && this.getTop().get().is(PBTags.Tops.INEDIBLE)) || this.getFeatures().stream().anyMatch(feature -> feature.is(PBTags.Features.INEDIBLE))) {
            return tryEatFlavor.consumesAction() || tryEatTop.consumesAction() || tryEatFeature.consumesAction() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (this.getSizeContainer().bite(world, pos, state, player, blockEntity, biteSize)) {
            player.awardStat(Stats.EAT_CAKE_SLICE);
            world.gameEvent(player, GameEvent.EAT, pos);
            player.getFoodData().eat(PedrosBakery.CONFIG.cakeBiteFood.get(), PedrosBakery.CONFIG.cakeBiteSaturation.get());
            if (!world.isClientSide()) {
                PBHelpers.update(blockEntity, (ServerLevel) world);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.FAIL;
    }

    public static void tick(CakeBatter<?> batter, List<? extends CakeBatter<?>> batterList, Level world, BlockPos pos, BlockState state, BlockEntity blockEntity) {
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

    public Map<CakeFeature, CompoundTag> getFeatureMap() {
        return this.features;
    }

    public CakeBatter<S> withFeatures(Map<CakeFeature, CompoundTag> features) {
        this.features = features;
        return this;
    }

    public CakeBatter<S> withFeature(CakeFeature feature, CompoundTag nbt) {
        Map<CakeFeature, CompoundTag> features = Maps.newHashMap(this.getFeatureMap());
        features.put(feature, nbt);
        return this.withFeatures(features);
    }

    public CakeBatter<S> withFeature(CakeFeature feature) {
        return this.withFeature(feature, new CompoundTag());
    }

    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
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

    public void bakeTick(Level world, BlockPos pos, BlockState state) {
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
        return new CakeBatter<>(this.getBakeTime(), ((S) this.getSizeContainer().copy()), this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), CompoundTag::copy)), this.isWaxed());
    }

    public <T extends BatterSizeContainer> CakeBatter<T> copy(T sizeContainer) {
        return new CakeBatter<>(this.getBakeTime(), sizeContainer, this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), CompoundTag::copy)), this.isWaxed());
    }
}
