package net.pedroricardo;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.block.PBBlocks;
import net.pedroricardo.block.PBCakeBlock;
import net.pedroricardo.block.extras.CakeBatter;
import net.pedroricardo.block.extras.CakeFeatures;
import net.pedroricardo.block.extras.CakeTop;
import net.pedroricardo.block.extras.CakeTops;
import net.pedroricardo.item.PBComponentTypes;
import net.pedroricardo.item.PBItems;

import java.util.Collections;

public class PBCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PedrosBakery.MOD_ID);
    public static final RegistryObject<CreativeModeTab> PEDROS_BAKERY = CREATIVE_MODE_TABS.register("deeper_darker", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + PedrosBakery.MOD_ID)).icon(() -> {
        CakeBatter layer = CakeBatter.getFullSizeDefault();
        layer.withBakeTime(PedrosBakery.CONFIG.ticksUntilCakeBaked.get());
        layer.withTop(CakeTops.SUGAR.get());
        layer.withFeature(CakeFeatures.SWEET_BERRIES.get());
        return PBCakeBlock.of(Collections.singletonList(layer));
    }).build());
    public static final RegistryObject<CreativeModeTab> BAKING_TRAYS = CREATIVE_MODE_TABS.register("deeper_darker", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup." + PedrosBakery.MOD_ID)).icon(() -> {
        ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.get().asItem());
        PBHelpers.set(stack, PBComponentTypes.SIZE.get(), 14);
        PBHelpers.set(stack, PBComponentTypes.HEIGHT.get(), 8);
        return stack;
    }).build());

    public static void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == PEDROS_BAKERY.get()) {
            event.accept(new ItemStack(PBBlocks.BEATER.get()));
            addFrostingBottles(event);
            event.accept(new ItemStack(PBBlocks.CAKE_STAND.get()));
            event.accept(new ItemStack(PBBlocks.PLATE.get()));
            event.accept(new ItemStack(PBBlocks.EXPANDABLE_BAKING_TRAY.get()));
            event.accept(new ItemStack(PBBlocks.CUPCAKE.get()));
            event.accept(new ItemStack(PBBlocks.CUPCAKE_TRAY.get()));
            event.accept(new ItemStack(PBBlocks.COOKIE_JAR.get()));
            event.accept(new ItemStack(PBBlocks.BUTTER_CHURN.get()));
            event.accept(new ItemStack(PBBlocks.PIE.get()));
            event.accept(new ItemStack(PBItems.WHITE_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.ORANGE_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.MAGENTA_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.LIGHT_BLUE_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.YELLOW_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.LIME_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.PINK_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.GRAY_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.LIGHT_GRAY_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.CYAN_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.PURPLE_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.BLUE_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.BROWN_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.GREEN_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.RED_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.BLACK_SPRINKLES.get()));
            event.accept(new ItemStack(PBItems.DONUT.get()));
            event.accept(new ItemStack(PBItems.APPLE_COOKIE.get()));
            event.accept(new ItemStack(PBItems.BUTTER.get()));
            event.accept(new ItemStack(PBItems.BUTTER_CHURN_STAFF.get()));
            event.accept(new ItemStack(PBItems.DOUGH.get()));
            event.accept(new ItemStack(PBItems.SHAPED_COOKIE.get()));
        } else if (event.getTab() == BAKING_TRAYS.get()) {
            addBakingTrays(event);
        }
    }

    private static void addFrostingBottles(BuildCreativeModeTabContentsEvent event) {
        for (CakeTop top : CakeTops.registrySupplier.get().getValues().stream().toList()) {
            ItemStack stack = new ItemStack(PBItems.FROSTING_BOTTLE.get());
            PBHelpers.set(stack, PBComponentTypes.TOP.get(), top);
            event.accept(stack);
        }
    }

    private static void addBakingTrays(BuildCreativeModeTabContentsEvent event) {
        for (int w = 8; w <= 16; ++w) {
            for (int h = 8; h <= 16; ++h) {
                ItemStack stack = new ItemStack(PBBlocks.BAKING_TRAY.get().asItem());
                PBHelpers.set(stack, PBComponentTypes.SIZE.get(), w);
                PBHelpers.set(stack, PBComponentTypes.HEIGHT.get(), h);
                event.accept(stack);
            }
        }
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering item group");
    }
}
