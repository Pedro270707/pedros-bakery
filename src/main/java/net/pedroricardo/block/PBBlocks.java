package net.pedroricardo.block;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.helpers.CakeBatter;
import net.pedroricardo.item.*;

import java.util.function.BiConsumer;

public class PBBlocks {
    public static final Block CAKE = registerWithoutItem("cake", new PBCakeBlock(AbstractBlock.Settings.copy(Blocks.CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block CANDLE_CAKE = registerWithoutItem("candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.CANDLE, AbstractBlock.Settings.copy(Blocks.CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block WHITE_CANDLE_CAKE = registerWithoutItem("white_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.WHITE_CANDLE, AbstractBlock.Settings.copy(Blocks.WHITE_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block ORANGE_CANDLE_CAKE = registerWithoutItem("orange_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.ORANGE_CANDLE, AbstractBlock.Settings.copy(Blocks.ORANGE_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block MAGENTA_CANDLE_CAKE = registerWithoutItem("magenta_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.MAGENTA_CANDLE, AbstractBlock.Settings.copy(Blocks.MAGENTA_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block LIGHT_BLUE_CANDLE_CAKE = registerWithoutItem("light_blue_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.LIGHT_BLUE_CANDLE, AbstractBlock.Settings.copy(Blocks.LIGHT_BLUE_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block YELLOW_CANDLE_CAKE = registerWithoutItem("yellow_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.YELLOW_CANDLE, AbstractBlock.Settings.copy(Blocks.YELLOW_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block LIME_CANDLE_CAKE = registerWithoutItem("lime_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.LIME_CANDLE, AbstractBlock.Settings.copy(Blocks.LIME_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block PINK_CANDLE_CAKE = registerWithoutItem("pink_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.PINK_CANDLE, AbstractBlock.Settings.copy(Blocks.PINK_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block GRAY_CANDLE_CAKE = registerWithoutItem("gray_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.GRAY_CANDLE, AbstractBlock.Settings.copy(Blocks.GRAY_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block LIGHT_GRAY_CANDLE_CAKE = registerWithoutItem("light_gray_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.LIGHT_GRAY_CANDLE, AbstractBlock.Settings.copy(Blocks.LIGHT_GRAY_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block CYAN_CANDLE_CAKE = registerWithoutItem("cyan_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.CYAN_CANDLE, AbstractBlock.Settings.copy(Blocks.CYAN_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block PURPLE_CANDLE_CAKE = registerWithoutItem("purple_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.PURPLE_CANDLE, AbstractBlock.Settings.copy(Blocks.PURPLE_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block BLUE_CANDLE_CAKE = registerWithoutItem("blue_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.BLUE_CANDLE, AbstractBlock.Settings.copy(Blocks.BLUE_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block BROWN_CANDLE_CAKE = registerWithoutItem("brown_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.BROWN_CANDLE, AbstractBlock.Settings.copy(Blocks.BROWN_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block GREEN_CANDLE_CAKE = registerWithoutItem("green_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.GREEN_CANDLE, AbstractBlock.Settings.copy(Blocks.GREEN_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block RED_CANDLE_CAKE = registerWithoutItem("red_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.RED_CANDLE, AbstractBlock.Settings.copy(Blocks.RED_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block BLACK_CANDLE_CAKE = registerWithoutItem("black_candle_cake", new PBCandleCakeBlock((CandleBlock) Blocks.BLACK_CANDLE, AbstractBlock.Settings.copy(Blocks.BLACK_CANDLE_CAKE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block CAKE_PART = registerWithoutItem("cake_part", new PBCakeBlockPart(AbstractBlock.Settings.copy(CAKE)));
    public static final Block BEATER = register("beater", new BeaterBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).hardness(1.5f).resistance(1.5f).mapColor(MapColor.OFF_WHITE).requiresTool()));
    public static final Block BAKING_TRAY = register("baking_tray", new BakingTrayBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).hardness(1.0f).resistance(1.0f).mapColor(MapColor.OFF_WHITE).requiresTool().dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)), (name, block) -> PBItems.register(name, new BakingTrayItem(block, new Item.Settings().maxCount(1).component(PBComponentTypes.BATTER, CakeBatter.getEmpty()).component(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize()).component(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()))));
    public static final Block BAKING_TRAY_PART = registerWithoutItem("baking_tray_part", new BakingTrayBlockPart(AbstractBlock.Settings.copy(BAKING_TRAY)));
    public static final Block CAKE_STAND = register("cake_stand", new CakeStandBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.GLASS).hardness(0.4f).mapColor(MapColor.OFF_WHITE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)));
    public static final Block EXPANDABLE_BAKING_TRAY = register("expandable_baking_tray", new BakingTrayBlock(AbstractBlock.Settings.copy(BAKING_TRAY)), (name, block) -> PBItems.register(name, new ExpandableBakingTrayItem(block, new Item.Settings().maxCount(1).component(PBComponentTypes.BATTER, CakeBatter.getEmpty()).component(PBComponentTypes.SIZE, PedrosBakery.CONFIG.bakingTrayDefaultSize()).component(PBComponentTypes.HEIGHT, PedrosBakery.CONFIG.bakingTrayDefaultHeight()))));
    public static final Block CUPCAKE_TRAY = register("cupcake_tray", new CupcakeTrayBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.METAL).hardness(1.0f).resistance(1.0f).mapColor(MapColor.OFF_WHITE).requiresTool().dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)), (name, block) -> PBItems.register(name, new CupcakeTrayItem(block, new Item.Settings().maxCount(1).component(PBComponentTypes.BATTER, CakeBatter.getEmpty()))));
    public static final Block CUPCAKE = register("cupcake", new CupcakeBlock(AbstractBlock.Settings.create().sounds(BlockSoundGroup.WOOL).hardness(0.5f).resistance(0.5f).mapColor(MapColor.OFF_WHITE).dynamicBounds().nonOpaque().solidBlock(Blocks::never).blockVision(Blocks::never)), (name, block) -> PBItems.register(name, new CupcakeItem(block, new Item.Settings().maxCount(1))));

    private static Block register(final String name, Block block) {
        return register(name, block, (str, registeredBlock) -> PBItems.register(str, new BlockItem(registeredBlock, new Item.Settings())));
    }

    private static Block register(final String name, Block block, BiConsumer<String, Block> itemRegisterer) {
        Block registeredBlock = registerWithoutItem(name, block);
        itemRegisterer.accept(name, registeredBlock);
        return block;
    }

    private static Block registerWithoutItem(String name, Block block) {
        return Registry.register(Registries.BLOCK, Identifier.of(PedrosBakery.MOD_ID, name), block);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering blocks");
    }
}
