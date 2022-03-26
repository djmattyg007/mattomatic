package net.matthewgamble.mattomatic.tileentity;

import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities
{
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
        DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MattomaticMod.MOD_ID);

    public static final RegistryObject<TileEntityType<ProcessQueueTile>> PROCESS_QUEUE_TILE =
        TILE_ENTITIES.register("process_queue_tile", () -> TileEntityType.Builder.of(
            ProcessQueueTile::new, ModBlocks.PROCESS_QUEUE.get()
        ).build(null));

    public static void register(IEventBus eventBus)
    {
        TILE_ENTITIES.register(eventBus);
    }
}
