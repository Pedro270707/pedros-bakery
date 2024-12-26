package net.pedroricardo.datagen;

import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.pedroricardo.item.recipes.MixingPattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMixingPatternProvider implements DataProvider {
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public AbstractMixingPatternProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "mixing_patterns");
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(registryLookup -> {
            final HashSet<Identifier> set = Sets.newHashSet();
            final ArrayList<CompletableFuture<?>> list = new ArrayList<>();
            this.generate((id, pattern) -> {
                if (!set.add(id)) {
                    throw new IllegalStateException("Duplicate mixing pattern " + id);
                }
                JsonElement element = MixingPattern.Serializer.toJsonTree(pattern);
                list.add(DataProvider.writeToPath(writer, element, AbstractMixingPatternProvider.this.pathResolver.resolveJson(id)));
            });
            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    public abstract void generate(MixingPatternExporter exporter);

    @Override
    public String getName() {
        return "Mixing Patterns";
    }
}
