package net.matthewgamble.mattomatic.container;

import net.matthewgamble.mattomatic.block.ModBlocks;
import net.matthewgamble.mattomatic.tileentity.ProcessQueue;
import net.matthewgamble.mattomatic.tileentity.ProcessQueueTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.function.Supplier;

public class ProcessQueueContainer extends BaseContainer
{
    private final ProcessQueueTile tileEntity;
    private final IItemHandlerModifiable playerInv;
    private final IIntArray data;
    private Runnable listener;

    public ProcessQueueContainer(int containerId, ProcessQueueTile tileEntity, PlayerInventory playerInventory, IIntArray data)
    {
        super(ModContainers.PROCESS_QUEUE_CONTAINER.get(), containerId);

        this.tileEntity = tileEntity;
        this.tileEntity.addListener(this);

        this.playerInv = new InvWrapper(playerInventory);
        checkContainerDataCount(data, 6);
        this.data = data;

        this.addPlayerInventorySlots(this.playerInv, 8, 86);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            addSlot(new InsertOnlySlot(h, 0, 26, 53));
            addSlot(new ExtractOnlySlot(h, 1, 134, 53));
        });

        Supplier<Iterable<ItemStack>> queueSupplier = this.tileEntity::getQueueItems;
        ReadOnlyInventory queueInv = new ReadOnlyInventory(queueSupplier, ProcessQueue.QUEUE_SIZE);

        int x = 134;
        for (int i = 0; i < ProcessQueue.QUEUE_SIZE; i++) {
            addSlot(new ReadOnlySlot(queueInv, i, x, 17));
            x -= 18;
        }

        addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        World world = player.level;
        if (world == null) {
            return false;
        }

        if (this.tileEntity.isRemoved()) {
            return false;
        }

        return stillValid(
            IWorldPosCallable.create(world, this.tileEntity.getBlockPos()),
            player,
            ModBlocks.PROCESS_QUEUE.get()
        );
    }

    public void setListener(Runnable listener)
    {
        this.listener = listener;
    }

    public void onChanged()
    {
        if (this.listener != null) {
            this.listener.run();
        }
    }

    @Override
    public void removed(PlayerEntity player)
    {
        super.removed(player);
        if (!player.level.isClientSide) {
            this.tileEntity.removeListener(this);
        }
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot)
    {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index)
    {
        System.out.println("quick move, index: " + index);
        System.out.println("quick move, total slots: " + this.slots.size());
        Slot sourceSlot = this.slots.get(index);
        System.out.println("quick move, slot is " + (sourceSlot == null ? "null" : "not null"));
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            if (sourceSlot != null) {
                System.out.println("quick move, slot has " + (sourceSlot.hasItem() ? "item" : "no item"));
            }
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack sourceStackCopy = sourceStack.copy();
        System.out.println("quick move, source slot contains " + sourceStack.getCount() + " " + sourceStack.getDisplayName().plainCopy().getString());

        if (index < 36) {
            System.out.println("quick move, moving from player inventory");
            if (!this.moveItemStackTo(sourceStack, 36, 37, false)) {
                System.out.println("quick move, guess it didn't work");
                return ItemStack.EMPTY;
            }
            System.out.println("quick move, cool beans");
        } else if (index == 36) {
            return ItemStack.EMPTY;
        } else if (index == 37) {
            if (!this.moveItemStackTo(sourceStack, 0, 36, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < (36 + 2 + ProcessQueue.QUEUE_SIZE)) {
            return ItemStack.EMPTY;
        } else {
            System.err.println("Invalid slot index in Process Queue container: " + index);
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        }  else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return sourceStackCopy;
    }
}
