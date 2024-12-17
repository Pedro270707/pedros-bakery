package net.pedroricardo.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;

public class PBNetworkRegistry {
    public static void init() {
        PedrosBakery.LOGGER.debug("Registering payload types and receivers");

        PayloadTypeRegistry.playS2C().register(SetCookieShapePayload.ID, SetCookieShapePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(SetCookieShapePayload.ID, SetCookieShapePayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ToggleCookiePixelC2SPayload.ID, ToggleCookiePixelC2SPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ToggleCookiePixelC2SPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.togglePixel(payload.pixel());
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
