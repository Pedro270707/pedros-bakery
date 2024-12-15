package net.pedroricardo.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.tags.PBTags;

import java.util.concurrent.CompletableFuture;

public class PBBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public PBBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).setReplace(false).add(PBBlocks.BEATER, PBBlocks.BAKING_TRAY, PBBlocks.BAKING_TRAY_PART, PBBlocks.EXPANDABLE_BAKING_TRAY, PBBlocks.CUPCAKE_TRAY);
        this.getOrCreateTagBuilder(BlockTags.CANDLE_CAKES).setReplace(false).addTag(PBTags.Blocks.CANDLE_CAKES);

        this.getOrCreateTagBuilder(PBTags.Blocks.BAKES_CAKE).addOptionalTag(BlockTags.FIRE).addOptionalTag(BlockTags.CAMPFIRES).add(Blocks.MAGMA_BLOCK, Blocks.LAVA, Blocks.LAVA_CAULDRON);
        this.getOrCreateTagBuilder(PBTags.Blocks.CANDLE_CAKES).add(
                PBBlocks.WHITE_CANDLE_CAKE,
                PBBlocks.ORANGE_CANDLE_CAKE,
                PBBlocks.MAGENTA_CANDLE_CAKE,
                PBBlocks.LIGHT_BLUE_CANDLE_CAKE,
                PBBlocks.YELLOW_CANDLE_CAKE,
                PBBlocks.LIME_CANDLE_CAKE,
                PBBlocks.PINK_CANDLE_CAKE,
                PBBlocks.GRAY_CANDLE_CAKE,
                PBBlocks.LIGHT_GRAY_CANDLE_CAKE,
                PBBlocks.CYAN_CANDLE_CAKE,
                PBBlocks.PURPLE_CANDLE_CAKE,
                PBBlocks.BLUE_CANDLE_CAKE,
                PBBlocks.BROWN_CANDLE_CAKE,
                PBBlocks.GREEN_CANDLE_CAKE,
                PBBlocks.RED_CANDLE_CAKE,
                PBBlocks.BLACK_CANDLE_CAKE
        );
        this.getOrCreateTagBuilder(PBTags.Blocks.CAKES).add(PBBlocks.CAKE).addTag(PBTags.Blocks.CANDLE_CAKES);
    }
}
