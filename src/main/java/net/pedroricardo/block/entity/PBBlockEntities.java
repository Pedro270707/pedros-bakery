package net.pedroricardo.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.PBBlocks;

public class PBBlockEntities {
    public static final BlockEntityType<? extends PBCakeBlockEntity> CAKE = register(BlockEntityType.Builder.create(
            PBCakeBlockEntity::new,
            PBBlocks.CAKE,
            PBBlocks.CANDLE_CAKE,
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
            PBBlocks.BLACK_CANDLE_CAKE), "cake");

    public static final BlockEntityType<? extends PBCakeBlockEntityPart> CAKE_PART = register(BlockEntityType.Builder.create(
            PBCakeBlockEntityPart::new,
            PBBlocks.CAKE_PART), "cake_part");

    public static final BlockEntityType<? extends BeaterBlockEntity> BEATER = register(BlockEntityType.Builder.create(
            BeaterBlockEntity::new,
            PBBlocks.BEATER), "beater");

    public static final BlockEntityType<? extends BakingTrayBlockEntity> BAKING_TRAY = register(BlockEntityType.Builder.create(
            BakingTrayBlockEntity::new,
            PBBlocks.BAKING_TRAY), "baking_tray");

    public static final BlockEntityType<? extends BakingTrayBlockEntityPart> BAKING_TRAY_PART = register(BlockEntityType.Builder.create(
            BakingTrayBlockEntityPart::new,
            PBBlocks.BAKING_TRAY_PART), "baking_tray_part");

    public static final BlockEntityType<? extends CakeStandBlockEntity> CAKE_STAND = register(BlockEntityType.Builder.create(
            CakeStandBlockEntity::new,
            PBBlocks.CAKE_STAND), "cake_stand");

    private static <T extends BlockEntity> BlockEntityType<T> register(BlockEntityType.Builder<T> builder, String id) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(PedrosBakery.MOD_ID, id), builder.build());
    }
}