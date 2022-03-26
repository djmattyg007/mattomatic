package net.matthewgamble.mattomatic.block;

import net.matthewgamble.mattomatic.MattomaticMod;
import net.matthewgamble.mattomatic.item.ModItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(
        ForgeRegistries.BLOCKS,
        MattomaticMod.MOD_ID
    );

    private static final AbstractBlock.Properties standardMachineProperties = AbstractBlock.Properties.of(Material.METAL)
        .strength(15f)
        .harvestLevel(2)
        .harvestTool(ToolType.PICKAXE)
        .requiresCorrectToolForDrops()
        .isValidSpawn(ModBlocks::never);

    public static final RegistryObject<Block> PROCESS_QUEUE = registerBlock(
        "process_queue",
        () -> new ProcesssQueueBlock(standardMachineProperties)
    );

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block)
    {
        ModItems.ITEMS.register(name, () -> new BlockItem(
            block.get(),
            new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)
        ));
    }

    private static Boolean never(BlockState state, IBlockReader reader, BlockPos pos, EntityType<?> entityType)
    {
        // Duplicated from net.minecraft.block.Blocks
        return false;
    }
}
