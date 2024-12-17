package net.pedroricardo.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PBNetworkRegistry {
    public static final Identifier SET_COOKIE_SHAPE = new Identifier(PedrosBakery.MOD_ID, "set_cookie_shape");
    public static final Identifier TOGGLE_COOKIE_SHAPE = new Identifier(PedrosBakery.MOD_ID, "toggle_cookie_shape");

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering payload types and receivers");

        ServerPlayNetworking.registerGlobalReceiver(SET_COOKIE_SHAPE, (server, player, handler, buf, responseSender) -> {
            if (player.currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                Set<Vector2i> set = buf.decodeAsJson(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new));
                cookieTable.setCookieShape(set);
            }
        });

        ServerPlayNetworking.registerGlobalReceiver(TOGGLE_COOKIE_SHAPE, (server, player, handler, buf, responseSender) -> {
            if (player.currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                Vector2i pixel = buf.decodeAsJson(PBCodecs.VECTOR_2I);
                cookieTable.togglePixel(pixel);
                PacketByteBuf buf1 = PacketByteBufs.create();
                buf1.encodeAsJson(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new), cookieTable.getCookieShape());
                responseSender.sendPacket(SET_COOKIE_SHAPE, buf1);
            }
        });
    }
}
