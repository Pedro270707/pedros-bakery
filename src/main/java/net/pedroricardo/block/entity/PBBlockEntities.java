package net.pedroricardo.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;

public class PBBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PedrosBakery.MOD_ID);

    public static final RegistryObject<BlockEntityType<? extends PBCakeBlockEntity>> CAKE = BLOCK_ENTITY_TYPES.register("cake", () -> BlockEntityType.Builder.of(
            PBCakeBlockEntity::new,
            PBBlocks.CAKE.get(),
            PBBlocks.CANDLE_CAKE.get(),
            PBBlocks.WHITE_CANDLE_CAKE.get(),
            PBBlocks.ORANGE_CANDLE_CAKE.get(),
            PBBlocks.MAGENTA_CANDLE_CAKE.get(),
            PBBlocks.LIGHT_BLUE_CANDLE_CAKE.get(),
            PBBlocks.YELLOW_CANDLE_CAKE.get(),
            PBBlocks.LIME_CANDLE_CAKE.get(),
            PBBlocks.PINK_CANDLE_CAKE.get(),
            PBBlocks.GRAY_CANDLE_CAKE.get(),
            PBBlocks.LIGHT_GRAY_CANDLE_CAKE.get(),
            PBBlocks.CYAN_CANDLE_CAKE.get(),
            PBBlocks.PURPLE_CANDLE_CAKE.get(),
            PBBlocks.BLUE_CANDLE_CAKE.get(),
            PBBlocks.BROWN_CANDLE_CAKE.get(),
            PBBlocks.GREEN_CANDLE_CAKE.get(),
            PBBlocks.RED_CANDLE_CAKE.get(),
            PBBlocks.BLACK_CANDLE_CAKE.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends PBCakeBlockEntityPart>> CAKE_PART = BLOCK_ENTITY_TYPES.register("cake_part", () -> BlockEntityType.Builder.of(
            PBCakeBlockEntityPart::new,
            PBBlocks.CAKE_PART.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends BeaterBlockEntity>> BEATER = BLOCK_ENTITY_TYPES.register("beater", () -> BlockEntityType.Builder.of(
            BeaterBlockEntity::new,
            PBBlocks.BEATER.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends BakingTrayBlockEntity>> BAKING_TRAY = BLOCK_ENTITY_TYPES.register("baking_tray", () -> BlockEntityType.Builder.of(
            BakingTrayBlockEntity::new,
            PBBlocks.BAKING_TRAY.get(),
            PBBlocks.EXPANDABLE_BAKING_TRAY.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends BakingTrayBlockEntityPart>> BAKING_TRAY_PART = BLOCK_ENTITY_TYPES.register("baking_tray_part", () -> BlockEntityType.Builder.of(
            BakingTrayBlockEntityPart::new,
            PBBlocks.BAKING_TRAY_PART.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends CakeStandBlockEntity>> CAKE_STAND = BLOCK_ENTITY_TYPES.register("cake_stand", () -> BlockEntityType.Builder.of(
            CakeStandBlockEntity::new,
            PBBlocks.CAKE_STAND.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends PlateBlockEntity>> PLATE = BLOCK_ENTITY_TYPES.register("plate", () -> BlockEntityType.Builder.of(
            PlateBlockEntity::new,
            PBBlocks.PLATE.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends CupcakeTrayBlockEntity>> CUPCAKE_TRAY = BLOCK_ENTITY_TYPES.register("cupcake_tray", () -> BlockEntityType.Builder.of(
            CupcakeTrayBlockEntity::new,
            PBBlocks.CUPCAKE_TRAY.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends CupcakeBlockEntity>> CUPCAKE = BLOCK_ENTITY_TYPES.register("cupcake", () -> BlockEntityType.Builder.of(
            CupcakeBlockEntity::new,
            PBBlocks.CUPCAKE.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends CookieJarBlockEntity>> COOKIE_JAR = BLOCK_ENTITY_TYPES.register("cookie_jar", () -> BlockEntityType.Builder.of(
            CookieJarBlockEntity::new,
            PBBlocks.COOKIE_JAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<? extends PieBlockEntity>> PIE = BLOCK_ENTITY_TYPES.register("pie", () -> BlockEntityType.Builder.of(
            PieBlockEntity::new,
            PBBlocks.PIE.get()).build(null));
}