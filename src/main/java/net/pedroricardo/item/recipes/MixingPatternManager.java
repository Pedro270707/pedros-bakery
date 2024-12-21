package net.pedroricardo.item.recipes;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.item.ItemStack;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.extras.beater.Liquid;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MixingPatternManager extends JsonDataLoader implements SimpleSynchronousResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private ImmutableMap<Identifier, MixingPattern> patterns = ImmutableMap.of();

    public MixingPatternManager() {
        super(GSON, "mixing_patterns");
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, MixingPattern> builder = ImmutableMap.builder();
        for (Map.Entry<Identifier, JsonElement> entry : prepared.entrySet()) {
            Identifier identifier = entry.getKey();
            try {
                MixingPattern recipe = MixingPattern.Serializer.fromJson(entry.getValue());
                if (recipe != null) {
                    builder.put(identifier, recipe);
                } else {
                    PedrosBakery.LOGGER.warn("Could not load mixing pattern {}", identifier);
                }
            } catch (JsonParseException | IllegalArgumentException runtimeException) {
                PedrosBakery.LOGGER.error("Parsing error loading mixing pattern {}", identifier, runtimeException);
            }
        }
        this.patterns = builder.build();
        PedrosBakery.LOGGER.info("Loaded {} mixing patterns", this.patterns.size());
    }

    public Optional<MixingPatternEntry> getFirstMatch(Collection<ItemStack> input, Liquid base) {
        if (input.isEmpty()) {
            return Optional.empty();
        }
        return this.getAllMatches(input, base).stream().findFirst();
    }

    private List<MixingPatternEntry> getAllMatches(Collection<ItemStack> input, Liquid base) {
        return this.patterns.entrySet().stream().filter(entry -> entry.getValue().matches(input, base)).sorted().map(entry -> new MixingPatternEntry(entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(PedrosBakery.MOD_ID, "mixing_patterns");
    }

    @Override
    public void reload(ResourceManager manager) {
    }
}
