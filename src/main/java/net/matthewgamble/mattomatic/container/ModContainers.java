package net.matthewgamble.mattomatic.container;

import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.tileentity.ProcessQueueTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
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
        () -> IForgeContainerType.create((containerId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            World world = inv.player.level;

            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ProcessQueueTile) {
                return new ProcessQueueContainer(containerId, (ProcessQueueTile) tileEntity, inv, new IntArray(6));
            } else {
                throw new IllegalStateException("Incorrect or missing Process Queue tile entity.");
            }
        })
    );

    public static void register(IEventBus eventBus)
    {
        CONTAINERS.register(eventBus);
    }
}
