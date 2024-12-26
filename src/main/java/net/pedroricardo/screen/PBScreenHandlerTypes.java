package net.pedroricardo.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.pedroricardo.PedrosBakery;

public class PBScreenHandlerTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, PedrosBakery.MOD_ID);

    public static final RegistryObject<MenuType<CookieTableScreenHandler>> COOKIE_TABLE = register("cookie_table", CookieTableScreenHandler::new);

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory) {
        return MENU_TYPES.register(id, () -> new MenuType<>(factory, FeatureFlags.VANILLA_SET));
    }

    public static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> register(String id, MenuType.MenuSupplier<T> factory, FeatureFlag... requiredFeatures) {
        return MENU_TYPES.register(id, () -> new MenuType<>(factory, FeatureFlags.REGISTRY.subset(requiredFeatures)));
    }
}
