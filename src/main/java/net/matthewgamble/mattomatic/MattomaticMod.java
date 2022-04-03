package net.matthewgamble.mattomatic;

import net.matthewgamble.mattomatic.block.ModBlocks;
import net.matthewgamble.mattomatic.container.ModContainers;
import net.matthewgamble.mattomatic.item.ModItems;
import net.matthewgamble.mattomatic.screen.ProcessQueueScreen;
import net.matthewgamble.mattomatic.tileentity.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MattomaticMod.MOD_ID)
public class MattomaticMod
{
    public static final String MOD_ID = "mattomatic";

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public MattomaticMod()
    {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(eventBus);
        ModBlocks.register(eventBus);
        ModTileEntities.register(eventBus);
        ModContainers.register(eventBus);

        eventBus.addListener(this::setup);
        // Register the doClientStuff method for modloading
        eventBus.addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static String modId(String key)
    {
        return modId(":", key);
    }

    public static String modId(String sep, String key)
    {
        return MOD_ID + sep + key;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            MattomaticNet.registerPackets();
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().options);

        event.enqueueWork(() -> {
            ScreenManager.register(
                ModContainers.PROCESS_QUEUE_CONTAINER.get(),
                ProcessQueueScreen::new
            );
        });
    }
}
