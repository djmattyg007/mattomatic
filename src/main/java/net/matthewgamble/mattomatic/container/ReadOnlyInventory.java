package net.matthewgamble.mattomatic.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.function.Supplier;

public class ReadOnlyInventory implements IInventory
{
    //private final Supplier<Iterable<ItemStack>> invSupplier;
    private final int size;
    private final List<ItemStack> items;

    public ReadOnlyInventory(/*Supplier<Iterable<ItemStack>> inventorySupplier, */int size)
    {
        //this.invSupplier = inventorySupplier;
        this.size = size;
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);

        //refreshItems();
    }

//    private void refreshItems()
//    {
//        System.out.println("readonly inv, refreshing items");
//        this.items = NonNullList.withSize(size, ItemStack.EMPTY);
//
//        Iterable<ItemStack> invItems = invSupplier.get();
//        int idx = 0;
//        for (ItemStack invItem : invItems) {
//            System.out.println("refreshing items, idx " + idx + ", " + invItem.getCount() + " " + invItem.getDisplayName().plainCopy().getString());
//            this.items.set(idx, invItem);
//            idx++;
//        }
//    }

    @Override
    public int getContainerSize()
    {
        return this.size;
    }

    @Override
    public boolean isEmpty()
    {
        for (ItemStack stack : items) {
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

//    @Override
//    public ItemStack removeItem(int slot, int qty)
//    {
//        return ItemStack.EMPTY;
//    }

    @Override
    public ItemStack removeItem(int p_70298_1_, int p_70298_2_)
    {
        ItemStack itemstack = ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

//    @Override
//    public ItemStack removeItemNoUpdate(int slot)
//    {
//        return ItemStack.EMPTY;
//    }

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
        //refreshItems();
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
