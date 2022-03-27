package net.matthewgamble.mattomatic.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class ExtractOnlySlotItemHandler implements IItemHandlerModifiable
{
    private final int slotId;
    private ItemStack stack = ItemStack.EMPTY;

    public ExtractOnlySlotItemHandler(int slotId)
    {
        this.slotId = slotId;
    }

    public ExtractOnlySlotItemHandler(int slotId, ItemStack initialStack)
    {
        this.slotId = slotId;
        this.stack = initialStack;
    }

    @Override
    public int getSlots()
    {
        return this.slotId + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot == this.slotId) {
            return this.stack;
        } else if (slot >= 0 && slot < this.slotId) {
            return ItemStack.EMPTY;
        }

        throw new IndexOutOfBoundsException("This handler only has one slot.");
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        if (slot == this.slotId) {
            this.stack = stack;
            return;
        } else if (slot >= 0 && slot < this.slotId) {
            return;
        }

        throw new IndexOutOfBoundsException("This handler only has one slot.");
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        return stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (this.stack.isEmpty() || amount <= 0 || slot != this.slotId) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (this.stack.getCount() < amount) {
                return this.stack.copy();
            } else {
                ItemStack copy = this.stack.copy();
                copy.setCount(amount);
                return copy;
            }
        }

        int extractCount = Math.min(this.stack.getCount(), amount);
        ItemStack extracted = this.stack.split(extractCount);
        return extracted;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return false;
    }
}
