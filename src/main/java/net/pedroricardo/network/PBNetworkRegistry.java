package net.pedroricardo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;

public class PBNetworkRegistry {
    public static void init() {
        PedrosBakery.LOGGER.debug("Initializing payload type and receiver registry");

        PayloadTypeRegistry.playS2C().register(SetCookieShapePayload.ID, SetCookieShapePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetCookieShapePayload.ID, SetCookieShapePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetCookiePixelC2SPayload.ID, SetCookiePixelC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(SetCookiePixelC2SPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setPixel(payload.pixel(), payload.value());
                context.responseSender().sendPacket(new SetCookieShapePayload(cookieTable.getCookieShape()));
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(SetCookieShapePayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(payload.shape());
                context.responseSender().sendPacket(new SetCookieShapePayload(cookieTable.getCookieShape()));
            }
        });
    }
}
