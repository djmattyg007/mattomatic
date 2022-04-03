package net.matthewgamble.mattomatic;

import net.matthewgamble.mattomatic.packets.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MattomaticNet
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(MattomaticMod.MOD_ID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );

    public static void registerPackets()
    {
        int id = 0;

        INSTANCE.registerMessage(id++, MachineSideStateChangePacket.class, MachineSideStateChangePacket::encode, MachineSideStateChangePacket::decode, MachineSideStateChangePacket::receiveMessage);
    }

    public static void messageServer(Packet packet)
    {
        INSTANCE.sendToServer(packet);
    }
}
