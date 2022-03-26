package net.matthewgamble.mattomatic.item;

import net.matthewgamble.mattomatic.MattomaticMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
        ForgeRegistries.ITEMS,
        MattomaticMod.MOD_ID
    );

    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
