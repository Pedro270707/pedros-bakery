package net.pedroricardo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import org.joml.Vector2i;

public record ToggleCookiePixelC2SPayload(Vector2i pixel) implements CustomPayload {
    public static final CustomPayload.Id<ToggleCookiePixelC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(PedrosBakery.MOD_ID, "toggle_cookie_pixel"));
    public static final PacketCodec<RegistryByteBuf, ToggleCookiePixelC2SPayload> CODEC = PacketCodec.tuple(PBCodecs.PACKET_VECTOR_2I, ToggleCookiePixelC2SPayload::pixel, ToggleCookiePixelC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
