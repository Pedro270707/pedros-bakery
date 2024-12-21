package net.pedroricardo.block;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.item.*;

import java.util.function.Function;
import java.util.function.Supplier;

public class PBBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, PedrosBakery.MOD_ID);

    public static final Block CAKE = register("cake", () -> new PBCakeBlock(BlockBehaviour.Properties.copy(Blocks.CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).isViewBlocking((state, pos, world) -> false)), (block) -> new PBCakeBlockItem(block, new Item.Properties()));
    public static final Block CANDLE_CAKE = registerWithoutItem("candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.CANDLE, BlockBehaviour.Properties.copy(Blocks.CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block WHITE_CANDLE_CAKE = registerWithoutItem("white_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.WHITE_CANDLE, BlockBehaviour.Properties.copy(Blocks.WHITE_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block ORANGE_CANDLE_CAKE = registerWithoutItem("orange_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.ORANGE_CANDLE, BlockBehaviour.Properties.copy(Blocks.ORANGE_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block MAGENTA_CANDLE_CAKE = registerWithoutItem("magenta_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.MAGENTA_CANDLE, BlockBehaviour.Properties.copy(Blocks.MAGENTA_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block LIGHT_BLUE_CANDLE_CAKE = registerWithoutItem("light_blue_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.LIGHT_BLUE_CANDLE, BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block YELLOW_CANDLE_CAKE = registerWithoutItem("yellow_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.YELLOW_CANDLE, BlockBehaviour.Properties.copy(Blocks.YELLOW_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block LIME_CANDLE_CAKE = registerWithoutItem("lime_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.LIME_CANDLE, BlockBehaviour.Properties.copy(Blocks.LIME_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block PINK_CANDLE_CAKE = registerWithoutItem("pink_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.PINK_CANDLE, BlockBehaviour.Properties.copy(Blocks.PINK_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block GRAY_CANDLE_CAKE = registerWithoutItem("gray_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.GRAY_CANDLE, BlockBehaviour.Properties.copy(Blocks.GRAY_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block LIGHT_GRAY_CANDLE_CAKE = registerWithoutItem("light_gray_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.LIGHT_GRAY_CANDLE, BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block CYAN_CANDLE_CAKE = registerWithoutItem("cyan_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.CYAN_CANDLE, BlockBehaviour.Properties.copy(Blocks.CYAN_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block PURPLE_CANDLE_CAKE = registerWithoutItem("purple_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.PURPLE_CANDLE, BlockBehaviour.Properties.copy(Blocks.PURPLE_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block BLUE_CANDLE_CAKE = registerWithoutItem("blue_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.BLUE_CANDLE, BlockBehaviour.Properties.copy(Blocks.BLUE_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block BROWN_CANDLE_CAKE = registerWithoutItem("brown_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.BROWN_CANDLE, BlockBehaviour.Properties.copy(Blocks.BROWN_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block GREEN_CANDLE_CAKE = registerWithoutItem("green_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.GREEN_CANDLE, BlockBehaviour.Properties.copy(Blocks.GREEN_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block RED_CANDLE_CAKE = registerWithoutItem("red_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.RED_CANDLE, BlockBehaviour.Properties.copy(Blocks.RED_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block BLACK_CANDLE_CAKE = registerWithoutItem("black_candle_cake", () -> new PBCandleCakeBlock((CandleBlock) Blocks.BLACK_CANDLE, BlockBehaviour.Properties.copy(Blocks.BLACK_CANDLE_CAKE).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block CAKE_PART = registerWithoutItem("cake_part", () -> new PBCakeBlockPart(BlockBehaviour.Properties.copy(CAKE)));
    public static final Block BEATER = register("beater", () -> new BeaterBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).forceSolidOn().destroyTime(1.5f).explosionResistance(1.5f).mapColor(MapColor.QUARTZ).requiresTool()));
    public static final Block BAKING_TRAY = register("baking_tray", () -> new BakingTrayBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).forceSolidOn().destroyTime(1.0f).explosionResistance(1.0f).mapColor(MapColor.QUARTZ).requiresTool().dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)), (name, block) -> PBItems.register(name, () -> new BakingTrayItem(block, () -> new Item.Settings().maxCount(1))));
    public static final Block BAKING_TRAY_PART = registerWithoutItem("baking_tray_part", () -> new BakingTrayBlockPart(BlockBehaviour.Properties.copy(BAKING_TRAY)));
    public static final Block CAKE_STAND = register("cake_stand", () -> new CakeStandBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).forceSolidOn().destroyTime(0.4f).mapColor(MapColor.QUARTZ).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block PLATE = register("plate", () -> new PlateBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).forceSolidOn().destroyTime(0.4f).mapColor(MapColor.QUARTZ).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block EXPANDABLE_BAKING_TRAY = register("expandable_baking_tray", () -> new BakingTrayBlock(BlockBehaviour.Properties.copy(BAKING_TRAY)), (name, block) -> PBItems.register(name, () -> new ExpandableBakingTrayItem(block, () -> new Item.Settings().maxCount(1))));
    public static final Block CUPCAKE_TRAY = register("cupcake_tray", () -> new CupcakeTrayBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).forceSolidOn().destroyTime(1.0f).explosionResistance(1.0f).mapColor(MapColor.QUARTZ).requiresTool().dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)), (name, block) -> PBItems.register(name, () -> new CupcakeTrayItem(block, () -> new Item.Settings().maxCount(1))));
    public static final Block CUPCAKE = register("cupcake", () -> new CupcakeBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOL).destroyTime(0.5f).explosionResistance(0.5f).mapColor(MapColor.QUARTZ).dynamicShape().noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)), (name, block) -> PBItems.register(name, () -> new CupcakeItem(block, () -> new Item.Settings())));
    public static final Block COOKIE_JAR = register("cookie_jar", () -> new CookieJarBlock(BlockBehaviour.Properties.of().sound(SoundType.GLASS).destroyTime(0.5f).explosionResistance(0.5f).mapColor(MapColor.WOOD).noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block BUTTER_CHURN = register("butter_churn", () -> new ButterChurnBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOD).strength(1.5f).mapColor(MapColor.WOOD).noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)));
    public static final Block PIE = register("pie", () -> new PieBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOL).strength(1.0f).mapColor(MapColor.WOOD).noOcclusion().isSuffocating((state, pos, world) -> false).blockVision((state, pos, world) -> false)), (name, block) -> PBItems.register(name, () -> new PieItem(block, () -> new Item.Settings())));
    public static final Block COOKIE_TABLE = register("cookie_table", () -> new CookieTableBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));

    private static RegistryObject<Block> register(final String name, Supplier<Block> block) {
        return register(name, block, (block1) -> new BlockItem(block1, () -> new Item.Properties()));
    }

    private static RegistryObject<Block> register(final String name, Supplier<Block> block, Function<Block, Item> itemRegisterer) {
        RegistryObject<Block> registeredBlock = registerWithoutItem(name, block);
        PBItems.ITEMS.register(name, () -> itemRegisterer.apply(registeredBlock.get()));
        return registeredBlock;
    }

    private static RegistryObject<Block> registerWithoutItem(String name, Supplier<Block> block) {
        return BLOCKS.register(name, block);
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering blocks");
    }
}
