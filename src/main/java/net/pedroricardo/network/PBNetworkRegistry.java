package net.pedroricardo.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;

import java.util.ArrayList;
import java.util.HashSet;

public class PBNetworkRegistry {
    public static final Identifier SET_COOKIE_SHAPE = new Identifier(PedrosBakery.MOD_ID, "set_cookie_shape");

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering payload types and receivers");

        ServerPlayNetworking.registerGlobalReceiver(SET_COOKIE_SHAPE, (server, player, handler, buf, responseSender) -> {
            if (player.currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new).decode(NbtOps.INSTANCE, buf.readNbt()).get().left().ifPresent(pair -> {
                    cookieTable.setCookieShape(pair.getFirst());
                });
            }
        });
    }
}
