package p455w0rd.danknull.client.gui;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import codechicken.nei.guihook.IContainerTooltipHandler;

// doing it like this because some mods like "waila" add their own tooltips so we must register after them to be able to
// clear the list correctly
public class NEITooltipHandler implements IContainerTooltipHandler {

    @Override
    public List<String> handleItemTooltip(GuiContainer gui, ItemStack itemstack, int mousex, int mousey,
        List<String> currenttip) {
        if (gui instanceof GuiDankNull guiDankNull) {
            Slot s = guiDankNull.getSlotAtPosition(mousex, mousey);
            if (s instanceof Slot) {
                guiDankNull.renderToolTip(itemstack, mousex, mousey);
                currenttip.clear();
            }
        }
        return currenttip;
    }
}
