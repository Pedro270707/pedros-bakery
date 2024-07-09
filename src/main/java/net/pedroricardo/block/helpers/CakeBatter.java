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

public class CakeBatter {
    private int bakeTime;
    private float height;
    private float bites;
    private float size;
    private CakeFlavor flavor;
    private Optional<CakeTop> top;
    private Map<CakeFeature, NbtCompound> features;

    public CakeBatter(int bakeTime, CakeFlavor cakeFlavor) {
        this(bakeTime, 16.0f, cakeFlavor);
    }

    public CakeBatter(int bakeTime, float height, CakeFlavor cakeFlavor) {
        this(bakeTime, height, 0.0f, 16.0f, cakeFlavor, Optional.empty(), Maps.newHashMap());
    }

    public CakeBatter(int bakeTime, float height, float bites, float size, CakeFlavor flavor, Optional<CakeTop> top, Map<CakeFeature, NbtCompound> features) {
        this.bakeTime = bakeTime;
        this.height = height;
        this.bites = bites;
        this.size = size;
        this.flavor = flavor;
        this.top = top;
        this.features = features;
    }

    private static final CakeBatter DEFAULT = new CakeBatter(0, 8, 0, 14, CakeFlavors.VANILLA, Optional.empty(), Map.of());
    private static final CakeBatter EMPTY = new CakeBatter(0, 0, 0, 0, CakeFlavors.VANILLA, Optional.empty(), Map.of());

    public static final Codec<CakeBatter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("bake_time").orElse(0).forGetter(CakeBatter::getBakeTime),
                    Codec.FLOAT.fieldOf("height").orElse(8.0f).forGetter(CakeBatter::getHeight),
                    Codec.FLOAT.fieldOf("bites").orElse(0.0f).forGetter(CakeBatter::getBites),
                    Codec.FLOAT.fieldOf("size").orElse(14.0f).forGetter(CakeBatter::getSize),
                    CakeFlavors.REGISTRY.getCodec().fieldOf("flavor").orElse(CakeFlavors.REGISTRY.getDefaultEntry().get().value()).forGetter(CakeBatter::getFlavor),
                    CakeTops.REGISTRY.getCodec().optionalFieldOf("top").forGetter(CakeBatter::getTop),
                    Codec.unboundedMap(CakeFeatures.REGISTRY.getCodec(), NbtCompound.CODEC).fieldOf("features").orElse(Map.of()).forGetter(CakeBatter::getFeatureMap))
            .apply(instance, CakeBatter::new));
    public static final PacketCodec<RegistryByteBuf, CakeBatter> PACKET_CODEC = PBHelpers.tuplePacketCodec(PacketCodecs.VAR_INT, CakeBatter::getBakeTime, PacketCodecs.FLOAT, CakeBatter::getHeight, PacketCodecs.FLOAT, CakeBatter::getBites, PacketCodecs.FLOAT, CakeBatter::getSize, PacketCodecs.registryValue(CakeFlavors.REGISTRY_KEY), CakeBatter::getFlavor, PacketCodecs.optional(PacketCodecs.registryValue(CakeTops.REGISTRY_KEY)), CakeBatter::getTop, PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.registryValue(CakeFeatures.REGISTRY_KEY), PacketCodecs.NBT_COMPOUND), CakeBatter::getFeatureMap, CakeBatter::new);

    public static CakeBatter getDefault() {
        return DEFAULT.copy();
    }

    public static CakeBatter getEmpty() {
        return EMPTY.copy();
    }

    public NbtCompound toNbt(NbtCompound nbt) {
        NbtElement result = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();

        if (result instanceof NbtCompound compound) {
            return compound;
        }
        return nbt;
    }

    public static CakeBatter fromNbt(@Nullable NbtCompound nbt) {
        if (nbt == null) {
            return DEFAULT;
        }
        DataResult<CakeBatter> dataResult = CODEC.parse(NbtOps.INSTANCE, nbt);
        return dataResult.result().orElse(DEFAULT);
    }

    public static List<CakeBatter> listFrom(@Nullable NbtCompound nbt) {
        if (nbt == null || !nbt.contains("batter", NbtElement.LIST_TYPE)) {
            return Collections.emptyList();
        }
        return nbt.getList("batter", NbtList.COMPOUND_TYPE).stream()
                .filter(layer -> layer.getType() == NbtElement.COMPOUND_TYPE)
                .map(layer -> CakeBatter.fromNbt((NbtCompound) layer)).collect(Collectors.toCollection(Lists::newArrayList));
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

    public CakeBatter withBakeTime(int bakeTime) {
        this.bakeTime = bakeTime;
        return this;
    }

    public float getHeight() {
        return this.height;
    }

    public CakeBatter withHeight(float height) {
        this.height = height;
        return this;
    }

    public float getSize() {
        return this.size;
    }

    public CakeBatter withSize(float size) {
        this.size = size;
        return this;
    }

    public CakeFlavor getFlavor() {
        return this.flavor;
    }

    public CakeBatter withFlavor(CakeFlavor flavor) {
        this.flavor = flavor;
        return this;
    }

    public Optional<CakeTop> getTop() {
        return this.top;
    }

    public CakeBatter withTop(@Nullable CakeTop top) {
        this.top = Optional.ofNullable(top);
        return this;
    }

    public List<CakeFeature> getFeatures() {
        return this.features.keySet().stream().toList();
    }

    public Map<CakeFeature, NbtCompound> getFeatureMap() {
        return this.features;
    }

    public CakeBatter withFeatures(Map<CakeFeature, NbtCompound> features) {
        this.features = features;
        return this;
    }

    public CakeBatter withFeature(CakeFeature feature, NbtCompound nbt) {
        Map<CakeFeature, NbtCompound> features = Maps.newHashMap(this.getFeatureMap());
        features.put(feature, nbt);
        return this.withFeatures(features);
    }

    public CakeBatter withFeature(CakeFeature feature) {
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
        CakeBatter layer = (CakeBatter) o;
        return this.getBakeTime() == layer.getBakeTime() && this.getHeight() == layer.getHeight() && this.getBites() == layer.getBites() && this.getSize() == layer.getSize() && this.getFlavor() == layer.getFlavor() && Objects.equals(this.getTop(), layer.getTop()) && Objects.equals(this.getFeatureMap(), layer.getFeatureMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.bites, this.size, this.top);
    }

    public CakeBatter copy() {
        return new CakeBatter(this.getBakeTime(), this.getHeight(), this.getBites(), this.getSize(), this.getFlavor(), this.getTop(), Maps.newHashMap(Maps.transformValues(this.getFeatureMap(), NbtCompound::copy)));
    }
}
