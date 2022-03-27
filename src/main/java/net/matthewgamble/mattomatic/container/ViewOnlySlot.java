package net.matthewgamble.mattomatic.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ViewOnlySlot extends Slot
{
    public ViewOnlySlot(IInventory inv, int slot, int x, int y)
    {
        super(inv, slot, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack)
    {
        return false;
    }

    @Override
    public boolean mayPickup(PlayerEntity player)
    {
        return false;
    }

//    @Override
//    public void set(ItemStack stack)
//    {
//    }

//    @Override
//    public ItemStack remove(int amount)
//    {
//        return ItemStack.EMPTY;
//    }
}
