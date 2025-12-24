package p455w0rd.danknull.inventory.slot;

import p455w0rd.danknull.util.inv.IItemHandler;
import p455w0rd.danknull.util.inv.SlotItemHandler;

/**
 * @author p455w0rd
 */
public class SlotDankNull extends SlotItemHandler {

    private final int index;

    public SlotDankNull(final IItemHandler handler, final int index, final int x, final int y) {
        super(handler, index, x, y);
        this.index = index;
    }

    // @Override
    // public boolean canTakeStack(EntityPlayer playerIn) {
    // // If there is an item, we should be able to take it.
    // // We let the actual extraction logic (decrStackSize) handle the "if" later.
    // return this.getItemHandler()
    // .getStackInSlot(index) != null;
    // }

}
