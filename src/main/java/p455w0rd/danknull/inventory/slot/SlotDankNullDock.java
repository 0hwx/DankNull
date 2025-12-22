package p455w0rd.danknull.inventory.slot;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.util.inv.IItemHandler;

/**
 * @author p455w0rd
 */
public class SlotDankNullDock extends SlotDankNull {

    private final int cachedIndex;

    public SlotDankNullDock(final IItemHandler handler, final int index, final int x, final int y) {
        super(handler, index, x, y);
        cachedIndex = index;
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    @Override
    @Nonnull
    public ItemStack getStack() {
        return getDankNullHandler().getStackInSlot(cachedIndex);
    }

    public DankNullHandler getDankNullHandler() {
        return (DankNullHandler) getItemHandler();
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable

    /**
     * Helper method to put a stack in the slot.
     */
    @Override
    public void putStack(@Nonnull final ItemStack stack) {
        getDankNullHandler().setStackInSlot(cachedIndex, stack);
        onSlotChanged();
    }

    @Override
    public int getItemStackLimit(@Nonnull final ItemStack stack) {
        final ItemStack maxAdd = stack.copy();
        final int maxInput = stack.getMaxStackSize();
        maxAdd.stackSize = (maxInput);
        final ItemStack currentStack = getDankNullHandler().getStackInSlot(cachedIndex);
        getDankNullHandler().setStackInSlot(cachedIndex, null);
        final ItemStack remainder = getDankNullHandler().insertItem(cachedIndex, maxAdd, true);
        getDankNullHandler().setStackInSlot(cachedIndex, currentStack);
        return maxInput - remainder.stackSize;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean canTakeStack(final EntityPlayer playerIn) {
        return getDankNullHandler().extractItemIngoreExtractionMode(cachedIndex, 1, true) != null;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    @Override
    @Nonnull
    public ItemStack decrStackSize(final int amount) {
        return getDankNullHandler().extractItemIngoreExtractionMode(cachedIndex, amount, false);
    }

}
