package net.pedroricardo.datagen;

import com.google.common.collect.Sets;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.pedroricardo.item.recipes.MixingPattern;
import net.pedroricardo.item.recipes.MixingPatterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractMixingPatternProvider implements DataProvider {
    private final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;

    public AbstractMixingPatternProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this.pathResolver = output.getResolver(MixingPatterns.REGISTRY_KEY);
        this.registryLookupFuture = registryLookupFuture;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return this.registryLookupFuture.thenCompose(registryLookup -> this.run(writer, registryLookup));
    }

    public CompletableFuture<?> run(DataWriter writer, final RegistryWrapper.WrapperLookup registryLookup) {
        final HashSet<Identifier> set = Sets.newHashSet();
        final ArrayList<CompletableFuture<?>> list = new ArrayList<>();
        this.generate((id, pattern) -> {
            if (!set.add(id)) {
                throw new IllegalStateException("Duplicate mixing pattern " + id);
            }
            list.add(DataProvider.writeCodecToPath(writer, registryLookup, MixingPattern.CODEC, pattern, AbstractMixingPatternProvider.this.pathResolver.resolveJson(id)));
        });
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public abstract void generate(MixingPatternExporter exporter);

    @Override
    public String getName() {
        return "Mixing Patterns";
    }
}
