package net.pedroricardo.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtOps;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PBClientNetworkRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PBNetworkRegistry.TOGGLE_COOKIE_SHAPE, (client, handler, buf, responseSender) -> {
            if (client.player != null && client.player.currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                Set<Vector2i> set = buf.decodeAsJson(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new));
                cookieTable.setCookieShape(set);
            }
        });
    }
}
