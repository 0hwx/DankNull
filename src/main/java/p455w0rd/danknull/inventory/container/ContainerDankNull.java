package p455w0rd.danknull.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.inventory.slot.SlotDankNull;
import p455w0rd.danknull.inventory.slot.SlotDankNullDock;
import p455w0rd.danknull.inventory.slot.SlotLocked;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.util.inv.ClickType;
import p455w0rd.danknull.util.inv.ItemHandlerHelper;

/**
 * @author BrockWS
 */
public abstract class ContainerDankNull extends Container {

    protected final EntityPlayer player;

    public ContainerDankNull(final EntityPlayer player) {
        this.player = player;
    }

    protected void init() {
        final InventoryPlayer playerInv = player.inventory;
        final DankNullHandler handler = getHandler();
        int lockedSlot = -1;
        final int numRows = handler.getTier()
            .getNumRows();
        for (int i = 0; i < playerInv.getSizeInventory(); i++) {
            final ItemStack currStack = playerInv.getStackInSlot(i);
            if (ItemStack.areItemStacksEqual(currStack, getDankNullStack())) {
                lockedSlot = i;
            }

        }
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(
                new SlotLocked(playerInv, i, i * 20 + 9 + i, 90 + numRows - 1 + numRows * 20 + 6, lockedSlot == i));
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                int slotIndex = j + i * 9 + 9;
                addSlotToContainer(
                    new SlotLocked(
                        playerInv,
                        slotIndex,
                        j * 20 + 9 + j,
                        149 + numRows - 1 + i - (6 - numRows) * 20 + i * 20,
                        lockedSlot == slotIndex));
            }
        }
        for (int i = 0; i < handler.getTier()
            .getNumRows(); i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(createDankNullSlot(handler, i, j));
            }
        }
    }

    public abstract DankNullHandler getHandler();

    public abstract ItemStack getDankNullStack();

    protected boolean isDock() {
        return false;
    }

    private SlotDankNull createDankNullSlot(final DankNullHandler handler, final int i, final int j) {
        return isDock() ? new SlotDankNullDock(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20)
            : new SlotDankNull(handler, j + i * 9, j * 20 + 9 + j, 19 + i + i * 20);
    }

    @Override
    public boolean canInteractWith(final EntityPlayer player) {
        return getHandler() != null;
    }

    @Override
    public Slot getSlot(final int slotId) {
        if (slotId < inventorySlots.size() && slotId >= 0) {
            return inventorySlots.get(slotId);
        }
        return null;
    }

    @Override
    public ItemStack slotClick(final int index, final int dragType, final int click, final EntityPlayer player) {
        ClickType clickType = ClickType.fromNumber(click);
        final Slot slot = getSlot(index);

        if (clickType == ClickType.QUICK_MOVE) {
            return transferStackInSlot(player, index);
        }

        if (clickType == ClickType.QUICK_CRAFT) {
            // This allows the vanilla drag-distribute logic to run
            // but it will respect your SlotDankNull#getSlotStackLimit and isItemValid overrides.
            return super.slotClick(index, dragType, click, player);
        }

        if (slot instanceof SlotDankNull) {
            InventoryPlayer inventoryPlayer = player.inventory;
            ItemStack heldStack = inventoryPlayer.getItemStack();
            ItemStack slotStack = slot.getStack();

            // Prevent putting a Dank/Null inside another Dank/Null
            if (ItemDankNull.isDankNull(heldStack)) return null;

            if (click == 0) { // PICKUP (Standard Click)
                if (heldStack != null) { // Insert into Dank/Null
                    ItemStack toAdd = heldStack.copy();
                    if (dragType == 1) toAdd.stackSize = 1;

                    ItemStack leftover = addStack(toAdd);

                    if (dragType == 0) {
                        inventoryPlayer.setItemStack(leftover); // Returns null or leftover
                    } else if (dragType == 1) {
                        if (leftover == null) {
                            heldStack.stackSize--;
                        }
                        inventoryPlayer.setItemStack(heldStack.stackSize <= 0 ? null : heldStack);
                    }
                    this.detectAndSendChanges();
                    return null;
                } else if (slotStack != null) { // Take out of Dank/Null
                    int amount = Math.min(slotStack.stackSize, slotStack.getMaxStackSize());
                    if (dragType == 1) amount = Math.max(1, amount / 2);

                    ItemStack extracted = slot.decrStackSize(amount);
                    inventoryPlayer.setItemStack(extracted);
                    this.detectAndSendChanges();
                    return null;
                }
            }
        }

        // Fallback to vanilla for player inventory slots
        return super.slotClick(index, dragType, click, player);

    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        Slot slot = (Slot) this.inventorySlots.get(index);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }

        ItemStack stack = slot.getStack();
        ItemStack copy = stack.copy();
        DankNullHandler handler = getHandler();

        if (!(slot instanceof SlotDankNull)) {
            int originalSize = stack.stackSize;

            ItemStack remainder = transferToDankNull(stack);

            if (remainder != null && remainder.stackSize == originalSize) {
                return null; // Nothing moved
            }

            slot.putStack(remainder != null && remainder.stackSize > 0 ? remainder : null);
        }

        else {
            int maxToMove = stack.stackSize;
            ItemStack toMove = slot.decrStackSize(maxToMove);

            if (toMove == null) return null;

            if (!this.mergeItemStack(toMove, 0, 36, true)) {
                getHandler().insertItem(slot.getSlotIndex(), toMove, false);
                return null;
            }
        }

        handler.onContentsChanged(-1);
        handler.updateSelectedSlot();
        slot.onSlotChanged();
        detectAndSendChanges();
        return copy;
    }

    protected ItemStack transferToDankNull(ItemStack stackToInsert) {
        DankNullHandler handler = getHandler();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack inSlot = handler.getStackInSlot(i);
            if (inSlot != null && ItemHandlerHelper.canItemStacksStack(stackToInsert, inSlot)) {
                int limit = handler.getSlotLimit(i);
                int canAdd = limit - inSlot.stackSize;

                if (canAdd > 0) {
                    int toAdd = Math.min(stackToInsert.stackSize, canAdd);
                    inSlot.stackSize += toAdd;
                    stackToInsert.stackSize -= toAdd;
                }
            }
            if (stackToInsert.stackSize <= 0) return null;
        }

        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i) == null && handler.isItemValid(i, stackToInsert)) {
                int limit = handler.getSlotLimit(i);
                if (stackToInsert.stackSize > limit) {
                    handler.setStackInSlot(i, stackToInsert.splitStack(limit));
                } else {
                    handler.setStackInSlot(i, stackToInsert);
                    return null;
                }
            }
        }
        return stackToInsert;
    }

    private ItemStack addStack(final ItemStack stack) {
        if (stack == null) return null;
        ItemStack leftover = stack.copy();
        DankNullHandler handler = getHandler();

        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.isItemValid(i, leftover)) {
                leftover = handler.insertItem(i, leftover, false);
                if (leftover == null) break;
            }
        }

        if (leftover != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i) == null && handler.isItemValid(i, leftover)) {
                    handler.setStackInSlot(i, leftover);
                    leftover = null;
                    break;
                }
            }
        }

        handler.updateSelectedSlot();

        return leftover;
    }
}
