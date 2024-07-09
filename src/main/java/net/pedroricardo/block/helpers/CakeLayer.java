package net.pedroricardo.block.helpers;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.pedroricardo.PBHelpers;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBCakeBlockEntity;
import net.pedroricardo.block.tags.PBTags;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CakeLayer {
    private int bakeTime;
    private float height;
    private float bites;
    private float size;
    private CakeFlavor flavor;
    private Optional<CakeTop> top;
    private Map<CakeFeature, NbtCompound> features;

    public CakeLayer(int bakeTime, CakeFlavor cakeFlavor) {
        this(bakeTime, 16.0f, cakeFlavor);
    }

    public CakeLayer(int bakeTime, float height, CakeFlavor cakeFlavor) {
        this(bakeTime, height, 0.0f, 16.0f, cakeFlavor, Optional.empty(), Maps.newHashMap());
    }

    public CakeLayer(int bakeTime, float height, float bites, float size, CakeFlavor flavor, Optional<CakeTop> top, Map<CakeFeature, NbtCompound> features) {
        this.bakeTime = bakeTime;
        this.height = height;
        this.bites = bites;
        this.size = size;
        this.flavor = flavor;
        this.top = top;
        this.features = features;
    }

    private static final CakeLayer DEFAULT = new CakeLayer(0, 8, 0, 14, CakeFlavors.VANILLA, Optional.empty(), Map.of());
    private static final CakeLayer EMPTY = new CakeLayer(0, 0, 0, 0, CakeFlavors.VANILLA, Optional.empty(), Map.of());

    public static final Codec<CakeLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeLayer::getBakeTime),
                    Codec.FLOAT.fieldOf("height").orElse(8.0f).forGetter(CakeLayer::getHeight),
                    Codec.FLOAT.fieldOf("bites").orElse(0.0f).forGetter(CakeLayer::getBites),
                    Codec.FLOAT.fieldOf("size").orElse(14.0f).forGetter(CakeLayer::getSize),
                    CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").orElse(CakeFlavors.REGISTRY.getDefaultEntry().get().value()).forGetter(CakeLayer::getFlavor),
                    CakeTops.REGISTRY.getCodec().optionalFieldOf("top").forGetter(CakeLayer::getTop),
                    Codec.unboundedMap(CakeFeatures.REGISTRY.getCodec(), NbtCompound.CODEC).fieldOf("features").orElse(Map.of()).forGetter(CakeLayer::getFeatureMap))
            .apply(instance, CakeLayer::new));
    public static final PacketCodec<RegistryByteBuf, CakeLayer> PACKET_CODEC = PBHelpers.tuplePacketCodec(PacketCodecs.VAR_INT, CakeLayer::getBakeTime, PacketCodecs.FLOAT, CakeLayer::getHeight, PacketCodecs.FLOAT, CakeLayer::getBites, PacketCodecs.FLOAT, CakeLayer::getSize, PacketCodecs.registryValue(CakeFlavors.REGISTRY_KEY), CakeLayer::getFlavor, PacketCodecs.optional(PacketCodecs.registryValue(CakeTops.REGISTRY_KEY)), CakeLayer::getTop, PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.registryValue(CakeFeatures.REGISTRY_KEY), PacketCodecs.NBT_COMPOUND), CakeLayer::getFeatureMap, CakeLayer::new);

    public static CakeLayer getDefault() {
        return DEFAULT.copy();
    }

    public static CakeLayer getEmpty() {
        return EMPTY.copy();
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        NbtElement result = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();

        if (result instanceof NbtCompound compound) {
            return compound;
        }
        return nbt;
    }

    public static CakeLayer fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return DEFAULT;
        }
        DataResult<CakeLayer> dataResult = CODEC.parse(NbtOps.INSTANCE, nbt);
        return dataResult.result().orElse(DEFAULT);
    }

    public static List<CakeLayer> listFrom(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("batter", NbtElement.LIST_TYPE)) {
            return Collections.emptyList();
        }
        return nbt.getList("batter", NbtList.COMPOUND_TYPE).stream()
                .filter(layer -> layer.getType() == NbtElement.COMPOUND_TYPE)
                .map(layer -> CakeLayer.fromNbt((NbtCompound) layer)).collect(Collectors.toCollection(Lists::newArrayList));
    }

    public float getBites() {
        return this.bites;
    }

    public void bite(World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake, float biteSize) {
        this.getFlavor().onTryEat(this, world, pos, state, player);
        if (this.getTop().isPresent()) {
            this.getTop().get().onTryEat(this, world, pos, state, player, cake);
        }
        for (CakeFeature feature : this.getFeatures()) {
            feature.onTryEat(this, world, pos, state, player, cake);
        }
        if (this.getFlavor().isIn(PBTags.Flavors.INEDIBLE) || (this.getTop().isPresent() && this.getTop().get().isIn(PBTags.Tops.INEDIBLE)) || this.getFeatures().stream().anyMatch(feature -> feature.isIn(PBTags.Features.INEDIBLE))) {
            return;
        }
        this.bites += biteSize;
        if (this.bites > this.getSize()) {
            this.bites = this.getSize();
        }
        player.incrementStat(Stats.EAT_CAKE_SLICE);
        world.emitGameEvent(player, GameEvent.EAT, pos);
        player.getHungerManager().add(PedrosBakery.CONFIG.cakeBiteFood(), PedrosBakery.CONFIG.cakeBiteSaturation());
        PBHelpers.updateListeners(cake);
    }

    public void tick(World world, BlockPos pos, BlockState state, PBCakeBlockEntity blockEntity) {
        this.getFlavor().tick(this, world, pos, state, blockEntity);
        if (this.getTop().isPresent()) {
            this.getTop().get().tick(this, world, pos, state, blockEntity);
        }
        for (CakeFeature feature : this.getFeatures()) {
            feature.tick(this, world, pos, state, blockEntity);
        }
    }

    public int getBakeTime() {
        return this.bakeTime;
    }

    public CakeLayer withBakeTime(int bakeTime) {
        this.bakeTime = bakeTime;
        return this;
    }

    public float getHeight() {
        return this.height;
    }

    public CakeLayer withHeight(float height) {
        this.height = height;
        return this;
    }

    public float getSize() {
        return this.size;
    }

    public CakeLayer withSize(float size) {
        this.size = size;
        return this;
    }

    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public CakeLayer withFlavor(CakeFlavor flavor) {
        this.flavor = flavor;
        return this;
    }

    public Optional<CakeTop> getTop() {
        return this.top;
    }

    public CakeLayer withTop(@Nullable CakeTop top) {
        this.top = Optional.ofNullable(top);
        return this;
    }

    public List<CakeFeature> getFeatures() {
        return this.features.keySet().stream().toList();
    }

    public Map<CakeFeature, NbtCompound> getFeatureMap() {
        return this.features;
    }

    public CakeLayer withFeatures(Map<CakeFeature, NbtCompound> features) {
        this.features = features;
        return this;
    }

    public CakeLayer withFeature(CakeFeature feature, NbtCompound nbt) {
        Map<CakeFeature, NbtCompound> features = Maps.newHashMap(this.getFeatureMap());
        features.put(feature, nbt);
        return this.withFeatures(features);
    }

    public CakeLayer withFeature(CakeFeature feature) {
        return this.withFeature(feature, new NbtCompound());
    }

    public boolean isEmpty() {
        return this.getHeight() == 0 || this.getSize() == 0 || this.getBites() >= this.getSize();
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
        CakeLayer layer = (CakeLayer) o;
        return this.getBakeTime() == layer.getBakeTime() && this.getHeight() == layer.getHeight() && this.getBites() == layer.getBites() && this.getSize() == layer.getSize() && this.getFlavor() == layer.getFlavor() && Objects.equals(this.getTop(), layer.getTop()) && Objects.equals(this.getFeatureMap(), layer.getFeatureMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.bites, this.size, this.top);
    }

    public CakeLayer copy() {
        return new CakeLayer(this.getBakeTime(), this.getHeight(), this.getBites(), this.getSize(), this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), NbtCompound::copy)));
    }
}
