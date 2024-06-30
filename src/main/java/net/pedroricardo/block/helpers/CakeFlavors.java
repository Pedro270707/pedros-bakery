package net.pedroricardo.block.helpers;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.registry.DefaultedRegistry;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleDefaultedRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.block.entity.PBCakeBlockEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CakeFlavors {
    public static final RegistryKey<Registry<CakeFlavor>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(PedrosBakery.MOD_ID, "cake_flavor"));
    public static DefaultedRegistry<CakeFlavor> REGISTRY = FabricRegistryBuilder.from(new SimpleDefaultedRegistry<>(Identifier.of(PedrosBakery.MOD_ID, "vanilla").toString(), REGISTRY_KEY, Lifecycle.stable(), true)).buildAndRegister();

    private static final Map<ItemConvertible, CakeFlavor> ITEM_TO_FLAVOR = new HashMap<>();

    private static CakeFlavor register(String name, CakeFlavor flavor) {
        ITEM_TO_FLAVOR.put(flavor.item(), flavor);
        return Registry.register(REGISTRY, Identifier.of(PedrosBakery.MOD_ID, name), flavor);
    }

    public static final CakeFlavor VANILLA = register("vanilla", new CakeFlavor(null, Items.WHEAT));
    public static final CakeFlavor CHOCOLATE = register("chocolate", new CakeFlavor(VANILLA, Items.COCOA_BEANS));
    public static final CakeFlavor SWEET_BERRY = register("sweet_berry", new CakeFlavor(VANILLA, Items.SWEET_BERRIES));
    public static final CakeFlavor COAL = register("coal", new CakeFlavor(null, Items.COAL) {
        @Override
        public void onTryEat(CakeLayer layer, World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake) {
            super.onTryEat(layer, world, pos, state, player, cake);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 10, 2));
        }
    });
    public static final CakeFlavor TNT = register("tnt", new CakeFlavor(null, Items.TNT) {
        @Override
        public void onTryEat(CakeLayer layer, World world, BlockPos pos, BlockState state, PlayerEntity player, PBCakeBlockEntity cake) {
            super.onTryEat(layer, world, pos, state, player, cake);
            world.createExplosion(null, pos.toCenterPos().getX(), pos.toCenterPos().getY(), pos.toCenterPos().getZ(), (layer.getSize() * layer.getHeight()) / 64.0f, World.ExplosionSourceType.BLOCK);
        }
    });

    public static Optional<CakeFlavor> from(ItemConvertible item) {
        if (item == null || item == Items.AIR || ITEM_TO_FLAVOR.get(item) == null) {
            return Optional.empty();
        }
        return Optional.of(ITEM_TO_FLAVOR.get(item));
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing flavor registry");
    }
}
