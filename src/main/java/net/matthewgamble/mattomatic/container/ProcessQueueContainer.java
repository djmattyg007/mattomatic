package net.matthewgamble.mattomatic.container;

import net.matthewgamble.mattomatic.block.ModBlocks;
import net.matthewgamble.mattomatic.tileentity.IQueueInventory;
import net.matthewgamble.mattomatic.tileentity.MachineSideState;
import net.matthewgamble.mattomatic.tileentity.ProcessQueue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ProcessQueueContainer extends BaseContainer
{
    private final IWorldPosCallable blockLookup;
    private final IIntArray data;
    private final IInventory queueInv;

    public ProcessQueueContainer(int containerId, PlayerInventory playerInventory, IWorldPosCallable blockLookup, IIntArray data, IItemHandler insertHandler, IItemHandler extractHandler, IQueueInventory queueInv)
    {
        super(ModContainers.PROCESS_QUEUE_CONTAINER.get(), containerId);

        this.blockLookup = blockLookup;

        checkContainerDataCount(data, 6);
        this.data = data;

        this.addPlayerInventorySlots(new InvWrapper(playerInventory), 8, 86);

        this.addSlot(new InsertOnlySlot(insertHandler, 0, 26, 53) {
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                if (queueInv.getQueueLength() >= ProcessQueue.QUEUE_SIZE) {
                    return false;
                }

                return super.mayPlace(stack);
            }

            @Override
            public void setChanged()
            {
                ProcessQueueContainer.this.broadcastChanges();
            }
        });
        this.addSlot(new ExtractOnlySlot(extractHandler, 1, 134, 53));

        this.queueInv = queueInv;

        int x = 134;
        for (int i = 0; i < ProcessQueue.QUEUE_SIZE; i++) {
            this.addSlot(new ViewOnlySlot(this.queueInv, i, x, 17));
            x -= 18;
        }

        this.addDataSlots(this.data);
    }

    @Override
    public boolean stillValid(PlayerEntity player)
    {
        World world = player.level;
        if (world == null) {
            return false;
        }

        return stillValid(
            this.blockLookup,
            player,
            ModBlocks.PROCESS_QUEUE.get()
        );
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

    public MachineSideState getSideState(int sideId)
    {
        int stateId = this.data.get(sideId);
        return MachineSideState.fromStateId(stateId);
    }

    public void setSideState(int sideId, int stateId)
    {
        this.data.set(sideId, stateId);
    }

    public void setSideStateToNext(int sideId)
    {
        int currentSideState = this.getSideState(sideId).getValue();
        int nextSideState = (currentSideState % 3) + 1;
        this.setSideState(sideId, nextSideState);
    }
}
