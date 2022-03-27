package net.matthewgamble.mattomatic.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ExtractOnlySlot extends Slot
{
    private final IItemHandler itemHandler;
    private final int realSlot;

    public ExtractOnlySlot(IItemHandler itemHandler, int slot, int x, int y)
    {
        super(new Inventory(1), 0, x, y);
        this.itemHandler = itemHandler;
        this.realSlot = slot;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getItem()
    {
        return this.itemHandler.getStackInSlot(this.realSlot);
    }

//    @Override
//    public void set(ItemStack stack)
//    {
//    }

    @Override
    public ItemStack remove(int amount)
    {
        super.remove(amount);
        return this.itemHandler.extractItem(this.realSlot, amount, false);
    }

    @Override
    public void setChanged()
    {
        // It's expected that the item handler has already taken care of "on change" notifications.
    }

    @Override
    public int getMaxStackSize()
    {
        return this.itemHandler.getSlotLimit(this.realSlot);
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }
}
