package net.pedroricardo.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PBNetworkRegistry {
    private static final String PROTOCOL_VERSION = "1.0";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PedrosBakery.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION::equals),
            NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION::equals)
    );
    private static int ID;
    private static int nextID() {
        return ID++;
    }

    public static void init() {
        PedrosBakery.LOGGER.debug("Registering payload types and receivers");
        INSTANCE.registerMessage(nextID(), SetCookieShapePacket.class, SetCookieShapePacket::toBytes, SetCookieShapePacket::new, SetCookieShapePacket::handle);
        INSTANCE.registerMessage(nextID(), SetCookiePixelPacket.class, SetCookiePixelPacket::toBytes, SetCookiePixelPacket::new, SetCookiePixelPacket::handle);
    }
}
