package net.matthewgamble.mattomatic.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class InsertOnlySlotItemHandler implements IItemHandler
{
    public InsertOnlySlotItemHandler()
    {
    }

    @Override
    public int getSlots()
    {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot == 0) {
            return ItemStack.EMPTY;
        }

        throw new IndexOutOfBoundsException("This handler only has one slot.");
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!this.isItemValid(slot, stack)) {
            return stack;
        }

        int slotLimit = this.getSlotLimit(slot);
        int stackCount = stack.getCount();

        if (stackCount < slotLimit) {
            return ItemStack.EMPTY;
        }

        return ItemHandlerHelper.copyStackWithSize(stack, stackCount - slotLimit);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return slot == 0;
    }
}
