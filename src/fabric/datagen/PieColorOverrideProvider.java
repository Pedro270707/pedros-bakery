package net.pedroricardo.datagen;

import net.minecraft.data.DataOutput;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.item.PBItems;

import java.util.concurrent.CompletableFuture;

public class PieColorOverrideProvider extends AbstractPieColorOverrideProvider {
    public PieColorOverrideProvider(DataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    public void generate(PieColorOverrideExporter exporter) {
        exporter.accept(Identifier.of(PedrosBakery.MOD_ID, "default"), PBBlocks.PIE.asItem(), TextColor.fromRgb(0xE6BA7E));
    }
}
