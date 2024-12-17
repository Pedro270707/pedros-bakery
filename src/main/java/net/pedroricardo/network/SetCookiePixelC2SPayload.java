package net.pedroricardo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import org.joml.Vector2i;

public record SetCookiePixelC2SPayload(Vector2i pixel, boolean value) implements CustomPayload {
    public static final CustomPayload.Id<SetCookiePixelC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(PedrosBakery.MOD_ID, "set_cookie_pixel"));
    public static final PacketCodec<RegistryByteBuf, SetCookiePixelC2SPayload> CODEC = PacketCodec.tuple(PBCodecs.PACKET_VECTOR_2I, SetCookiePixelC2SPayload::pixel, PacketCodecs.BOOL, SetCookiePixelC2SPayload::value, SetCookiePixelC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
