package net.matthewgamble.mattomatic.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class InsertOnlySlot extends Slot
{
    private final IItemHandler itemHandler;

    public InsertOnlySlot(IItemHandler itemHandler, int slot, int x, int y)
    {
        super(new Inventory(0), slot, x, y);
        this.itemHandler = itemHandler;
    }

    @Override
    public boolean mayPickup(PlayerEntity player)
    {
        return false;
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        ItemStack remainder = this.itemHandler.insertItem(this.getSlotIndex(), stack, true);
        return !remainder.equals(stack, true);
    }

    @Override
    @Nonnull
    public ItemStack getItem()
    {
        return ItemStack.EMPTY;
    }

    @Override
    public void set(ItemStack stack)
    {
        this.itemHandler.insertItem(this.getSlotIndex(), stack, false);
    }

    @Override
    public ItemStack remove(int amount)
    {
        return ItemStack.EMPTY;
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
