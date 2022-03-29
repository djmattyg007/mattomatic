package net.matthewgamble.mattomatic.container;

import net.matthewgamble.mattomatic.tileentity.IQueueInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;

public class ClientReadOnlyInventory implements IQueueInventory
{
    private final int size;
    private final List<ItemStack> items;

    public ClientReadOnlyInventory(int size)
    {
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize()
    {
        return this.size;
    }

    @Override
    public int getQueueLength()
    {
        int nonEmptySlots = 0;

        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                nonEmptySlots++;
            }
        }

        return nonEmptySlots;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return this.items.get(slot);
    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_)
    {
        ItemStack itemstack = ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_70304_1_)
    {
        ItemStack itemstack = this.items.get(p_70304_1_);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(p_70304_1_, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
        this.items.set(slot, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        this.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack)
    {
        return false;
    }

    @Override
    public void setChanged()
    {
    }

    @Override
    public boolean stillValid(PlayerEntity p_70300_1_)
    {
        return true;
    }

    @Override
    public void clearContent()
    {
    }
}
