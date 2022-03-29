package net.matthewgamble.mattomatic.container;

import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.tileentity.ProcessQueue;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers
{
    public static DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(
        ForgeRegistries.CONTAINERS,
        MattomaticMod.MOD_ID
    );

    public static final RegistryObject<ContainerType<ProcessQueueContainer>> PROCESS_QUEUE_CONTAINER = CONTAINERS.register(
        "process_queue_container",
        () -> IForgeContainerType.create((containerId, playerInventory, data) -> {
            BlockPos pos = data.readBlockPos();
            World world = playerInventory.player.level;
            IWorldPosCallable blockLookup = IWorldPosCallable.create(world, pos);

            return new ProcessQueueContainer(
                containerId,
                playerInventory,
                blockLookup,
                new IntArray(6),
                new InsertOnlySlotItemHandler(),
                new ExtractOnlySlotItemHandler(1),
                new ClientReadOnlyInventory(ProcessQueue.QUEUE_SIZE)
            );
        })
    );

    public static void register(IEventBus eventBus)
    {
        CONTAINERS.register(eventBus);
    }
}
