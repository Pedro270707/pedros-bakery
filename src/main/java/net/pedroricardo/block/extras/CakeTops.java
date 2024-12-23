package net.pedroricardo.block.extras;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.*;
import net.pedroricardo.PedrosBakery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class CakeTops {
    public static final ResourceKey<Registry<CakeTop>> REGISTRY_KEY = ResourceKey.createRegistryKey(new ResourceLocation(PedrosBakery.MOD_ID, "cake_top"));
    public static final DeferredRegister<CakeTop> CAKE_TOPS = DeferredRegister.create(REGISTRY_KEY, PedrosBakery.MOD_ID);
    public static Supplier<IForgeRegistry<CakeTop>> registrySupplier; // Note: registry supplier value may be NULL

    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void newRegistryEvent(@NotNull NewRegistryEvent event) {
        registrySupplier = event.create(new RegistryBuilder<CakeTop>()
                .setName(REGISTRY_KEY.location())
                .hasTags());
    }

    public static final RegistryObject<CakeTop> SUGAR = CAKE_TOPS.register("sugar", () -> new CakeTop(null, Ingredient.of(Items.SUGAR), 0xFFFDF4D8) {
        @Override
        public void onDrink(ItemStack stack, Level world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 50, 2));
        }
    });
    public static final RegistryObject<CakeTop> CHOCOLATE = CAKE_TOPS.register("chocolate", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.COCOA_BEANS), 0xFF594939) {
        @Override
        public void onDrink(ItemStack stack, Level world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addEffect(new MobEffectInstance(MobEffects.LUCK, 100, 0));
        }
    });
    public static final RegistryObject<CakeTop> SCULK = CAKE_TOPS.register("sculk", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.SCULK), 0xFF052A32) {
        @Override
        public void onDrink(ItemStack stack, Level world, LivingEntity user) {
            super.onDrink(stack, world, user);
            user.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
        }
    });
    public static final RegistryObject<CakeTop> CHORUS = CAKE_TOPS.register("chorus", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.CHORUS_FRUIT), 0xFFA381A2) {
        @Override
        public void onDrink(ItemStack stack, Level world, LivingEntity user) {
            super.onDrink(stack, world, user);
            if (!world.isClientSide()) {
                for (int i = 0; i < 16; ++i) {
                    SoundSource soundCategory;
                    SoundEvent soundEvent;
                    double d = user.getX() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                    double e = Mth.clamp(user.getY() + (double)(user.getRandom().nextInt(16) - 8), world.getMinBuildHeight(), (world.getMinBuildHeight() + ((ServerLevel) world).getLogicalHeight() - 1));
                    double f = user.getZ() + (user.getRandom().nextDouble() - 0.5) * 16.0;
                    if (user.isPassenger()) {
                        user.stopRiding();
                    }
                    Vec3 vec3d = user.position();
                    if (!user.randomTeleport(d, e, f, true)) continue;
                    world.gameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Context.of(user));
                    if (user instanceof Fox) {
                        soundEvent = SoundEvents.FOX_TELEPORT;
                        soundCategory = SoundSource.NEUTRAL;
                    } else {
                        soundEvent = SoundEvents.CHORUS_FRUIT_TELEPORT;
                        soundCategory = SoundSource.PLAYERS;
                    }
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory, 1.0f, 1.0f);
                    user.resetFallDistance();
                    break;
                }
            }
        }

        @Override
        public InteractionResult onTryEat(CakeBatter layer, Level world, BlockPos pos, BlockState state, Player player, BlockEntity cake) {
            if (world instanceof ServerLevel) {
                ResourceKey<Level> registryKey = world.dimension() == Level.END ? Level.OVERWORLD : Level.END;
                ServerLevel serverWorld = ((ServerLevel)world).getServer().getLevel(registryKey);
                if (serverWorld == null) {
                    return InteractionResult.PASS;
                }
                player.changeDimension(serverWorld);
            }
            return InteractionResult.SUCCESS;
        }
    });
    public static final RegistryObject<CakeTop> RED_MUSHROOM = CAKE_TOPS.register("red_mushroom", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.RED_MUSHROOM), 0xFFC92B29));
    public static final RegistryObject<CakeTop> BROWN_MUSHROOM = CAKE_TOPS.register("brown_mushroom", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.BROWN_MUSHROOM), 0xFF977251));
    public static final RegistryObject<CakeTop> SWEET_BERRY = CAKE_TOPS.register("sweet_berry", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.SWEET_BERRIES), 0xFFF6C9BD));
    public static final RegistryObject<CakeTop> DIRT = CAKE_TOPS.register("dirt", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.DIRT), 0xFF79553A));
    public static final RegistryObject<CakeTop> GRASS = CAKE_TOPS.register("grass", () -> new CakeTop(SUGAR.get(), Ingredient.of(Items.GRASS, Items.TALL_GRASS), 0xFF486E3E));

    @Nullable
    public static CakeTop from(ItemStack item) {
        if (item == null || item.getItem() == Items.AIR || registrySupplier.get() == null) {
            return null;
        }
        for (CakeTop flavor : registrySupplier.get().getValues().stream().toList()) {
            if (flavor.ingredient().test(item)) {
                return flavor;
            }
        }
        return null;
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing top registry");
    }
}