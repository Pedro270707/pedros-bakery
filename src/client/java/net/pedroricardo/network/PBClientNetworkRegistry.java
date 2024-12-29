package net.pedroricardo.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;

public class PBClientNetworkRegistry {
    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing client payload type and receiver registry");

        ClientPlayNetworking.registerGlobalReceiver(SetCookieShapePayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(payload.shape());
            }
        });
    }
}
