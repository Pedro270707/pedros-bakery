package net.pedroricardo.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Supplier;

public class SetCookiePixelPacket {
    private final Vector2i pixel;
    private final boolean value;

    public SetCookiePixelPacket(FriendlyByteBuf buf) {
        this.pixel = buf.readJsonWithCodec(PBCodecs.VECTOR_2I);
        this.value = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(PBCodecs.VECTOR_2I, this.pixel);
        buf.writeBoolean(this.value);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isServer() && context.get().getSender() != null && context.get().getSender().containerMenu instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setPixel(this.pixel, this.value);
                PBNetworkRegistry.INSTANCE.send(PacketDistributor.PLAYER.with(() -> context.get().getSender()), new SetCookieShapePacket(cookieTable.getCookieShape()));
            }
        });

        context.get().setPacketHandled(true);
    }
}
