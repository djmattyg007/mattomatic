package net.matthewgamble.mattomatic.tileentity;

import net.matthewgamble.mattomatic.block.Fullness;
import net.matthewgamble.mattomatic.block.ProcesssQueueBlock;
import net.matthewgamble.mattomatic.container.ProcessQueueContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public class ProcessQueueTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity
{
    public static final ITextComponent NAME = new TranslationTextComponent("screen.mattomatic.process_queue");

    private ItemStack outputStack = ItemStack.EMPTY;

    private LinkedList<ItemStack> queue = makeQueue();

    private final Map<Direction, MachineSideState> sideConfig = makeSideConfig();

    private int cooldownTime = -1;

    private final IIntArray dataAccess = new IIntArray() {
        public int get(int sideId)
        {
            switch (sideId) {
                case 0:
                    return sideConfig.get(Direction.UP).getValue();
                case 1:
                    return sideConfig.get(Direction.DOWN).getValue();
                case 2:
                    return sideConfig.get(Direction.NORTH).getValue();
                case 3:
                    return sideConfig.get(Direction.SOUTH).getValue();
                case 4:
                    return sideConfig.get(Direction.EAST).getValue();
                case 5:
                    return sideConfig.get(Direction.WEST).getValue();
                default:
                    return MachineSideState.INACTIVE.getValue();
            }
        }

        public void set(int sideId, int stateId)
        {
            MachineSideState state = MachineSideState.fromStateId(stateId);
            switch (sideId) {
                case 0:
                    sideConfig.put(Direction.UP, state);
                    break;
                case 1:
                    sideConfig.put(Direction.DOWN, state);
                    break;
                case 2:
                    sideConfig.put(Direction.NORTH, state);
                    break;
                case 3:
                    sideConfig.put(Direction.SOUTH, state);
                    break;
                case 4:
                    sideConfig.put(Direction.EAST, state);
                    break;
                case 5:
                    sideConfig.put(Direction.WEST, state);
                    break;
            }
        }

        @Override
        public int getCount()
        {
            return 6;
        }
    };

    public ProcessQueueTile(TileEntityType<?> tileEntityTypeIn)
    {
        super(tileEntityTypeIn);
    }

    public ProcessQueueTile()
    {
        this(ModTileEntities.PROCESS_QUEUE_TILE.get());
    }

    private static LinkedList<ItemStack> makeQueue()
    {
        return new LinkedList<>();
    }

    private static Map<Direction, MachineSideState> makeSideConfig()
    {
        EnumMap<Direction, MachineSideState> sideConfig = new EnumMap<>(Direction.class);
        sideConfig.put(Direction.UP, MachineSideState.INPUT);
        sideConfig.put(Direction.DOWN, MachineSideState.OUTPUT);
        sideConfig.put(Direction.NORTH, MachineSideState.INACTIVE);
        sideConfig.put(Direction.SOUTH, MachineSideState.INACTIVE);
        sideConfig.put(Direction.EAST, MachineSideState.INACTIVE);
        sideConfig.put(Direction.WEST, MachineSideState.INACTIVE);
        return sideConfig;
    }

    public Iterable<ItemStack> getQueueItems()
    {
        return this.queue;
    }

    public int getQueueLength()
    {
        return this.queue.size();
    }

    public float getQueueLengthPercent()
    {
        int queueLength = getQueueLength();

        return (float) queueLength / ProcessQueue.QUEUE_SIZE;
    }

    public int getQueueLengthPercentInt()
    {
        return (int) Math.ceil(this.getQueueLengthPercent() * 100);
    }

    public int getMaxStackSize()
    {
        return 64;
    }

    public void setChanged()
    {
        if (this.level != null) {
            BlockState blockState = this.level.getBlockState(this.worldPosition);
            this.level.setBlockAndUpdate(this.worldPosition, blockState.setValue(ProcesssQueueBlock.FULLNESS, Fullness.fromFraction(this.getQueueLengthPercent())));
        }

        super.setChanged();
    }

    public ItemStack addItemToQueue(ItemStack newItem)
    {
        return this.addItemToQueue(newItem, false);
    }

    private ItemStack addItemToQueue(ItemStack newItem, boolean simulate)
    {
        if (newItem.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack lastItem = this.queue.peekLast();
        if (lastItem != null) {
            InternalAddResult addResult = this.addToStack(newItem, lastItem, simulate);
            InternalAddResult fillResult = this.fillQueue(addResult.stack, simulate);
            if (!simulate && (addResult.changed || fillResult.changed)) {
                this.setChanged();
            }
            return fillResult.stack;
        }

        if (this.outputStack.isEmpty()) {
            int newItemStackSize = Math.min(newItem.getMaxStackSize(), this.getMaxStackSize());

            if (newItem.getCount() <= newItemStackSize) {
                if (!simulate) {
                    this.outputStack = newItem;
                    this.setChanged();
                }
                return ItemStack.EMPTY;
            }

            ItemStack remainder = newItem.copy();
            ItemStack firstQueueItem = remainder.split(newItemStackSize);
            if (!simulate) {
                this.outputStack = firstQueueItem;
            }
            InternalAddResult fillResult = this.fillQueue(remainder, simulate);
            if (!simulate) {
                this.setChanged();
            }
            return fillResult.stack;
        }

        InternalAddResult addResult = this.addToStack(newItem, this.outputStack, simulate);
        InternalAddResult fillResult = this.fillQueue(addResult.stack, simulate);
        if (!simulate && (addResult.changed || fillResult.changed)) {
            this.setChanged();
        }
        return fillResult.stack;
    }

    private InternalAddResult addToStack(ItemStack newItem, ItemStack dest, boolean simulate)
    {
        int invMaxStackSize = this.getMaxStackSize();
        int destCount = dest.getCount();

        // If the destination stack is already full, don't proceed.
        if (destCount >= Math.min(dest.getMaxStackSize(), invMaxStackSize)) {
            return new InternalAddResult(newItem, false);
        }

        // If the item in the destination slot is not the same item being inserted, don't proceed.
        if (!ItemHandlerHelper.canItemStacksStack(newItem, dest)) {
            return new InternalAddResult(newItem, false);
        }

        int availableCount = Math.min(newItem.getMaxStackSize(), invMaxStackSize - destCount);
        int newItemCount = newItem.getCount();

        if (newItemCount <= availableCount) {
            if (!simulate) {
                dest.grow(newItemCount);
            }
            return new InternalAddResult(ItemStack.EMPTY, true);
        }

        ItemStack shrinkItem = newItem.copy();
        shrinkItem.shrink(availableCount);
        if (!simulate) {
            dest.grow(availableCount);
        }
        return new InternalAddResult(shrinkItem, true);
    }

    private InternalAddResult fillQueue(ItemStack remainder, boolean simulate)
    {
        int newItemStackSize = Math.min(remainder.getMaxStackSize(), this.getMaxStackSize());

        Supplier<Integer> queueSize;
        int currentQueueSize = this.queue.size();
        if (simulate) {
            int finalCurrentQueueSize = currentQueueSize;
            queueSize = () -> finalCurrentQueueSize;
        } else {
            queueSize = () -> this.queue.size();
        }

        boolean changed = false;
        while (queueSize.get() < ProcessQueue.QUEUE_SIZE && !remainder.isEmpty()) {
            ItemStack tempRemainder = remainder.copy();
            ItemStack nextQueueItem = tempRemainder.split(Math.min(tempRemainder.getCount(), newItemStackSize));
            if (simulate) {
                currentQueueSize++;
                int finalCurrentQueueSize = currentQueueSize;
                queueSize = () -> finalCurrentQueueSize;
            } else {
                this.queue.addLast(nextQueueItem);
            }
            remainder = tempRemainder;
            changed = true;
        }

        return new InternalAddResult(remainder, changed);
    }

    public ItemStack pullItemFromQueue(int amount)
    {
        return this.pullItemFromQueue(amount, false);
    }

    private ItemStack pullItemFromQueue(int amount, boolean simulate)
    {
        if (this.outputStack.isEmpty() || amount <= 0) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (this.outputStack.getCount() < amount) {
                return this.outputStack.copy();
            } else {
                ItemStack copy = this.outputStack.copy();
                copy.setCount(amount);
                return copy;
            }
        }

        int extractCount = Math.min(this.outputStack.getCount(), amount);
        ItemStack extracted = this.outputStack.split(extractCount);
        this.setChanged();
        return extracted;
    }

    public void tick()
    {
        if (this.level == null || this.level.isClientSide) {
            return;
        }

        this.cooldownTime--;
        if (this.cooldownTime > 0) {
            return;
        }
        this.cooldownTime = 10;

        if (!this.outputStack.isEmpty() || this.queue.size() == 0) {
            return;
        }

        this.outputStack = this.queue.removeFirst();
        this.setChanged();
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT packetData = getUpdateTag();
        return new SUpdateTileEntityPacket(this.worldPosition, 36, packetData);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        CompoundNBT packetData = pkt.getTag();
        BlockState state = this.level.getBlockState(this.worldPosition);
        this.handleUpdateTag(state, packetData);
    }

    @Override
    public CompoundNBT getUpdateTag()
    {
        CompoundNBT updateTag = new CompoundNBT();
        this.save(updateTag);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag)
    {
        this.load(state, tag);
    }

    @Override
    public void load(@Nonnull BlockState state, CompoundNBT nbt)
    {
        super.load(state, nbt);

        CompoundNBT outputStackNbt = nbt.getCompound("pqOutputStack");
        this.outputStack = ItemStack.of(outputStackNbt);

        this.queue = makeQueue();
        ListNBT queueItemsNbt = nbt.getList("Items", Constants.NBT.TAG_COMPOUND);
        int queueItemCount = queueItemsNbt.size();
        int queuePos = 0;
        for (int i = 0; i < queueItemCount && queuePos < ProcessQueue.QUEUE_SIZE; i++) {
            CompoundNBT queueItemNbt = queueItemsNbt.getCompound(queuePos);
            ItemStack queueItem = ItemStack.of(queueItemNbt);
            if (!queueItem.isEmpty()) {
                queuePos++;
                this.queue.add(queueItem);
            }
        }

        for (Direction direction : this.sideConfig.keySet()) {
            int sideState = nbt.getInt("Side_" + direction.getName());
            if (sideState > 0) {
                this.sideConfig.put(direction, MachineSideState.fromStateId(sideState));
            }
        }

        this.cooldownTime = nbt.getInt("QueueCooldown");
    }

    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound)
    {
        super.save(compound);

        CompoundNBT outputStackNbt = new CompoundNBT();
        this.outputStack.save(outputStackNbt);
        compound.put("pqOutputStack", outputStackNbt);

        ListNBT queueItemsNbt = new ListNBT();
        int queuePos = -1; // This is used purely to guarantee ordering when loading.
        for (ItemStack stack : this.queue) {
            if (stack.isEmpty()) {
                continue;
            }

            queuePos++;

            CompoundNBT queueItemNbt = new CompoundNBT();
            queueItemNbt.putInt("Slot", queuePos);
            stack.save(queueItemNbt);
            queueItemsNbt.add(queueItemNbt);
        }
        compound.put("Items", queueItemsNbt);

        for (Map.Entry<Direction, MachineSideState> sideStateEntry : this.sideConfig.entrySet()) {
            compound.putInt("Side_" + sideStateEntry.getKey().getName(), sideStateEntry.getValue().getValue());
        }

        compound.putInt("QueueCooldown", this.cooldownTime);

        return compound;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction facing)
    {
        if (this.remove || cap != CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return super.getCapability(cap, facing);
        }

        if (facing == null) {
            return LazyOptional.of(() -> new InvWrapper(this)).cast();
        }

        if (this.sideConfig.get(facing) == MachineSideState.INACTIVE) {
            return LazyOptional.empty();
        }

        return LazyOptional.of(() -> new SidedInvWrapper(this, facing)).cast();
    }

    public ITextComponent getDisplayName()
    {
        return NAME;
    }

    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity player)
    {
        World world = player.level;
        IWorldPosCallable blockLookup = IWorldPosCallable.create(world, this.worldPosition);

        IItemHandler invWrapper = new InvWrapper(this);
        IQueueInventory queueInvWrapper = new QueueInvWrapper(this);
        return new ProcessQueueContainer(
            windowId,
            playerInventory,
            blockLookup,
            this.dataAccess,
            invWrapper,
            invWrapper,
            queueInvWrapper
        );
    }

    public void dropAllContents(World world, BlockPos pos)
    {
        InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), this.outputStack);
        for (ItemStack queueItemStack : this.queue) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), queueItemStack);
        }
        this.queue = makeQueue();
    }

    private class InternalAddResult
    {
        private ItemStack stack;
        private boolean changed;

        private InternalAddResult(ItemStack stack, boolean changed)
        {
            this.stack = stack;
            this.changed = changed;
        }
    }

    private class SidedInvWrapper implements IItemHandler
    {
        private final ProcessQueueTile inv;
        private final Direction side;

        private SidedInvWrapper(ProcessQueueTile inv, Direction side)
        {
            this.inv = inv;
            this.side = side;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            SidedInvWrapper that = (SidedInvWrapper) o;

            return this.inv.equals(that.inv) && this.side == that.side;
        }

        @Override
        public int hashCode()
        {
            return this.inv.hashCode();
        }

        @Override
        public int getSlots()
        {
            if (this.inv.sideConfig.get(this.side) == MachineSideState.INACTIVE) {
                return 0;
            }

            return 1;
        }

        @Override
        public int getSlotLimit(int slot)
        {
            return this.inv.getMaxStackSize();
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            if (slot != 0) {
                return ItemStack.EMPTY;
            }

            MachineSideState sideState = this.inv.sideConfig.get(this.side);
            switch (sideState) {
                case OUTPUT:
                    return this.inv.outputStack;
                default:
                    return ItemStack.EMPTY;
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            if (slot != 0) {
                return false;
            }

            return this.inv.sideConfig.get(this.side) == MachineSideState.INPUT;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (slot != 0) {
                return stack;
            }

            MachineSideState sideState = this.inv.sideConfig.get(this.side);
            if (sideState != MachineSideState.INPUT) {
                return stack;
            }

            return this.inv.addItemToQueue(stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (amount == 0) {
                return ItemStack.EMPTY;
            }

            MachineSideState sideState = this.inv.sideConfig.get(this.side);
            if (sideState != MachineSideState.OUTPUT) {
                return ItemStack.EMPTY;
            }

            return this.inv.pullItemFromQueue(amount, simulate);
        }
    }

    private class InvWrapper implements IItemHandler
    {
        private final ProcessQueueTile inv;

        private InvWrapper(ProcessQueueTile inv)
        {
            this.inv = inv;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }

            InvWrapper that = (InvWrapper) o;

            return this.inv.equals(that.inv);
        }

        @Override
        public int hashCode()
        {
            return this.inv.hashCode();
        }

        public int getSlots()
        {
            return 2;
        }

        public int getSlotLimit(int slot)
        {
            return this.inv.getMaxStackSize();
        }

        public ItemStack getStackInSlot(int slot)
        {
            switch (slot) {
                case 0:
                    return ItemStack.EMPTY;
                case 1:
                    return this.inv.outputStack;
                default:
                    throw new IndexOutOfBoundsException("The Process Queue has 1 input slot and 1 output slot.");
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack)
        {
            return slot == 0;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
        {
            if (!this.isItemValid(slot, stack)) {
                return stack;
            }

            return this.inv.addItemToQueue(stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            if (slot != 1) {
                return ItemStack.EMPTY;
            }

            return this.inv.pullItemFromQueue(amount, simulate);
        }
    }

    private class QueueInvWrapper implements IQueueInventory
    {
        private final ProcessQueueTile inv;

        private QueueInvWrapper(ProcessQueueTile inv)
        {
            this.inv = inv;
        }

        @Override
        public int getContainerSize()
        {
            return ProcessQueue.QUEUE_SIZE;
        }

        @Override
        public int getQueueLength()
        {
            return this.inv.queue.size();
        }

        @Override
        public boolean isEmpty()
        {
            return this.inv.queue.size() == 0;
        }

        @Override
        public ItemStack getItem(int slot)
        {
            List<ItemStack> queue = this.inv.queue;
            if (slot < ProcessQueue.QUEUE_SIZE && slot >= queue.size()) {
                // This ensures that callers will still receive an exception if they
                // request an invalid slot.
                return ItemStack.EMPTY;
            }

            return queue.get(slot);
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
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack)
        {
            return false;
        }

        @Override
        public boolean stillValid(PlayerEntity player)
        {
            return true;
        }

        @Override
        public void clearContent()
        {
        }
    }
}
