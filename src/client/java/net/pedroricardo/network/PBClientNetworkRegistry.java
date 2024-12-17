package net.pedroricardo.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.pedroricardo.screen.CookieTableScreenHandler;

public class PBClientNetworkRegistry {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SetCookieShapeS2CPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(payload.shape());
            }
        });
    }
}
