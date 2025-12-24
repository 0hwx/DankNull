package p455w0rd.danknull.integration.waila;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import p455w0rd.danknull.api.DankNullTier;
import p455w0rd.danknull.blocks.tiles.TileDankNullDock;
import p455w0rd.danknull.init.ModBlocks;
import p455w0rd.danknull.integration.WAILA;
import p455w0rd.danknull.inventory.DankNullHandler;

/**
 * @author p455w0rd
 *
 */
public class WAILADankNullDockProvider implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final ItemStack stack = new ItemStack(ModBlocks.DANKNULL_DOCK);
        final TileEntity tile = accessor.getTileEntity();
        if (tile != null && tile instanceof TileDankNullDock) {
            final TileDankNullDock te = (TileDankNullDock) tile;
            final NBTTagCompound nbttagcompound = new NBTTagCompound();
            te.writeToNBT(nbttagcompound);
            stack.setTagInfo("BlockEntityTag", nbttagcompound);
        }
        return stack;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final TileDankNullDock dankDock = (TileDankNullDock) accessor.getTileEntity();
        if (dankDock.getDankNull() != null) {
            ItemStack dockedDankNull = dankDock.getDankNull();
            if (dockedDankNull != null) {
                currenttip.add(WAILA.toolTipEnclose);

                // Tier color and name
                EnumChatFormatting colorCode = DankNullTier.Rarities.getRarityFromMeta(
                    DankNullTier.getTier(dockedDankNull)
                        .ordinal()).rarityColor;
                currenttip.add(colorCode + dockedDankNull.getDisplayName() + EnumChatFormatting.GRAY + " Docked");

                DankNullHandler handler = dankDock.getDankHandler();
                if (handler != null) {
                    int selectedIndex = handler.getSelected();

                    // Safety check: ensure the slot exists (prevents the 'Slot X not in range' crash in WAILA)
                    if (selectedIndex >= 0 && selectedIndex < handler.getSlots()) {
                        ItemStack selectedStack = handler.getStackInSlot(selectedIndex);
                        if (selectedStack != null) {
                            currenttip.add(
                                selectedStack.getDisplayName() + " "
                                    + StatCollector.translateToLocal("dn.selected.desc"));

                            String countDisplay = (DankNullTier.getTier(dockedDankNull) == DankNullTier.CREATIVE)
                                ? StatCollector.translateToLocal("dn.infinite.desc")
                                : String.valueOf(selectedStack.stackSize);

                            currenttip.add(StatCollector.translateToLocal("dn.count.desc") + ": " + countDisplay);
                            currenttip.add(
                                StatCollector.translateToLocal("dn.extract_mode.desc") + ": "
                                    + handler.getExtractionMode(selectedIndex)
                                        .getTooltip());
                        }
                    }
                }
                currenttip.add(WAILA.toolTipEnclose);
            }
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip,
        final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
        int y, int z) {
        return null;
    }
}
