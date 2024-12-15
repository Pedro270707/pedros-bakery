package net.pedroricardo.item.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.profiler.Profiler;
import net.pedroricardo.PedrosBakery;

import java.util.Map;
import java.util.Optional;

public class PieColorOverrides extends JsonDataLoader implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    public static final MapCodec<Map<Item, TextColor>> CODEC = Codecs.strictUnboundedMap(Registries.ITEM.getCodec(), TextColor.CODEC).fieldOf("overrides");
    private ImmutableMap<Item, TextColor> overrides = ImmutableMap.of();

    public PieColorOverrides() {
        super(GSON, "pie_color_override");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Item, TextColor> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            Identifier identifier = entry.getKey();
            try {
                Map<Item, TextColor> map = CODEC.codec().parse(JsonOps.INSTANCE, entry.getValue()).getOrThrow(JsonParseException::new);
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
    public Identifier getFabricId() {
        return Identifier.of(PedrosBakery.MOD_ID, "pie_color_overrides");
    }

    @Override
    public void reload(ResourceManager manager) {
    }
}
