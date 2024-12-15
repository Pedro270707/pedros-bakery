package net.pedroricardo.datagen;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.pedroricardo.item.recipes.PieColorOverrides;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractPieColorOverrideProvider implements DataProvider {
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public AbstractPieColorOverrideProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "pie_color_override");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(registryLookup -> this.run(writer, registryLookup));
    }

    public CompletableFuture<?> run(DataWriter writer, final RegistryWrapper.WrapperLookup registryLookup) {
        final ArrayList<CompletableFuture<?>> list = new ArrayList<>();
        final Map<Identifier, HashMap<Item, TextColor>> maps = new HashMap<>();
        this.generate((id, item, color) -> {
            if (maps.containsKey(id)) {
                maps.get(id).put(item.asItem(), color);
            } else {
                HashMap<Item, TextColor> map = new HashMap<>();
                map.put(item.asItem(), color);
                maps.put(id, map);
            }
        });
        for (Map.Entry<Identifier, HashMap<Item, TextColor>> entry : maps.entrySet()) {
            JsonElement element = PieColorOverrides.CODEC.codec().encodeStart(JsonOps.INSTANCE, entry.getValue()).get().orThrow();
            list.add(DataProvider.writeToPath(writer, element, AbstractPieColorOverrideProvider.this.pathResolver.resolveJson(entry.getKey())));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public abstract void generate(PieColorOverrideExporter exporter);

    @Override
    public String getName() {
        return "Pie Color Overrides";
    }
}
