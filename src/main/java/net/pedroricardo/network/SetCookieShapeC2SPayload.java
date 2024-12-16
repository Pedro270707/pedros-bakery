package net.pedroricardo.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.PedrosBakery;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public record SetCookieShapeC2SPayload(Set<Vector2i> shape) implements CustomPayload {
    public static final CustomPayload.Id<SetCookieShapeC2SPayload> ID = new CustomPayload.Id<>(Identifier.of(PedrosBakery.MOD_ID, "set_cookie_shape"));
    public static final PacketCodec<RegistryByteBuf, SetCookieShapeC2SPayload> CODEC = PacketCodec.tuple(PBCodecs.PACKET_VECTOR_2I.collect(PacketCodecs.toList()).xmap(HashSet::new, ArrayList::new), SetCookieShapeC2SPayload::shape, SetCookieShapeC2SPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
