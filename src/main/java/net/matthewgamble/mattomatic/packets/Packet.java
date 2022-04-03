package net.matthewgamble.mattomatic.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface Packet
{
    void encode(PacketBuffer buffer);

    void receiveMessage(Supplier<NetworkEvent.Context> context);
}
