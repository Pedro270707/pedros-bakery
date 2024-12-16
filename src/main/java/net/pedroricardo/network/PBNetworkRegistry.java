package net.pedroricardo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;

public class PBNetworkRegistry {
    public static void init() {
        PedrosBakery.LOGGER.debug("Registering payload types and receivers");

        PayloadTypeRegistry.playC2S().register(SetCookieShapeC2SPayload.ID, SetCookieShapeC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetCookieShapeC2SPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(payload.shape());
            }
        });
    }
}
