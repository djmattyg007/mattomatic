package net.matthewgamble.mattomatic.container;

import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ExtractOnlySlot extends Slot
{
    private final IItemHandler itemHandler;

    public ExtractOnlySlot(IItemHandler itemHandler, int slot, int x, int y)
    {
        super(new Inventory(0), slot, x, y);
        this.itemHandler = itemHandler;
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
        return this.itemHandler.getStackInSlot(this.getSlotIndex());
    }

    @Override
    public void set(ItemStack stack)
    {
        if (this.itemHandler instanceof IItemHandlerModifiable) {
            ((IItemHandlerModifiable) this.itemHandler).setStackInSlot(this.getSlotIndex(), stack);
        }
    }

    @Override
    public ItemStack remove(int amount)
    {
        return this.itemHandler.extractItem(this.getSlotIndex(), amount, false);
    }

    @Override
    public void setChanged()
    {
        // It's expected that the item handler has already taken care of "on change" notifications.
    }

    @Override
    public int getMaxStackSize()
    {
        return this.itemHandler.getSlotLimit(this.getSlotIndex());
    }

    @Override
    public int getMaxStackSize(ItemStack stack)
    {
        return Math.min(this.getMaxStackSize(), stack.getMaxStackSize());
    }
}
