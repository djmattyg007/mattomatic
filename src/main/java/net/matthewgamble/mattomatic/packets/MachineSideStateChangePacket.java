package net.matthewgamble.mattomatic.packets;

import net.matthewgamble.mattomatic.container.ProcessQueueContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MachineSideStateChangePacket implements Packet
{
    private final int sideId;
    private final int stateId;

    public MachineSideStateChangePacket(int sideId, int stateId)
    {
        this.sideId = sideId;
        this.stateId = stateId;
    }

    public static MachineSideStateChangePacket setToNext(int sideId)
    {
        return new MachineSideStateChangePacket(sideId, -1);
    }

    public static MachineSideStateChangePacket setToPrev(int sideId)
    {
        return new MachineSideStateChangePacket(sideId, -2);
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(this.sideId);
        buffer.writeInt(this.stateId);
    }

    public static MachineSideStateChangePacket decode(PacketBuffer buffer)
    {
        return new MachineSideStateChangePacket(
            buffer.readInt(),
            buffer.readInt()
        );
    }

    public void receiveMessage(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity sender = context.get().getSender();
            if (sender != null && sender.containerMenu instanceof ProcessQueueContainer) {
                ProcessQueueContainer menu = (ProcessQueueContainer) sender.containerMenu;
                switch (this.stateId) {
                    case -1:
                        menu.setSideStateToNext(this.sideId);
                        break;
                    case -2:
                        menu.setSideStateToPrev(this.sideId);
                        break;
                    default:
                        menu.setSideState(this.sideId, this.stateId);
                        break;
                }
            }
        });

        context.get().setPacketHandled(true);
    }
}
