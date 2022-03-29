package net.matthewgamble.mattomatic.tileentity;

import net.minecraft.inventory.IInventory;

public interface IQueueInventory extends IInventory
{
    int getQueueLength();
}
