package net.matthewgamble.mattomatic.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.List;
import java.util.function.Supplier;

public class ReadOnlyInventory implements IInventory
{
    private final Supplier<Iterable<ItemStack>> invSupplier;
    private final int size;
    private List<ItemStack> items;

    public ReadOnlyInventory(Supplier<Iterable<ItemStack>> inventorySupplier, int size)
    {
        this.invSupplier = inventorySupplier;
        this.size = size;

        refreshItems();
    }

    private void refreshItems()
    {
        System.out.println("readonly inv, refreshing items");
        this.items = NonNullList.withSize(size, ItemStack.EMPTY);

        Iterable<ItemStack> invItems = invSupplier.get();
        int idx = 0;
        for (ItemStack invItem : invItems) {
            System.out.println("refreshing items, idx " + idx + ", " + invItem.getCount() + " " + invItem.getDisplayName().plainCopy().getString());
            this.items.set(idx, invItem);
            idx++;
        }
    }

    @Override
    public int getContainerSize()
    {
        return size;
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

    @Override
    public ItemStack removeItem(int slot, int qty)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack)
    {
    }

    @Override
    public void setChanged()
    {
        refreshItems();
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
