package net.pedroricardo.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.pedroricardo.PBCodecs;
import net.pedroricardo.screen.CookieTableScreenHandler;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class SetCookieShapePacket {
    private final Set<Vector2i> shape;

    public SetCookieShapePacket(Set<Vector2i> shape) {
        this.shape = shape;
    }

    public SetCookieShapePacket(FriendlyByteBuf buf) {
        this.shape = buf.readJsonWithCodec(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new));
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeJsonWithCodec(PBCodecs.VECTOR_2I.listOf().xmap(HashSet::new, ArrayList::new), this.shape);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (context.get().getDirection().getReceptionSide().isServer() && context.get().getSender() != null && context.get().getSender().containerMenu instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(this.shape);
            } else if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.containerMenu instanceof CookieTableScreenHandler cookieTable) {
                cookieTable.setCookieShape(this.shape);
            }
        });

        context.get().setPacketHandled(true);
    }
}
