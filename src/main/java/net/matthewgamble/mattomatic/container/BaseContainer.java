package net.matthewgamble.mattomatic.container;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class BaseContainer extends Container
{
    public BaseContainer(ContainerType<?> processQueueContainer, int containerId)
    {
        super(processQueueContainer, containerId);
    }

    protected void addPlayerInventorySlots(IItemHandler handler, int leftColPos, int topRowPos)
    {
        int invRows = 3;
        int invCols = 9;
        int invSlotSideLen = 18;

        // Add the three rows for the player's inventory.
        addSlotBox(handler, 9, leftColPos, topRowPos, invCols, invSlotSideLen, invRows, invSlotSideLen);
        topRowPos += 58;
        // Add another row for the player's hotbar inventory.
        addSlotRange(handler, 0, leftColPos, topRowPos, invCols, invSlotSideLen);
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int xAmount, int dx, int yAmount, int dy)
    {
        for (int i = 0; i < yAmount; i++) {
            index = addSlotRange(handler, index, x, y, xAmount, dx);
            y += dy;
        }

        return index;
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int xAmount, int dx)
    {
        for (int i = 0; i < xAmount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }

        return index;
    }
}
