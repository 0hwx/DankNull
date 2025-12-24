package p455w0rd.danknull.inventory.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import p455w0rd.danknull.inventory.DankNullHandler;
import p455w0rd.danknull.items.ItemDankNull;

/**
 * @author p455w0rd
 */
public class ContainerDankNullItem extends ContainerDankNull {

    private final DankNullHandler handler;
    private final ItemStack stack;

    public ContainerDankNullItem(final EntityPlayer player, ItemStack stack) {
        super(player);
        this.stack = stack;
        handler = DankNullHandler.fromStack(player.worldObj, stack);
        init();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return stack != null && stack.getItem() instanceof ItemDankNull && super.canInteractWith(player);
    }

    @Override
    public DankNullHandler getHandler() {
        return handler;
    }

    @Override
    public ItemStack getDankNullStack() {
        return stack;
    }

}
