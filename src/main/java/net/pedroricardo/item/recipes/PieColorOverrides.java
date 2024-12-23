package net.pedroricardo.item.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.pedroricardo.PedrosBakery;

import java.util.Map;
import java.util.Optional;

public class PieColorOverrides extends SimpleJsonResourceReloadListener implements ResourceManagerReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final MapCodec<Map<Item, TextColor>> CODEC = Codec.unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), TextColor.CODEC).fieldOf("overrides");
    private ImmutableMap<Item, TextColor> overrides = ImmutableMap.of();

    public PieColorOverrides() {
        super(GSON, "pie_color_override");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<Item, TextColor> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> entry : prepared.entrySet()) {
            ResourceLocation identifier = entry.getKey();
            try {
                Map<Item, TextColor> map = CODEC.codec().parse(JsonOps.INSTANCE, entry.getValue()).get().orThrow();
                builder.putAll(map);
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                PedrosBakery.LOGGER.error("Parsing error loading pie color override {}", identifier, runtimeException);
            }
        }
        this.overrides = builder.build();
        PedrosBakery.LOGGER.info("Loaded {} pie color overrides", this.overrides.size());
    }

    public Optional<TextColor> get(Item item) {
        if (!this.overrides.containsKey(item)) {
            return Optional.empty();
        }
        return Optional.ofNullable(this.overrides.get(item));
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
    }
}
