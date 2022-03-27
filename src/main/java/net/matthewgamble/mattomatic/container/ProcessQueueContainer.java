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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ProcessQueueContainer extends BaseContainer
{
    private final ProcessQueueTile tileEntity;
    private final IItemHandlerModifiable playerInv;
    private final IIntArray data;
    private final ReadOnlyInventory queueInv;

    public ProcessQueueContainer(int containerId, ProcessQueueTile tileEntity, PlayerInventory playerInventory, IIntArray data, IItemHandler insertHandler, IItemHandler extractHandler)
    {
        super(ModContainers.PROCESS_QUEUE_CONTAINER.get(), containerId);

        this.tileEntity = tileEntity;
        this.tileEntity.addListener(this);

        this.playerInv = new InvWrapper(playerInventory);
        checkContainerDataCount(data, 6);
        this.data = data;

        this.addPlayerInventorySlots(this.playerInv, 8, 86);

        this.addSlot(new InsertOnlySlot(insertHandler, 0, 26, 53));
        this.addSlot(new ExtractOnlySlot(extractHandler, 1, 134, 53));

        this.queueInv = new ReadOnlyInventory(ProcessQueue.QUEUE_SIZE) {
            @Override
            public void setChanged()
            {
                super.setChanged();
                ProcessQueueContainer.this.slotsChanged(this);
            }
        };

        int x = 134;
        for (int i = 0; i < ProcessQueue.QUEUE_SIZE; i++) {
            this.addSlot(new ViewOnlySlot(this.queueInv, i, x, 17));
            x -= 18;
        }

        this.addDataSlots(this.data);

        this.handleQueueUpdate(this.tileEntity.getQueueItems());
    }

    public void handleQueueUpdate(Iterable<ItemStack> queue)
    {
        int slot = 0;
        for (ItemStack stack : queue) {
            this.queueInv.setItem(slot, stack);
            slot++;
        }

        for (; slot < ProcessQueue.QUEUE_SIZE; slot++) {
            this.queueInv.setItem(slot, ItemStack.EMPTY);
        }
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
            // TODO: This pos callable should be passed in through the constructor
            // Once the listener system is eliminated (in favour of passing in another inventory for the queue),
            // we'll no longer need to pass in the tile entity at all.
            IWorldPosCallable.create(world, this.tileEntity.getBlockPos()),
            player,
            ModBlocks.PROCESS_QUEUE.get()
        );
    }

    @Override
    public void removed(PlayerEntity player)
    {
        System.out.println("container removed");
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
    public boolean canDragTo(Slot slot)
    {
        return !(slot instanceof ViewOnlySlot);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index)
    {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack sourceStackCopy = sourceStack.copy();

        if (index < 36) {
            if (!this.moveItemStackTo(sourceStack, 36, 37, false)) {
                return ItemStack.EMPTY;
            }
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
